package com.example.Incident.repo;

import com.example.Incident.model.Group;
import com.example.Incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
