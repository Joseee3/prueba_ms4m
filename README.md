Backend - Simulación de Camiones

1. Descripción

Backend desarrollado en Java con Spring Boot para la simulación de cinco camiones que recorren rutas viales a partir de información proporcionada mediante un archivo JSON.

El sistema permite cargar y validar los datos de rutas, ubicaciones de carga y ubicaciones de descarga; construir una red vial; calcular rutas mediante el algoritmo de Dijkstra; simular el movimiento de cinco camiones y generar un reporte consolidado de velocidades.

⸻

2. Tecnologías utilizadas

* Java 17
* Spring Boot
* Maven
* Jackson
* API REST
* Algoritmo de Dijkstra
* Haversine para cálculo de distancias
* Spring Scheduling

No se utiliza una base de datos. Los datos originales se encuentran en un archivo JSON incluido dentro del proyecto y el estado de la simulación se mantiene en memoria.

⸻

3. Estructura del proyecto

src/
├── main/
│   ├── java/
│   │   └── com/jose/pruebams4m/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── service/
│   │       └── Util/
│   └── resources/
│       └── data/
│           └── data-prueba.json
└── test/

⸻

4. Datos de entrada

El archivo utilizado por el sistema es:

src/main/resources/data/data-prueba.json

El JSON contiene tres arreglos principales:

* Routes: tramos de carretera.
* Load: ubicaciones de carga.
* Dump: ubicaciones de descarga.

Las rutas contienen puntos ordenados [latitud, longitud]. Los puntos consecutivos de cada tramo representan segmentos transitables.

El sistema valida la estructura del JSON antes de utilizar los datos y no modifica el archivo original.

⸻

5. Construcción de la red vial

A partir de los puntos de Routes se construye un grafo.

Cada coordenada representa un nodo y cada par de puntos consecutivos genera una conexión bidireccional.

La distancia de cada conexión se calcula mediante la fórmula de Haversine y se utiliza como costo para encontrar la ruta.

La representación simplificada es:

Punto 1 ───── Punto 2 ───── Punto 3
             │
             │
          Punto 4

Los camiones únicamente pueden desplazarse por las conexiones existentes en este grafo.

⸻

6. Cálculo de rutas

Para encontrar una ruta entre una ubicación de carga y una ubicación de descarga se utiliza el algoritmo de Dijkstra.

El proceso es:

1. Se identifica el nodo de la red más cercano a la ubicación de carga.
2. Se identifica el nodo más cercano a la ubicación de descarga.
3. Se ejecuta Dijkstra sobre el grafo.
4. La distancia geográfica de cada segmento se utiliza como costo.
5. Se obtiene una secuencia de nodos que representa la ruta del camión.

Cuando existen rutas con el mismo costo, se utiliza como criterio de desempate el menor id_trm_cs inicial.

Si una combinación de carga y descarga no está conectada, el sistema puede evaluar otra combinación disponible y registra esta decisión en la respuesta mediante:

rutaAlternativa
motivoAsignacion

⸻

7. Simulación de camiones

El sistema crea exactamente cinco camiones con identificadores estables:

IDS26-D3R6-001
IDS26-D3R6-002
IDS26-D3R6-003
IDS26-D3R6-004
IDS26-D3R6-005

Cada camión recibe:

* Una ubicación de carga.
* Una ubicación de descarga.
* Una ruta calculada.
* Una velocidad variable.
* Una posición actual.
* Un estado.
* Un timestamp.
* Muestras históricas de velocidad.

Las velocidades se generan aleatoriamente dentro del rango configurado:

18 km/h - 48 km/h

La actualización de la simulación se realiza cada 2.5 segundos.

El movimiento se calcula utilizando el tiempo transcurrido y la velocidad del camión. La posición se interpola entre los nodos consecutivos de la ruta, evitando movimientos en línea recta fuera de las carreteras.

⸻

8. Reproducibilidad

La simulación utiliza una semilla configurable:

simulation.seed=4367

Con la misma semilla y configuración se puede reproducir la secuencia de velocidades generada durante una ejecución.

También es posible configurar los límites de velocidad:

simulation.speed.min=18
simulation.speed.max=48

⸻

9. API REST

Consultar tramos

GET /api/tramos

Devuelve todos los tramos cargados desde el JSON.

Consultar ubicaciones de carga

GET /api/cargas

Consultar ubicaciones de descarga

GET /api/descargas

Consultar todas las ubicaciones

GET /api/ubicaciones

Iniciar o reiniciar la simulación

POST /api/simulacion/iniciar

Crea los cinco camiones y devuelve su estado inicial.

Consultar estado actual

