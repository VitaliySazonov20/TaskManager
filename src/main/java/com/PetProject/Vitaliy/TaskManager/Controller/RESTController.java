package com.PetProject.Vitaliy.TaskManager.Controller;


import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.*;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RESTController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/users")
    public List<UserModel> getAllUserModels(){
        return userService.getAllUsers().stream()
                .map(user -> new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        try {
            userService.deleteUserById(BigInteger.valueOf(Long.parseLong(id)));
            return ResponseEntity.ok().body("User deleted successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting user");
        }

    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> editUser(@PathVariable String id){
        try{
            User user = userService.getUserById(BigInteger.valueOf(Long.parseLong(id)));
            UserModel userModel = new UserModel(user.getFirstName(), user.getLastName(), user.getEmail());
            return ResponseEntity.ok(userModel);
        } catch (UserNotFoundException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error fetching user");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}")
    public ResponseEntity<String> editUser(@PathVariable String id,
                                           @RequestBody UserModel userModel){
        try {
            User user = userService.getUserById(BigInteger.valueOf(Long.parseLong(id)));
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setEmail(user.getEmail());
            userService.saveUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error editing user");
        }

    }

    @PostMapping("/tasks/{taskId}/comments")
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
                        comment.getText(),
                        comment.getCreationTimestamp()
                ));
    }

    @PatchMapping("/tasks/{taskId}/priority")
    public ResponseEntity<String> changePriority(@PathVariable BigInteger taskId,
                                                 @RequestBody String priorityString){
        Task task = taskService.getTaskById(taskId);
        Priority priority = Priority.valueOf(priorityString);
        task.setPriority(priority);
        taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.OK).body("Status successfully changed");
    }
}
