package com.PetProject.Vitaliy.TaskManager.Model;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;

import java.util.List;

public class AllTaskOfGivenUser {
    private List<Task> taskList;

    public AllTaskOfGivenUser(List<Task> taskList) {
        this.taskList = taskList;
    }

    public AllTaskOfGivenUser() {
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
