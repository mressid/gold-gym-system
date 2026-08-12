package com.BackEnd.Master.GYM.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.BackEnd.Master.GYM.Exceptions.ResourceNotFoundException;
import com.BackEnd.Master.GYM.dto.customerDto;
import com.BackEnd.Master.GYM.entity.AppUsers;
import com.BackEnd.Master.GYM.entity.Pack;
import com.BackEnd.Master.GYM.entity.Subscription;
import com.BackEnd.Master.GYM.entity.customer;
import com.BackEnd.Master.GYM.Mapper.customerMapper;
import com.BackEnd.Master.GYM.services.AppUserService;
import com.BackEnd.Master.GYM.services.PackService;
import com.BackEnd.Master.GYM.services.StorageService;
import com.BackEnd.Master.GYM.services.SubscriptionService;
import com.BackEnd.Master.GYM.services.customerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customer")
@CrossOrigin("*")
public class customerController {

    private final customerService custService;
    private final customerMapper custMapper;
    private final AppUserService userRepo;
    private final PackService packService;
    private final SubscriptionService subscriptionService;
    private final StorageService storageService;

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<customerDto> findById(@PathVariable Long id) {
        customer entity = custService.findById(id);
        customerDto customerDto = custMapper.map(entity);
        return ResponseEntity.ok(customerDto);
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @GetMapping()
    public ResponseEntity<List<customerDto>> findAll(
            @RequestParam(required = false) Boolean validOnly,
            @RequestParam(required = false, defaultValue = "daysLeft") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {
        List<customer> entities = custService.findAll();
        List<customerDto> customerDtos = custMapper.map(entities);

        Map<Long, Subscription> currentSubs = subscriptionService.findCurrentByCustomerIds(
                customerDtos.stream().map(customerDto::getId).toList());

        LocalDate today = LocalDate.now();
        for (customerDto dto : customerDtos) {
            Subscription sub = currentSubs.get(dto.getId());
            if (sub != null && sub.getDateFin() != null) {
                dto.setSubscriptionEndDate(sub.getDateFin());
                dto.setSubscriptionValid(!sub.getDateFin().isBefore(today));
                dto.setSubscriptionDaysLeft((int) ChronoUnit.DAYS.between(today, sub.getDateFin()));
            } else {
                dto.setSubscriptionValid(false);
            }
        }

        List<customerDto> result = customerDtos;
        if (Boolean.TRUE.equals(validOnly)) {
            result = result.stream().filter(customerDto::getSubscriptionValid).toList();
        }
        if ("daysLeft".equalsIgnoreCase(sortBy)) {
            // nullsLast wraps the (possibly reversed) numeric comparator, so customers with no
            // subscription always sort last regardless of direction - reversing the whole
            // comparator instead would flip "nulls last" into "nulls first" too.
            Comparator<Integer> numeric = "desc".equalsIgnoreCase(direction)
                    ? Comparator.<Integer>reverseOrder()
                    : Comparator.<Integer>naturalOrder();
            Comparator<customerDto> cmp = Comparator.comparing(
                    customerDto::getSubscriptionDaysLeft, Comparator.nullsLast(numeric));
            result = result.stream().sorted(cmp).toList();
        } else if ("name".equalsIgnoreCase(sortBy)) {
            Comparator<customerDto> cmp = Comparator.comparing(customerDto::getUserName, String.CASE_INSENSITIVE_ORDER);
            result = result.stream().sorted("desc".equalsIgnoreCase(direction) ? cmp.reversed() : cmp).toList();
        }

        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/count")
    public ResponseEntity<Long> countAllCustomers() {
        long entities = custService.count();
        return ResponseEntity.ok(entities);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/search")
    public ResponseEntity<List<customerDto>> searchCustomers(@RequestParam String query) {
        List<customer> entities = custService.searchCustomers(query);
        return ResponseEntity.ok(custMapper.map(entities));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/filtre-name")
    public ResponseEntity<customerDto> filtre(@RequestParam String userName) {
        customer entity = custService.findByUserName(userName);
        customerDto customerDto = custMapper.map(entity);
        return ResponseEntity.ok(customerDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/filtre-user/{id}")
    public ResponseEntity<List<customerDto>> findByUserId(@PathVariable Long id) {
        List<customer> entity = custService.findByUserId(id);
        List<customerDto> customerDto = custMapper.map(entity);
        return ResponseEntity.ok(customerDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<customerDto> insert(
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam("pack") String pack,
            @RequestParam("userId") Long userId,
            @RequestParam("montPay") String montPay,
            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        AppUsers user = userRepo.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (dateFin == null) {
            Pack packEntity = packService.findByName(pack)
                    .orElseThrow(() -> new ResourceNotFoundException("Pack not found with name: " + pack));
            dateFin = dateDebut.plusMonths(packEntity.getNMonth());
        }

        customer customer = new customer();
        customer.setUserName(userName);
        customer.setEmail(email);
        customer.setTelephone(telephone);
        customer.setDateDebut(dateDebut);
        customer.setDateFin(dateFin);
        customer.setPack(pack);
        customer.setUser(user);
        customer.setMontPay(montPay);

        if (profileImage.isEmpty()) {
            throw new RuntimeException("Profile image is required");
        }

        customer.setProfileImage(storageService.upload(profileImage, "customer-images"));

        customer entity = custService.insert(customer);

        customerDto responseDto = custMapper.map(entity);

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @PutMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<customerDto> update(
            @RequestParam("id") Long id,
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("dateDebut") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(value = "dateFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam("pack") String pack,
            @RequestParam("userId") Long userId,
            @RequestParam("montPay") String montPay,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {

        customer currentCustomer = custService.findById(id);
        if (currentCustomer == null) {
            throw new ResourceNotFoundException("customer not found with ID: " + id);
        }
        String currentImage = currentCustomer.getProfileImage();

        if (dateFin == null) {
            Pack packEntity = packService.findByName(pack)
                    .orElseThrow(() -> new ResourceNotFoundException("Pack not found with name: " + pack));
            dateFin = dateDebut.plusMonths(packEntity.getNMonth());
        }

        currentCustomer.setUserName(userName);
        currentCustomer.setEmail(email);
        currentCustomer.setTelephone(telephone);
        currentCustomer.setDateDebut(dateDebut);
        currentCustomer.setDateFin(dateFin);
        currentCustomer.setPack(pack);
        currentCustomer.setMontPay(montPay);

        AppUsers user = userRepo.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        currentCustomer.setUser(user);

        if (profileImage != null && !profileImage.isEmpty()) {
            currentCustomer.setProfileImage(storageService.upload(profileImage, "customer-images"));
            if (currentImage != null) {
                storageService.delete(currentImage);
            }
        }

        customer updatedCustomer = custService.update(currentCustomer);

        customerDto responseDto = custMapper.map(updatedCustomer);

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {

        customer customer = custService.findById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("customer not found with ID: " + id);
        }
        storageService.delete(customer.getProfileImage());

        custService.deleteById(id);

        String jsonResponse = "{\"message\": \"Customer and associated image deleted successfully.\"}";

        return ResponseEntity.ok(jsonResponse);
    }

}
