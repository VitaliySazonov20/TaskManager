package com.PetProject.Vitaliy.TaskManager.Controller;


import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    //handler method to go to the login page
    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        //create empty form for model
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(Model model,
                               @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                               BindingResult result,
                               RedirectAttributes redirectAttributes){
        if(userService.checkIfEmailExists(registrationForm.getEmail())){
            result.rejectValue("email","error.email", "Email already exists");
        }
        if(result.hasErrors()){
            return "registration";
        }
        userService.registerUser(registrationForm);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/login";
    }
}
