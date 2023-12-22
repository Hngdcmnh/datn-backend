package com.mshop.storageservice.config;

public enum StorageSource {
    GOOGLE_STORAGE("google-storage"),
    CLOUDINARY("cloudinary"),
    STORAGE_SERVICE("storage-service");

    final String value;

    StorageSource(String value) {
        this.value = value;
    }

    public static StorageSource fromValue(String value) {
        switch (value.toLowerCase()) {
            case "google-storage":
                return GOOGLE_STORAGE;
            case "cloudinary":
                return CLOUDINARY;
            default:
                return STORAGE_SERVICE;
        }
    }
}
