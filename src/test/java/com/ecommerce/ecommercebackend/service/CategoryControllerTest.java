package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.CategoryResponseDTO;
import com.ecommerce.ecommercebackend.entity.Category;
import com.ecommerce.ecommercebackend.service.CategoryService;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private CategoryService categoryService;


    // Required because JwtAuthenticationFilter
    // depends on JwtService
    @MockitoBean
    private JwtService jwtService;      


    // Required because JwtAuthenticationFilter
    // also depends on CustomUserDetailsService
    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    // =========================================================
    // TEST 1 : CREATE CATEGORY
    // =========================================================

    @Test
    void createCategory_shouldCreateCategory()
            throws Exception {

        Category savedCategory = new Category();

        savedCategory.setId(1L);
        savedCategory.setName("Electronics");


        when(categoryService.saveCategory(any()))
                .thenReturn(savedCategory);


        String json = """
                {
                    "name": "Electronics"
                }
                """;


        mockMvc.perform(
                        post("/categories")
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
                                .value("Electronics")
                );


        verify(categoryService)
                .saveCategory(any());
    }


    // =========================================================
    // TEST 2 : GET ALL CATEGORIES
    // =========================================================

    @Test
    void getAllCategories_shouldReturnCategories()
            throws Exception {

        CategoryResponseDTO category1 =
                new CategoryResponseDTO(
                        1L,
                        "Electronics"
                );


        CategoryResponseDTO category2 =
                new CategoryResponseDTO(
                        2L,
                        "Clothing"
                );


        when(categoryService.getAllCategories())
                .thenReturn(
                        List.of(
                                category1,
                                category2
                        )
                );


        mockMvc.perform(
                        get("/categories")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Electronics")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("Clothing")
                );


        verify(categoryService)
                .getAllCategories();
    }


    // =========================================================
    // TEST 3 : GET CATEGORY BY ID
    // =========================================================

    @Test
    void getCategoryById_shouldReturnCategory()
            throws Exception {

        CategoryResponseDTO category =
                new CategoryResponseDTO(
                        1L,
                        "Electronics"
                );


        when(categoryService.getCategoryById(1L))
                .thenReturn(category);


        mockMvc.perform(
                        get("/categories/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Electronics")
                );


        verify(categoryService)
                .getCategoryById(1L);
    }


    // =========================================================
    // TEST 4 : DELETE CATEGORY
    // =========================================================

    @Test
    void deleteCategory_shouldDeleteCategory()
            throws Exception {

        doNothing()
                .when(categoryService)
                .deleteCategory(1L);


        mockMvc.perform(
                        delete("/categories/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .value(
                                        "Category deleted successfully"
                                )
                );


        verify(categoryService)
                .deleteCategory(1L);
    }
}