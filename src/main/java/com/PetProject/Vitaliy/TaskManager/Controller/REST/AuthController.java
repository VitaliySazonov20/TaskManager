package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;


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
}
