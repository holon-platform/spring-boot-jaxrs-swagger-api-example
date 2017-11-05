package com.holonplatform.example.api;

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
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.OAuth2Definition;
import io.swagger.annotations.OAuth2Definition.Flow;
import io.swagger.annotations.Scope;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;

@Authenticate // require authentication
@ApiDefinition(docsPath = "/api/docs", title = "Example API", version = "v1", prettyPrint = true)
@SwaggerDefinition(securityDefinition = @SecurityDefinition(oAuth2Definitions = @OAuth2Definition(key = "jwt-auth", description = "JWT Bearer token", flow = Flow.IMPLICIT, authorizationUrl = "https://example.org/api/oauth2", scopes = {
		@Scope(name = "ROLE1", description = "Test role 1"),
		@Scope(name = "ROLE2", description = "Test role 2") })))
@Api(value = "Test API", authorizations = @Authorization("jwt-auth"))
@Component
@Path("/api")
public class ApiEndpoint {

	@Inject
	private Realm realm;

	@ApiOperation("Ping request")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK: pong", response = String.class) })
	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.ok("pong").build();
	}

	@ApiOperation(value = "Get protected resource", authorizations = @Authorization(value = "jwt-auth", scopes = @AuthorizationScope(scope = "ROLE1", description = "")))
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
	@GET
	@Path("/protected")
	@Produces(MediaType.TEXT_PLAIN)
	public Response protectedOperation() {
		return Response.ok("protected").build();
	}

	@ApiOperation(value = "Get user name", authorizations = @Authorization(value = "jwt-auth", scopes = @AuthorizationScope(scope = "ROLE2", description = "")))
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
	@GET
	@Path("/user")
	@Produces(MediaType.TEXT_PLAIN)
	public Response userOperation(@Context SecurityContext securityContext) {

		// get user principal name from JAX-RS SecurityContext
		String principalName = securityContext.getUserPrincipal().getName();

		return Response.ok(principalName).build();
	}

	@ApiOperation(value = "Get user details", authorizations = @Authorization(value = "jwt-auth", scopes = @AuthorizationScope(scope = "ROLE2", description = "")))
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = UserDetails.class) })
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
