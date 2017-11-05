# Using JWT for API authentication: external configuration

Example using external authorization roles configuration instead of the `javax.security.*` annotations.

# Path based authorization configuration

The roles required to access a resource path are specified in the [application.yml](application.yml) configuration file using the `example.authz.paths` property prefix.
