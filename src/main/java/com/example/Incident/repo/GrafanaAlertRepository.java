package com.example.Incident.repo;

import com.example.Incident.model.GrafanaAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GrafanaAlertRepository extends JpaRepository<GrafanaAlert, Long> {
}