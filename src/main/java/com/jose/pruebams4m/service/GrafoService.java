package com.jose.pruebams4m.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jose.pruebams4m.model.Grafo;
import com.jose.pruebams4m.model.Nodo;
import com.jose.pruebams4m.model.Tramo;
import com.jose.pruebams4m.Util.DistanciaUtil;

@Service
public class GrafoService {

    private final JsonDataService jsonDataService;

    private Grafo grafo;

    public GrafoService(JsonDataService jsonDataService) {
        this.jsonDataService = jsonDataService;
        construirGrafo();
    }

    private void construirGrafo() {

        grafo = new Grafo();

        List<Tramo> tramos = jsonDataService.getTramos();

        for (Tramo tramo : tramos) {

            if (tramo.getPoints() == null
                    || tramo.getPoints().size() < 2) {
                continue;
            }

            List<List<Double>> puntos = tramo.getPoints();

            for (int i = 0; i < puntos.size() - 1; i++) {

                List<Double> puntoA = puntos.get(i);
                List<Double> puntoB = puntos.get(i + 1);

                if (puntoA == null || puntoB == null
                        || puntoA.size() < 2
                        || puntoB.size() < 2) {
                    continue;
                }

                double latA = puntoA.get(0);
                double lonA = puntoA.get(1);

                double latB = puntoB.get(0);
                double lonB = puntoB.get(1);

                Nodo nodoA = crearNodo(latA, lonA);
                Nodo nodoB = crearNodo(latB, lonB);

                double distancia = DistanciaUtil.calcularDistancia(
                        latA,
                        lonA,
                        latB,
                        lonB
                );

                grafo.agregarConexion(
                        nodoA,
                        nodoB,
                        distancia,
                        tramo.getIdTrmCs()
                );

                grafo.agregarConexion(
                        nodoB,
                        nodoA,
                        distancia,
                        tramo.getIdTrmCs()
                );
            }
        }
    }

    private Nodo crearNodo(double latitud, double longitud) {

        String id = String.format(
                "%.6f_%.6f",
                latitud,
                longitud
        );

        return new Nodo(
                id,
                latitud,
                longitud
        );
    }

    public Grafo getGrafo() {
        return grafo;
    }
}