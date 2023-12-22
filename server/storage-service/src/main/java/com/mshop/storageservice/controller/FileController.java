package com.mshop.storageservice.controller;

import com.sudo248.domain.base.BaseResponse;
import com.mshop.storageservice.config.StorageSource;
import com.mshop.storageservice.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upload(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "source", required = false, defaultValue = "google-storage") String source
    ) {
        final StorageSource storageSource = StorageSource.fromValue(source);
        if (storageSource == StorageSource.CLOUDINARY) return BaseResponse.status(HttpStatus.BAD_REQUEST, "Cloudinary not support upload file");
        return fileService.uploadFile(file, storageSource);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("fileName") String fileName
    ) {
        return fileService.downloadFile(fileName);
    }
}
