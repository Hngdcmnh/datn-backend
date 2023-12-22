package com.mshop.storageservice.controller.dto;

public class FileDto {
    private final String fileUrl;

    public FileDto(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
