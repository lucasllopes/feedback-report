package br.com.fiap.lambda.producer;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class RdsDataClientProducer {

    @Produces
    @Singleton
    public RdsDataClient rdsDataClient() {

        return RdsDataClient.builder().build();
    }
}
