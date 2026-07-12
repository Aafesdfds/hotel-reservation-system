package com.hotel.service;

import com.hotel.dao.RoomDao;
import com.hotel.model.Room;

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
