package com.BackEnd.Master.GYM.repository;

import com.BackEnd.Master.GYM.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepo extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCustomerId(Long customerId);

    List<Subscription> findByCustomerIdIn(List<Long> customerIds);

    Optional<Subscription> findTopByCustomerIdOrderByDateFinDesc(Long customerId);
}
