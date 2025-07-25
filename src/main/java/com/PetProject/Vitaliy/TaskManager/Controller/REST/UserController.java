package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "User management")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of all users")
    @GetMapping
    public List<UserModel> getAllUserModels(){
        return userService.getAllUsers().stream()
                .map(user -> new UserModel(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getUserCredentials().getRole()))
                .collect(Collectors.toList());
    }
    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User has been deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body("User has been deleted");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }

    }
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){
        try{
            User user = userService.getUserById(id);
            UserModel userModel = new UserModel(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUserCredentials().getRole());
            return ResponseEntity.ok(userModel);
        } catch (UserNotFoundException e){
            return  ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(summary = "Update User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id,
            @RequestBody UserModel userModel){
        try {
            User user = userService.getUserById(id);
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setEmail(user.getEmail());
            userService.saveUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }

    }

}
