# Cidaas Quarkus Extension

Quarkus Extension to integrate cidaas seamlessly to Java Project, which used Quarkus Framework.

## Installation

From Maven pom.xml, add the following dependency:

```java
<dependency>
    <groupId>de.cidaas</groupId>
    <artifactId>cidaas-quarkus-extension</artifactId>
    <version>{EXTENSION_VERSION}</version>
</dependency>
```

## Initialisation

After adding extension dependency, add the following line to application.properties file:

```java
de.cidaas.quarkus.extension.CidaasClient/mp-rest/url=<your_cidaas_base_url>
```

It will ensure a correct api url to be called for token verification.

## Usage

### Token verification by using introspection endpoint

To do token verification by using cidaas introspection endpoint, you will need to add the following annotation to your function.

| Annotation    | Description                                          |
|---------------|------------------------------------------------------|
| RolesAllowed | List of roles to be checked during token validation |
| GroupsAllowed | List of groups to be checked during token validation. Each of the group in annotation have the following format: GROUP_ID:GROUP_ROLE |
| ScopesAllowed | List of scopes to be checked during token validation |

An Example of a function being secured with cidaas quarkus extension looks like this:

```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@RolesAllowed({
    "SECONDARY_ADMIN",
    "USER"
})
@GroupsAllowed({
    "CIDAAS_ADMINS:SECONDARY_ADMIN",
    "CIDAAS_USERS:USER"
})
@ScopesAllowed({
    "profile",
    "cidaas:menu_read"
})
public String helloProtected() {
    return "Hello from protected api";
}
```