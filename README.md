# Buscaminas Distribuido TCP

Laboratorio de Computación en Internet I (09810 - TIC). Juego de Buscaminas cliente/servidor
sobre sockets TCP con serialización JSON (Gson) y un pool fijo de hilos en el servidor.

## Estructura

```
clase_tcp_udp/
├── build.gradle            configuración común (java + gson)
├── settings.gradle         declara los submódulos server y client
├── server/                 servidor TCP multihilo (ThreadPool de 5 hilos)
└── client/                 cliente interactivo de consola
```

## Requisitos

- **JDK 17** (Gradle 8.6 no soporta JDK 25/26; falla con `Unsupported class file major version`).
  Si su JDK por defecto es más nuevo, ejecute cada comando con `JAVA_HOME` apuntando al 17:

  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  ```

## Ejecución

Servidor (escucha en `0.0.0.0`, puerto 12345 por defecto):

```bash
./gradlew :server:run                       # puerto 12345
./gradlew :server:run --args="9000"         # puerto alterno
```

Cliente (en otra terminal):

```bash
./gradlew :client:run --console=plain                            # localhost:12345
./gradlew :client:run --console=plain --args="192.168.1.20 9000" # host y puerto explícitos
```

También puede generar lanzadores nativos y usarlos directamente, que es más cómodo para abrir
varias terminales a la vez:

```bash
./gradlew installDist
./server/build/install/server/bin/server 12345
./client/build/install/client/bin/client localhost 12345
```

El servidor acepta un segundo argumento `--console` que habilita el juego local por consola
del código base. Sin ese argumento corre como servidor de red puro.

## Protocolo

Una petición JSON por conexión, terminada en `\n`. El servidor responde con una línea JSON y
cierra el socket (*short-lived connection*).

| Acción        | Parámetros (`data`) | Respuesta (`data`)            |
|---------------|---------------------|-------------------------------|
| `INIT_GAME`   | `n`, `m`, `minas`   | `board`                       |
| `SELECT_CELL` | `i`, `j`            | `board`, `win`, `gameEnd`     |
| `MARK_CELL`   | `i`, `j`            | `board`                       |
| `GET_BOARD`   | —                   | `board`                       |
| `SOW_ALL`     | —                   | `board`                       |

## Prueba de concurrencia

Levante el servidor y abra dos o más terminales con clientes apuntando al mismo host. La
bitácora del servidor muestra qué hilo del pool atiende cada petición:

```
[pool-1-thread-2] action=MARK_CELL data={i=0, j=2}
[pool-1-thread-3] action=MARK_CELL data={i=0, j=1}
[pool-1-thread-5] action=SELECT_CELL data={i=8, j=1}
```

El servidor además vuelca el tablero a su salida estándar en cada petición (`ServicesImpl.printBoard`),
por lo que las líneas de bitácora pueden aparecer intercaladas con ese volcado.

## Informe

Las respuestas al cuestionario de análisis conceptual están en [`docs/INFORME.md`](docs/INFORME.md)
y [`docs/INFORME.pdf`](docs/INFORME.pdf).
