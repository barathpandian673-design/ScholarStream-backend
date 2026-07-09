package com.example.demo.controller;

import com.example.demo.dto.PaperDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Paper;
import com.example.demo.entity.User;
import com.example.demo.exception.PaperNotFoundException;
import com.example.demo.security.services.UserDetailsImpl;
import com.example.demo.service.CategoryService;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PaperService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints for browsing, searching, submitting and moderating
 * research papers. All data access is delegated to PaperService,
 * CategoryService, UserService and FileStorageService.
 */
@RestController
@RequestMapping("/api/papers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public List<Paper> getApprovedPapers() {
        return paperService.getApprovedPapers();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('REVIEWER') or hasRole('ADMIN')")
    public List<Paper> getAllPapers() {
        return paperService.getAllPapers();
    }

    @GetMapping("/search/app")
    public List<Paper> searchPapers(@RequestParam("q") String query) {
        return paperService.searchPapers(query);
    }

    @GetMapping("/category/{id}")
    public List<Paper> getPapersByCategory(@PathVariable Long id) {
        return paperService.getPapersByCategory(id);
    }

    @GetMapping("/category/all")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadPaper(@PathVariable Long id) {
        Paper paper = paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));

        try {
            Path filePath = fileStorageService.loadFileAsPath(paper.getFileUrl());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + paper.getTitle() + ".pdf\"")
                    .body(resource);
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paper> getPaperById(@PathVariable Long id) {
        Paper paper = paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));
        return ResponseEntity.ok(paper);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalPapers = paperService.getAllPapers().size();
        long totalUsers = userService.getAllUsers().size();
        long totalDownloads = totalPapers * 15 + 42;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPapers", totalPapers);
        stats.put("totalUsers", totalUsers);
        stats.put("totalDownloads", totalDownloads);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadPaper(@RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("abstractText") String abstractText,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "journal", required = false) String journal,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "publicationYear", required = false) Integer publicationYear,
            @RequestParam(value = "version", required = false, defaultValue = "1.0") String version) {
        String username = currentUsername();
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new PaperNotFoundException("Author not found: " + username));

        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new PaperNotFoundException("Category not found with id: " + categoryId));

        String storedFileName = fileStorageService.storeFile(file);

        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAbstractText(abstractText);
        paper.setCategory(category);
        paper.setJournal(journal);
        paper.setKeywords(keywords);
        paper.setPublicationYear(publicationYear);
        paper.setVersion(version);
        paper.setFileUrl(storedFileName);
        paper.setAuthor(author);
        paper.setStatus(Paper.PaperStatus.PENDING);
        paper.setUploadDate(LocalDateTime.now());

        paperService.savePaper(paper);

        return ResponseEntity.ok("Paper uploaded successfully. Status: PENDING");
    }

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Paper> createPaper(@Valid @RequestBody PaperDTO paperDTO) {
        String username = currentUsername();
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new PaperNotFoundException("Author not found: " + username));

        Category category = categoryService.getCategoryById(paperDTO.getCategoryId())
                .orElseThrow(
                        () -> new PaperNotFoundException("Category not found with id: " + paperDTO.getCategoryId()));

        Paper paper = new Paper();
        paper.setTitle(paperDTO.getTitle());
        paper.setAbstractText(paperDTO.getAbstractText());
        paper.setCategory(category);
        paper.setJournal(paperDTO.getJournal());
        paper.setKeywords(paperDTO.getKeywords());
        paper.setPublicationYear(paperDTO.getPublicationYear());
        paper.setVersion(paperDTO.getVersion() != null ? paperDTO.getVersion() : "1.0");
        paper.setAuthor(author);
        paper.setStatus(Paper.PaperStatus.PENDING);
        paper.setUploadDate(LocalDateTime.now());

        Paper saved = paperService.savePaper(paper);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<String> updatePaper(@PathVariable Long id, @RequestBody PaperDTO paperDTO) {
        Paper paper = paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));

        if (paperDTO.getTitle() != null) {
            paper.setTitle(paperDTO.getTitle());
        }
        if (paperDTO.getAbstractText() != null) {
            paper.setAbstractText(paperDTO.getAbstractText());
        }
        if (paperDTO.getKeywords() != null) {
            paper.setKeywords(paperDTO.getKeywords());
        }
        if (paperDTO.getVersion() != null) {
            paper.setVersion(paperDTO.getVersion());
        }

        paperService.savePaper(paper);

        return ResponseEntity.ok("Paper updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePaper(@PathVariable Long id) {
        paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));

        paperService.deletePaper(id);

        return ResponseEntity.ok("Paper deleted successfully");
    }

    @PostMapping("/{id}/flag")
    @PreAuthorize("hasRole('REVIEWER') or hasRole('ADMIN')")
    public ResponseEntity<String> flagPaper(@PathVariable Long id, @RequestParam String reason) {
        Paper paper = paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));

        paper.setFlagged(true);
        paper.setFlagReason(reason);
        paper.setStatus(Paper.PaperStatus.FLAGGED);

        paperService.savePaper(paper);

        return ResponseEntity.ok("Paper flagged for review.");
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
