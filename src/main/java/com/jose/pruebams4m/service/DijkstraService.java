package com.jose.pruebams4m.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.jose.pruebams4m.model.Conexion;
import com.jose.pruebams4m.model.Grafo;
import com.jose.pruebams4m.model.Nodo;
import com.jose.pruebams4m.Util.DistanciaUtil;
import com.jose.pruebams4m.model.Ubicacion;

@Service
public class DijkstraService {

    private final GrafoService grafoService;

    public DijkstraService(GrafoService grafoService) {
        this.grafoService = grafoService;
    }

    public List<Nodo> calcularRuta(Nodo origen, Nodo destino) {

        Grafo grafo = grafoService.getGrafo();

        Map<String, Double> distancias = new HashMap<>();
        Map<String, Nodo> anteriores = new HashMap<>();

        Map<String, Integer> primerIdTramo = new HashMap<>();

        Set<String> visitados = new HashSet<>();


        PriorityQueue<NodoDistancia> cola =
                new PriorityQueue<>(
                        Comparator
                                .comparingDouble(
                                        NodoDistancia::getDistancia
                                )
                                .thenComparingInt(
                                        NodoDistancia::getPrimerIdTramo
                                )
                );

        for (Nodo nodo : grafo.obtenerNodos()) {
            distancias.put(nodo.getId(), Double.MAX_VALUE);
            primerIdTramo.put(nodo.getId(), Integer.MAX_VALUE);
        }

        distancias.put(origen.getId(), 0.0);
        primerIdTramo.put(origen.getId(), Integer.MAX_VALUE);

        cola.add(
                new NodoDistancia(
                        origen,
                        0.0,
                        Integer.MAX_VALUE
                )
        );

        while (!cola.isEmpty()) {

            NodoDistancia actual = cola.poll();
            Nodo nodoActual = actual.getNodo();

            if (visitados.contains(nodoActual.getId())) {
                continue;
            }

            visitados.add(nodoActual.getId());

            if (nodoActual.getId().equals(destino.getId())) {
                break;
            }

            for (Conexion conexion :
                    grafo.obtenerConexiones(nodoActual)) {

                Nodo vecino = conexion.getDestino();

                if (visitados.contains(vecino.getId())) {
                    continue;
                }

                double nuevaDistancia =
                        distancias.get(nodoActual.getId())
                        + conexion.getDistancia();

                int nuevoPrimerIdTramo;

                if (nodoActual.getId().equals(origen.getId())) {
                    nuevoPrimerIdTramo =
                            conexion.getIdTramo();
                } else {
                    nuevoPrimerIdTramo =
                            primerIdTramo.get(nodoActual.getId());
                }

                double distanciaActual =
                        distancias.get(vecino.getId());

                int primerIdActual =
                        primerIdTramo.get(vecino.getId());

                /*
                 * Caso 1:
                 * encontramos una ruta más corta.
                 *
                 * Caso 2:
                 * encontramos una ruta con la misma distancia,
                 * pero cuyo primer id_trm_cs es menor.
                 */
                boolean mejorRuta =
                        nuevaDistancia < distanciaActual;

                boolean mismoCostoConMejorTramo =
                        Double.compare(
                                nuevaDistancia,
                                distanciaActual
                        ) == 0
                        && nuevoPrimerIdTramo < primerIdActual;

                if (mejorRuta || mismoCostoConMejorTramo) {

                    distancias.put(
                            vecino.getId(),
                            nuevaDistancia
                    );

                    anteriores.put(
                            vecino.getId(),
                            nodoActual
                    );

                    primerIdTramo.put(
                            vecino.getId(),
                            nuevoPrimerIdTramo
                    );

                    cola.add(
                            new NodoDistancia(
                                    vecino,
                                    nuevaDistancia,
                                    nuevoPrimerIdTramo
                            )
                    );
                }
            }
        }

        if (!origen.getId().equals(destino.getId())
                && !anteriores.containsKey(destino.getId())) {

            return Collections.emptyList();
        }

        List<Nodo> ruta = new ArrayList<>();

        Nodo actual = destino;

        while (actual != null) {

            ruta.add(actual);

            if (actual.getId().equals(origen.getId())) {
                break;
            }

            actual = anteriores.get(actual.getId());
        }

        Collections.reverse(ruta);

        return ruta;
    }

    public Nodo encontrarNodoMasCercano(
            double latitud,
            double longitud) {

        Grafo grafo = grafoService.getGrafo();

        Nodo cercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Nodo nodo : grafo.obtenerNodos()) {

            double distancia =
                    DistanciaUtil.calcularDistancia(
                            latitud,
                            longitud,
                            nodo.getLatitud(),
                            nodo.getLongitud()
                    );

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                cercano = nodo;
            }
        }

        return cercano;
    }

    private static class NodoDistancia {

        private final Nodo nodo;
        private final double distancia;
        private final int primerIdTramo;

        public NodoDistancia(
                Nodo nodo,
                double distancia,
                int primerIdTramo) {

            this.nodo = nodo;
            this.distancia = distancia;
            this.primerIdTramo = primerIdTramo;
        }

        public Nodo getNodo() {
            return nodo;
        }

        public double getDistancia() {
            return distancia;
        }

        public int getPrimerIdTramo() {
            return primerIdTramo;
        }
    }

    public List<Nodo> calcularRuta(
            Ubicacion carga,
            Ubicacion descarga) {

        Nodo origen = encontrarNodoMasCercano(
                carga.getLatitud(),
                carga.getLongitud()
        );

        Nodo destino = encontrarNodoMasCercano(
                descarga.getLatitud(),
                descarga.getLongitud()
        );

        if (origen == null || destino == null) {
            return Collections.emptyList();
        }

        return calcularRuta(origen, destino);
    }
}