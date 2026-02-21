package com.moveinsync.metrobooking.repository;

import com.moveinsync.metrobooking.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    Optional<Stop> findByCode(String code);
    Optional<Stop> findByName(String name);
    boolean existsByCode(String code);
}