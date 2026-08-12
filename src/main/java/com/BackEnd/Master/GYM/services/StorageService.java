package com.BackEnd.Master.GYM.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    // Uploads the file under the given folder prefix and returns its object key (not a full URL)
    String upload(MultipartFile file, String folder);

    // Removes the object referenced by an object key previously returned by upload(); no-op if blank
    void delete(String objectKey);

    // Builds the full public URL for an object key using the currently configured host, so stored
    // data never has to bake in a specific MinIO host/port
    String resolveUrl(String objectKey);
}
