package de.cidaas.quarkus.extension;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(GroupsAllowed.class)
public @interface GroupAllowed {
	String id();
    String[] roles();
    boolean strictRolesValidation() default false;
}
