package com.holonplatform.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.holonplatform.auth.AuthenticationToken;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.jwt.JwtAuthenticator;
import com.holonplatform.auth.jwt.JwtConfiguration;

/**
 * The application entry-point.
 * 
 * Running using Spring Boot maven plugin: <code>mvn spring-boot:run</code>
 */
@SpringBootApplication
public class Application {

	/* Authentication and authorization realm */
	@Bean
	public Realm realm(JwtConfiguration jwtConfiguration) {
		return Realm.builder()
				// HTTP Bearer authorization schema resolver
				.resolver(AuthenticationToken.httpBearerResolver())
				// authenticator using the provided JwtConfiguration, automatically created by Holon using the
				// holon.jwt.* configuration properties
				.authenticator(JwtAuthenticator.builder().configuration(jwtConfiguration).build())
				// default authorizer
				.withDefaultAuthorizer().build();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
