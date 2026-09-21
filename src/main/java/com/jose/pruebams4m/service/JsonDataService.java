package com.jose.pruebams4m.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jose.pruebams4m.model.Tramo;
import com.jose.pruebams4m.model.Ubicacion;

@Service
public class JsonDataService {

    private final ObjectMapper objectMapper;

    private List<Tramo> tramos;
    private List<Ubicacion> cargas;
    private List<Ubicacion> descargas;

    public JsonDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        cargarDatos();
    }

    private void cargarDatos() {

        try {

            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("data/data-prueba.json");

            if (inputStream == null) {
                throw new IllegalStateException(
                        "No se encontró el archivo data/data-prueba.json"
                );
            }

            JsonNode root = objectMapper.readTree(inputStream);

            validarEstructuraPrincipal(root);

            validarRutas(root.get("Routes"));
            validarUbicaciones(root.get("Load"), "Load");
            validarUbicaciones(root.get("Dump"), "Dump");

            tramos = objectMapper.convertValue(
                    root.get("Routes"),
                    new TypeReference<List<Tramo>>() {}
            );

            cargas = convertirUbicaciones(
                    root.get("Load"),
                    "CARGA"
            );

            descargas = convertirUbicaciones(
                    root.get("Dump"),
                    "DESCARGA"
            );

            System.out.println("JSON validado y cargado correctamente.");
            System.out.println("Tramos cargados: " + tramos.size());
            System.out.println("Cargas cargadas: " + cargas.size());
            System.out.println("Descargas cargadas: " + descargas.size());

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Error al validar o cargar el archivo JSON: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private void validarEstructuraPrincipal(JsonNode root) {

        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException(
                    "La raíz del JSON debe ser un objeto."
            );
        }

        validarArray(root, "Routes");
        validarArray(root, "Load");
        validarArray(root, "Dump");
    }

    private void validarArray(
            JsonNode root,
            String nombre) {

        JsonNode nodo = root.get(nombre);

        if (nodo == null) {
            throw new IllegalArgumentException(
                    "El campo '" + nombre + "' no existe."
            );
        }

        if (!nodo.isArray()) {
            throw new IllegalArgumentException(
                    "El campo '" + nombre + "' debe ser un arreglo."
            );
        }
    }

    private void validarRutas(JsonNode rutas) {

        for (int i = 0; i < rutas.size(); i++) {

            JsonNode ruta = rutas.get(i);

            if (!ruta.isObject()) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "] debe ser un objeto."
                );
            }

            JsonNode id = ruta.get("id_trm_cs");
            JsonNode nombre = ruta.get("nombre_tramo");
            JsonNode color = ruta.get("color");
            JsonNode points = ruta.get("points");

            if (id == null || !id.isInt()) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'id_trm_cs' debe ser entero."
                );
            }

            if (nombre == null || !nombre.isTextual()) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'nombre_tramo' debe ser texto."
                );
            }

            if (color == null || !color.isTextual()) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'color' debe ser texto."
                );
            }

            if (!color.asText().matches(
                    "^#[0-9A-Fa-f]{6}$")) {

                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'color' debe ser hexadecimal."
                );
            }

            if (points == null || !points.isArray()) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'points' debe ser un arreglo."
                );
            }

            if (points.size() < 2) {
                throw new IllegalArgumentException(
                        "Routes[" + i + "]: 'points' debe contener al menos 2 puntos."
                );
            }

            for (int j = 0; j < points.size(); j++) {

                JsonNode punto = points.get(j);

                if (!punto.isArray() || punto.size() < 2) {
                    throw new IllegalArgumentException(
                            "Routes[" + i + "].points[" + j
                                    + "] debe contener [latitud, longitud]."
                    );
                }

                if (!punto.get(0).isNumber()
                        || !punto.get(1).isNumber()) {

                    throw new IllegalArgumentException(
                            "Routes[" + i + "].points[" + j
                                    + "] debe contener coordenadas numéricas."
                    );
                }

                validarCoordenadas(
                        punto.get(0).asDouble(),
                        punto.get(1).asDouble(),
                        "Routes[" + i + "].points[" + j + "]"
                );
            }
        }
    }

    private void validarUbicaciones(
            JsonNode ubicaciones,
            String tipo) {

        for (int i = 0; i < ubicaciones.size(); i++) {

            JsonNode ubicacion = ubicaciones.get(i);

            if (!ubicacion.isObject()) {
                throw new IllegalArgumentException(
                        tipo + "[" + i + "] debe ser un objeto."
                );
            }

            JsonNode id = ubicacion.get("id");
            JsonNode nombre = ubicacion.get("name");
            JsonNode coor = ubicacion.get("coor");
            JsonNode radio = ubicacion.get("radio");

            if (id == null || !id.isInt()) {
                throw new IllegalArgumentException(
                        tipo + "[" + i + "]: 'id' debe ser entero."
                );
            }

            if (nombre == null || !nombre.isTextual()) {
                throw new IllegalArgumentException(
                        tipo + "[" + i + "]: 'name' debe ser texto."
                );
            }

            if (coor == null
                    || !coor.isArray()
                    || coor.size() < 2) {

                throw new IllegalArgumentException(
                        tipo + "[" + i
                                + "]: 'coor' debe contener [latitud, longitud]."
                );
            }

            if (!coor.get(0).isNumber()
                    || !coor.get(1).isNumber()) {

                throw new IllegalArgumentException(
                        tipo + "[" + i
                                + "]: las coordenadas deben ser numéricas."
                );
            }

            validarCoordenadas(
                    coor.get(0).asDouble(),
                    coor.get(1).asDouble(),
                    tipo + "[" + i + "].coor"
            );

            if (radio != null
                    && !radio.isNull()
                    && !radio.isNumber()) {

                throw new IllegalArgumentException(
                        tipo + "[" + i
                                + "]: 'radio' debe ser numérico o null."
                );
            }
        }
    }

    private void validarCoordenadas(
            double latitud,
            double longitud,
            String ubicacion) {

        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException(
                    ubicacion + ": latitud fuera de rango."
            );
        }

        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException(
                    ubicacion + ": longitud fuera de rango."
            );
        }
    }

    private List<Ubicacion> convertirUbicaciones(
            JsonNode nodo,
            String tipo) {

        List<Ubicacion> ubicaciones = new ArrayList<>();

        for (JsonNode item : nodo) {

            Integer id = item.get("id").asInt();

            String nombre = item.get("name").asText();

            JsonNode coor = item.get("coor");

            double latitud = coor.get(0).asDouble();
            double longitud = coor.get(1).asDouble();

            Double radio = null;

            if (item.has("radio")
                    && !item.get("radio").isNull()) {

                radio = item.get("radio").asDouble();
            }

            Ubicacion ubicacion = new Ubicacion(
                    id,
                    nombre,
                    latitud,
                    longitud,
                    tipo,
                    radio
            );

            ubicaciones.add(ubicacion);
        }

        return ubicaciones;
    }

    public List<Tramo> getTramos() {
        return tramos;
    }

    public List<Ubicacion> getCargas() {
        return cargas;
    }

    public List<Ubicacion> getDescargas() {
        return descargas;
    }
}