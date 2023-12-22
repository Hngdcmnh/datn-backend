package com.mshop.storageservice.controller.dto;

public class ImageDto {
    private final String imageUrl;

    public ImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
