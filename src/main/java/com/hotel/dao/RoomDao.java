package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoomDao {

    /** 房间列表，可按房型和房态过滤，联表带出房型名 */
    public List<Room> findAll(Integer typeId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, rt.name AS type_name FROM room r JOIN room_type rt ON r.type_id = rt.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (typeId != null && typeId > 0) {
            sql.append(" AND r.type_id = ?");
            params.add(typeId);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND r.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY r.room_no");
        List<Room> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房间失败", e);
        }
        return list;
    }

    public Room findById(int id) {
        String sql = "SELECT r.*, rt.name AS type_name FROM room r JOIN room_type rt ON r.type_id = rt.id WHERE r.id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房间失败", e);
        }
    }

    public boolean existsRoomNo(String roomNo, int excludeId) {
        String sql = "SELECT 1 FROM room WHERE room_no = ? AND id <> ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roomNo);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房间号失败", e);
        }
    }

    public void insert(Room r) {
        String sql = "INSERT INTO room(room_no,type_id,floor,status,note) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNo());
            ps.setInt(2, r.getTypeId());
            ps.setInt(3, r.getFloor());
            ps.setString(4, r.getStatus() == null ? "AVAILABLE" : r.getStatus());
            ps.setString(5, r.getNote());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("新增房间失败", e);
        }
    }

    public void update(Room r) {
        String sql = "UPDATE room SET room_no=?, type_id=?, floor=?, status=?, note=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNo());
            ps.setInt(2, r.getTypeId());
            ps.setInt(3, r.getFloor());
            ps.setString(4, r.getStatus());
            ps.setString(5, r.getNote());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改房间失败", e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE room SET status=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新房态失败", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM room WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除房间失败", e);
        }
    }

    public boolean hasReservation(int roomId) {
        String sql = "SELECT 1 FROM reservation WHERE room_id=? LIMIT 1";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房间订单失败", e);
        }
    }

    /** 各房态的房间数量，仪表盘和房态看板用 */
    public Map<String, Integer> countByStatus() {
        String sql = "SELECT status, COUNT(*) c FROM room GROUP BY status";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("c"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计房态失败", e);
        }
        return map;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM room";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计房间失败", e);
        }
    }

    /**
     * 统计每个房型在 [checkin, checkout) 内还能订几间：排除维修房和已被占用（预订/入住）的房。
     * 返回 typeId -> 可订数量。
     */
    public Map<Integer, Integer> availableCountByType(Date checkin, Date checkout) {
        String sql = "SELECT r.type_id, COUNT(*) c FROM room r "
                + "WHERE r.status <> 'MAINTENANCE' AND r.id NOT IN ("
                + "  SELECT room_id FROM reservation WHERE room_id IS NOT NULL "
                + "  AND status IN ('RESERVED','CHECKED_IN') "
                + "  AND checkin_date < ? AND checkout_date > ?"
                + ") GROUP BY r.type_id";
        Map<Integer, Integer> map = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, checkout);
            ps.setDate(2, checkin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("type_id"), rs.getInt("c"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计可订房失败", e);
        }
        return map;
    }

    /**
     * 预订并发控制第 1 步：把该房型的所有房间行加排他锁（FOR UPDATE）。
     * 同一房型的并发预订会在这里排队，从而避免把同一间房卖给两个人。
     * 必须在同一个事务的 Connection 上执行。
     */
    public void lockRoomsOfType(Connection c, int typeId) throws SQLException {
        String sql = "SELECT id FROM room WHERE type_id = ? ORDER BY id FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 只为加锁，不需要读内容
                }
            }
        }
    }

    /**
     * 预订并发控制第 2 步：在已加锁的前提下，找一间该房型、该日期区间内空闲、非维修的房。
     * 找不到返回 -1（说明这个房型这几天订满了）。
     */
    public int findFreeRoom(Connection c, int typeId, Date checkin, Date checkout) throws SQLException {
        String sql = "SELECT r.id FROM room r "
                + "WHERE r.type_id = ? AND r.status <> 'MAINTENANCE' "
                + "AND NOT EXISTS ("
                + "  SELECT 1 FROM reservation res WHERE res.room_id = r.id "
                + "  AND res.status IN ('RESERVED','CHECKED_IN') "
                + "  AND res.checkin_date < ? AND res.checkout_date > ?"
                + ") ORDER BY r.id LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            ps.setDate(2, checkout);
            ps.setDate(3, checkin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    private Room map(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id"));
        r.setRoomNo(rs.getString("room_no"));
        r.setTypeId(rs.getInt("type_id"));
        r.setFloor(rs.getInt("floor"));
        r.setStatus(rs.getString("status"));
        r.setNote(rs.getString("note"));
        r.setTypeName(rs.getString("type_name"));
        return r;
    }
}
