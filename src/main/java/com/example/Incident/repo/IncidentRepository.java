package com.example.Incident.repo;

import com.example.Incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    @Query("SELECT i FROM Incident i LEFT JOIN FETCH i.updates WHERE i.status = 'Resolved'")
    List<Incident> findResolvedIncidents();

    List<Incident> findByEscalatedToEmailsContaining(String email);

    @Query("SELECT i FROM Incident i WHERE :affectedSystem MEMBER OF i.affectedSystems")
    List<Incident> findByAffectedSystem(@Param("affectedSystem") String affectedSystem);

//    List<Incident> findBySource(String source);

    List<Incident> findByEscalatedTo(String escalatedTo);

//    List<Incident> findByTitleContainingIgnoreCaseOrAssigneeContainingIgnoreCaseOrStatusContainingIgnoreCaseOrEscalatedToContainingIgnoreCase(
//            String title, String assignee, String status, String escalatedTo);

    @Query("SELECT DATE(i.createdAt) AS date, COUNT(i) AS count FROM Incident i GROUP BY DATE(i.createdAt) ORDER BY date ASC")
    List<Object[]> groupIncidentsByDate();

    List<Incident> findByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);


    List<Incident> findByTitleContainingIgnoreCaseOrAssigneeContainingIgnoreCaseOrStatusContainingIgnoreCaseOrSeverityContainingIgnoreCaseOrEscalatedToContainingIgnoreCaseOrSourcesContainingIgnoreCaseOrAffectedSystemsContainingIgnoreCase(String searchTerm, String searchTerm1, String searchTerm2, String searchTerm3, String searchTerm4, String searchTerm5,String searchTerm56 );
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status = 'Resolved'")
    long countResolvedIncidents();

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status <> 'Resolved'")
    long countOpenIncidents();

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.severity IN :severities")
    long countBySeverityIn(@Param("severities") List<String> severities);

}