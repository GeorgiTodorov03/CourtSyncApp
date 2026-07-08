package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class SportHall {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("district")
    private String district;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("pricePerHour")
    private BigDecimal pricePerHour;

    @SerializedName("openTime")
    private String openTime;

    @SerializedName("closeTime")
    private String closeTime;

    @SerializedName("rating")
    private double rating;

    @SerializedName("reviewCount")
    private int reviewCount;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("hallType")
    private String hallType;

    @SerializedName("sportName")
    private String sportName;

    @SerializedName("sportId")
    private Long sportId;

    @SerializedName("bookingsToday")
    private int bookingsToday;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    @SerializedName("distanceKm")
    private Double distanceKm;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public BigDecimal getPricePerHour() { return pricePerHour; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getImageUrl() { return imageUrl; }
    public String getHallType() { return hallType; }
    public String getSportName() { return sportName; }
    public Long getSportId() { return sportId; }
    public int getBookingsToday() { return bookingsToday; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public Double getDistanceKm() { return distanceKm; }
}
