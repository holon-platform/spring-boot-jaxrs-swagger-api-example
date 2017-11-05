# Using JWT for API authentication: external configuration

Example using external authorization roles configuration instead of the `javax.security.*` annotations.

# Path based authorization configuration

The roles required to access a resource path are specified in the [application.yml](src/main/resources/application.yml) configuration file using the `example.authz.paths` property prefix.

The path-role map is loaded into the [PathRolesConfiguration](src/main/java/com/holonplatform/example/authz/PathRolesConfiguration.java) bean using the Spring Boot `@ConfigurationProperties` annotation.
