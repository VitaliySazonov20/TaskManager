package com.PetProject.Vitaliy.TaskManager.Controller;


import com.PetProject.Vitaliy.TaskManager.Model.PasswordChange;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.SecurityContextService;
import com.PetProject.Vitaliy.TaskManager.Service.UserCredentialsService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private UserCredentialsService userCredentialsService;


    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);


    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model){
        try{
            if (userService.checkIfEmailExists(registrationForm.getEmail())) {
                result.rejectValue("email", "error.email", "Email already exists");
            }
            if (result.hasErrors()) {
                return "registration";
            }
            userService.registerUser(registrationForm);
            redirectAttributes.addFlashAttribute("success", true);
        }catch (Exception e){
            log.error("Failed to register user");
            model.addAttribute("error",
                    "Registration failed. Please try again later");
            return "registration";
        }
        return "redirect:/login";
    }

    @PostMapping("/user/saveProfile")
    public String saveCurrentUser(@Valid @ModelAttribute("editedUser") UserModel editedUser,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  @ModelAttribute("passwordChange") PasswordChange passwordChange,
                                  Model model){
        try{
            User currentUser = securityContextService.getCurrentUser();

            if (!editedUser.getEmail().equals(currentUser.getEmail())) {
                if (userService.checkIfEmailExists(editedUser.getEmail())) {
                    result.rejectValue("email", "email.exists", "Email already exists");
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("showPasswordSection", false);
                return "currentUser";
            }

            currentUser.setEmail(editedUser.getEmail());
            currentUser.setFirstName(editedUser.getFirstName());
            currentUser.setLastName(editedUser.getLastName());

            redirectAttributes.addFlashAttribute("success", true);
            userService.saveUser(currentUser);
        } catch (Exception e){
            log.error("Couldn't save the users profile",e);
            redirectAttributes.addFlashAttribute("error","Failed to update profile");
        }
        return "redirect:/user";
    }

    @PostMapping("/user/changePassword")
    public String changePassword(Model model,
                                 @Valid @ModelAttribute("passwordChange")PasswordChange passwordChange,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 @ModelAttribute("editedUser") UserModel userModel){
        try{
            User currentUser = securityContextService.getCurrentUser();
            if (!userCredentialsService.verifyPasswords(currentUser.getUserCredentials().getPassword(),
                    passwordChange.getOldPassword())) {
                result.rejectValue("oldPassword", "password.mismatch",
                        "Password doesn't match with current password");
            }
            if (!passwordChange.getNewPassword().equals(passwordChange.getConfirmNewPassword())) {
                result.rejectValue("confirmNewPassword", "password.mismatch",
                        "Passwords need to match to confirm");
            }

            if (result.hasErrors()) {
                model.addAttribute("showPasswordSection", true);

                userModel.setFirstName(currentUser.getFirstName());
                userModel.setLastName(currentUser.getLastName());
                userModel.setEmail(currentUser.getEmail());
                model.addAttribute("editedUser", userModel);

                return "currentUser";
            }
            UserCredentials currentUserCred = currentUser.getUserCredentials();
            currentUserCred.setPasswordHash(userCredentialsService.encodePassword(passwordChange.getNewPassword()));
            currentUser.setUserCredentials(currentUserCred);
            redirectAttributes.addFlashAttribute("success", true);
            userService.saveUser(currentUser);
            userCredentialsService.saveUserCred(currentUserCred);
        } catch (Exception e){
            log.error("Couldn't save the user's new password" ,e);
            redirectAttributes.addFlashAttribute("error","Failed to update password");
        }

        return "redirect:/user";
    }

    @GetMapping("/user/saveProfile")
    public String refreshSaveProfile(){
        return "redirect:/user";
    }

    @GetMapping("/user/changePassword")
    public String refreshChangePassword(){
        return "redirect:/user";
    }
}
