package com.example.Incident.repo;

import com.example.Incident.model.Incident;
import com.example.Incident.model.KnowledgeBaseArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseArticleRepository extends JpaRepository<KnowledgeBaseArticle, Long> {
    List<KnowledgeBaseArticle> findByCategoryContainingIgnoreCaseOrTagsContainingIgnoreCase(String category, String tags);
    boolean existsByIncident(Incident incident);
    List<KnowledgeBaseArticle> findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCaseOrCategoryContainingIgnoreCase(String searchTerm, String searchTerm1, String searchTerm2);
}

