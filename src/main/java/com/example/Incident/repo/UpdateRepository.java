package com.example.Incident.repo;

import com.example.Incident.model.Update;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateRepository extends JpaRepository<Update, Long> {
}
