package de.cidaas.quarkus.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TokenValidation {
	String tokenTypeHint() default "";

	String[] roles() default {};

	GroupAllowed[] groups() default {};

	String[] scopes() default {};

	boolean strictRoleValidation() default false;

	boolean strictGroupValidation() default false;

	boolean strictScopeValidation() default false;

	boolean strictValidation() default false;

	boolean offlineValidation() default false;
}
