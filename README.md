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

