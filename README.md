# Using JWT for API authentication: external configuration

Example using external authorization roles configuration instead of the `javax.security.*` annotations.

# Path based authorization configuration

The roles required to access a resource path are specified in the [application.yml](src/main/resources/application.yml) configuration file using the `example.authz.paths` property prefix.

The path-role map is loaded into the [PathRolesConfiguration](src/main/java/com/holonplatform/example/authz/PathRolesConfiguration.java) bean using the Spring Boot `@ConfigurationProperties` annotation.

# Authorization filter

The [PathBasedAuthorizationDynamicFeature](src/main/java/com/holonplatform/example/authz/PathBasedAuthorizationDynamicFeature.java) is the JAX-RS dynamic feature which uses the `PathRolesConfiguration` to check if role-based authorization control has to added to a JAX-RS resource class or method, relying on the resource path.

This feature is automatically registered in Jersey by the Holon Platform, since it is declared as a Spring component and as a JAX-RS _provider_.
