package com.hotel.dao;

import com.hotel.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 经营统计相关的聚合查询：月度营收、入住率、房型分布等。
 * 统计口径统一排除已取消订单。
 */
public class StatDao {

    /** 某年每月的营收，month(1-12) -> 金额 */
    public Map<Integer, BigDecimal> monthlyRevenue(int year) {
        String sql = "SELECT MONTH(checkin_date) m, COALESCE(SUM(total_price),0) rev "
                + "FROM reservation WHERE YEAR(checkin_date)=? AND status <> 'CANCELLED' "
                + "GROUP BY MONTH(checkin_date)";
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("m"), rs.getBigDecimal("rev"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计月度营收失败", e);
        }
        return map;
    }

    /** 某年每月的订单量 */
    public Map<Integer, Integer> monthlyCount(int year) {
        String sql = "SELECT MONTH(checkin_date) m, COUNT(*) c "
                + "FROM reservation WHERE YEAR(checkin_date)=? AND status <> 'CANCELLED' "
                + "GROUP BY MONTH(checkin_date)";
        Map<Integer, Integer> map = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("m"), rs.getInt("c"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计月度订单失败", e);
        }
        return map;
    }

    /** 某月被占用的间夜数（用来算入住率）：区间与该月重叠的部分累加 */
    public long roomNightsInMonth(Date monthStart, Date monthEnd) {
        String sql = "SELECT COALESCE(SUM(DATEDIFF(LEAST(checkout_date, ?), GREATEST(checkin_date, ?))),0) n "
                + "FROM reservation WHERE status <> 'CANCELLED' AND checkin_date < ? AND checkout_date > ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, monthEnd);
            ps.setDate(2, monthStart);
            ps.setDate(3, monthEnd);
            ps.setDate(4, monthStart);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("n") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计间夜数失败", e);
        }
    }

    /** 各房型的订单量与营收，房型热度分析用 */
    public List<Object[]> typeDistribution() {
        String sql = "SELECT rt.name, COUNT(res.id) cnt, COALESCE(SUM(res.total_price),0) rev "
                + "FROM room_type rt LEFT JOIN reservation res "
                + "ON res.type_id = rt.id AND res.status <> 'CANCELLED' "
                + "GROUP BY rt.id, rt.name ORDER BY cnt DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getString("name"), rs.getInt("cnt"), rs.getBigDecimal("rev")});
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计房型分布失败", e);
        }
        return list;
    }

    public Map<String, Integer> reservationStatusCounts() {
        String sql = "SELECT status, COUNT(*) c FROM reservation GROUP BY status";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("c"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计订单状态失败", e);
        }
        return map;
    }

    /** 指定状态集合的总营收，例如已入住+已退房=已实现营收 */
    public BigDecimal revenueByStatuses(String statusInClause) {
        String sql = "SELECT COALESCE(SUM(total_price),0) FROM reservation WHERE status IN " + statusInClause;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("统计营收失败", e);
        }
    }
}