GET /api/simulacion

Devuelve la posición, velocidad, estado y demás información de los cinco camiones.

Consultar reporte

GET /api/simulacion/reporte

Devuelve un reporte consolidado por camión.

El reporte incluye:

* Número de muestras.
* Velocidad mínima.
* Velocidad máxima.
* Velocidad promedio.
* Cantidad de muestras menores a 23 km/h.
* Explicación en lenguaje natural.

⸻

10. Manejo de errores

El backend valida:

* Existencia de Routes, Load y Dump.
* Tipo de cada campo.
* Coordenadas válidas.
* Estructura de los puntos de las rutas.
* Formato hexadecimal de los colores.
* Existencia de ubicaciones de carga y descarga.
* Disponibilidad de rutas entre las ubicaciones.

Los errores se devuelven mediante respuestas HTTP controladas.

Ejemplo:

{
  "timestamp": "2026-09-21T12:00:00",
  "status": 400,
  "error": "ERROR_OPERACION",
  "mensaje": "No fue posible encontrar una ruta válida."
}

El sistema no modifica silenciosamente los datos de entrada.

⸻

11. Arquitectura

El backend sigue una estructura por capas:

Controller
    ↓
Service
    ↓
Model / Grafo
    ↓
JSON

Controller

Expone los endpoints REST.

Service

Contiene la lógica de negocio, carga de datos, construcción del grafo, cálculo de rutas y simulación.

Model

Representa los camiones, ubicaciones, nodos, conexiones y demás entidades utilizadas por el sistema.

Config

Contiene configuraciones como Jackson, CORS y programación de tareas.

Exception

Centraliza el manejo de errores de la API.

⸻

12. Ejecución local

Requisitos

* Java 17
* Maven

Ejecutar con Maven

Desde la carpeta raíz del proyecto:

./mvnw spring-boot:run

También puede ejecutarse desde Spring Tool Suite, Eclipse, IntelliJ IDEA o cualquier IDE compatible con Spring Boot.

El backend se ejecuta por defecto en:

http://localhost:8080

⸻

13. Prueba rápida

Después de iniciar el backend se puede comprobar:

GET http://localhost:8080/api/tramos

Para iniciar la simulación:

POST http://localhost:8080/api/simulacion/iniciar

Para consultar el estado:

GET http://localhost:8080/api/simulacion

Para consultar el reporte:

GET http://localhost:8080/api/simulacion/reporte

⸻

14. Decisiones técnicas

Dijkstra

Se utilizó Dijkstra porque permite encontrar la ruta de menor distancia en un grafo cuyos costos son no negativos.

Haversine

Se utiliza para calcular la distancia geográfica entre dos coordenadas.

Polling

La actualización del estado de los camiones se puede consultar periódicamente desde el frontend.

Se eligió esta alternativa por su implementación sencilla y adecuada para el alcance de la evaluación.

Memoria

No se utiliza una base de datos debido a que la evaluación no requiere persistencia. La información de la simulación se mantiene en memoria.

⸻

15. Supuestos

* Los puntos consecutivos de cada tramo son transitables.
* Las coordenadas están expresadas como [latitud, longitud].
* La velocidad se expresa en km/h.
* La simulación utiliza un intervalo de actualización de 2.5 segundos.
* Las ubicaciones de carga y descarga se asocian al nodo de carretera geográficamente más cercano.
* El estado de la simulación no necesita persistirse después de reiniciar el backend.

⸻

16. Limitaciones

* La simulación mantiene su estado únicamente en memoria.
* Al reiniciar el backend se pierde el estado y las muestras de la simulación anterior.
* La comunicación con el frontend utiliza polling en lugar de WebSocket.
* El sistema depende de la calidad y conectividad de los datos proporcionados.
* No se implementa autenticación ni administración de usuarios, debido a que no forma parte del alcance solicitado.

⸻

17. Mejoras futuras

Con mayor tiempo de desarrollo se podrían implementar:

* Comunicación mediante WebSocket.
* Persistencia de ejecuciones.
* Mayor cobertura de pruebas automatizadas.
* Algoritmo A* como alternativa de búsqueda.
* Configuración de parámetros desde la interfaz.
* Mejoras en las validaciones de datos.
* Despliegue automatizado mediante CI/CD.

⸻

18. Estado del proyecto

El backend implementa el flujo principal solicitado para la evaluación:

JSON
 ↓
Validación
 ↓
Construcción del grafo
 ↓
Dijkstra
 ↓
Asignación de carga/descarga
 ↓
5 camiones
 ↓
Simulación
 ↓
Reporte de velocidades

Código del examen: IDS26-D3R6