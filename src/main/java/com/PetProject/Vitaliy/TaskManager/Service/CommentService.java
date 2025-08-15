package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.CommentRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }


    @Transactional(readOnly = true)
    public List<Comment> getCommentsById(BigInteger taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(()->
                new EntityNotFoundException("Task not found with ID: " + taskId));
        return commentRepository.findByTaskId(task.getId());
    }

    public Comment getCommentById(BigInteger commentId){
        return commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found with ID: " + commentId));
    }
}
