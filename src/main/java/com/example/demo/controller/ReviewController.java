package com.example.demo.controller;

import com.example.demo.entity.Paper;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.exception.PaperNotFoundException;
import com.example.demo.service.PaperService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for reviewer feedback. Submitting a review with status
 * APPROVED or REJECTED also moves the related paper into the
 * matching {@link Paper.PaperStatus}.
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PaperService paperService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('REVIEWER') or hasRole('ADMIN')")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @PostMapping
    @PreAuthorize("hasRole('REVIEWER') or hasRole('ADMIN')")
    public ResponseEntity<String> submitReview(@RequestBody Review reviewRequest,
                                                @RequestParam Long paperId,
                                                @RequestParam Long reviewerId) {
        Paper paper = paperService.getPaperById(paperId)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + paperId));

        User reviewer = userService.findById(reviewerId)
                .orElseThrow(() -> new PaperNotFoundException("Reviewer not found with id: " + reviewerId));

        Review review = new Review();
        review.setComments(reviewRequest.getComments());
        review.setRating(reviewRequest.getRating());
        review.setStatus(reviewRequest.getStatus());
        review.setPaper(paper);
        review.setReviewer(reviewer);

        reviewService.saveReview(review);

        if (review.getStatus() == Review.ReviewStatus.APPROVED) {
            paper.setStatus(Paper.PaperStatus.APPROVED);
        } else {
            paper.setStatus(Paper.PaperStatus.REJECTED);
        }
        paperService.savePaper(paper);

        return ResponseEntity.ok("Review submitted and paper status updated.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REVIEWER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}
