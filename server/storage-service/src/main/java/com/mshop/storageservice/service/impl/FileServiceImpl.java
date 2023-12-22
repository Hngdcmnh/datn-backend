package com.mshop.storageservice.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.exception.ApiException;
import com.mshop.storageservice.config.StorageSource;
import com.mshop.storageservice.controller.dto.FileDto;
import com.mshop.storageservice.controller.dto.ImageDto;
import com.mshop.storageservice.service.FileService;
import com.mshop.storageservice.uitls.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    @Value("${store.file.path}")
    private String storeFilePath;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Value("${gcp.bucket.file.dir}")
    private String fileDir;

    private final Storage storage;

    final ServletContext servletContext;

    public FileServiceImpl(Storage storage, ServletContext servletContext) {
        this.storage = storage;
        this.servletContext = servletContext;
    }

    @Override
    public ResponseEntity<?> uploadFile(MultipartFile file, StorageSource source) {
        return handleException(() -> {
            String filename = file.getOriginalFilename();
            try {

                switch (source) {
                    case GOOGLE_STORAGE:
                        filename = uploadFileToGoogleCloud(file);
                        break;
                    case STORAGE_SERVICE:
                        filename = storeFileOnServer(file);
                    default:
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Cloudinary not support file");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Could not save image file: " + filename, e);
            }
            FileDto fileDto = new FileDto(filename);
            return BaseResponse.ok(fileDto);
        });
    }

    private String storeFileOnServer(MultipartFile file) throws IOException {
        String fileName = "storage_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        Path storePath = Paths.get(storeFilePath);
        Path imagePath = storePath.resolve(fileName);
        Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private String uploadFileToGoogleCloud(MultipartFile file) throws IOException {
        String storageFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String fileName = fileDir + "/" + storageFileName;
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return storageFileName;
    }

    @Override
    public ResponseEntity<?> downloadFile(String fileName) {
        try {
            MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
            if (fileName.startsWith("storage_")) {
                Path storePath = Paths.get(storeFilePath);
                Path filePath = storePath.resolve(fileName);
                final File file = filePath.toFile();

                final InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                        .contentType(mediaType)
                        .contentLength(file.length())
                        .body(resource);
            } else {
                String storageFileName = fileDir + "/" + fileName;
                Blob blob = storage.get(bucketName, storageFileName);
                final ByteArrayResource resource = new ByteArrayResource(blob.getContent());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                        .contentType(mediaType)
                        .contentLength(resource.contentLength())
                        .body(resource);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return BaseResponse.status(HttpStatus.BAD_REQUEST, "Not found file " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
