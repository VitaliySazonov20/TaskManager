package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Service.CommentService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {


    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentModel> addComment(
            @PathVariable BigInteger taskId,
            @RequestBody String message,
            @AuthenticationPrincipal UserCredentials userCredentials) {
        Comment comment = new Comment();
        comment.setTask(taskService.getTaskById(taskId));
        comment.setUser(userCredentials.getUser());
        comment.setText(message);
        commentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CommentModel(
                        comment.getTask().getId(),
                        comment.getUser().getId(),
                        comment.getId(),
                        comment.getText()
                ));
    }

    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<String> changePriority(@PathVariable BigInteger taskId,
                                                 @RequestBody String priorityString){
        Task task = taskService.getTaskById(taskId);
        Priority priority = Priority.valueOf(priorityString);
        task.setPriority(priority);
        taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.OK).body("Status successfully changed");
    }
}
