package com.jose.pruebams4m.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jose.pruebams4m.model.Tramo;

@Service
public class RutaService {

    private final JsonDataService jsonDataService;

    public RutaService(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    public List<Tramo> obtenerTramos() {
        return jsonDataService.getTramos();
    }
    
}