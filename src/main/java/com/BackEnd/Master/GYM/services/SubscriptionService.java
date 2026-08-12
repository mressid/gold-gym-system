package com.BackEnd.Master.GYM.services;

import com.BackEnd.Master.GYM.entity.Subscription;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubscriptionService {

    Subscription findById(Long id);

    List<Subscription> findAll();

    List<Subscription> findByCustomerId(Long customerId);

    // Most recent subscription (by end date) for a customer, i.e. their current plan
    Optional<Subscription> findCurrentByCustomerId(Long customerId);

    // Bulk version of findCurrentByCustomerId, keyed by customerId, for enriching lists without N+1 queries
    Map<Long, Subscription> findCurrentByCustomerIds(List<Long> customerIds);

    Subscription insert(Subscription subscription);

    Subscription update(Subscription subscription);

    void deleteById(Long id);
}
