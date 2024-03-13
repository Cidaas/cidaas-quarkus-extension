package de.cidaas.quarkus.extension.runtime;

import de.cidaas.quarkus.extension.TokenIntrospectionRequest;

public interface IntrospectionService {
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest);
}
