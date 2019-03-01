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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	@Test
	public void testPing() {

		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/api").request().path("ping")
				.get(String.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatus());
		Assert.assertEquals("pong", response.as(String.class).orElse(null));
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
		ResponseEntity<String> response = RestClient.forTarget("http://localhost:9999/actuator/info").request()
				.get(String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatus());
	}

}
