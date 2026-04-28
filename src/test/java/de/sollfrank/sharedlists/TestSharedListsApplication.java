package de.sollfrank.sharedlists;

import org.springframework.boot.SpringApplication;

public class TestSharedListsApplication {

    public static void main(String[] args) {
        SpringApplication.from(SharedListsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
