package com.demoJob.demo;

import com.demoJob.demo.dto.request.RegisterRequest;
import com.demoJob.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

//		System.out.println(new BCryptPasswordEncoder().encode("123456789"));
	}

//	@Bean
//	CommandLineRunner run(UserService userService) {
//		return args -> {
//			RegisterRequest request = RegisterRequest.builder()
//					.username("admin")
//					.email("admin@gmail.com")
//					.firstName("Admin")
//					.lastName("System")
//					.password(new BCryptPasswordEncoder().encode("123456789"))
//					.build();
//
//			if (userService.findByUsername("admin").isEmpty()) {
//				userService.createUser(request);
//				System.out.println("Seeder Data Successfully");
//			} else {
//				System.out.println("Username already exists");
//			}
//		};
//	}
}
