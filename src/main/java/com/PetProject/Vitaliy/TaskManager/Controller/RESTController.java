package com.PetProject.Vitaliy.TaskManager.Controller;


import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.*;
import com.PetProject.Vitaliy.TaskManager.entity.*;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    @Autowired
    private LogExportService logExportService;

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
            UserModel userModel = new UserModel(user.getId(),user.getFirstName(), user.getLastName(), user.getEmail());
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
                        comment.getText()
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationForm registrationForm,
                                          BindingResult result){
        if(result.hasErrors()){
            Map<String,String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error->{
                String field = error.getField().equals("passwordsMatch")
                        ? "confirmationPassword":error.getField();
                errors.put(field,error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        if(userService.checkIfEmailExists(registrationForm.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("email","Email already exists"));
        }
        userService.registerUser(registrationForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationForm);
    }

    @GetMapping(value = "/logs/export",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> exportLogs(@RequestParam(required = false) String action,
                                               @RequestParam(required = false) String username,
                                               @RequestParam(required = false) LocalDateTime startDate,
                                               @RequestParam(required = false) LocalDateTime endDate){

        try {
            Resource resource = logExportService.exportToJson(action,username,startDate,endDate);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=audit-logs-"+ LocalDateTime.now()+ ".json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Export failed".getBytes()));
        }
    }
}
