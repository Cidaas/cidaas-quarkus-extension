package de.cidaas.quarkus.extension.deployment;

import de.cidaas.quarkus.extension.runtime.CidaasService;
import de.cidaas.quarkus.extension.runtime.TokenIntrospectionResponseFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerResponseFilterBuildItem;

class CidaasQuarkusExtensionProcessor {

    private static final String FEATURE = "cidaas-quarkus-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    CustomContainerResponseFilterBuildItem registerProviders() {
        return new CustomContainerResponseFilterBuildItem(TokenIntrospectionResponseFilter.class.getName());
    }
    
    @BuildStep
    AdditionalBeanBuildItem registerCidaasService() {
        return new AdditionalBeanBuildItem(CidaasService.class);
    }
}
