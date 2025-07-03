package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.CommentRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;


    @Transactional
    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> getCommentsById(BigInteger taskId){
        return commentRepository.findByTaskId(taskId);
    }
}
