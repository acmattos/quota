package br.com.acmattos.quota.controller;

import br.com.acmattos.quota.entity.User;
import br.com.acmattos.quota.service.QuotaService;
import br.com.acmattos.quota.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

@Tag(name = "User", description = "the user API endpoint")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserController.class);
    private final QuotaService quotaService;
    private final UserService userService;

    public UserController(
        final QuotaService quotaService,
        final UserService userService) {
        this.quotaService = Objects.requireNonNull(quotaService);
        this.userService = Objects.requireNonNull(userService);
    }

    @Operation(
        summary = "Create User",
        description = "creates a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "User already exists")
    })
    @PostMapping(produces = { "application/json" })
    public ResponseEntity<?> createUser(
        @Valid
        @RequestBody
        User user) {
        LOGGER.info("Creating user");
        return userService.createUser(user)
            .<ResponseEntity<?>>map(created ->
                ResponseEntity
                    .created(URI.create("/api/users/" + user.getId()))
                    .body(user))
            .orElseGet(() ->  ResponseEntity
                .badRequest()
                .body("User already exists"));
    }

    @Operation(
        summary = "Get User",
        description = "gets user identified by userId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(path = "/{userId}", produces = { "application/json" })
    public ResponseEntity<?> getUser(
        @NotBlank
        @PathVariable(name="userId")
        String userId) {
        return userService.getUserById(userId)
            .<ResponseEntity<?>>map(found ->
                ResponseEntity
                    .ok(found))
            .orElseGet(() -> ResponseEntity
                .notFound()
                .build());
    }

    @Operation(
        summary = "Update User",
        description = "updates user identified by userId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "400", description = "User updated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(path = "/{userId}", produces = { "application/json" })
    public ResponseEntity<?> updateUser(
        @PathVariable(name="userId")
        String userId,
        @Valid
        @RequestBody
        User updatedUser){
        return userService.updateUser(userId, updatedUser)
            .<ResponseEntity<?>>map(updated ->
                ResponseEntity
                    .ok(updated))
            .orElseGet(() -> ResponseEntity
                .notFound()
                .build());
    }

    @Operation(
        summary = "Delete User",
        description = "deletes user identified by userId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(
        @PathVariable(name="userId") String userId){
        return userService.deleteUserById(userId)
            .<ResponseEntity<?>>map(deleted ->
                ResponseEntity
                    .noContent()
                    .build())
            .orElseGet(() -> ResponseEntity
                .notFound()
                .build());
    }

    @Operation(
        summary = "Consume quotas",
        description = "consumes quotas for a specific userId, returning the current quota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quota not exceeded: [quota]"),
        @ApiResponse(responseCode = "400", description = "Quota exceeded!")
    })
    @GetMapping(value = "/{userId}/quotas", produces = "plain/text")
    public ResponseEntity<String> consumeQuota(
        @PathVariable(name="userId") String userId) {
        if(quotaService.isAllowedFor(userId)) {
            return ResponseEntity.ok("Quota not exceeded: "
                + quotaService.getUserQuota(userId));
        }
        return ResponseEntity.badRequest().body("Quota exceeded!");
    }

    @Operation(
        summary = "Get users' quotas",
        description = "gets quotas for all users consuming the specified quota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quota not exceeded: [quota]")
    })
    @GetMapping(value="/quotas", produces = { "application/json" })
    public ResponseEntity<?> getUsersQuota() {
        return ResponseEntity.ok(quotaService.getUserQuotaMap());
    }
}
