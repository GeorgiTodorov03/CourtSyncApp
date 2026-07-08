package com.courtsync.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String city;
    private String profileImageUrl;
}
