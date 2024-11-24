package com.example.Incident.services;

import com.example.Incident.model.Incident;
import com.example.Incident.model.KnowledgeBaseArticle;
import com.example.Incident.repo.KnowledgeBaseArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseArticleRepository articleRepository;

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

