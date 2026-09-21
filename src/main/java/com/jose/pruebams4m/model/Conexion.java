package com.jose.pruebams4m.model;

public class Conexion {

    private final Nodo destino;
    private final double distancia;
    private final Integer idTramo;

    public Conexion(
            Nodo destino,
            double distancia,
            Integer idTramo) {

        this.destino = destino;
        this.distancia = distancia;
        this.idTramo = idTramo;
    }

    public Nodo getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public Integer getIdTramo() {
        return idTramo;
    }
}