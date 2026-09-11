package com.ecommerce.ecommercebackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;


    @Column(unique = true, nullable = false)
    private String email;


    private String password;


    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Order> orders;


    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private Cart cart;

}