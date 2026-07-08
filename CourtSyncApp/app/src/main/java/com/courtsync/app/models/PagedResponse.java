package com.courtsync.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PagedResponse<T> {
    @SerializedName("content")
    private List<T> content;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("number")
    private int number;

    @SerializedName("last")
    private boolean last;

    public List<T> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getNumber() { return number; }
    public boolean isLast() { return last; }
}
