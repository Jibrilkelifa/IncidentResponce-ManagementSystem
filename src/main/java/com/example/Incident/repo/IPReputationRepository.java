package com.example.Incident.repo;

import com.example.Incident.model.IPReputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPReputationRepository extends JpaRepository<IPReputation, Long> {
    Optional<IPReputation> findByIpAddress(String ipAddress);
}
