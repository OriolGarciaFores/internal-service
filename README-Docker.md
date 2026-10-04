# Docker — Guía de comandos

Guía rápida de los comandos Docker utilizados para ejecutar los proyectos:

- `internal-service` → Spring Boot
- `tebra-network` → Red Docker compartida
- `internal-service-data` → Volumen Docker para SQLite

---

## Índice

- [1. Comprobar Docker](#1-comprobar-docker)
- [2. Imágenes](#2-imágenes)
- [3. Contenedores](#3-contenedores)
- [4. Arrancar y detener contenedores](#4-arrancar-y-detener-contenedores)
- [5. Rebuild de una aplicacion](#5-rebuild-de-una-aplicación)
- [6. Logs](#6-logs)
- [7. Entrar en un contenedor](#7-entrar-en-un-contenedor)
- [8. Información del contenedor](#8-información-del-contenedor)
- [9. Red docker](#9-red-docker)
- [10. Probar conectividad entre contenedores](#10-probar-conectividad-entre-contenedores)
- [11. Volúmenes](#11-volúmenes)
- [12. Variables de entorno](#12-variables-de-entorno)
- [13. Comprobar el estado general](#13-comprobar-el-estado-general)
- [14. Limpieza](#14-limpieza)
- [15. Flujo habitual de desarrollo](#15-flujo-habitual-de-desarrollo)
- [16. Arquitectura actual](#16-arquitectura-actual)

---

## 1. Comprobar Docker

### Ver versión

```bash
docker --version
```

### Ver información de Docker

```bash
docker info
```

---

# 2. Imágenes

## Listar imágenes

```bash
docker images
```

También:

```bash
docker image ls
```

## Crear una imagen

Desde la carpeta que contiene el `Dockerfile`:

```bash
docker build -t internal-service-local .
```

### Ver historial de una imagen

```bash
docker history internal-service-local
```

### Eliminar una imagen

```bash
docker rmi internal-service-local
```

Si un contenedor todavía utiliza la imagen, primero hay que eliminar el contenedor.

---

# 3. Contenedores

## Listar contenedores ejecutándose

```bash
docker ps
```

## Listar todos los contenedores

```bash
docker ps -a
```

## Crear y arrancar un contenedor

Ejemplo Spring Boot:

```bash
docker run -d \
  --name internal-service-local \
  --network tebra-network \
  -p 8080:8080 \
  -e TZ=Europe/Madrid \
  -e DISCORD_BOT_URL=http://alts-bot-local:1234 \
  -v internal-service-data:/app/data \
  internal-service-local:latest
```

### Significado

- `-d` → Ejecuta el contenedor en segundo plano
- `--name internal-service-local` → Nombre del contenedor
- `--network tebra-network` → Conecta el contenedor a la red Docker compartida
- `-p 8080:8080` → Mapea puerto PC:contenedor para acceder servicios fuera el contenedor
- `-e TZ=Europe/Madrid` → Configura la zona horaria del contenedor
- `-e DISCORD_BOT_URL=http://alts-bot-local:1234` → Indica a Spring dónde está el bot, el nombre dns se asocia al nombre del contenedor
- `-v internal-service-data:/app/data` → Monta el volumen de SQLite ya creado

---

# 4. Arrancar y detener contenedores

## Arrancar un contenedor existente

```bash
docker start internal-service-local
```

## Detenerlo

```bash
docker stop internal-service-local
```

## Reiniciarlo

```bash
docker restart internal-service-local
```

## Eliminar el contenedor

```bash
docker rm internal-service-local
```

Si está ejecutándose:

```bash
docker rm -f internal-service-local
```

`-f` lo detiene y elimina.

> Eliminar el contenedor **no elimina el volumen Docker**.

---

# 5. Rebuild de una aplicación

Cuando modificamos código:

```bash
docker build -t internal-service-local .
```

Después hay que recrear el contenedor para utilizar la nueva imagen:

```bash
docker stop internal-service-local
docker rm internal-service-local
```

Y volver a ejecutar:

```bash
docker run -d \
  --name internal-service-local \
  --network tebra-network \
  -p 8080:8080 \
  -e TZ=Europe/Madrid \
  -e DISCORD_BOT_URL=http://alts-bot-local:1234 \
  -v internal-service-data:/app/data \
  internal-service-local:latest
```

El volumen:

```text
internal-service-data
```

continúa existiendo y conserva SQLite.

---

# 6. Logs

## Ver logs

```bash
docker logs internal-service-local
```

## Seguir logs en tiempo real

```bash
docker logs -f internal-service-local
```

Salir del seguimiento:

```text
Ctrl + C
```

Esto no detiene el contenedor.

## Ver las últimas líneas

```bash
docker logs --tail 100 internal-service-local
```

## Logs desde una fecha

```bash
docker logs --since 10m internal-service-local
```

---

# 7. Entrar en un contenedor

## Abrir una shell

Para Alpine:

```bash
docker exec -it internal-service-local sh
```

Salir:

```bash
exit
```

---

# 8. Información del contenedor

## Inspeccionar un contenedor

```bash
docker inspect internal-service-local
```

Esto muestra configuración, red, volúmenes, variables, etc.

## Ver los puertos

```bash
docker port internal-service-local
```

Ejemplo:

```text
8080/tcp -> 0.0.0.0:8080
```

Significa:

```text
PC:8080 → Docker:8080
```

## Ver procesos

```bash
docker top internal-service-local
```

---

# 9. Red Docker

```text
tebra-network
```

Esto permite que los contenedores se encuentren mediante su nombre.

## Crear la red

```bash
docker network create tebra-network
```

Solo es necesario hacerlo una vez.

## Listar redes

```bash
docker network ls
```

## Inspeccionar la red

```bash
docker network inspect tebra-network
```

Aquí podemos comprobar qué contenedores están conectados.

## Conectar un contenedor existente

```bash
docker network connect tebra-network alts-bot-local
```

## Desconectar un contenedor

```bash
docker network disconnect tebra-network alts-bot-local
```

---

# 10. Probar conectividad entre contenedores

Entrar en Spring:

```bash
docker exec -it internal-service-local sh
```

Desde dentro se puede comprobar la resolución del bot:

```bash
ping alts-bot-local
```

> Algunas imágenes Alpine no incluyen `ping`.

También se puede utilizar `wget` si está disponible:

```bash
wget -qO- http://alts-bot-local:1234
```

Salir:

```bash
exit
```

---

# 11. Volúmenes

El servicio Spring utiliza un volumen Docker para SQLite:

```text
internal-service-data
```

SQLite está en:

```text
/app/data/internal-service.db
```

## Crear el volumen

```bash
docker volume create internal-service-data
```

Solo es necesario hacerlo una vez.

## Listar volúmenes

```bash
docker volume ls
```

## Inspeccionar un volumen

```bash
docker volume inspect internal-service-data
```

## Ver los archivos del volumen

```bash
docker run --rm \
  -v internal-service-data:/data \
  alpine \
  ls -lah /data
```

Debería aparecer:

```text
internal-service.db
```

## Eliminar un volumen

```bash
docker volume rm internal-service-data
```

> ⚠️ Esto elimina los datos persistidos de SQLite. No ejecutar salvo que se quiera borrar la base de datos.

---

# 12. Variables de entorno

Ver las variables de un contenedor:

```bash
docker inspect internal-service-local
```

O entrando al contenedor:

```bash
docker exec -it internal-service-local sh
```

y:

```bash
env
```

Salir:

```bash
exit
```

---

# 13. Comprobar el estado general

### Contenedores

```bash
docker ps
```

### Redes

```bash
docker network ls
```

### Volúmenes

```bash
docker volume ls
```

### Imágenes

```bash
docker images
```

Una comprobación rápida:

```bash
docker ps
docker network ls
docker volume ls
```

---

# 14. Limpieza

## Contenedores parados

```bash
docker container prune
```

> ⚠️ Elimina todos los contenedores detenidos.

## Imágenes sin utilizar

```bash
docker image prune
```

## Volúmenes sin utilizar

```bash
docker volume prune
```

> ⚠️ Tener especial cuidado con los volúmenes porque pueden contener datos.

## Limpieza general

```bash
docker system prune
```

> ⚠️ No utilizarlo sin revisar qué va a eliminar.

---

# 15. Flujo habitual de desarrollo

Cuando modifico Spring:

```bash
docker build -t internal-service-local .
```

Después:

```bash
docker stop internal-service-local
docker rm internal-service-local
```

Y vuelvo a arrancarlo:

```bash
docker run -d \
  --name internal-service-local \
  --network tebra-network \
  -p 8080:8080 \
  -e TZ=Europe/Madrid \
  -e DISCORD_BOT_URL=http://alts-bot-local:1234 \
  -v internal-service-data:/app/data \
  internal-service-local:latest
```

Finalmente:

```bash
docker logs -f internal-service-local
```

Para comprobar que Spring ha arrancado correctamente.

---

# 16. Arquitectura actual

```text
┌───────────────────────────────────────────────┐
│                 Docker                        │
│                                               │
│  ┌────────────────────┐                       │
│  │ alts-bot-local     │                       │
│  │ Node.js            │                       │
│  │ :1234              │                       │
│  └──────────┬─────────┘                       │
│             │                                 │
│             │ tebra-network                   │
│             │                                 │
│  ┌──────────▼─────────┐                       │
│  │ internal-service   │                       │
│  │ Spring Boot        │                       │
│  │ :8080              │                       │
│  └──────────┬─────────┘                       │
│             │                                 │
│             ▼                                 │
│  ┌────────────────────┐                       │
│  │ internal-service-  │                       │
│  │ data               │                       │
│  │ SQLite             │                       │
│  └────────────────────┘                       │
│                                               │
└───────────────────────────────────────────────┘
```

Spring es accesible desde el PC mediante:

```text
http://localhost:8080
```

Spring accede al bot mediante:

```text
http://alts-bot-local:1234
```

SQLite se mantiene en:

```text
internal-service-data
```
