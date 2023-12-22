package com.mshop.storageservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sudo248.domain.base.BaseResponse;
import com.mshop.storageservice.config.StorageSource;
import com.mshop.storageservice.controller.dto.ImageDto;
import com.mshop.storageservice.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;


@Service
public class ImageServiceImpl implements ImageService {

    @Value("${store.image.path}")
    private String storeImagePath;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Value("${gcp.bucket.image.dir}")
    private String imageDir;

    private final Cloudinary cloudinary;

    private final Storage storage;

    public ImageServiceImpl(Cloudinary cloudinary, Storage storage) {
        this.cloudinary = cloudinary;
        this.storage = storage;
    }

    @Override
    public ResponseEntity<BaseResponse<?>> storeImage(MultipartFile image, StorageSource source) {
        return handleException(() -> {
            String imageName = image.getOriginalFilename();
            try {
                switch (source) {
                    case GOOGLE_STORAGE:
                        imageName = uploadImageToGoogleCloud(image);
                        break;
                    case CLOUDINARY:
                        imageName = uploadImageCloudinary(image);
                        break;
                    default:
                        imageName = storeImageOnServer(image);
                }

            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + imageName, ioe);
            }
            ImageDto imageDto = new ImageDto(imageName);
            return BaseResponse.ok(imageDto);
        });
    }

    private String storeImageOnServer(MultipartFile image) throws IOException {
        String imageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        InputStream inputStream = image.getInputStream();
        Path storePath = Paths.get(storeImagePath);
        Path imagePath = storePath.resolve(imageName);
        Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
        return imageName;
    }

    private String uploadImageCloudinary(MultipartFile image) throws IOException {
        String imageName = System.currentTimeMillis() + "_" + getImageName(Objects.requireNonNull(image.getOriginalFilename()));
        Map result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap(
                "public_id",imageName,
                "folder","Soc"
        ));
        return (String)result.get("secure_url");
    }

    private String uploadImageToGoogleCloud(MultipartFile image) throws IOException {
        String storageImageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        String imageName = imageDir + "/" + storageImageName;
        BlobId blobId = BlobId.of(bucketName, imageName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(image.getContentType()).build();
        storage.create(blobInfo, image.getBytes());
        String encodeImageName = UriUtils.encode(storageImageName, "UTF-8");
        return "https://storage.googleapis.com/"+ bucketName + "/"+ imageDir + "/" + encodeImageName;
    }

    private String getImageName(String originalFileName) {
        return originalFileName.substring(0, originalFileName.lastIndexOf('.'));
    }
}
