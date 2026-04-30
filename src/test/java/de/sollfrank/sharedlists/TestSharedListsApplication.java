package de.sollfrank.sharedlists;

import de.sollfrank.sharedlists.util.KeycloakEnvPostProcessor;
import de.sollfrank.sharedlists.util.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestSharedListsApplication {

    public static void main(String[] args) {
        SpringApplication.from(SharedListsApplication::main)
                .with(TestcontainersConfiguration.class, KeycloakEnvPostProcessor.class)
                .run(args);
    }

}
