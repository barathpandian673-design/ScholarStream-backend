package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Paper;
import com.example.demo.entity.User;
import com.example.demo.exception.PaperNotFoundException;
import com.example.demo.service.CommentService;
import com.example.demo.service.PaperService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Endpoints for the per-paper discussion thread.
 */
@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PaperService paperService;

    @Autowired
    private UserService userService;

    @GetMapping("/{paperId}")
    public List<Comment> getComments(@PathVariable Long paperId) {
        return commentService.getCommentsByPaper(paperId);
    }

    @PostMapping
    public ResponseEntity<String> addComment(@RequestBody Comment commentRequest,
                                              @RequestParam Long userId,
                                              @RequestParam Long paperId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new PaperNotFoundException("User not found with id: " + userId));
        Paper paper = paperService.getPaperById(paperId)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + paperId));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setPaper(paper);
        comment.setCreatedAt(LocalDateTime.now());

        commentService.saveComment(comment);

        return ResponseEntity.ok("Comment added");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted");
    }
}
