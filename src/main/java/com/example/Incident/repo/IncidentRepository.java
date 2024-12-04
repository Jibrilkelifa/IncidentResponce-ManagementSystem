package com.example.Incident.repo;

import com.example.Incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    @Query("SELECT i FROM Incident i LEFT JOIN FETCH i.updates WHERE i.status = 'Resolved'")
    List<Incident> findResolvedIncidents();

    List<Incident> findByEscalatedToEmailsContaining(String email);
    @Query("SELECT i FROM Incident i WHERE :affectedSystem MEMBER OF i.affectedSystems")
    List<Incident> findByAffectedSystem(@Param("affectedSystem") String affectedSystem);

//    List<Incident> findBySource(String source);

    List<Incident> findByEscalatedTo(String escalatedTo);

    List<Incident> findByTitleContainingIgnoreCaseOrAssigneeContainingIgnoreCaseOrStatusContainingIgnoreCaseOrEscalatedToContainingIgnoreCase(
            String title, String assignee, String status, String escalatedTo);
}