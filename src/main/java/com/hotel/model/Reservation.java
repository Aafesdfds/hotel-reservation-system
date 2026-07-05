package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Reservation {
    private int id;
    private String orderNo;
    private int userId;
    private int typeId;
    private Integer roomId;      // 分配到的具体房间，可能为空
    private Date checkinDate;
    private Date checkoutDate;
    private int nights;
    private String guestName;
    private String guestPhone;
    private String guestIdCard;
    private int guestCount;
    private BigDecimal totalPrice;
    private String status;       // RESERVED 已预订 / CHECKED_IN 已入住 / CHECKED_OUT 已退房 / CANCELLED 已取消
    private String remark;
    private Timestamp createdAt;

    // 非数据库字段，联表查出来给页面显示
    private String typeName;
    private String roomNo;
    private String username;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public Date getCheckinDate() { return checkinDate; }
    public void setCheckinDate(Date checkinDate) { this.checkinDate = checkinDate; }
    public Date getCheckoutDate() { return checkoutDate; }
    public void setCheckoutDate(Date checkoutDate) { this.checkoutDate = checkoutDate; }
    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public String getGuestIdCard() { return guestIdCard; }
    public void setGuestIdCard(String guestIdCard) { this.guestIdCard = guestIdCard; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "RESERVED": return "已预订";
            case "CHECKED_IN": return "已入住";
            case "CHECKED_OUT": return "已退房";
            case "CANCELLED": return "已取消";
            default: return status;
        }
    }
}
