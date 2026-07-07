package com.example.demo.controller;

import com.example.demo.entity.Paper;
import com.example.demo.entity.User;
import com.example.demo.exception.PaperNotFoundException;
import com.example.demo.service.PaperService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Administrative endpoints: user listing, paper status overrides and
 * system-wide reporting. Every endpoint requires the ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaperService paperService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/papers/{id}/status")
    public ResponseEntity<String> updatePaperStatus(@PathVariable Long id,
                                                      @RequestParam Paper.PaperStatus status) {
        Paper paper = paperService.getPaperById(id)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + id));

        paper.setStatus(status);
        paperService.savePaper(paper);

        return ResponseEntity.ok("Paper status updated to " + status);
    }

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        List<User> users = userService.getAllUsers();
        List<Paper> allPapers = paperService.getAllPapers();
        long pending = allPapers.stream().filter(p -> p.getStatus() == Paper.PaperStatus.PENDING).count();
        long approved = allPapers.stream().filter(p -> p.getStatus() == Paper.PaperStatus.APPROVED).count();

        Map<String, Object> report = new HashMap<>();
        report.put("totalUsers", users.size());
        report.put("totalPapers", allPapers.size());
        report.put("pendingPapers", pending);
        report.put("approvedPapers", approved);

        return ResponseEntity.ok(report);
    }
}
