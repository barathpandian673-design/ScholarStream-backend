package com.example.demo.service;

import com.example.demo.entity.Bookmark;
import com.example.demo.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for bookmarks.
 */
@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    public List<Bookmark> getBookmarksByUser(Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }

    public Optional<Bookmark> findByUserIdAndPaperId(Long userId, Long paperId) {
        return bookmarkRepository.findByUserIdAndPaperId(userId, paperId);
    }

    public Bookmark saveBookmark(@NonNull Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(@NonNull Long id) {
        bookmarkRepository.deleteById(id);
    }
}
