package com.ecommerce.ecommercebackend.controller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import com.ecommerce.ecommercebackend.dto.PageResponseDTO;
import com.ecommerce.ecommercebackend.dto.ProductResponseDTO;
import com.ecommerce.ecommercebackend.entity.Product;
import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.FileStorageService;
import com.ecommerce.ecommercebackend.service.JwtService;
import com.ecommerce.ecommercebackend.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ProductService productService;


    @MockitoBean
    private FileStorageService fileStorageService;


    @MockitoBean
    private JwtService jwtService;


    @MockitoBean
    private CustomUserDetailsService userDetailsService;


    // =========================================================
    // TEST 1 : GET ALL PRODUCTS
    // =========================================================
    @BeforeEach
    void setUpSecurityContext() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "testadmin",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }


    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllProducts_shouldReturnProducts() throws Exception {

        ProductResponseDTO product =
                new ProductResponseDTO(
                        1L,
                        "Laptop",
                        50000.0,
                        "Gaming Laptop",
                        "laptop.jpg",
                        10,
                        1L,
                        "Electronics"
                );


        PageResponseDTO<ProductResponseDTO> response =
                new PageResponseDTO<>(
                        List.of(product),
                        0,
                        5,
                        1,
                        1,
                        true
                );


        when(productService.getAllProducts(
                0,
                5,
                "id",
                "asc"
        )).thenReturn(response);


        mockMvc.perform(
                        get("/products")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].name")
                                .value("Laptop")
                )
                .andExpect(
                        jsonPath("$.content[0].price")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1)
                );
    }


    // =========================================================
    // TEST 2 : GET PRODUCT BY ID
    // =========================================================

    @Test
    void getProductById_shouldReturnProduct() throws Exception {

        ProductResponseDTO product =
                new ProductResponseDTO(
                        1L,
                        "Laptop",
                        50000.0,
                        "Gaming Laptop",
                        "laptop.jpg",
                        10,
                        1L,
                        "Electronics"
                );


        when(productService.getProductById(1L))
                .thenReturn(product);


        mockMvc.perform(
                        get("/products/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Laptop")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.stock")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.categoryId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.categoryName")
                                .value("Electronics")
                );
    }


    // =========================================================
    // TEST 3 : SEARCH PRODUCTS
    // =========================================================

    @Test
    void searchProducts_shouldReturnMatchingProducts()
            throws Exception {

        ProductResponseDTO product =
                new ProductResponseDTO(
                        1L,
                        "Laptop",
                        50000.0,
                        "Gaming Laptop",
                        "laptop.jpg",
                        10,
                        1L,
                        "Electronics"
                );


        when(productService.searchProducts("Laptop"))
                .thenReturn(List.of(product));


        mockMvc.perform(
                        get("/products/search")
                                .param("keyword", "Laptop")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Laptop")
                );
    }


    // =========================================================
    // TEST 4 : GET PRODUCTS BY CATEGORY
    // =========================================================

    @Test
    void getProductsByCategory_shouldReturnProducts()
            throws Exception {

        ProductResponseDTO product =
                new ProductResponseDTO(
                        1L,
                        "Laptop",
                        50000.0,
                        "Gaming Laptop",
                        "laptop.jpg",
                        10,
                        1L,
                        "Electronics"
                );


        when(productService.getProductsByCategory(1L))
                .thenReturn(List.of(product));


        mockMvc.perform(
                        get("/products/category/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Laptop")
                );
    }


    // =========================================================
    // TEST 5 : SEARCH BY NAME AND CATEGORY
    // =========================================================

    @Test
    void searchProductsByCategory_shouldReturnProducts()
            throws Exception {

        ProductResponseDTO product =
                new ProductResponseDTO(
                        1L,
                        "Laptop",
                        50000.0,
                        "Gaming Laptop",
                        "laptop.jpg",
                        10,
                        1L,
                        "Electronics"
                );


        when(
                productService.searchProductsByCategory(
                        "Laptop",
                        1L
                )
        ).thenReturn(List.of(product));


        mockMvc.perform(
                        get("/products/search/category")
                                .param(
                                        "keyword",
                                        "Laptop"
                                )
                                .param(
                                        "categoryId",
                                        "1"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Laptop")
                );
    }


    // =========================================================
    // TEST 6 : CREATE PRODUCT WITH IMAGE
    // =========================================================

    @Test
    void createProduct_shouldCreateProduct()
            throws Exception {

        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "laptop.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "fake-image-data".getBytes()
                );


        Product savedProduct = new Product();

        savedProduct.setId(1L);
        savedProduct.setName("Laptop");
        savedProduct.setPrice(50000.0);
        savedProduct.setDescription(
                "Gaming Laptop"
        );
        savedProduct.setImageUrl(
                "laptop.jpg"
        );
        savedProduct.setStock(10);


        when(fileStorageService.saveFile(any()))
                .thenReturn("laptop.jpg");


        when(productService.saveProduct(any()))
                .thenReturn(savedProduct);


        mockMvc.perform(
                        multipart("/products")
                                .file(image)
                                .param(
                                        "name",
                                        "Laptop"
                                )
                                .param(
                                        "price",
                                        "50000"
                                )
                                .param(
                                        "description",
                                        "Gaming Laptop"
                                )
                                .param(
                                        "stock",
                                        "10"
                                )
                                .param(
                                        "categoryId",
                                        "1"
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Laptop")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(50000.0)
                )
                .andExpect(
                        jsonPath("$.stock")
                                .value(10)
                );


        verify(fileStorageService)
                .saveFile(any());


        verify(productService)
                .saveProduct(any());
    }


    // =========================================================
    // TEST 7 : UPDATE PRODUCT
    // =========================================================

    @Test
    void updateProduct_shouldUpdateProduct()
            throws Exception {

        Product updatedProduct = new Product();

        updatedProduct.setId(1L);
        updatedProduct.setName(
                "Updated Laptop"
        );
        updatedProduct.setPrice(60000.0);
        updatedProduct.setDescription(
                "Updated Laptop"
        );
        updatedProduct.setStock(20);


        when(
                productService.updateProduct(
                        any(Product.class)
                )
        ).thenReturn(updatedProduct);


        String json = """
                {
                    "id": 1,
                    "name": "Updated Laptop",
                    "price": 60000.0,
                    "description": "Updated Laptop",
                    "stock": 20
                }
                """;


        mockMvc.perform(
                        put("/products")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Updated Laptop")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(60000.0)
                )
                .andExpect(
                        jsonPath("$.stock")
                                .value(20)
                );
    }


    // =========================================================
    // TEST 8 : DELETE PRODUCT
    // =========================================================

    @Test
    void deleteProduct_shouldDeleteProduct()
            throws Exception {

        doNothing()
                .when(productService)
                .deleteProduct(1L);


        mockMvc.perform(
                        delete("/products/1")
                )
                .andExpect(
                        status().isNoContent()
                );


        verify(productService)
                .deleteProduct(1L);
    }
}