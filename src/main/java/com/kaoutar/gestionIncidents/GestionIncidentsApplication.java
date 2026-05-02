package com.kaoutar.gestionIncidents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
@EnableAsync
@EnableMethodSecurity
@SpringBootApplication
public class GestionIncidentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionIncidentsApplication.class, args);
	}

}
