package com.example.Incident.repo;

import com.example.Incident.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    @Query("SELECT a FROM Alert a WHERE a.alertName = :alertName AND a.host = :host AND a.activeAt = :activeAt")
    Optional<Alert> findExistingAlert(@Param("alertName") String alertName,
                                      @Param("host") String host,
                                      @Param("activeAt") String activeAt);
}
