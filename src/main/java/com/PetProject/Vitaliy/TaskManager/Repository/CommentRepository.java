package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long > {

    List<Comment> findByTaskId(BigInteger taskId);

    Optional<Comment> findById(BigInteger commentId);
}
