package com.BackEnd.Master.GYM;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import com.BackEnd.Master.GYM.entity.AppUsers;
import com.BackEnd.Master.GYM.entity.Roles;
import com.BackEnd.Master.GYM.repository.AppUserRepo;
import com.BackEnd.Master.GYM.repository.RolesRepo;
import com.BackEnd.Master.GYM.services.AppUserService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class MasterGymApplication {

	private final AppUserRepo appUserRepository;
	private final RolesRepo rolesRepo;
	private final PasswordEncoder passwordEncoder;
	private final AppUserService appUserService;

	@Value("${app.admin.display-name}")
	private String adminDisplayName;

	@Value("${app.admin.email}")
	private String adminEmail;

	@Value("${app.admin.telephone}")
	private String adminTelephone;

	@Value("${app.admin.password}")
	private String adminPassword;

	public static void main(String[] args) {
		SpringApplication.run(MasterGymApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase() {
		return args -> {
			// Vérifier si un rôle "Admin" existe, sinon le créer
			Optional<Roles> adminRoleOpt = rolesRepo.findByRoleName("ROLE_Admin");
			Roles adminRole = adminRoleOpt.orElseGet(() -> {
				Roles newRole = new Roles();
				newRole.setRoleName("ROLE_Admin");
				newRole.setDescription("Administrator role with full access");
				return rolesRepo.save(newRole);
			});

			Optional<Roles> coatchRoleOpt = rolesRepo.findByRoleName("ROLE_Coach");
			coatchRoleOpt.orElseGet(() -> {
				Roles newcoatchRole = new Roles();
				newcoatchRole.setRoleName("ROLE_Coach");
				newcoatchRole.setDescription("Standard user role with limited access");
				return rolesRepo.save(newcoatchRole);
			});

			Optional<Roles> userRoleOpt = rolesRepo.findByRoleName("ROLE_User");
			userRoleOpt.orElseGet(() -> {
				Roles newUserRole = new Roles();
				newUserRole.setRoleName("ROLE_User");
				newUserRole.setDescription("Standard user role with limited access");
				return rolesRepo.save(newUserRole);
			});

			AppUsers existingAdmin = appUserRepository.findByEmail(adminEmail);
			if (existingAdmin == null) {
				AppUsers adminUser = new AppUsers();
				adminUser.setDisplayName(adminDisplayName);
				adminUser.setEmail(adminEmail);
				adminUser.setTelephone(adminTelephone);
				adminUser.setMotDePasse(passwordEncoder.encode(adminPassword));
				adminUser.setRole(adminRole);
				adminUser.setProfileImage("default.png");
				adminUser.setDescription("Administrator account with full access");

				// Goes through the service so userName gets normalized/derived from displayName,
				// same as every other account created via POST /user
				appUserService.insert(adminUser);
				System.out.println("Admin user created successfully");
			} else {
				System.out.println("Admin user already exists, skipping creation.");
			}

		};
	}

}
