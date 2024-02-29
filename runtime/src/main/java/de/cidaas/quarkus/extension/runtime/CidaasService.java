package de.cidaas.quarkus.extension.runtime;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CidaasService {
	public boolean introspectToken() {
        return false;
	}
}
