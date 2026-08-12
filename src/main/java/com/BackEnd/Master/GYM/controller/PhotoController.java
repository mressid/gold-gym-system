package com.BackEnd.Master.GYM.controller;

import com.BackEnd.Master.GYM.dto.PhotoDto;
import com.BackEnd.Master.GYM.entity.Album;
import com.BackEnd.Master.GYM.entity.Photo;
import com.BackEnd.Master.GYM.repository.AlbumRepo;
import com.BackEnd.Master.GYM.Mapper.PhotoMapper;
import com.BackEnd.Master.GYM.services.PhotoService;
import com.BackEnd.Master.GYM.services.StorageService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.BackEnd.Master.GYM.Exceptions.ResourceNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/photos")
@CrossOrigin("*")
public class PhotoController {

    private final PhotoService photoService;
    private final PhotoMapper photoMapper;
    private final AlbumRepo albumRepo;
    private final StorageService storageService;

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> findById(@PathVariable Long id) {
        Photo entity = photoService.findById(id);
        PhotoDto dto = photoMapper.map(entity);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PhotoDto>> findAll() {
        List<Photo> entities = photoService.findAll();
        List<PhotoDto> dtos = photoMapper.map(entities);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<PhotoDto>> findByAlbumId(@PathVariable Long albumId) {
        List<Photo> entities = photoService.findByAlbumId(albumId);
        List<PhotoDto> dtos = photoMapper.map(entities);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<PhotoDto> insert(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("albumId") Long albumId,
            @RequestParam("photoImage") MultipartFile photoImage) throws IOException {

        if (photoImage.isEmpty()) {
            throw new RuntimeException("Photo image is required");
        }

        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with ID: " + albumId));

        Photo photo = new Photo();
        photo.setName(name);
        photo.setDescription(description);
        photo.setUploadDate(LocalDate.now());
        photo.setAlbum(album);

        if (photoImage.isEmpty()) {
            throw new RuntimeException("Profile image is required");
        }

        photo.setImageName(storageService.upload(photoImage, "gallery"));

        Photo savedEntity = photoService.insert(photo);
        PhotoDto responseDto = photoMapper.map(savedEntity);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<PhotoDto> update(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("albumId") Long albumId,
            @RequestParam(value = "photoImage", required = false) MultipartFile photoImage) throws IOException {

        Photo currentPhoto = photoService.findById(id);
        String currentImage = currentPhoto.getImageName();

        currentPhoto.setName(name);
        currentPhoto.setDescription(description);

        if (photoImage != null && !photoImage.isEmpty()) {
            currentPhoto.setImageName(storageService.upload(photoImage, "gallery"));
            if (currentImage != null) {
                storageService.delete(currentImage);
            }
        }

        Photo updatedPhoto = photoService.update(currentPhoto);
        PhotoDto responseDto = photoMapper.map(updatedPhoto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        Photo photo = photoService.findById(id);
        if (photo == null) {
            throw new ResourceNotFoundException("Photo not found with ID: " + id);
        }

        storageService.delete(photo.getImageName());
        photoService.deleteById(id);

        String jsonResponse = "{\"message\": \"User and associated image deleted successfully.\"}";
        return ResponseEntity.ok(jsonResponse);

    }

}