package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic for comments.
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByPaper(Long paperId) {
        return commentRepository.findByPaperId(paperId);
    }

    public Comment saveComment(@NonNull Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(@NonNull Long id) {
        commentRepository.deleteById(id);
    }
}
