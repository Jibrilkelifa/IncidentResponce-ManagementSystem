package com.example.Incident.services;

import com.example.Incident.model.Incident;
import com.example.Incident.model.KnowledgeBaseArticle;
import com.example.Incident.model.Update;
import com.example.Incident.repo.IncidentRepository;
import com.example.Incident.repo.KnowledgeBaseArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseArticleRepository articleRepository;
    private final IncidentRepository incidentRepository;

    public KnowledgeBaseService(KnowledgeBaseArticleRepository articleRepository, IncidentRepository incidentRepository) {
        this.articleRepository = articleRepository;
        this.incidentRepository = incidentRepository;
    }
    public List<KnowledgeBaseArticle> createArticlesForResolvedIncidents() {
        // Step 1: Fetch resolved incidents or incidents with updates that have resolved status
        List<Incident> resolvedIncidents = incidentRepository.findResolvedIncidents();

        // Step 2: Generate a KnowledgeBaseArticle for each resolved incident if it doesn't already exist
        List<KnowledgeBaseArticle> articles = new ArrayList<>();
        for (Incident incident : resolvedIncidents) {
            // Check if an article already exists for this incident
            boolean articleExists = articleRepository.existsByIncident(incident);
            if (!articleExists) {
                // Create a new KnowledgeBaseArticle
                KnowledgeBaseArticle article = new KnowledgeBaseArticle();
                article.setTitle("Incident Resolved: " + incident.getTitle());
                article.setCategory("Incident Response");
                article.setTags("resolved");
                article.setContent(generateArticleContent(incident)); // Generate content from incident details
                article.setIncident(incident); // Link the article to the incident

                // Save the article to the database
                articleRepository.save(article);

                // Add the article to the list
                articles.add(article);
            }
        }

        return articles;
    }

    private String generateArticleContent(Incident incident) {
        // Generate the article content using the incident details in a formatted way
        StringBuilder content = new StringBuilder();
        content.append("Title: ").append(incident.getTitle()).append("\n\n");
        content.append("Description: ").append(incident.getDescription()).append("\n\n");
        content.append("Severity: ").append(incident.getSeverity()).append("\n\n");
        content.append("Created At: ").append(incident.getCreatedAt()).append("\n\n");

        // If the incident has any updates, include them in the content
        if (incident.getUpdates() != null && !incident.getUpdates().isEmpty()) {
            content.append("Updates:\n");
            for (Update update : incident.getUpdates()) {
                content.append("- ").append(update.getMessage()).append("\n");
                content.append("  New Status: ").append(update.getNewStatus()).append("\n");
                content.append("  Timestamp: ").append(update.getTimestamp()).append("\n\n");
            }
        }
        return content.toString();
    }
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms = 1 hour)
    public void scheduleResolvedIncidentsUpdate() {
        createArticlesForResolvedIncidents();
    }



    // Create or update article
    public KnowledgeBaseArticle saveArticle(KnowledgeBaseArticle article) {
        return articleRepository.save(article);
    }

    // Get all articles
    public List<KnowledgeBaseArticle> getAllArticles() {
        return articleRepository.findAll();
    }

    // Search articles by category or tags
    public List<KnowledgeBaseArticle> searchArticles(String searchTerm) {
        return articleRepository.findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm);
    }

    // Get article by ID
    public KnowledgeBaseArticle getArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article not found"));
    }

    // Delete article by ID
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

}

