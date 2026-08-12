package com.BackEnd.Master.GYM.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "App_user")
public class AppUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Login handle: normalized, unique, derived from displayName at creation - see UsernameNormalizer
    @Column(unique = true)
    private String userName;
    private String displayName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String telephone;
    private String motDePasse;
    private String profileImage;
    private String description;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;



}

