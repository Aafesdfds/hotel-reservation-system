package com.hotel.model;

public class Room {
    private int id;
    private String roomNo;
    private int typeId;
    private int floor;
    private String status;   // AVAILABLE 空闲 / OCCUPIED 已入住 / DIRTY 待清洁 / CLEANING 清洁中 / CLEANED 已清洁 / MAINTENANCE 维修
    private String note;

    private String typeName; // 非数据库字段，联表查出来的房型名

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    /** 房态的中文名，页面直接用 */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "AVAILABLE": return "空闲";
            case "OCCUPIED": return "已入住";
            case "DIRTY": return "待清洁";
            case "CLEANING": return "清洁中";
            case "CLEANED": return "已清洁";
            case "MAINTENANCE": return "维修";
            default: return status;
        }
    }
}
