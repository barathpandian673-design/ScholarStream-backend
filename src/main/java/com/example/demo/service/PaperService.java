package com.example.demo.service;

import com.example.demo.entity.Paper;
import com.example.demo.repository.PaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for papers, sitting between the controllers and
 * PaperRepository so that controllers never touch the repository
 * directly.
 */
@Service
public class PaperService {

    @Autowired
    private PaperRepository paperRepository;

    public List<Paper> getAllPapers() {
        return paperRepository.findAll();
    }

    public Optional<Paper> getPaperById(Long id) {
        return paperRepository.findById(id);
    }

    public Paper savePaper(Paper paper) {
        return paperRepository.save(paper);
    }

    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }

    public List<Paper> getApprovedPapers() {
        return paperRepository.findByStatus(Paper.PaperStatus.APPROVED);
    }

    public List<Paper> getPapersByStatusList() {
        return paperRepository.findAll();
    }

    public List<Paper> getPapersByCategory(Long categoryId) {
        return paperRepository.findByCategoryId(categoryId);
    }

    public List<Paper> getPapersByAuthor(Long authorId) {
        return paperRepository.findByAuthorId(authorId);
    }

    public List<Paper> searchPapers(String keyword) {
        return paperRepository.searchPapers(keyword);
    }
}
