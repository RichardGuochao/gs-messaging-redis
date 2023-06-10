package com.example.controller;

import com.example.connection.EnhacedHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class TestController {

    @Autowired
    EnhacedHttpClient enhacedHttpClient;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        CompletableFuture<List<String>> relationshipFuture = CompletableFuture.supplyAsync(this::getRelationshipList);
        relationshipFuture.thenCompose(this::getAccountList);

        return null;

    }

    private List<String> getRelationshipList() {
        return enhacedHttpClient.get("/relationship", ArrayList.class);
    }

    private List<String> getAccountList(List<String> relationship) {
        return enhacedHttpClient.get("/accounts", ArrayList.class);
    }

    private List<String> getFinancial() {
        return enhacedHttpClient.get("/financial", ArrayList.class);
    }
}
