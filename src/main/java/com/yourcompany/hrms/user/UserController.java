package com.yourcompany.hrms.user;

import com.yourcompany.hrms.dto.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<UserResponse>> createUser(@Valid @RequestBody RegisterRequest request) {
        String currentUserEmail = userService.getCurrentUserEmail();
        UserResponse userResponse = userService.createUser(request, currentUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("User created successfully", userResponse));
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users = userService.getAllUsers(search, pageable);
        return ResponseEntity.ok(ResponseWrapper.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> getUserById(@PathVariable Long id) {
        String currentUserEmail = userService.getCurrentUserEmail();
        UserResponse userResponse = userService.getUserById(id, currentUserEmail);
        return ResponseEntity.ok(ResponseWrapper.success(userResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<UserResponse>> updateUser(@PathVariable Long id, 
                                                                   @Valid @RequestBody UpdateUserRequest request) {
        String currentUserEmail = userService.getCurrentUserEmail();
        UserResponse userResponse = userService.updateUser(id, request, currentUserEmail);
        return ResponseEntity.ok(ResponseWrapper.success("User updated successfully", userResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseWrapper.success("User soft deleted successfully"));
    }
}

