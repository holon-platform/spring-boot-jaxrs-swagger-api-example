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
package com.holonplatform.example.test;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.jwt.JwtConfiguration;
import com.holonplatform.auth.jwt.JwtTokenBuilder;
import com.holonplatform.example.data.UserDetails;
import com.holonplatform.http.HttpHeaders;
import com.holonplatform.http.HttpStatus;
import com.holonplatform.http.rest.ResponseEntity;
import com.holonplatform.http.rest.RestClient;

/**
 * API unit test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class ApiTest {

	@Autowired
	private JwtConfiguration jwtConfiguration;

	@Test
	public void testUnauthorized() {

		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/api").request().path("ping")
				.get(String.class);

		Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
		// Assert.assertEquals("pong", response.as(String.class).orElse(null));
	}

	@Test
	public void testAuth() {

		// build an Authentication
		Authentication authc = Authentication.builder("testUser").build();

		// build a jwt token from Authentication
		String jwtToken = JwtTokenBuilder.get().buildJwt(jwtConfiguration, authc, UUID.randomUUID().toString());

		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/api").request().path("ping")
				// set the JWT token as Authorization Bearer header value
				.authorizationBearer(jwtToken)
				//
				.get(String.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatus());
		Assert.assertEquals("pong", response.as(String.class).orElse(null));

		// test role based authorization
		response = RestClient.forTarget("http://localhost:9999/api").request().path("protected")
				.authorizationBearer(jwtToken).get(String.class);
		Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatus());

		// Use an authentication with ROLE1
		authc = Authentication.builder("testUser").permission("ROLE1").build();
		jwtToken = JwtTokenBuilder.get().buildJwt(jwtConfiguration, authc, UUID.randomUUID().toString());

		response = RestClient.forTarget("http://localhost:9999/api").request().path("protected")
				.authorizationBearer(jwtToken).get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());

		// get user name
		authc = Authentication.builder("testUser").permission("ROLE1").permission("ROLE2").build();
		jwtToken = JwtTokenBuilder.get().buildJwt(jwtConfiguration, authc, UUID.randomUUID().toString());

		response = RestClient.forTarget("http://localhost:9999/api").request().path("user")
				.authorizationBearer(jwtToken).get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());
		Assert.assertEquals("testUser", response.as(String.class).orElse(null));
	}

	@Test
	public void testAuthenticationInfo() {

		Authentication authc = Authentication.builder("testUser")
				// permissions
				.permission("ROLE1").permission("ROLE2")
				// user details
				.parameter("firstName", "Test").parameter("lastName", "User")
				.parameter("email", "test@holon-platform.com")
				//
				.build();
		String jwtToken = JwtTokenBuilder.get().buildJwt(jwtConfiguration, authc, UUID.randomUUID().toString());

		ResponseEntity<UserDetails> response = RestClient.forTarget("http://localhost:9999/api").request()
				.path("details").authorizationBearer(jwtToken).get(UserDetails.class);

		UserDetails userDetails = response.getPayload().orElse(null);

		Assert.assertNotNull(userDetails);
		Assert.assertEquals("testUser", userDetails.getUserId());
		Assert.assertTrue(userDetails.isRole1());
		Assert.assertEquals("Test", userDetails.getFirstName());
		Assert.assertEquals("User", userDetails.getLastName());
		Assert.assertEquals("test@holon-platform.com", userDetails.getEmail());

	}

	@Test
	public void testApiDocs() {

		// as JSON
		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/api/docs").request()
				.get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());
		Assert.assertTrue(response.getPayload().isPresent());
		Assert.assertEquals("application/json", response.getHeaderValue(HttpHeaders.CONTENT_TYPE).orElse(null));

		// as YAML
		response = RestClient.forTarget("http://localhost:9999/api/docs").request().queryParameter("type", "yaml")
				.get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());
		Assert.assertTrue(response.getPayload().isPresent());
		Assert.assertEquals("application/yaml", response.getHeaderValue(HttpHeaders.CONTENT_TYPE).orElse(null));
	}

	@Test
	public void testActuator() {
		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/info").request()
				.get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());
	}

}
