package com.jose.pruebams4m.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tramo {

    @JsonProperty("id_trm_cs")
    private Integer idTrmCs;

    @JsonProperty("nombre_tramo")
    private String nombreTramo;

    private String color;

    private List<List<Double>> points;

    public Tramo() {
    }

    public Tramo(Integer idTrmCs, String nombreTramo,
                 String color, List<List<Double>> points) {
        this.idTrmCs = idTrmCs;
        this.nombreTramo = nombreTramo;
        this.color = color;
        this.points = points;
    }

    public Integer getIdTrmCs() {
        return idTrmCs;
    }

    public void setIdTrmCs(Integer idTrmCs) {
        this.idTrmCs = idTrmCs;
    }

    public String getNombreTramo() {
        return nombreTramo;
    }

    public void setNombreTramo(String nombreTramo) {
        this.nombreTramo = nombreTramo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<List<Double>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Double>> points) {
        this.points = points;
    }
    
    public Punto getPuntoInicial() {

        if (points == null || points.isEmpty()) {
            return null;
        }

        List<Double> coordenadas = points.get(0);

        return new Punto(
                coordenadas.get(0),
                coordenadas.get(1)
        );
    }

    public Punto getPuntoFinal() {

        if (points == null || points.isEmpty()) {
            return null;
        }

        List<Double> coordenadas = points.get(points.size() - 1);

        return new Punto(
                coordenadas.get(0),
                coordenadas.get(1)
        );
    }
}