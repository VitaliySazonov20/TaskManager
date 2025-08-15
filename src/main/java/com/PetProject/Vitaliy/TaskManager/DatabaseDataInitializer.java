package com.PetProject.Vitaliy.TaskManager;


import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Service.CommentService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.Service.UserCredentialsService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DatabaseDataInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private static final Random RANDOM = new Random();


    public void initTestData() {
        User admin = initAdminUser();
        User user = initSecondUser();
        initTasks(admin,user);
        initTaskComments(admin);
    }

    private void initTaskComments(User admin) {

        List<Task> taskList = taskService.getAllTasksCreatedByUser(admin);
        for (int i = 0; i < 5; i++) {
            Task task = taskList.get(i);
            List<Comment> comments = commentService.getCommentsById(task.getId());
            if (comments.isEmpty()) {
                Comment comment = new Comment();

                comment.setTask(task);
                comment.setText("First Comment for task " + (i + 1));
                comment.setUser(admin);
                commentService.saveComment(comment);
            }
        }
    }

    private void initTasks(User admin, User user) {

        List<TaskStatus> statuses = List.of(TaskStatus.values());
        List<Priority> priorities = List.of(Priority.values());
        List<Task> taskList = taskService.getAllTasksCreatedByUser(admin);
        if (taskList.isEmpty() || taskList.size() < 5) {
            for (int i = 0; i < 5 - taskList.size(); i++) {
                Task task = new Task();
                task.setStatus(statuses.get(i));
                task.setPriority(priorities.get(RANDOM.nextInt(priorities.size())));
                if (task.getStatus() != TaskStatus.BACKLOG)
                    task.setAssignedTo(user);
                task.setCreatedBy(admin);
                task.setDescription("Description of Task " + (i + 1));
                task.setTitle("Task " + (i + 1));
                task.setUpdateDate(LocalDateTime.now());
                if (task.getStatus() == TaskStatus.OVERDUE)
                    task.setDueDate(LocalDateTime.now().minusSeconds(10));
                taskService.saveTask(task);
            }
        }
    }

    private User initAdminUser() {
        try {
            return userService.getUserByEmail("FakeAdminEmail@email.com");
        } catch (UserNotFoundException e) {
            User admin = new User();
            //set name and email for user
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("FakeAdminEmail@email.com");


            UserCredentials adminCreds = new UserCredentials();

            adminCreds.setUser(admin);
            //encrypt and save password
            adminCreds.setPasswordHash(passwordEncoder.encode("password"));
            adminCreds.setRole(Role.ADMIN);
            admin.setUserCredentials(adminCreds);
            userCredentialsService.saveUserCred(adminCreds);
            userService.saveUser(admin);
            return admin;
        }
    }

    private User initSecondUser() {

        try {
            return userService.getUserByEmail("FakeEmail@email.com");
        } catch (UserNotFoundException e) {
            User user = new User();

            user.setFirstName("Regular");
            user.setLastName("User");
            user.setEmail("FakeEmail@email.com");

            UserCredentials userCredentials = new UserCredentials();

            userCredentials.setRole(Role.USER);
            userCredentials.setPasswordHash(passwordEncoder.encode("qwe123"));
            userCredentials.setUser(user);

            user.setUserCredentials(userCredentials);

            userCredentialsService.saveUserCred(userCredentials);
            userService.saveUser(user);
            return user;
        }
    }
}

