package com.hotel.service;

import com.hotel.dao.RoomDao;
import com.hotel.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 房间管理：增删改 + 房态流转。
 * 房态流转：空闲 -> 已入住(办入住时自动) -> 待清洁(办退房时自动) -> 清洁中 -> 已清洁 -> 空闲。
 * 其中"已清洁"表示房间打扫完、等待确认，确认后再变回空闲可订。
 */
public class RoomService {

    private final RoomDao roomDao = new RoomDao();

    private static final List<String> HOUSEKEEPING =
            Arrays.asList("AVAILABLE", "DIRTY", "CLEANING", "CLEANED", "MAINTENANCE");

    /** 房态全集（含已入住），编辑房间表单里可回填的合法值 */
    private static final List<String> ALL_STATUSES =
            Arrays.asList("AVAILABLE", "OCCUPIED", "DIRTY", "CLEANING", "CLEANED", "MAINTENANCE");

    public void add(Room r) throws BookingException {
        if (r.getRoomNo() == null || r.getRoomNo().trim().isEmpty()) {
            throw new BookingException("请填写房间号");
        }
        if (roomDao.existsRoomNo(r.getRoomNo().trim(), 0)) {
            throw new BookingException("房间号 " + r.getRoomNo() + " 已存在");
        }
        r.setRoomNo(r.getRoomNo().trim());
        if (r.getStatus() == null || r.getStatus().isEmpty()) {
            r.setStatus("AVAILABLE");
        }
        roomDao.insert(r);
    }

    /**
     * 批量新增房间：从起始房号往后连续生成 count 间，楼层、房型、房态、备注都一样。
     * 比如起始 301、数量 5，就建 301、302、303、304、305。
     * 已经存在的房号自动跳过，返回一句结果说明（新增了几间、跳过了哪些）。
     */
    public String batchAdd(String startNo, int count, int typeId, int floor, String status, String note)
            throws BookingException {
        if (startNo == null || startNo.trim().isEmpty()) {
            throw new BookingException("请填写起始房间号");
        }
        if (count < 1) {
            count = 1;
        }
        if (count > 100) {
            throw new BookingException("一次最多新增 100 间");
        }
        List<String> roomNos = buildRoomNos(startNo.trim(), count);

        List<String> added = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (String no : roomNos) {
            Room r = new Room();
            r.setRoomNo(no);
            r.setTypeId(typeId);
            r.setFloor(floor);
            r.setStatus(status);
            r.setNote(note);
            try {
                add(r);              // 复用单间的校验和插入，房号重复会抛异常
                added.add(no);
            } catch (BookingException e) {
                skipped.add(no);     // 房号已存在，跳过这一间
            }
        }

        StringBuilder msg = new StringBuilder("成功新增 ").append(added.size()).append(" 间房");
        if (!skipped.isEmpty()) {
            msg.append("，跳过 ").append(skipped.size()).append(" 个已存在的房号：").append(joinNos(skipped));
        }
        return msg.toString();
    }

    /** 从起始房号连续生成 count 个房号：保留末尾数字的位数逐个加一，如 301 生成 301、302… */
    private List<String> buildRoomNos(String startNo, int count) throws BookingException {
        int i = startNo.length();
        while (i > 0 && Character.isDigit(startNo.charAt(i - 1))) {
            i--;
        }
        String prefix = startNo.substring(0, i);
        String digits = startNo.substring(i);
        if (digits.isEmpty()) {
            if (count > 1) {
                throw new BookingException("批量新增时，起始房间号要以数字结尾（如 301）");
            }
            List<String> single = new ArrayList<>();
            single.add(startNo);
            return single;
        }
        int width = digits.length();
        long start = Long.parseLong(digits);
        List<String> list = new ArrayList<>();
        for (int k = 0; k < count; k++) {
            String num = String.valueOf(start + k);
            while (num.length() < width) {   // 不足原位数就补零，保持 301、302 这样对齐
                num = "0" + num;
            }
            list.add(prefix + num);
        }
        return list;
    }

    private String joinNos(List<String> nos) {
        int show = Math.min(nos.size(), 10);   // 太多只列前 10 个，免得提示太长
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < show; i++) {
            if (i > 0) {
                sb.append("、");
            }
            sb.append(nos.get(i));
        }
        if (nos.size() > show) {
            sb.append(" 等");
        }
        return sb.toString();
    }

    public void update(Room r) throws BookingException {
        if (r.getRoomNo() == null || r.getRoomNo().trim().isEmpty()) {
            throw new BookingException("请填写房间号");
        }
        if (roomDao.existsRoomNo(r.getRoomNo().trim(), r.getId())) {
            throw new BookingException("房间号 " + r.getRoomNo() + " 已被其它房间使用");
        }
        if (r.getStatus() == null || !ALL_STATUSES.contains(r.getStatus())) {
            throw new BookingException("不支持的房态");
        }
        Room current = roomDao.findById(r.getId());
        if (current == null) {
            throw new BookingException("房间不存在");
        }
        // 在住房不能通过编辑表单直接改走房态，必须先办理退房，避免绕过房态流转
        if ("OCCUPIED".equals(current.getStatus()) && !"OCCUPIED".equals(r.getStatus())) {
            throw new BookingException("房间正在入住，请先办理退房再修改房态");
        }
        r.setRoomNo(r.getRoomNo().trim());
        roomDao.update(r);
    }

    public void delete(int roomId) throws BookingException {
        if (roomDao.hasReservation(roomId)) {
            throw new BookingException("该房间已有订单记录，不能删除，可改为维修状态");
        }
        roomDao.delete(roomId);
    }

    /** 房态流转。已入住的房要先办退房，不能在这里直接改动。 */
    public void changeStatus(int roomId, String target) throws BookingException {
        if (!HOUSEKEEPING.contains(target)) {
            throw new BookingException("不支持的房态");
        }
        Room r = roomDao.findById(roomId);
        if (r == null) {
            throw new BookingException("房间不存在");
        }
        if ("OCCUPIED".equals(r.getStatus())) {
            throw new BookingException("房间正在入住，请先办理退房");
        }
        roomDao.updateStatus(roomId, target);
    }
}
