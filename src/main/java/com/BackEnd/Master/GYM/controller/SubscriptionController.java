package com.BackEnd.Master.GYM.controller;

import com.BackEnd.Master.GYM.Mapper.SubscriptionMapper;
import com.BackEnd.Master.GYM.dto.SubscriptionDto;
import com.BackEnd.Master.GYM.entity.Subscription;
import com.BackEnd.Master.GYM.entity.customer;
import com.BackEnd.Master.GYM.services.SubscriptionService;
import com.BackEnd.Master.GYM.services.customerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
@CrossOrigin("*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final customerService customerService;
    private final SubscriptionMapper subscriptionMapper;

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionMapper.entityToDto(subscriptionService.findById(id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> findAll() {
        return ResponseEntity.ok(subscriptionMapper.entityToDto(subscriptionService.findAll()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SubscriptionDto>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(subscriptionMapper.entityToDto(subscriptionService.findByCustomerId(customerId)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/customer/{customerId}/current")
    public ResponseEntity<SubscriptionDto> findCurrentByCustomerId(@PathVariable Long customerId) {
        return subscriptionService.findCurrentByCustomerId(customerId)
                .map(subscription -> ResponseEntity.ok(subscriptionMapper.entityToDto(subscription)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @PostMapping
    public ResponseEntity<SubscriptionDto> insert(@RequestBody SubscriptionDto dto) {
        customer owner = customerService.findById(dto.getCustomerId());
        Subscription subscription = subscriptionMapper.dtoToEntity(dto);
        subscription.setCustomer(owner);
        Subscription saved = subscriptionService.insert(subscription);
        return ResponseEntity.ok(subscriptionMapper.entityToDto(saved));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @PostMapping("/customer/{customerId}/renew")
    public ResponseEntity<SubscriptionDto> renew(@PathVariable Long customerId, @RequestBody SubscriptionDto dto) {
        customer owner = customerService.findById(customerId);
        Subscription subscription = subscriptionMapper.dtoToEntity(dto);
        subscription.setCustomer(owner);
        Subscription saved = subscriptionService.insert(subscription);
        return ResponseEntity.ok(subscriptionMapper.entityToDto(saved));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @PutMapping
    public ResponseEntity<SubscriptionDto> update(@RequestBody SubscriptionDto dto) {
        Subscription subscription = subscriptionMapper.dtoToEntity(dto);
        Subscription updated = subscriptionService.update(subscription);
        return ResponseEntity.ok(subscriptionMapper.entityToDto(updated));
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        subscriptionService.deleteById(id);
    }
}
