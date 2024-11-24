package com.example.Incident.controller;

import com.example.Incident.model.Incident;
import com.example.Incident.model.KnowledgeBaseArticle;
import com.example.Incident.services.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    // Create or update article
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<KnowledgeBaseArticle> createArticle(@RequestBody KnowledgeBaseArticle article) {
        KnowledgeBaseArticle createdArticle = knowledgeBaseService.saveArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    // Get all articles
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<KnowledgeBaseArticle>> getAllArticles() {
        List<KnowledgeBaseArticle> articles = knowledgeBaseService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    // Search articles
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<KnowledgeBaseArticle>> searchArticles(@RequestParam(required = false) String searchTerm) {
        List<KnowledgeBaseArticle> articles;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            articles = knowledgeBaseService.searchArticles(searchTerm);
        } else {
            articles = knowledgeBaseService.getAllArticles(); // Get all incidents if no search term
        }
        return ResponseEntity.ok(articles);
    }

    // Get article by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<KnowledgeBaseArticle> getArticleById(@PathVariable Long id) {
        KnowledgeBaseArticle article = knowledgeBaseService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    // Delete article
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        knowledgeBaseService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}

