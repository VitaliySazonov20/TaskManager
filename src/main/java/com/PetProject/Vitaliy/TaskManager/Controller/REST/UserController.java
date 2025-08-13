package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "User management")
@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users in the system." +
                    "Returns an empty list if no users are found.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all users"),
            @ApiResponse(responseCode = "204", description = "No users found in the system"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUserModels(){
        try{
            List<User> users = userService.getAllUsers();
            if(users.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            List<UserModel> userModels = users.stream()
                    .filter(Objects::nonNull)
                    .map(user->{
                        return new UserModel(
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getUserCredentials().getRole()
                       );
                    }).collect(Collectors.toList());
            return ResponseEntity.ok(userModels);
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .build();
        }
    }
    @Operation(
            summary = "Delete user",
            description = "Permanently removes a user. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User has been deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Data conflict (user might have existing references)"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PreAuthorize("hasRole('ADMIN') or @security.isOwner(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){
        if(id == null|| id.compareTo(BigInteger.ZERO)<=0){
            return ResponseEntity.badRequest().body("ID must be a positive number");
        }
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok().body(String.format("User with ID %s deleted successfully", id));
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete user due to data constraints");
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Unable to process deletion request");
        }

    }
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves complete user information. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Data access conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or @security.isOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id){

        if(id == null || id.compareTo(BigInteger.ZERO)<=0){
            return ResponseEntity.badRequest()
                    .body("Id must be a positive number");
        }
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
        }catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Database error occurred");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @Operation(
            summary = "Update User",
            description = "Partially updates user information. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request body"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Data access conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN') or @security.isOwner(#id)")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Parameter(description = "User ID")
            @PathVariable BigInteger id,
            @Valid @RequestBody UserModel userModel){

        if (id == null || id.compareTo(BigInteger.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body("ID must be a positive number");
        }

        try {
            User user = userService.getUserById(id);
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());

            if(userService.checkIfEmailExists(userModel.getEmail())){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email already exists");
            }

            user.setEmail(userModel.getEmail());
            userService.saveUser(user);
            return ResponseEntity.ok().body("User updated successfully");
        } catch(UserNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Database error during update");
        }catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("Failed to update user");
        }

    }

}
