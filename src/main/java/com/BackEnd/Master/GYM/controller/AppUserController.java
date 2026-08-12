package com.BackEnd.Master.GYM.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.BackEnd.Master.GYM.Exceptions.ResourceNotFoundException;
import com.BackEnd.Master.GYM.dto.AppUserDto;
import com.BackEnd.Master.GYM.entity.AppUsers;
import com.BackEnd.Master.GYM.entity.Roles;
import com.BackEnd.Master.GYM.Mapper.AppUserMapper;
import com.BackEnd.Master.GYM.services.AppUserService;
import com.BackEnd.Master.GYM.services.StorageService;
import com.BackEnd.Master.GYM.repository.RolesRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class AppUserController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final RolesRepo rolesRepo;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    // @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> findById(@PathVariable Long id) {
        AppUsers entity = appUserService.findById(id);
        AppUserDto userDto = appUserMapper.map(entity);
        return ResponseEntity.ok(userDto);
    }

    // @PreAuthorize("hasAuthority('ROLE_Admin')")
    @GetMapping()
    public ResponseEntity<List<AppUserDto>> findAll() {
        List<AppUsers> entities = appUserService.findAll();
        List<AppUserDto> userDtos = appUserMapper.map(entities);
        return ResponseEntity.ok(userDtos);
    }
    

    @GetMapping("/count")
    public ResponseEntity<Long> countAllUsers() {
        long entities = appUserService.count();
        return ResponseEntity.ok(entities);
    }


    @GetMapping("/count-coach")
    public ResponseEntity<Long> countByRole(@RequestParam String roleName) {
        long entities = appUserService.countByRoleRoleName(roleName);
        return ResponseEntity.ok(entities);
    }


    @GetMapping("/search")
    public ResponseEntity<List<AppUserDto>> searchUsers(@RequestParam String query) {
        List<AppUsers> entities = appUserService.searchUsers(query);
        return ResponseEntity.ok(appUserMapper.map(entities));
    }

    @GetMapping("/by-role")
    public ResponseEntity<List<AppUserDto>> findByRoleName(@RequestParam String roleName) {
        List<AppUsers> entities = appUserService.findByRoleRoleName(roleName);
        List<AppUserDto> userDtos = appUserMapper.map(entities);
        return ResponseEntity.ok(userDtos);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/filtre")
    public ResponseEntity<AppUserDto> filtre(@RequestParam String userName) {
        AppUsers entity = appUserService.findByUserName(userName);
        AppUserDto userDto = appUserMapper.map(entity);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<AppUserDto> insert(
            @RequestParam("displayName") String displayName,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("motDePasse") String motDePasse,
            @RequestParam("roleName") String roleName,
            @RequestParam("description") String description,
            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        Roles role = rolesRepo.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Hachage du mot de passe avec BCrypt
        String hashedPassword = passwordEncoder.encode(motDePasse);

        AppUsers user = new AppUsers();
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setTelephone(telephone);
        user.setMotDePasse(hashedPassword);
        user.setRole(role);
        user.setDescription(description);

        if (profileImage.isEmpty()) {
            throw new RuntimeException("Profile image is required");
        }

        user.setProfileImage(storageService.upload(profileImage, "profile-images"));

        AppUsers entity = appUserService.insert(user);

        AppUserDto responseDto = appUserMapper.map(entity);

        return ResponseEntity.ok(responseDto);
    }


    @PutMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<AppUserDto> update(
            @RequestParam("id") Long id,
            @RequestParam("displayName") String displayName,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam(value = "motDePasse", required = false) String motDePasse,
            @RequestParam("roleName") String roleName,
            @RequestParam("description") String description,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        AppUsers currentUser = appUserService.findById(id);
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        String currentImage = currentUser.getProfileImage();

        currentUser.setDisplayName(displayName);
        currentUser.setEmail(email);
        currentUser.setTelephone(telephone);
        currentUser.setDescription(description);
        // Only touch the password when a new one is actually supplied - otherwise leave the stored hash untouched
        if (motDePasse != null && !motDePasse.isBlank()) {
            currentUser.setMotDePasse(passwordEncoder.encode(motDePasse));
        }
        Roles role = rolesRepo.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));
        currentUser.setRole(role);

        if (profileImage != null && !profileImage.isEmpty()) {
            currentUser.setProfileImage(storageService.upload(profileImage, "profile-images"));
            if (currentImage != null) {
                storageService.delete(currentImage);
            }
        }

        AppUsers updatedUser = appUserService.update(currentUser);

        AppUserDto responseDto = appUserMapper.map(updatedUser);

        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {

        AppUsers user = appUserService.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        storageService.delete(user.getProfileImage());

        appUserService.deleteById(id);

        // Crée une chaîne JSON
        String jsonResponse = "{\"message\": \"User and associated image deleted successfully.\"}";

        return ResponseEntity.ok(jsonResponse);
    }

    @PatchMapping("/password")
    public ResponseEntity<AppUserDto> updatePassword(@RequestBody AppUserDto pass) {

        AppUsers currentUser = appUserService.findById(pass.getId());
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found with ID: " + pass.getId());
        }

        // Hachage du mot de passe avec BCrypt
        String hashedPassword = passwordEncoder.encode(pass.getMotDePasse());
        currentUser.setMotDePasse(hashedPassword);

        AppUsers updatedUser = appUserService.update(currentUser);

        AppUserDto responseDto = appUserMapper.map(updatedUser);

        return ResponseEntity.ok(responseDto);
    }

}
