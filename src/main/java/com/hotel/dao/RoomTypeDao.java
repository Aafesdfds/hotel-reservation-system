package com.hotel.dao;

import com.hotel.model.RoomType;
import com.hotel.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDao {

    public List<RoomType> findAll() {
        String sql = "SELECT rt.*, (SELECT COUNT(*) FROM room r WHERE r.type_id = rt.id) AS total_rooms "
                + "FROM room_type rt ORDER BY rt.price";
        List<RoomType> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs, true));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房型失败", e);
        }
        return list;
    }

    public RoomType findById(int id) {
        String sql = "SELECT rt.*, (SELECT COUNT(*) FROM room r WHERE r.type_id = rt.id) AS total_rooms "
                + "FROM room_type rt WHERE rt.id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs, true) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询房型失败", e);
        }
    }

    public int insert(RoomType t) {
        String sql = "INSERT INTO room_type(name,price,capacity,bed_type,area,amenities,description,image,created_at) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, t);
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("新增房型失败", e);
        }
    }

    public void update(RoomType t) {
        String sql = "UPDATE room_type SET name=?,price=?,capacity=?,bed_type=?,area=?,amenities=?,description=?,image=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, t);
            ps.setInt(9, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改房型失败", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM room_type WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除房型失败", e);
        }
    }

    public int countRoomsOfType(int typeId) {
        String sql = "SELECT COUNT(*) FROM room WHERE type_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计房间失败", e);
        }
    }

    private void bind(PreparedStatement ps, RoomType t) throws SQLException {
        ps.setString(1, t.getName());
        ps.setBigDecimal(2, t.getPrice());
        ps.setInt(3, t.getCapacity());
        ps.setString(4, t.getBedType());
        ps.setInt(5, t.getArea());
        ps.setString(6, t.getAmenities());
        ps.setString(7, t.getDescription());
        ps.setString(8, t.getImage());
    }

    private RoomType map(ResultSet rs, boolean withTotal) throws SQLException {
        RoomType t = new RoomType();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setPrice(rs.getBigDecimal("price"));
        t.setCapacity(rs.getInt("capacity"));
        t.setBedType(rs.getString("bed_type"));
        t.setArea(rs.getInt("area"));
        t.setAmenities(rs.getString("amenities"));
        t.setDescription(rs.getString("description"));
        t.setImage(rs.getString("image"));
        if (withTotal) {
            t.setTotalRooms(rs.getInt("total_rooms"));
        }
        return t;
    }
}
