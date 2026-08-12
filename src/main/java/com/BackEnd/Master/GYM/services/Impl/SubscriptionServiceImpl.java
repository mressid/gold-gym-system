package com.BackEnd.Master.GYM.services.Impl;

import com.BackEnd.Master.GYM.Exceptions.EntityNotFoundException;
import com.BackEnd.Master.GYM.Exceptions.InvalidEntityException;
import com.BackEnd.Master.GYM.entity.Pack;
import com.BackEnd.Master.GYM.entity.Subscription;
import com.BackEnd.Master.GYM.repository.SubscriptionRepo;
import com.BackEnd.Master.GYM.services.PackService;
import com.BackEnd.Master.GYM.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepo subscriptionRepo;
    private final PackService packService;

    @Override
    public Subscription findById(Long id) {
        return subscriptionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with ID: " + id));
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionRepo.findAll();
    }

    @Override
    public List<Subscription> findByCustomerId(Long customerId) {
        return subscriptionRepo.findByCustomerId(customerId);
    }

    @Override
    public Optional<Subscription> findCurrentByCustomerId(Long customerId) {
        return subscriptionRepo.findTopByCustomerIdOrderByDateFinDesc(customerId);
    }

    @Override
    public Map<Long, Subscription> findCurrentByCustomerIds(List<Long> customerIds) {
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        return subscriptionRepo.findByCustomerIdIn(customerIds).stream()
                .collect(Collectors.toMap(
                        s -> s.getCustomer().getId(),
                        s -> s,
                        (a, b) -> a.getDateFin().isAfter(b.getDateFin()) ? a : b));
    }

    @Override
    public Subscription insert(Subscription subscription) {
        if (subscription.getNMonth() == null) {
            if (subscription.getPackName() == null) {
                throw new InvalidEntityException("Number of months is required");
            }
            Pack pack = packService.findByName(subscription.getPackName())
                    .orElseThrow(() -> new InvalidEntityException("Pack not found with name: " + subscription.getPackName()));
            subscription.setNMonth(pack.getNMonth());
        }

        LocalDate startDate = LocalDate.now();
        if (subscription.getCustomer() != null) {
            Optional<Subscription> lastSubscription = subscriptionRepo
                    .findTopByCustomerIdOrderByDateFinDesc(subscription.getCustomer().getId());
            // Renewing before expiry extends the existing coverage instead of starting a new gap-free period from today
            if (lastSubscription.isPresent() && lastSubscription.get().isValid()) {
                startDate = lastSubscription.get().getDateFin();
            }
        }

        subscription.setDateDebut(startDate);
        subscription.setDateFin(startDate.plusMonths(subscription.getNMonth()));
        return subscriptionRepo.save(subscription);
    }

    @Override
    public Subscription update(Subscription subscription) {
        var currentSubscription = subscriptionRepo.findById(subscription.getId())
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with ID: " + subscription.getId()));

        LocalDate newDateDebut = subscription.getDateDebut();
        LocalDate newDateFin = subscription.getDateFin();
        if (newDateDebut != null && newDateFin != null && currentSubscription.getCustomer() != null) {
            boolean overlapsAnother = subscriptionRepo.findByCustomerId(currentSubscription.getCustomer().getId()).stream()
                    .filter(other -> !other.getId().equals(currentSubscription.getId()))
                    .anyMatch(other -> overlaps(newDateDebut, newDateFin, other.getDateDebut(), other.getDateFin()));
            if (overlapsAnother) {
                throw new InvalidEntityException("Subscription dates overlap with another subscription for this customer");
            }
        }

        currentSubscription.setPackName(subscription.getPackName());
        currentSubscription.setNMonth(subscription.getNMonth());
        currentSubscription.setPrice(subscription.getPrice());
        currentSubscription.setDateDebut(newDateDebut);
        currentSubscription.setDateFin(newDateFin);
        currentSubscription.setAmountPaid(subscription.getAmountPaid());
        currentSubscription.setNote(subscription.getNote());
        return subscriptionRepo.save(currentSubscription);
    }

    // Ranges are allowed to touch at a shared boundary day (that's how renewals intentionally chain)
    private boolean overlaps(LocalDate startA, LocalDate endA, LocalDate startB, LocalDate endB) {
        if (startB == null || endB == null) {
            return false;
        }
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    @Override
    public void deleteById(Long id) {
        var currentSubscription = subscriptionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found with ID: " + id));
        subscriptionRepo.delete(currentSubscription);
    }
}
