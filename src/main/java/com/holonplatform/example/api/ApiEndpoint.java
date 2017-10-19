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
package com.holonplatform.example.api;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.springframework.stereotype.Component;

import com.holonplatform.auth.Authentication;
import com.holonplatform.auth.Realm;
import com.holonplatform.auth.annotations.Authenticate;
import com.holonplatform.example.data.UserDetails;
import com.holonplatform.jaxrs.swagger.annotations.ApiDefinition;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Authenticate // require authentication
@ApiDefinition(docsPath = "/api/docs", title = "Example API", version = "v1", prettyPrint = true)
@Api("Test API")
@Component
@Path("/api")
public class ApiEndpoint {

	@Inject
	private Realm realm;

	@PermitAll // no specific role required
	@ApiOperation("Ping request")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK: pong", response = String.class) })
	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.ok("pong").build();
	}

	@RolesAllowed("ROLE1") // ROLE1 is required
	@ApiOperation("Get protected resource")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
	@GET
	@Path("/protected")
	@Produces(MediaType.TEXT_PLAIN)
	public Response protectedOperation() {
		return Response.ok("protected").build();
	}

	@RolesAllowed("ROLE2") // ROLE2 is required
	@ApiOperation("Get user name")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
	@GET
	@Path("/user")
	@Produces(MediaType.TEXT_PLAIN)
	public Response userOperation(@Context SecurityContext securityContext) {

		// get user principal name from JAX-RS SecurityContext
		String principalName = securityContext.getUserPrincipal().getName();

		return Response.ok(principalName).build();
	}

	@RolesAllowed("ROLE2") // ROLE2 is required
	@ApiOperation("Get user details")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
	@GET
	@Path("/details")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDetails userDetails(@Context SecurityContext securityContext) {

		// the Holon platform Authentication is available as the JAX-RS SecurityContext user principal
		Authentication auth = (Authentication) securityContext.getUserPrincipal();

		// use Realm to perform authorization checks
		boolean hasRole1 = realm.isPermitted(auth, "ROLE1");

		UserDetails userDetails = new UserDetails();
		userDetails.setUserId(auth.getName());
		userDetails.setRole1(hasRole1);
		auth.getParameter("firstName", String.class).ifPresent(p -> userDetails.setFirstName(p));
		auth.getParameter("lastName", String.class).ifPresent(p -> userDetails.setLastName(p));
		auth.getParameter("email", String.class).ifPresent(p -> userDetails.setEmail(p));

		return userDetails;
	}

}
