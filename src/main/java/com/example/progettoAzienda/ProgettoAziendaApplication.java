package com.example.progettoAzienda;

import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.example.progettoAzienda.repositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
@EnableCosmosRepositories(basePackageClasses = UserRepository.class)
public class ProgettoAziendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgettoAziendaApplication.class, args);
        System.out.println(UUID.randomUUID().toString());
    }

}
