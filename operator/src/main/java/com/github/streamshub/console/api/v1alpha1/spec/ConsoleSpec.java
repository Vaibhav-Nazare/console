package com.github.streamshub.console.api.v1alpha1.spec;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.streamshub.console.api.v1alpha1.spec.metrics.MetricsSource;

import io.fabric8.generator.annotation.Required;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.sundr.builder.annotations.Buildable;

@Buildable(builderPackage = "io.fabric8.kubernetes.api.builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
// Enable validation rules for unique names when array maxItems and string maxLength can be specified
// to influence Kubernetes's estimated rule cost.
// https://github.com/fabric8io/kubernetes-client/pull/6447
//
// @ValidationRule(value = """
//         !has(self.metricsSources) ||
//           self.metricsSources.all(s1, self.metricsSources.exists_one(s2, s2.name == s1.name))
//         """,
//         message = "Metrics source names must be unique")
public class ConsoleSpec {

    @Required
    String hostname;

    Images images;

    List<MetricsSource> metricsSources;

    List<SchemaRegistry> schemaRegistries;

    List<KafkaCluster> kafkaClusters = new ArrayList<>();

    List<EnvVar> env;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<MetricsSource> getMetricsSources() {
        return metricsSources;
    }

    public void setMetricsSources(List<MetricsSource> metricsSources) {
        this.metricsSources = metricsSources;
    }

    public List<SchemaRegistry> getSchemaRegistries() {
        return schemaRegistries;
    }

    public void setSchemaRegistries(List<SchemaRegistry> schemaRegistries) {
        this.schemaRegistries = schemaRegistries;
    }

    public List<KafkaCluster> getKafkaClusters() {
        return kafkaClusters;
    }

    public void setKafkaClusters(List<KafkaCluster> kafkaClusters) {
        this.kafkaClusters = kafkaClusters;
    }

    public List<EnvVar> getEnv() {
        return env;
    }

    public void setEnv(List<EnvVar> env) {
        this.env = env;
    }
}
