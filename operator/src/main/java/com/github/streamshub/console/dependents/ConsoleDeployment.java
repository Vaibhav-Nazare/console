package com.github.streamshub.console.dependents;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.github.streamshub.console.api.v1alpha1.Console;
import com.github.streamshub.console.api.v1alpha1.spec.Images;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@ApplicationScoped
@KubernetesDependent(
        labelSelector = ConsoleResource.MANAGEMENT_SELECTOR,
        resourceDiscriminator = ConsoleLabelDiscriminator.class)
public class ConsoleDeployment extends CRUDKubernetesDependentResource<Deployment, Console> implements ConsoleResource {

    public static final String NAME = "console-deployment";

    @Inject
    ConsoleServiceAccount serviceAccount;

    @Inject
    ConsoleSecret secret;

    @Inject
    @ConfigProperty(name = "console.deployment.default-api-image")
    String defaultAPIImage;

    @Inject
    @ConfigProperty(name = "console.deployment.default-ui-image")
    String defaultUIImage;

    public ConsoleDeployment() {
        super(Deployment.class);
    }

    @Override
    public String resourceName() {
        return NAME;
    }

    @Override
    protected Deployment desired(Console primary, Context<Console> context) {
        Deployment desired = load(context, "console.deployment.yaml", Deployment.class);
        String name = instanceName(primary);
        String configSecretName = secret.instanceName(primary);

        var imagesSpec = Optional.ofNullable(primary.getSpec().getImages());
        String imageAPI = imagesSpec.map(Images::getApi).orElse(defaultAPIImage);
        String imageUI = imagesSpec.map(Images::getUi).orElse(defaultUIImage);

        return desired.edit()
            .editMetadata()
                .withName(name)
                .withNamespace(primary.getMetadata().getNamespace())
                .withLabels(commonLabels("console"))
            .endMetadata()
            .editSpec()
                .editSelector()
                    .withMatchLabels(Map.of(INSTANCE_LABEL, name))
                .endSelector()
                .editTemplate()
                    .editMetadata()
                        .addToLabels(Map.of(INSTANCE_LABEL, name))
                        .addToAnnotations(
                                "streamshub.github.com/dependency-digest",
                                serializeDigest(context, "console-digest"))
                    .endMetadata()
                    .editSpec()
                        .withServiceAccountName(serviceAccount.instanceName(primary))
                        .editMatchingVolume(vol -> "config".equals(vol.getName()))
                            .editSecret()
                                .withSecretName(configSecretName)
                            .endSecret()
                        .endVolume()
                        .editMatchingContainer(c -> "console-api".equals(c.getName()))
                            .withImage(imageAPI)
                            .addAllToEnv(coalesce(primary.getSpec().getEnv(), Collections::emptyList))
                        .endContainer()
                        .editMatchingContainer(c -> "console-ui".equals(c.getName()))
                            .withImage(imageUI)
                            .editMatchingEnv(env -> "NEXTAUTH_URL".equals(env.getName()))
                                .withValue(getAttribute(context, ConsoleIngress.NAME + ".url", String.class))
                            .endEnv()
                            .editMatchingEnv(env -> "NEXTAUTH_SECRET".equals(env.getName()))
                                .editValueFrom()
                                    .editSecretKeyRef()
                                        .withName(configSecretName)
                                    .endSecretKeyRef()
                                .endValueFrom()
                            .endEnv()
                        .endContainer()
                    .endSpec()
                .endTemplate()
            .endSpec()
            .build();
    }
}
