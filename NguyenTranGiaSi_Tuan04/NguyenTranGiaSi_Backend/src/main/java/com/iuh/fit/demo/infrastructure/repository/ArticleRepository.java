package com.iuh.fit.demo.infrastructure.repository;

import com.iuh.fit.demo.domain.content.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}