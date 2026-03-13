package com.iuh.fit.demo.presentation;

import com.iuh.fit.demo.domain.content.Article;
import com.iuh.fit.demo.infrastructure.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*") // Allows the Vite dev server
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setTitle(articleDetails.getTitle());
                    article.setContent(articleDetails.getContent());
                    article.setStatus(articleDetails.getStatus());
                    return ResponseEntity.ok(articleRepository.save(article));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(article -> {
                    articleRepository.delete(article);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}