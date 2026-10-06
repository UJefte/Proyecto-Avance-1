# Control de acceso con QR para condominios

Proyecto final (avance 1). API REST en **Spring Boot** para controlar la entrada y salida de residentes,
trabajadores y visitas mediante pases QR.

## Qué hace

- Registra departamentos, residentes y trabajadores (con horario permitido).
- Genera un pase QR para cada residente y trabajador, y pases temporales para visitas
  (únicos o recurrentes, con vigencia y días permitidos).
- El guardia escanea el QR: el sistema valida y registra **entrada o salida** (PERMITIDO / DENEGADO + motivo).
- Visitas sin QR: el guardia las registra y el residente aprueba o rechaza; al aprobar se genera un pase de un solo uso.
- Bitácora de accesos consultable.

## Cómo ejecutarlo

Requisitos: Java 17+, Maven y MySQL.

1. Copia `src/main/resources/secrets.properties.example` como `secrets.properties` y pon tu contraseña de MySQL.
2. Por defecto usa MySQL en `localhost:3307`, usuario `root`. Se puede cambiar con las variables
   `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD`. La base `condominios_acceso` se crea sola.
3. Ejecuta:

```cmd
mvn spring-boot:run
```

La API queda en `http://localhost:8080`.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST / GET | `/departamentos` | Alta y listado |
| POST / GET | `/residentes` | Alta y listado (`GET /residentes/{id}`) |
| POST / GET | `/trabajadores` | Alta y listado |
| POST | `/pases/residente/{id}` | Pase permanente de residente |
| POST | `/pases/trabajador/{id}` | Pase permanente de trabajador |
| POST | `/pases/visita` | Pase temporal de visita |
| GET | `/pases` | Lista de pases |
| PUT | `/pases/{id}/desactivar` | Desactiva un pase |
| POST | `/accesos/validar` | Escanea un QR (`{"codigo": "..."}`) |
| GET | `/accesos` | Bitácora de accesos |
| POST | `/accesos/visita-sin-qr` | Guardia registra visita sin QR |
| GET | `/solicitudes?estado=PENDIENTE` | Solicitudes de visitas |
| PUT | `/solicitudes/{id}/resolver` | Residente aprueba o rechaza (`{"aprobada": true}`) |

### Ejemplo rápido

```json
POST /departamentos
{ "numero": "101", "torre": "A" }

POST /residentes
{ "nombre": "Ana López", "correo": "ana@correo.com", "departamentoId": 1 }

POST /pases/visita
{
  "nombreVisitante": "Carlos Ruiz",
  "tipo": "VISITA",
  "residenteId": 1,
  "vigenteDesde": "2026-10-06T08:00:00",
  "vigenteHasta": "2026-10-06T22:00:00",
  "usosMaximos": 1
}

POST /accesos/validar
{ "codigo": "<codigo del pase>" }
```

## Estructura

```
src/main/java/com/condominios/acceso
├── controller   # endpoints REST
├── service      # reglas de negocio (validación del QR, solicitudes)
├── repository   # acceso a datos (Spring Data JPA)
├── entity       # entidades y enums
├── dto          # objetos de entrada y salida
└── exception    # errores y manejador global
```

## Pendiente para las siguientes entregas

Login y roles, imagen del QR, subida de la foto de identificación, correos, reportes PDF/Excel,
Docker, despliegue, pruebas Postman y K6, interfaz y diagramas.
