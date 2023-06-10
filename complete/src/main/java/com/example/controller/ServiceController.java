package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ServiceController {

    @GetMapping("/relationship")
    public List<String> getRelationshipList() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String>  result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            result.add("relationship" + i);
        }
        return new ArrayList<>();
    }

    @GetMapping("/accounts")
    public List<String> getAccountList() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String>  result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            result.add("account" + i);
        }
        return new ArrayList<>();
    }

    @GetMapping("/financial")
    public List<String> getFinancialServices() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String>  result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            result.add("financial" + i);
        }
        return new ArrayList<>();
    }
}
