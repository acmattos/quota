package br.com.acmattos.quota.controller;

import br.com.acmattos.quota.entity.User;
import br.com.acmattos.quota.service.QuotaService;
import br.com.acmattos.quota.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    private QuotaService quotaService;
    @MockBean
    private UserService userService;

    @Test
    void givenAUserWhenCreateUserIsCalledThenResponseIsCreated() throws Exception {
        // Given
        when(userService.createUser(any())).thenReturn((Optional.of(new User())));
        String jsonBody = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";
        // When
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        // Then
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1))
            .createUser(any());
    }

    @Test
    void givenAnAlreadyCreatedUserWhenCreateUserIsCalledThenResponseIsBadRequest() throws Exception {
        // Given
        when(userService.createUser(any())).thenReturn((Optional.empty()));
        String jsonBody = "{\"firstName\": \"John\", \"lastName\": \"Doe\"}";
        // When
        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
        // Then
            .andExpect(status().isBadRequest())
            .andExpect(content().string("User already exists"));

        verify(userService, times(1))
            .createUser(any());
    }

    @Test
    void givenAUserIdWhenGetUserIsCalledThenResponseIsOk() throws Exception {
        // Given
        when(userService.getUserById(any()))
            .thenReturn((Optional.of(
                new User().setFirstName("John").setLastName("Doe"))));
        // When
        mockMvc.perform(
                get("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646")
                    .contentType(MediaType.APPLICATION_JSON))
       // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1))
            .getUserById(any());
    }

    @Test
    void givenAnInvalidUserIdWhenGetUserIsCalledThenResponseIsNotFound() throws Exception {
        // Given - an invalid user id
        // When
        mockMvc.perform(
                get("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646")
                    .contentType(MediaType.APPLICATION_JSON))
            // Then
            .andExpect(status().isNotFound());

        verify(userService, times(1))
            .getUserById(any());
    }

    @Test
    void givenAUserWhenUpdateUserIsCalledThenResponseIsOk() throws Exception {
        // Given
        User user = new User().setFirstName("Joe").setLastName("Doe II");
        when(userService.updateUser(any(), any())).thenReturn((Optional.of(user)));
        String jsonBody = "{\"firstName\": \"Joe\", \"lastName\": \"Doe II\"}";
        // When
        mockMvc.perform(
                put("/api/users/"+user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
        // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Joe"))
            .andExpect(jsonPath("$.lastName").value("Doe II"));

        verify(userService, times(1))
            .updateUser(any(), any());
    }

    @Test
    void givenAnInvalidUserIdWhenUpdateUserIsCalledThenResponseIsNotFound() throws Exception {
        // Given
        User user = new User().setFirstName("Joe").setLastName("Doe II");
        String jsonBody = "{\"firstName\": \"Joe\", \"lastName\": \"Doe II\"}";
        // When
        mockMvc.perform(
                put("/api/users/"+user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
        // Then
            .andExpect(status().isNotFound());

        verify(userService, times(1))
            .updateUser(any(), any());
    }

    @Test
    void givenAUserIdWhenDeleteUserIsCalledThenResponseIsNoContent() throws Exception {
        // Given
        User user = new User().setFirstName("Joe").setLastName("Doe II");
        when(userService.deleteUserById(any())).thenReturn(Optional.of(user));
        // When
        mockMvc.perform(
                delete("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646")
                    .contentType(MediaType.APPLICATION_JSON))
        // Then
            .andExpect(status().isNoContent());

        verify(userService, times(1))
            .deleteUserById(any());
    }

    @Test
    void givenAInvalidUserIdWhenDeleteUserIsCalledThenResponseIsNotFound() throws Exception {
        // Given - an invalid user id
        // When
        mockMvc.perform(
                delete("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646")
                    .contentType(MediaType.APPLICATION_JSON))
        // Then
            .andExpect(status().isNotFound());

        verify(userService, times(1))
            .deleteUserById(any());
    }

    @Test
    void givenAUserIdWhenConsumeQuotaIsCalledThenResponseIsOk() throws Exception {
        // Given
        when(quotaService.isAllowedFor(any())).thenReturn(true);
        when(quotaService.getUserQuota(any())).thenReturn(1);
        // When
        mockMvc.perform(
                get("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646/quotas")
                    .contentType(MediaType.APPLICATION_JSON))
        // Then
            .andExpect(status().isOk())
            .andExpect(content().string("Quota not exceeded: 1"));

        verify(quotaService, times(1))
            .isAllowedFor(any());
        verify(quotaService, times(1))
            .getUserQuota(any());
    }

    @Test
    void givenAUserIdThatExceededQuotaWhenConsumeQuotaIsCalledThenResponseIsBadRequest() throws Exception {
        // Given
        when(quotaService.isAllowedFor(any())).thenReturn(false);
        // When
        mockMvc.perform(
                get("/api/users/5ef1bc49-eb46-4b3c-b67a-c26a31362646/quotas")
                    .contentType(MediaType.APPLICATION_JSON))
        // Then
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Quota exceeded!"));

        verify(quotaService, times(1))
            .isAllowedFor(any());
        verify(quotaService, never())
            .getUserQuota(any());
    }

    @Test
    void givenAUserIdWithQuotaWhenGetUsersQuotaIsCalledThenResponseIsOk() throws Exception {
        // Given
        ConcurrentMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
        map.put("5ef1bc49-eb46-4b3c-b67a-c26a31362646", new AtomicInteger(1));
        when(quotaService.getUserQuotaMap()).thenReturn(map);
        // When
        mockMvc.perform(
                get("/api/users/quotas")
                    .contentType(MediaType.APPLICATION_JSON))
       // Then
            .andExpect(status().isOk())
            .andExpect(content().string("{\"5ef1bc49-eb46-4b3c-b67a-c26a31362646\":1}"));

        verify(quotaService, times(1))
            .getUserQuotaMap();
    }
}