package com.example.demo.repository;

import com.example.demo.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    List<Paper> findByStatus(Paper.PaperStatus status);

    List<Paper> findByCategoryId(Long categoryId);

    List<Paper> findByAuthorId(Long authorId);

    List<Paper> findByTitleContainingOrKeywordsContainingOrAbstractTextContaining(
            String title, String keywords, String abstractText);

    @Query("SELECT p FROM Paper p WHERE p.title LIKE %?1% OR p.keywords LIKE %?1% OR p.abstractText LIKE %?1%")
    List<Paper> searchPapers(String keyword);
}
