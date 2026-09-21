package com.jose.pruebams4m.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.pruebams4m.model.Camion;
import com.jose.pruebams4m.model.ReporteCamion;
import com.jose.pruebams4m.service.SimulacionService;

@RestController
@RequestMapping("/api/simulacion")
public class SimulacionController {

    private final SimulacionService simulacionService;

    public SimulacionController(
            SimulacionService simulacionService) {

        this.simulacionService = simulacionService;
    }

    @PostMapping("/iniciar")
    public List<Camion> iniciar() {
        return simulacionService.iniciarSimulacion();
    }

    @GetMapping
    public List<Camion> estado() {
        return simulacionService.obtenerCamiones();
    }
    
    @GetMapping("/reporte")
    public List<ReporteCamion> reporte() {
        return simulacionService.obtenerReporte();
    }
}