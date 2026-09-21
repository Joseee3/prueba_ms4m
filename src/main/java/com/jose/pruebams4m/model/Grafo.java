package com.jose.pruebams4m.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafo {

    private final Map<String, Nodo> nodos = new HashMap<>();

    private final Map<String, List<Conexion>> conexiones = new HashMap<>();

    public void agregarNodo(Nodo nodo) {

        nodos.putIfAbsent(nodo.getId(), nodo);

        conexiones.putIfAbsent(
                nodo.getId(),
                new ArrayList<>()
        );
    }

    public void agregarConexion(
            Nodo origen,
            Nodo destino,
            double distancia,
            Integer idTramo) {

        agregarNodo(origen);
        agregarNodo(destino);

        conexiones.get(origen.getId()).add(
                new Conexion(
                        destino,
                        distancia,
                        idTramo
                )
        );
    }

    public Nodo obtenerNodo(String id) {
        return nodos.get(id);
    }

    public List<Conexion> obtenerConexiones(Nodo nodo) {

        return conexiones.getOrDefault(
                nodo.getId(),
                Collections.emptyList()
        );
    }

    public Collection<Nodo> obtenerNodos() {
        return nodos.values();
    }

    public int cantidadNodos() {
        return nodos.size();
    }

    public int cantidadConexiones() {

        return conexiones.values()
                .stream()
                .mapToInt(List::size)
                .sum();
    }
}