/*
 * Copyright 2016-2017 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
				.withResolver(AuthenticationToken.httpBearerResolver())
				// authenticator using the provided JwtConfiguration, automatically created by Holon using the
				// holon.jwt.* configuration properties
				.withAuthenticator(JwtAuthenticator.builder().configuration(jwtConfiguration).build())
				// default authorizer
				.withDefaultAuthorizer().build();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
