package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.JWT.JwtTokenUtil;
import com.PetProject.Vitaliy.TaskManager.Model.LoginRequest;
import com.PetProject.Vitaliy.TaskManager.Model.LoginResponse;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Service.CustomUserDetailsService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Operation(
            summary = "Register new user account",
            description = "Creates new user with email verification requirements"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                            "firstName": "John",
                                            "lastName": "Doe",
                                            "email": "John@Doe.com",
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400",description = "Validation errors"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationForm registrationForm,
                                          BindingResult result){
        try{
            if (result.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                result.getFieldErrors().forEach(error -> {
                    String field = error.getField().equals("passwordsMatch")
                            ? "confirmationPassword" : error.getField();
                    errors.put(field, error.getDefaultMessage());
                });
                return ResponseEntity.badRequest().body(errors);
            }

            if (userService.checkIfEmailExists(registrationForm.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        Map.of("email", "Email already exists"));
            }
            userService.registerUser(registrationForm);
            return ResponseEntity.ok().body(
                    Map.of("firstName", registrationForm.getFirstName(),
                            "lastName", registrationForm.getLastName(),
                            "email", registrationForm.getEmail()));
        } catch (DataAccessException e){
            return ResponseEntity.internalServerError()
                    .body("Failed to complete registration due to database issues");
        }catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("Registration failed. Please try again.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> getJwtToken(@RequestBody LoginRequest loginRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),loginRequest.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
