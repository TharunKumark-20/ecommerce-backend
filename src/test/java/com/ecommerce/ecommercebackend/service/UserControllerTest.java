package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.service.UserService;

import org.junit.jupiter.api.Test;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    @MockitoBean
    private UserService userService;


    // =========================================================
    // TEST 1 : SAVE USER
    // =========================================================

    @Test
    void saveUser_shouldCreateUser()
            throws Exception {

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@example.com");


        when(userService.saveUser(any(User.class)))
                .thenReturn(savedUser);


        String json = """
                {
                    "name": "John",
                    "email": "john@example.com"
                }
                """;


        mockMvc.perform(
                        post("/users")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("John")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("john@example.com")
                );


        verify(userService)
                .saveUser(any(User.class));
    }


    // =========================================================
    // TEST 2 : GET ALL USERS
    // =========================================================

    @Test
    void getAllUsers_shouldReturnUsers()
            throws Exception {

        User user1 = new User();

        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@example.com");


        User user2 = new User();

        user2.setId(2L);
        user2.setName("David");
        user2.setEmail("david@example.com");


        when(userService.getAllUsers())
                .thenReturn(
                        List.of(
                                user1,
                                user2
                        )
                );


        mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("John")
                )
                .andExpect(
                        jsonPath("$[0].email")
                                .value("john@example.com")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("David")
                )
                .andExpect(
                        jsonPath("$[1].email")
                                .value("david@example.com")
                );


        verify(userService)
                .getAllUsers();
    }


    // =========================================================
    // TEST 3 : GET USER BY ID
    // =========================================================

    @Test
    void getUserById_shouldReturnUser()
            throws Exception {

        User user = new User();

        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");


        when(userService.getUserById(1L))
                .thenReturn(
                        Optional.of(user)
                );


        mockMvc.perform(
                        get("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("John")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("john@example.com")
                );


        verify(userService)
                .getUserById(1L);
    }


    // =========================================================
    // TEST 4 : DELETE USER
    // =========================================================

    @Test
    void deleteUser_shouldDeleteUser()
            throws Exception {

        doNothing()
                .when(userService)
                .deleteUser(1L);


        mockMvc.perform(
                        delete("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .value(
                                        "User deleted successfully"
                                )
                );


        verify(userService)
                .deleteUser(1L);
    }
}