package com.jose.pruebams4m.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jose.pruebams4m.model.Camion;
import com.jose.pruebams4m.model.Nodo;
import com.jose.pruebams4m.model.Punto;
import com.jose.pruebams4m.model.ReporteCamion;
import com.jose.pruebams4m.model.Ubicacion;
import com.jose.pruebams4m.Util.DistanciaUtil;

import org.springframework.beans.factory.annotation.Value;

@Service
public class SimulacionService {

	@Value("${simulation.seed:4367}")
	private long semilla;

	@Value("${simulation.speed.min:18}")
	private double velocidadMinima;

	@Value("${simulation.speed.max:48}")
	private double velocidadMaxima;

    private final JsonDataService jsonDataService;
    private final DijkstraService dijkstraService;

    private List<Camion> camiones = new ArrayList<>();

    private Random random = new Random(semilla);

    public SimulacionService(
            JsonDataService jsonDataService,
            DijkstraService dijkstraService) {

        this.jsonDataService = jsonDataService;
        this.dijkstraService = dijkstraService;
    }

    public synchronized List<Camion> iniciarSimulacion() {

        random = new Random(semilla);

        List<Ubicacion> cargas =
                jsonDataService.getCargas();

        List<Ubicacion> descargas =
                jsonDataService.getDescargas();

        if (cargas == null || cargas.isEmpty()) {
            throw new IllegalStateException(
                    "No existen ubicaciones de carga disponibles."
            );
        }

        if (descargas == null || descargas.isEmpty()) {
            throw new IllegalStateException(
                    "No existen ubicaciones de descarga disponibles."
            );
        }

        List<Camion> nuevosCamiones = new ArrayList<>();

        int indiceCarga = 0;

        for (int i = 1; i <= 5; i++) {

            boolean rutaEncontrada = false;

            for (int c = 0;
                 c < cargas.size() && !rutaEncontrada;
                 c++) {

                Ubicacion carga =
                        cargas.get(
                                (indiceCarga + c)
                                % cargas.size()
                        );

                for (int d = 0;
                     d < descargas.size() && !rutaEncontrada;
                     d++) {

                    Ubicacion descarga =
                            descargas.get(d);

                    List<Nodo> ruta =
                            dijkstraService.calcularRuta(
                                    carga,
                                    descarga
                            );

                    if (!ruta.isEmpty()) {

                    	double velocidad =
                    	        generarVelocidad();

                    	Camion camion =
                    	        new Camion(
                    	                String.format(
                    	                        "IDS26-D3R6-%03d",
                    	                        i
                    	                ),
                    	                carga,
                    	                descarga,
                    	                ruta,
                    	                velocidad
                    	        );

                    	boolean alternativa = (c > 0 || d > 0);

                    	camion.setRutaAlternativa(alternativa);

                    	if (alternativa) {
                    	    camion.setMotivoAsignacion(
                    	            "La combinación inicialmente evaluada "
                    	            + "no tenía conexión. Se seleccionó otra "
                    	            + "combinación con ruta disponible."
                    	    );
                    	} else {
                    	    camion.setMotivoAsignacion(
                    	            "Se encontró una ruta válida para la "
                    	            + "combinación evaluada."
                    	    );
                    	}

                    	camion.getMuestrasVelocidad()
                    	        .add(velocidad);

                    	nuevosCamiones.add(camion);

                    	rutaEncontrada = true;

                        indiceCarga =
                                (indiceCarga + c + 1)
                                % cargas.size();
                    }
                }
            }

            if (!rutaEncontrada) {

                throw new IllegalStateException(
                        "No fue posible encontrar una ruta válida "
                        + "para el camión IDS26-D3R6-"
                        + String.format("%03d", i)
                );
            }
        }

        camiones = nuevosCamiones;

        return camiones;
    }

    private double generarVelocidad() {
        return velocidadMinima
                + random.nextDouble()
                * (velocidadMaxima - velocidadMinima);
    }

    @Scheduled(fixedRate = 2500)
    public synchronized void actualizarSimulacion() {

        if (camiones.isEmpty()) {
            return;
        }

        for (Camion camion : camiones) {

            if ("FINALIZADO".equals(camion.getEstado())) {
                continue;
            }

            double velocidad =
                    generarVelocidad();

            camion.setVelocidadKmh(velocidad);

            camion.getMuestrasVelocidad()
                    .add(velocidad);

            double distanciaAvanzar =
                    velocidad * 2.5 / 3600.0;

            avanzarCamion(
                    camion,
                    distanciaAvanzar
            );

            camion.setTimestamp(
                    System.currentTimeMillis()
            );
        }
    }

