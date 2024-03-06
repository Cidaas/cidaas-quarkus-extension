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

To do token verification by using cidaas introspection endpoint, you will need to add the @TokenValidation annotation to your function. The annotation support the following optional members:

| Name                  | Description                                                                                                                                                                                                         | Default Value |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| roles                 | List of roles which are allowed to access secured api                                                                                                                                                               | Empty Array   |
| groups                | List of groups which are allowed to access secured api                                                                                                                                                              | Empty Array   |
| scopes                | List of scopes which are allowed to access secured api                                                                                                                                                              | Empty Array   |
| strictRoleValidation  | If true, user will need all roles from the list to access api. By default, user only need 1 of the roles                                                                                                            | false         |
| strictGroupValidation | If true, user will need all groups from the list to access api. By default, user only need 1 of the groups                                                                                                          | false         |
| strictScopeValidation | If true, user will need all scopes from the list to access api. By default, user only need 1 of the scopes                                                                                                          | false         |
| strictValidation      | If true, user will need to have each of defined validation (roles, groups and/or scopes). E.g. valid roles & valid scopes. By default, user will be able to access api only with 1 validation e.g. valid roles only | false         |
| tokenTypeHint         | described which type of token is currently being validated. e.g. access_token                                                                                                                                       | Empty String  |

to validate groups, @GroupAllowed Annotation(s) have to be added. It has the following member:

| Name                 | Description                                                                                                          | is required                |
|----------------------|----------------------------------------------------------------------------------------------------------------------|----------------------------|
| id                   | group id                                                                                                             | yes                        |
| roles                | List of group roles, which are allowed to access secured api                                                         | yes                        |
| strictRoleValidation | If true, user will need all roles from the group roles list to access api. By default, user only need 1 of the roles | no, default value is false |

Examples of  function being secured with cidaas quarkus extension looks like the following:

* User need only to be authenticated, without having any roles, groups or scopes to access the api
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation
public String helloProtected() {
    return "Hello from protected api";
}
```

* User need to have one of the "role1" or "role2" role to access the api
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation(roles = {"role1", "role2"})
public String helloProtected() {
    return "Hello from protected api";
}
```

* User need to either have one of the "role1" or "role2" role to access the api, or both "scope1" and "scope2" scopes
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation(
    roles = {"role1", "role2"},
    scopes = { "scope1", "scope2" },
    strictScopeValidation = true
)
public String helloProtected() {
    return "Hello from protected api";
}
```

* User need to have every roles and scopes to access the api
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation(
    roles = {"role1", "role2"},
    scopes = { "scope1", "scope2" },
    strictValidation = true,
    strictRoleValidation = true,
    strictScopeValidation = true
)
public String helloProtected() {
    return "Hello from protected api";
}
```

* User need to be either in the group with roles, or have one of the scopes to access the api
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation(
    groups = {
        @GroupAllowed(id="groupId", roles = { "groupRole" }),
    },
    scopes = { "scope1", "scope2" },
)
public String helloProtected() {
    return "Hello from protected api";
}
```

* User need to be both in the group1 with one of the  "groupRole1" or "groupRole2" role, and in the group2 with both "groupRole3" and "groupRole4" roles to access the api
```java
@GET
@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
@TokenValidation(
    groups = {
        @GroupAllowed(
            id="group1", 
            roles = { "groupRole1", "groupRole2" }
        ),
        @GroupAllowed(
            id="group2", 
            roles = { "groupRole3", "groupRole4" },
            strictRoleValidation=true 
        ),
    },
    strictGroupValidation = true,
)
public String helloProtected() {
    return "Hello from protected api";
}
```