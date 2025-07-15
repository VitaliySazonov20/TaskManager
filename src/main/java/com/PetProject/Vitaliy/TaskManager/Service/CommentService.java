package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.CommentRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }


    public List<Comment> getCommentsById(BigInteger taskId){
        if(taskId == null){
            throw new ServiceException("Task ID cannot be null");
        }
        if(!taskRepository.existsById(taskId)){
            throw new ServiceException("Task with ID " + taskId + " not found");
        }
        try {
            List<Comment> comments = commentRepository.findByTaskId(taskId);
            return comments != null? comments: Collections.emptyList();
        } catch (DataAccessException e){
            throw new ServiceException("Failed to comments", e);
        } catch (Exception e){
            throw new ServiceException("Unexpected error while retrieving comments", e);
        }

    }
}
