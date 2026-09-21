package com.jose.pruebams4m.model;

import java.util.ArrayList;
import java.util.List;

public class Camion {

    private String id;
    private Ubicacion carga;
    private Ubicacion descarga;
    private List<Nodo> ruta;

    private double velocidadKmh;
    private double distanciaRecorridaKm;
    private String estado;

    private Punto posicionActual;
    private long timestamp;

    private int indiceSegmento;
    private double distanciaEnSegmentoKm;
    
    private boolean rutaAlternativa;
    private String motivoAsignacion;

    private List<Double> muestrasVelocidad;

    public Camion() {
    }

    public Camion(
            String id,
            Ubicacion carga,
            Ubicacion descarga,
            List<Nodo> ruta,
            double velocidadKmh) {

        this.id = id;
        this.carga = carga;
        this.descarga = descarga;
        this.ruta = ruta;

        this.velocidadKmh = velocidadKmh;
        this.distanciaRecorridaKm = 0.0;
        this.estado = "EN_RUTA";

        this.indiceSegmento = 0;
        this.distanciaEnSegmentoKm = 0.0;

        this.muestrasVelocidad = new ArrayList<>();

        if (ruta != null && !ruta.isEmpty()) {
            Nodo inicio = ruta.get(0);

            this.posicionActual = new Punto(
                    inicio.getLatitud(),
                    inicio.getLongitud()
            );
        }

        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public Ubicacion getCarga() {
        return carga;
    }

    public Ubicacion getDescarga() {
        return descarga;
    }

    public List<Nodo> getRuta() {
        return ruta;
    }

    public double getVelocidadKmh() {
        return velocidadKmh;
    }

    public void setVelocidadKmh(double velocidadKmh) {
        this.velocidadKmh = velocidadKmh;
    }

    public double getDistanciaRecorridaKm() {
        return distanciaRecorridaKm;
    }

    public void setDistanciaRecorridaKm(double distanciaRecorridaKm) {
        this.distanciaRecorridaKm = distanciaRecorridaKm;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Punto getPosicionActual() {
        return posicionActual;
    }

    public void setPosicionActual(Punto posicionActual) {
        this.posicionActual = posicionActual;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIndiceSegmento() {
        return indiceSegmento;
    }

    public void setIndiceSegmento(int indiceSegmento) {
        this.indiceSegmento = indiceSegmento;
    }

    public double getDistanciaEnSegmentoKm() {
        return distanciaEnSegmentoKm;
    }

    public void setDistanciaEnSegmentoKm(double distanciaEnSegmentoKm) {
        this.distanciaEnSegmentoKm = distanciaEnSegmentoKm;
    }

    public List<Double> getMuestrasVelocidad() {
        return muestrasVelocidad;
    }
    
    public boolean isRutaAlternativa() {
        return rutaAlternativa;
    }

    public void setRutaAlternativa(boolean rutaAlternativa) {
        this.rutaAlternativa = rutaAlternativa;
    }

    public String getMotivoAsignacion() {
        return motivoAsignacion;
    }

    public void setMotivoAsignacion(String motivoAsignacion) {
        this.motivoAsignacion = motivoAsignacion;
    }
}