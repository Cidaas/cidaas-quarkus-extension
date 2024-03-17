package de.cidaas.quarkus.extension.deployment;

import de.cidaas.quarkus.extension.AddressValidationRequest;
import de.cidaas.quarkus.extension.AddressValidationResult;
import de.cidaas.quarkus.extension.GroupAllowed;
import de.cidaas.quarkus.extension.TokenValidation;
import de.cidaas.quarkus.extension.runtime.AddressValidationService;
import de.cidaas.quarkus.extension.runtime.AuthFilter;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomContainerRequestFilterBuildItem;

class CidaasQuarkusExtensionProcessor {

    private static final String FEATURE = "cidaas-quarkus-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
    CustomContainerRequestFilterBuildItem registerAuthFilter() {
        return new CustomContainerRequestFilterBuildItem(AuthFilter.class.getName());
    }
    
    @BuildStep
    AdditionalBeanBuildItem registerAdditonalBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(TokenValidation.class, GroupAllowed.class, AddressValidationService.class, 
                					AddressValidationRequest.class, AddressValidationResult.class)
                .build();
    }
}
