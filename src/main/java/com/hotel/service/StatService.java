package com.hotel.service;

import com.hotel.dao.ReservationDao;
import com.hotel.dao.RoomDao;
import com.hotel.dao.StatDao;
import com.hotel.dao.UserDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatService {

    private final StatDao statDao = new StatDao();
    private final RoomDao roomDao = new RoomDao();
    private final ReservationDao reservationDao = new ReservationDao();
    private final UserDao userDao = new UserDao();

    /** 仪表盘顶部的关键指标 */
    public Map<String, Object> dashboard() {
        Map<String, Object> m = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        Date sqlToday = Date.valueOf(today);

        int totalRooms = roomDao.countAll();
        Map<String, Integer> roomStatus = roomDao.countByStatus();
        int inHouse = reservationDao.countInHouse(sqlToday);

        m.put("totalRooms", totalRooms);
        m.put("roomStatus", roomStatus);
        m.put("available", roomStatus.getOrDefault("AVAILABLE", 0));
        m.put("occupied", roomStatus.getOrDefault("OCCUPIED", 0));
        m.put("cleaning", roomStatus.getOrDefault("CLEANING", 0)
                + roomStatus.getOrDefault("DIRTY", 0) + roomStatus.getOrDefault("CLEANED", 0));
        m.put("todayCheckin", reservationDao.countTodayCheckin(sqlToday));
        m.put("todayCheckout", reservationDao.countTodayCheckout(sqlToday));
        m.put("inHouse", inHouse);
        double occ = totalRooms == 0 ? 0 : inHouse * 100.0 / totalRooms;
        m.put("occupancyRate", round1(occ));

        BigDecimal monthRev = statDao.monthlyRevenue(today.getYear())
                .getOrDefault(today.getMonthValue(), BigDecimal.ZERO);
        m.put("monthRevenue", monthRev);
        m.put("memberCount", userDao.countByRole("USER"));
        m.put("reservedCount", reservationDao.countByStatus("RESERVED"));
        return m;
    }

    /** 房态看板：各房态数量（含中文名） */
    public List<String[]> roomStatusBoard() {
        Map<String, Integer> counts = roomDao.countByStatus();
        String[][] order = {
                {"AVAILABLE", "空闲"}, {"OCCUPIED", "已入住"}, {"DIRTY", "待清洁"},
                {"CLEANING", "清洁中"}, {"CLEANED", "已清洁"}, {"MAINTENANCE", "维修"}
        };
        List<String[]> list = new ArrayList<>();
        for (String[] o : order) {
            list.add(new String[]{o[0], o[1], String.valueOf(counts.getOrDefault(o[0], 0))});
        }
        return list;
    }

    /** 月度趋势：营收、订单量、入住率，用于折线/柱状图 */
    public MonthlyChart monthlyChart(int year) {
        Map<Integer, BigDecimal> rev = statDao.monthlyRevenue(year);
        Map<Integer, Integer> cnt = statDao.monthlyCount(year);
        int totalRooms = roomDao.countAll();
        LocalDate today = LocalDate.now();
        int lastMonth = (year == today.getYear()) ? today.getMonthValue() : 12;

        MonthlyChart chart = new MonthlyChart();
        for (int mo = 1; mo <= lastMonth; mo++) {
            YearMonth ym = YearMonth.of(year, mo);
            Date start = Date.valueOf(ym.atDay(1));
            Date end = Date.valueOf(ym.plusMonths(1).atDay(1));
            long nights = statDao.roomNightsInMonth(start, end);
            int days = ym.lengthOfMonth();
            double occ = totalRooms == 0 ? 0 : nights * 100.0 / (totalRooms * (double) days);
            chart.labels.add(mo + "月");
            chart.revenue.add(rev.getOrDefault(mo, BigDecimal.ZERO));
            chart.orders.add(cnt.getOrDefault(mo, 0));
            chart.occupancy.add(round1(occ));
        }
        return chart;
    }

    /** 各房型订单量与营收 */
    public List<Object[]> typeDistribution() {
        return statDao.typeDistribution();
    }

    /** 订单状态分布（中文名 -> 数量） */
    public Map<String, Integer> statusDistribution() {
        Map<String, Integer> raw = statDao.reservationStatusCounts();
        Map<String, Integer> out = new LinkedHashMap<>();
        put(out, raw, "RESERVED", "已预订");
        put(out, raw, "CHECKED_IN", "已入住");
        put(out, raw, "CHECKED_OUT", "已退房");
        put(out, raw, "CANCELLED", "已取消");
        return out;
    }

    private void put(Map<String, Integer> out, Map<String, Integer> raw, String key, String label) {
        if (raw.containsKey(key)) {
            out.put(label, raw.get(key));
        }
    }

    private double round1(double v) {
        return BigDecimal.valueOf(v).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public static class MonthlyChart {
        public List<String> labels = new ArrayList<>();
        public List<BigDecimal> revenue = new ArrayList<>();
        public List<Integer> orders = new ArrayList<>();
        public List<Double> occupancy = new ArrayList<>();
    }
}
