package com.holonplatform.example.authz;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Provider
@Component
public class PathBasedAuthorizationDynamicFeature implements DynamicFeature {

	@Autowired
	private PathRolesConfiguration pathRolesConfiguration;

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext configuration) {
		
		// get path-roles map
		final Map<String, String> pathRoles = pathRolesConfiguration.getPaths();
		
		// resource class
		String basePath = getPath(resourceInfo.getResourceClass()).orElse("");

		// resource method
		String path = basePath + getPath(resourceInfo.getResourceMethod()).orElse("");

		// check method authz
		if (pathRoles.containsKey(path)) {
			configuration.register(new RolesAllowedRequestFilter(pathRoles.get(path)));
			return;
		}

		// check class authz
		if (pathRoles.containsKey(basePath)) {
			configuration.register(new RolesAllowedRequestFilter(pathRoles.get(basePath)));
		}
	}

	private static Optional<String> getPath(AnnotatedElement ae) {
		if (ae.isAnnotationPresent(Path.class)) {
			return Optional.of(ae.getAnnotation(Path.class).value());
		}
		return Optional.empty();
	}

	@Priority(Priorities.AUTHORIZATION) // authorization filter - should go after any authentication filters
	private static class RolesAllowedRequestFilter implements ContainerRequestFilter {

		private final String[] rolesAllowed;

		RolesAllowedRequestFilter(final String... rolesAllowed) {
			this.rolesAllowed = (rolesAllowed != null) ? rolesAllowed : new String[] {};
		}

		@Override
		public void filter(final ContainerRequestContext requestContext) throws IOException {
			if (rolesAllowed.length > 0 && !isAuthenticated(requestContext)) {
				throw new ForbiddenException("Not authorized");
			}

			for (final String role : rolesAllowed) {
				if (requestContext.getSecurityContext().isUserInRole(role)) {
					return;
				}
			}
			
			throw new ForbiddenException("Not authorized");
		}

		private static boolean isAuthenticated(final ContainerRequestContext requestContext) {
			return requestContext.getSecurityContext().getUserPrincipal() != null;
		}
	}

}
