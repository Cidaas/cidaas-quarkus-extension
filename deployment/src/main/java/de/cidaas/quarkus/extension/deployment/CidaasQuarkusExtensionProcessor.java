package de.cidaas.quarkus.extension.deployment;

import de.cidaas.quarkus.extension.runtime.TokenIntrospectionResponseFilter;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;

class CidaasQuarkusExtensionProcessor {

    private static final String FEATURE = "cidaas-quarkus-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    ResteasyJaxrsProviderBuildItem registerProviders() {
        return new ResteasyJaxrsProviderBuildItem(TokenIntrospectionResponseFilter.class.getName());
    }
}
