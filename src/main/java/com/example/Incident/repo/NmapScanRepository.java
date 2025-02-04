package com.example.Incident.repo;

import com.example.Incident.model.NmapScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NmapScanRepository extends JpaRepository<NmapScan, Long> {
    List<NmapScan> findByStatus(String status);

}
