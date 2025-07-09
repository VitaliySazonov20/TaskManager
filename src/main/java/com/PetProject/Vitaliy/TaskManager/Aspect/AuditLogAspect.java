package com.PetProject.Vitaliy.TaskManager.Aspect;

import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Model.TaskModel;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.AuditLogService;
import com.PetProject.Vitaliy.TaskManager.Service.SecurityContextService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import jakarta.validation.Valid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Pointcut("execution(* com.PetProject.Vitaliy.TaskManager.Controller.AuthenticationController.registerUser(com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm,..)) && " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) && " +
            "args(@org.springframework.web.bind.annotation.ModelAttribute('registrationForm') registrationForm, ..)")
    public void registerUserFromRegistrationPage(){}

    @Pointcut("execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.registerUser(..)) &&" +
            "args(@org.springframework.web.bind.annotation.RequestBody, ..)")
    public void registerUserFromJSFetchRequest(){}


    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.GeneralViewController.addATask(..))")
    public void afterTaskCreated() {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Task savedTask = null;
        if (auditLogService.getLatestTaskByUserEmail(email).isPresent()) {
            savedTask = auditLogService.getLatestTaskByUserEmail(email).get();
        }
        AuditLog log = null;
        UserModel user = userToUserModel(currentUser);
//        user.setFirstName(currentUser.getFirstName());
//        user.setLastName(currentUser.getLastName());
//        user.setEmail(currentUser.getEmail());
//        user.setId(currentUser.getId());
//        user.setRole(currentUser.getUserCredentials().getRole());
        assert savedTask != null;
        TaskModel task = taskToTaskModel(savedTask);
//
//        task.setTitle(savedTask.getTitle());
//        task.setDescription(savedTask.getDescription());
//        task.setDueDate(savedTask.getDueDate());
//        task.setPriority(savedTask.getPriority());

        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("Task", task);

        if (savedTask != null) {
            log = new AuditLog(
                    email,
                    "TASK_CREATED",
                    email + " created the task \"" + savedTask.getTitle() + "\"",
                    map
            );

        } else {
            log = new AuditLog(email,
                    "TASK_CREATE_FAIL",
                    email + " failed to create a task",
                    map);
        }
        auditLogService.save(log);
    }

    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.GeneralViewController.acceptTask(java.math.BigInteger,..)) && args(id, ..)",
            argNames = "id")
    public void logAcceptingTask(BigInteger id) {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Task acceptedTask = taskService.getTaskById(id);

        UserModel user = userToUserModel(currentUser);
//        user.setFirstName(currentUser.getFirstName());
//        user.setLastName(currentUser.getLastName());
//        user.setEmail(currentUser.getEmail());
//        user.setId(currentUser.getId());
//        user.setRole(currentUser.getUserCredentials().getRole());

        TaskModel task = taskToTaskModel(acceptedTask);
//        task.setTitle(acceptedTask.getTitle());
//        task.setDescription(acceptedTask.getDescription());
//        task.setDueDate(acceptedTask.getDueDate());
//        task.setPriority(acceptedTask.getPriority());


        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("Task", task);


        AuditLog log = new AuditLog(
                email,
                "TASK_ACCEPTED",
                email + " accepted task \"" + acceptedTask.getTitle() + "\"",
                map
        );
        auditLogService.save(log);
    }


    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.GeneralViewController.completeTask(java.math.BigInteger,..)) && args(id, ..)", argNames = "id")
    public void logCompletingTask(BigInteger id) {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Task completedTask = taskService.getTaskById(id);

        UserModel user = userToUserModel(currentUser);
//        user.setFirstName(currentUser.getFirstName());
//        user.setLastName(currentUser.getLastName());
//        user.setEmail(currentUser.getEmail());
//        user.setId(currentUser.getId());
//        user.setRole(currentUser.getUserCredentials().getRole());

        TaskModel task = taskToTaskModel(completedTask);
//        task.setTitle(completedTask.getTitle());
//        task.setDescription(completedTask.getDescription());
//        task.setDueDate(completedTask.getDueDate());
//        task.setPriority(completedTask.getPriority());
//        task.setUserId(completedTask.getAssignedTo().getId());


        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("Task", task);


        AuditLog log = new AuditLog(
                email,
                "TASK_COMPLETED",
                email + " completed task \"" + completedTask.getTitle() + "\"",
                map
        );
        auditLogService.save(log);
    }

    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.AuthenticationController.registerUser(com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm,..)) && " +
                    "@annotation(org.springframework.web.bind.annotation.PostMapping) && " +
                    "args(@org.springframework.web.bind.annotation.ModelAttribute('registrationForm') registrationForm, ..)",
            returning = "returnString")
    public void logRegisteringUser(@ModelAttribute("registrationForm") RegistrationForm registrationForm, String returnString) {
            if (returnString.equals("redirect:/login")) {
                String email = registrationForm.getEmail();
                User registeredUser = userService.getUserByEmail(email);

                UserModel user = userToUserModel(registeredUser);
                Map<String, Object> map = new HashMap<>();
                map.put("User", user);

                AuditLog log = new AuditLog(
                        email,
                        "USER_REGISTERED",
                        email + " has been registered as a user",
                        map
                );
                auditLogService.save(log);
            }
    }

    @Before(value = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.deleteUser(" +
            "java.lang.String)) && args(id)",
            argNames = "id")
    public void beforeDeletingUser(String id) {
        BigInteger userId = new BigInteger(id);
        User currentUser = securityContextService.getCurrentUser();
        User deletedUser = userService.getUserById(userId);

        UserModel deleter = userToUserModel(currentUser);

        UserModel deletee = userToUserModel(deletedUser);

        Map<String, Object> map = new HashMap<>();

        map.put("User", deleter);
        map.put("Deleted_user", deletee);

        AuditLog log = new AuditLog(
                currentUser.getEmail(),
                "DELETE_USER",
                currentUser.getEmail() + " has deleted the user " + deletedUser.getEmail(),
                map
        );

        auditLogService.save(log);
    }

    @Before(value = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.editUser(" +
            "java.lang.String,com.PetProject.Vitaliy.TaskManager.Model.UserModel)) && @annotation(org.springframework.web.bind.annotation.PostMapping) && args(id, userModel)",
            argNames = "id, userModel")
    public void beforeEditingUser(String id, UserModel userModel) {
        BigInteger userId = new BigInteger(id);
        User currentUser = securityContextService.getCurrentUser();
        User editedUser = userService.getUserById(userId);

        UserModel user = userToUserModel(currentUser);

        UserModel editedUserModel = userToUserModel(editedUser);

        userModel.setRole(editedUserModel.getRole());
        userModel.setId(editedUserModel.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("User_Before_Edit", editedUserModel);
        map.put("User_After_Edit", userModel);

        AuditLog log = new AuditLog(
                currentUser.getEmail(),
                "USER_EDIT",
                currentUser.getEmail() + " edited user " + editedUser.getEmail(),
                map
        );

        auditLogService.save(log);

    }

    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.addComment(" +
                    "java.math.BigInteger,..)) && args(taskId, ..)",
            returning = "responseEntity",
            argNames = "taskId, responseEntity"
    )
    public void afterPostingComment(BigInteger taskId, ResponseEntity<CommentModel> responseEntity) {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Task task = taskService.getTaskById(taskId);
        CommentModel commentModel = responseEntity.getBody();

        UserModel user = userToUserModel(currentUser);

        TaskModel taskModel = taskToTaskModel(task);

        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("Task", taskModel);
        map.put("Comment", commentModel);

        AuditLog log = new AuditLog(
                email,
                "TASK_COMMENTED",
                currentUser.getEmail() + " left a comment on " + task.getTitle(),
                map
        );

        auditLogService.save(log);
    }


    @Before(
            value = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.changePriority(" +
                    "java.math.BigInteger,java.lang.String)) && args(taskId, priorityString)",
            argNames = "taskId, priorityString"
    )
    public void afterChangingPriorityForTask(BigInteger taskId, String priorityString) {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();
        Task task = taskService.getTaskById(taskId);

        UserModel user = userToUserModel(currentUser);

        TaskModel taskModel = taskToTaskModel(task);

        Map<String, Object> map = new HashMap<>();
        map.put("User", user);
        map.put("Task", taskModel);
        map.put("New_Priority", priorityString);
        AuditLog log = new AuditLog(
                email,
                "TASK_PRIORITY_CHANGE",
                email + " changed priority of " + task.getTitle() +
                        " from " + task.getPriority() + " to " + priorityString,
                map
        );
        auditLogService.save(log);
    }


    @AfterReturning(
            pointcut = "execution(* com.PetProject.Vitaliy.TaskManager.Controller.RESTController.registerUser(com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm,..)) && " +
                    "@annotation(org.springframework.web.bind.annotation.PostMapping) && " +
                    "args(registrationForm, ..)",
            argNames = "registrationForm"
    )
    public void logCreatedUser(RegistrationForm registrationForm) {
        User currentUser = securityContextService.getCurrentUser();
        String email = currentUser.getEmail();

        User createdUser = userService.getUserByEmail(registrationForm.getEmail());

        UserModel user = userToUserModel(currentUser);
        UserModel createdUserModel = userToUserModel(createdUser);

        Map<String,Object> map = new HashMap<>();
        map.put("User",user);
        map.put("New_User", createdUserModel);

        AuditLog log = new AuditLog(
                email,
                "USER_REGISTERED",
                email + " created new user " + createdUserModel.getEmail(),
                map
        );

        auditLogService.save(log);

    }

    private UserModel userToUserModel(User currentUser){
        UserModel user = new UserModel();
        user.setFirstName(currentUser.getFirstName());
        user.setLastName(currentUser.getLastName());
        user.setEmail(currentUser.getEmail());
        user.setId(currentUser.getId());
        user.setRole(currentUser.getUserCredentials().getRole());
        return user;
    }

    private TaskModel taskToTaskModel(Task task){
        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(task.getTitle());
        taskModel.setDescription(task.getDescription());
        taskModel.setDueDate(task.getDueDate());
        taskModel.setPriority(task.getPriority());
        taskModel.setUserId(task.getAssignedTo().getId());
        return taskModel;
    }



}
