package com.PetProject.Vitaliy.TaskManager.Controller;

import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.Service.SecurityContextService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class GeneralViewController {


    @Autowired
    private TaskService taskService;

    @Autowired
    private SecurityContextService securityContextService;


    @GetMapping("/tasks")
    public String showAllTasks(Model model){
        User currentUser = securityContextService.getCurrentUser();
        List<Task> assignedTasks = taskService.getAllTaskAssignedToUser(currentUser);
        List<Task> createdTasks = taskService.getAllTasksCreatedByUser(currentUser);
        model.addAttribute("assignedTasks",
                assignedTasks != null ? assignedTasks : Collections.emptyIterator());
        model.addAttribute("createdTasks",
                createdTasks != null? createdTasks: Collections.emptyList());
        return "tasks";

    }
}
