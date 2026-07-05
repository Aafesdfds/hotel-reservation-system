package com.hotel.model;

import java.math.BigDecimal;

public class RoomType {
    private int id;
    private String name;
    private BigDecimal price;
    private int capacity;
    private String bedType;
    private int area;
    private String amenities;
    private String description;
    private String image;

    // 下面几个不是数据库字段，是查询时临时算出来给页面用的
    private int totalRooms;      // 该房型总房间数
    private int availableCount;  // 所选日期内可订数量

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }
    public int getArea() { return area; }
    public void setArea(int area) { this.area = area; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    /** 设施按逗号拆成数组，方便页面做标签 */
    public String[] getAmenityList() {
        if (amenities == null || amenities.isEmpty()) {
            return new String[0];
        }
        return amenities.split(",");
    }
}
