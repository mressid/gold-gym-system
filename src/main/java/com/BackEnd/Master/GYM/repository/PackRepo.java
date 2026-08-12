package com.BackEnd.Master.GYM.repository;

import com.BackEnd.Master.GYM.entity.Pack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackRepo extends JpaRepository<Pack, Long> {
    List<Pack> findByDeletedAtIsNull();
    Optional<Pack> findByName(String name);
}
