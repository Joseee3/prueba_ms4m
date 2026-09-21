package com.jose.pruebams4m.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.pruebams4m.model.Tramo;
import com.jose.pruebams4m.service.JsonDataService;

@RestController
@RequestMapping("/api/tramos")
public class TramoController {

    private final JsonDataService jsonDataService;

    public TramoController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @GetMapping
    public List<Tramo> obtenerTramos() {
        return jsonDataService.getTramos();
    }
}