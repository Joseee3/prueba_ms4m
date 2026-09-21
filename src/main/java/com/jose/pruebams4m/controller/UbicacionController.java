package com.jose.pruebams4m.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.pruebams4m.model.Ubicacion;
import com.jose.pruebams4m.service.JsonDataService;

@RestController
@RequestMapping("/api")
public class UbicacionController {

    private final JsonDataService jsonDataService;

    public UbicacionController(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
    }

    @GetMapping("/cargas")
    public List<Ubicacion> obtenerCargas() {
        return jsonDataService.getCargas();
    }

    @GetMapping("/descargas")
    public List<Ubicacion> obtenerDescargas() {
        return jsonDataService.getDescargas();
    }
    
    @GetMapping("/ubicaciones")
    public List<Ubicacion> obtenerUbicaciones() {

        List<Ubicacion> ubicaciones = new java.util.ArrayList<>();

        ubicaciones.addAll(jsonDataService.getCargas());
        ubicaciones.addAll(jsonDataService.getDescargas());

        return ubicaciones;
    }
}