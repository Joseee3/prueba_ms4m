package com.jose.pruebams4m.model;

public class ReporteCamion {

    private String id;
    private int muestras;
    private double velocidadMinima;
    private double velocidadMaxima;
    private double velocidadPromedio;
    private int muestrasMenoresA23;
    private String explicacion;

    public ReporteCamion() {
    }

    public ReporteCamion(
            String id,
            int muestras,
            double velocidadMinima,
            double velocidadMaxima,
            double velocidadPromedio,
            int muestrasMenoresA23,
            String explicacion) {

        this.id = id;
        this.muestras = muestras;
        this.velocidadMinima = velocidadMinima;
        this.velocidadMaxima = velocidadMaxima;
        this.velocidadPromedio = velocidadPromedio;
        this.muestrasMenoresA23 = muestrasMenoresA23;
        this.explicacion = explicacion;
    }

    public String getId() {
        return id;
    }

    public int getMuestras() {
        return muestras;
    }

    public double getVelocidadMinima() {
        return velocidadMinima;
    }

    public double getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public double getVelocidadPromedio() {
        return velocidadPromedio;
    }

    public int getMuestrasMenoresA23() {
        return muestrasMenoresA23;
    }

    public String getExplicacion() {
        return explicacion;
    }
}