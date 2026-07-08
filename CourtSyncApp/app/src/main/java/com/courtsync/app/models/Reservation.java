package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Reservation {
    @SerializedName("id")
    private long id;

    @SerializedName("hallId")
    private long hallId;

    @SerializedName("hallName")
    private String hallName;

    @SerializedName("hallImageUrl")
    private String hallImageUrl;

    @SerializedName("sportName")
    private String sportName;

    @SerializedName("date")
    private String date;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("totalPrice")
    private BigDecimal totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("relativeDate")
    private String relativeDate;

    public long getId() { return id; }
    public long getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public String getHallImageUrl() { return hallImageUrl; }
    public String getSportName() { return sportName; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getRelativeDate() { return relativeDate; }
}