    private void avanzarCamion(
            Camion camion,
            double distanciaAvanzarKm) {

        List<Nodo> ruta =
                camion.getRuta();

        while (
                distanciaAvanzarKm > 0
                && camion.getIndiceSegmento()
                    < ruta.size() - 1) {

            int indice =
                    camion.getIndiceSegmento();

            Nodo actual =
                    ruta.get(indice);

            Nodo siguiente =
                    ruta.get(indice + 1);

            double distanciaSegmento =
                    DistanciaUtil.calcularDistancia(
                            actual.getLatitud(),
                            actual.getLongitud(),
                            siguiente.getLatitud(),
                            siguiente.getLongitud()
                    );

            double restante =
                    distanciaSegmento
                    - camion.getDistanciaEnSegmentoKm();

            if (distanciaAvanzarKm < restante) {

                double nuevaDistancia =
                        camion.getDistanciaEnSegmentoKm()
                        + distanciaAvanzarKm;

                double proporcion =
                        nuevaDistancia
                        / distanciaSegmento;

                double latitud =
                        actual.getLatitud()
                        + (siguiente.getLatitud()
                        - actual.getLatitud())
                        * proporcion;

                double longitud =
                        actual.getLongitud()
                        + (siguiente.getLongitud()
                        - actual.getLongitud())
                        * proporcion;

                camion.setPosicionActual(
                        new Punto(
                                latitud,
                                longitud
                        )
                );

                camion.setDistanciaEnSegmentoKm(
                        nuevaDistancia
                );

                camion.setDistanciaRecorridaKm(
                        camion.getDistanciaRecorridaKm()
                        + distanciaAvanzarKm
                );

                distanciaAvanzarKm = 0;

            } else {

                camion.setDistanciaRecorridaKm(
                        camion.getDistanciaRecorridaKm()
                        + restante
                );

                distanciaAvanzarKm -= restante;

                camion.setIndiceSegmento(
                        indice + 1
                );

                camion.setDistanciaEnSegmentoKm(
                        0
                );

                camion.setPosicionActual(
                        new Punto(
                                siguiente.getLatitud(),
                                siguiente.getLongitud()
                        )
                );
            }
        }

        if (
                camion.getIndiceSegmento()
                >= ruta.size() - 1
        ) {

            Nodo destino =
                    ruta.get(ruta.size() - 1);

            camion.setPosicionActual(
                    new Punto(
                            destino.getLatitud(),
                            destino.getLongitud()
                    )
            );

            camion.setEstado("FINALIZADO");

            camion.setVelocidadKmh(0);
        }
    }

    public synchronized List<Camion> obtenerCamiones() {
        return camiones;
    }
    
    public synchronized List<ReporteCamion> obtenerReporte() {

        List<ReporteCamion> reportes = new ArrayList<>();

        for (Camion camion : camiones) {

            List<Double> muestras =
                    camion.getMuestrasVelocidad();

            if (muestras == null || muestras.isEmpty()) {
                continue;
            }

            double minima =
                    muestras.stream()
                            .mapToDouble(Double::doubleValue)
                            .min()
                            .orElse(0);

            double maxima =
                    muestras.stream()
                            .mapToDouble(Double::doubleValue)
                            .max()
                            .orElse(0);

            double promedio =
                    muestras.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);

            int menoresA23 =
                    (int) muestras.stream()
                            .filter(v -> v < 23)
                            .count();

            double porcentaje =
                    (menoresA23 * 100.0)
                    / muestras.size();

            String explicacion;

            if (porcentaje > 25) {

                explicacion =
                        "El camión presenta una proporción "
                        + "superior al 25% de muestras por debajo "
                        + "de 23 km/h.";

            } else {

                explicacion =
                        "El camión presenta una proporción "
                        + "igual o menor al 25% de muestras por "
                        + "debajo de 23 km/h.";
            }

            reportes.add(
                    new ReporteCamion(
                            camion.getId(),
                            muestras.size(),
                            minima,
                            maxima,
                            promedio,
                            menoresA23,
                            explicacion
                    )
            );
        }

        return reportes;
    }
}