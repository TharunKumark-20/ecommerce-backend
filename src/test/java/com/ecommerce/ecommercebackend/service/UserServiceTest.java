package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.entity.User;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    // Test 1: Save User
    @Test
    void saveUser_shouldSaveUser() {

        User user = new User();

        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.save(user))
                .thenReturn(user);

        User result =
                userService.saveUser(user);

        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals(
                "test@example.com",
                result.getEmail()
        );

        verify(userRepository).save(user);
    }


    // Test 2: Get All Users
    @Test
    void getAllUsers_shouldReturnUsers() {

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<User> result =
                userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("User One", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("User Two", result.get(1).getName());

        verify(userRepository).findAll();
    }


    // Test 3: Get User By Id
    @Test
    void getUserById_shouldReturnUser() {

        User user = new User();

        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Optional<User> result =
                userService.getUserById(1L);

        assertEquals(1L, result.get().getId());
        assertEquals(
                "Test User",
                result.get().getName()
        );
        assertEquals(
                "test@example.com",
                result.get().getEmail()
        );

        verify(userRepository).findById(1L);
    }


    // Test 4: Get User By Id - Not Found
    @Test
    void getUserById_shouldReturnEmptyWhenUserNotFound() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<User> result =
                userService.getUserById(999L);

        assertEquals(Optional.empty(), result);

        verify(userRepository).findById(999L);
    }


    // Test 5: Delete User
    @Test
    void deleteUser_shouldDeleteUser() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }


    // Test 6: Delete User - Not Found
    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> userService.deleteUser(999L)
        );

        verify(userRepository).existsById(999L);
    }
}