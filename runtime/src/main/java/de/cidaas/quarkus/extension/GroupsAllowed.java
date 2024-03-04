package de.cidaas.quarkus.extension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GroupsAllowed {
	GroupAllowed[] value();
}
