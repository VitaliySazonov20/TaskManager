package com.PetProject.Vitaliy.TaskManager.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    //handler method to go to the login page
    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }
}
