package com.example.Incident.repo;

import com.example.Incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByEscalatedToEmail(String loggedInUserEmail);
}