package io.github.kitamura.subtrack_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Subtrack API Spring Boot application.
 * <p>
 * This backend service provides RESTful APIs for managing SaaS subscriptions, users, and subscription history.
 * <p>
 * Technologies: Spring Boot, PostgreSQL, JPA, Flyway.
 * <p>
 * For API details, validation, and error handling, see HELP.md in the project root.
 * <p>
 * All public classes and methods are documented with Javadoc for maintainability and extensibility.
 */
@SpringBootApplication
public class SubtrackApiApplication {

	/**
	 * Main method to launch the Spring Boot application.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(SubtrackApiApplication.class, args);
	}

}
