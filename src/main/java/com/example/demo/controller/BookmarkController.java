package com.example.demo.controller;

import com.example.demo.entity.Bookmark;
import com.example.demo.entity.Paper;
import com.example.demo.entity.User;
import com.example.demo.exception.PaperNotFoundException;
import com.example.demo.service.BookmarkService;
import com.example.demo.service.PaperService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Endpoints for saving and removing paper bookmarks for the
 * authenticated user.
 */
@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private PaperService paperService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Bookmark> getBookmarks(@RequestParam Long userId) {
        return bookmarkService.getBookmarksByUser(userId);
    }

    @PostMapping("/{paperId}")
    public ResponseEntity<String> addBookmark(@PathVariable Long paperId, @RequestParam Long userId) {
        Optional<Bookmark> existing = bookmarkService.findByUserIdAndPaperId(userId, paperId);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Already bookmarked");
        }

        User user = userService.findById(userId)
                .orElseThrow(() -> new PaperNotFoundException("User not found with id: " + userId));
        Paper paper = paperService.getPaperById(paperId)
                .orElseThrow(() -> new PaperNotFoundException("Paper not found with id: " + paperId));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setPaper(paper);
        bookmarkService.saveBookmark(bookmark);

        return ResponseEntity.ok("Bookmarked successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.ok("Bookmark removed");
    }
}
