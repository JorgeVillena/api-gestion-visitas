# Documentación: orden de consumo de la API (Gestión de visitas)

Base URL de ejemplo (local): `http://localhost:8080/api`  
En Render u otro host: `https://api-gestion-visitas-1.onrender.com/api`

Todas las rutas de esta guía van **después** del prefijo `/api` (`server.servlet.context-path`).

## Convenciones

| Elemento | Valor |
|----------|--------|
| Autenticación | `Authorization: Bearer <token_jwt>` (salvo registro/login) |
| Cuerpo | `Content-Type: application/json` |
| Roles (`perfil`) | Canónicos: `PEC` (promotor/PEC-PROMOTOR), `COORDINADOR`, `ESPECIALISTA` (supervisor), `DIRECTOR`. Alias aceptados en registro/login: `PROMOTOR`→PEC, `SUPERVISOR`→ESPECIALISTA, `ADMIN`→ESPECIALISTA. No usar `PROFESOR`. |
| Contraseña (registro) | Mínimo **6** caracteres (`@Size` en el API) |

Los **ids de visita** en respuestas JSON suelen ir como **string** (ej. `"12"`). En rutas (`/visitas/12`) puedes usar el número.

---

## 1. Autenticación (siempre primero)

### 1.1 Registro (público)

Solo si el usuario aún no existe.

```http
POST /api/auth/register
```

**Ejemplo**

```json
{
  "nombres": "María",
  "apellidos": "López",
  "usuario": "mlopez",
  "password": "secreto12",
  "perfil": "PEC",
  "schoolName": "IE 123",
  "modularCode": "1234567",
  "ugelName": "UGEL Lima Norte",
  "locationName": "Lima",
  "documentNumber": "12345678",
  "birthDate": "1990-05-15"
}
```

Los campos de escuela/documento/fecha son opcionales salvo los obligatorios del DTO.

**Si el registro devolvía 500 con SQL `22030` y “Value not permitted … PEC”:** la base H2 local tenía un `CHECK` antiguo en `users.perfil` (p. ej. solo `ADMIN`/`COORDINADOR`/`DIRECTOR`). Con la versión actual del API se elimina ese `CHECK` al arrancar; si persiste, para el servidor, borra `data/visitasdb.mv.db` (pierdes datos locales) y vuelve a levantar la app.

### 1.2 Login (público)

```http
POST /api/auth/login
```

**Ejemplo**

```json
{
  "usuario": "mlopez",
  "password": "secreto12",
  "perfil": "PEC"
}
```

**Respuesta (resumen):** incluye `token`, `id`, `username`, `perfil`, `fullName`, etc.  
Guarda `token` para el resto de llamadas.

### 1.3 Perfil actual (requiere JWT)

```http
GET /api/auth/me
Authorization: Bearer <token>
```

Útil tras abrir la app para refrescar datos del usuario.

### 1.4 Token FCM (opcional, tras login)

Para recibir notificaciones cuando la visita queda lista para revisión (supervisor).

```http
POST /api/devices/fcm-token
Authorization: Bearer <token>
```

**Ejemplo**

```json
{
  "token": "<fcm_device_token>"
}
```

Respuesta: `204 No Content`.

**Orden sugerido:** `login` → (opcional) `GET /auth/me` → (opcional) `POST /devices/fcm-token`.

---

## 2. Flujo por rol

### 2.1 Coordinador (`COORDINADOR`)

Orden típico del día a día:

1. **Listar promotores** (para armar la visita)  
   `GET /api/users/promotores`  
   Requiere rol: `COORDINADOR`, `ESPECIALISTA` o `DIRECTOR`.

2. **Crear visita** (el `coordinatorId` del body debe ser el id del usuario coordinador logueado)  
   `POST /api/visitas`

3. **Listar mis visitas** (por defecto filtra por coordinador)  
   `GET /api/visitas`  
   Opcional: `GET /api/visitas?scope=coordinator` (explícito).

4. **Ver detalle / validación GPS**  
   - `GET /api/visitas/{id}`  
   - `GET /api/visitas/{id}/validation`

5. **Durante la visita (en campo)**  
   - Check-in: `PATCH /api/visitas/{id}/coordinator-checkin`  
   - Cierre: `PATCH /api/visitas/{id}/coordinator-close`

6. **Ajustar datos de programación**  
   `PUT /api/visitas/{id}`

**Ejemplo crear visita**

```http
POST /api/visitas
Authorization: Bearer <token_coord>
```

```json
{
  "coordinatorId": "5",
  "promoterId": "8",
  "supervisorId": "12",
  "placeName": "IE Mariscal Castilla",
  "scheduledDate": "2026-06-10",
  "expectedStartTime": "08:00",
  "expectedEndTime": "10:00",
  "latitude": -12.0464,
  "longitude": -77.0428
}
```

**Ejemplo PATCH check-in coordinador**

```json
{
  "latitude": -12.0465,
  "longitude": -77.0429,
  "observation": "Llegada a institución",
  "evidenciaBase64": null
}
```

(`evidenciaBase64` puede ser imagen en Base64 o data-URL; opcional.)

---

### 2.2 Promotor (`PEC`)

1. **Listar visitas asignadas**  
   `GET /api/visitas` (por defecto scope promotor)  
   o `GET /api/visitas?scope=promoter`

2. **Detalle**  
   `GET /api/visitas/{id}`

3. **Registrar llegada** (flujo nuevo)  
   `PATCH /api/visitas/{id}/promoter-arrival`

4. **Registrar cierre**  
   `PATCH /api/visitas/{id}/promoter-close`

5. **Compatibilidad (equivale a llegada del promotor)**  
   `POST /api/visitas/{id}/registrar-entrada`  
   Cuerpo legacy con `latitud` / `longitud`:

```json
{
  "fechaHoraEntrada": "2026-06-10T08:05:00",
  "latitud": -12.0464,
  "longitud": -77.0428,
  "observacion": "Ingreso",
  "evidenciaBase64": null
}
```

6. **Evidencias adicionales**  
   - `POST /api/visit-evidences`  
   - `GET /api/visit-evidences/visit/{visitId}`

**Ejemplo PATCH llegada promotor**

```json
{
  "latitude": -12.0464,
  "longitude": -77.0428,
  "observation": "En puerta principal",
  "evidenciaBase64": null
}
```

**Respuesta PATCH (resumen):** `visitId`, `timestamp`, `message`; en cierres puede incluir `notificationSentToSupervisor`.

---

### 2.3 Especialista / Supervisor (`ESPECIALISTA`)

1. **Listar visitas donde soy supervisor**  
   `GET /api/visitas` o `?scope=supervisor`

2. **Detalle y validación**  
   - `GET /api/visitas/{id}`  
   - `GET /api/visitas/{id}/validation`  
   Respuesta validación (ejemplo conceptual): `distanceInMeters`, `isConsistent`, `status`, `message`.

3. **Revisión final**  
   - Si aún no existe: `GET /api/visit-reviews/visit/{visitId}` → `404`  
   - Crear o actualizar: `POST /api/visit-reviews`

**Ejemplo POST revisión**

```http
POST /api/visit-reviews
Authorization: Bearer <token_supervisor>
```

```json
{
  "visitId": "3",
  "finalStatus": "Conforme",
  "comment": "Visita conforme a lo programado."
}
```

`finalStatus`: `Conforme` u `Observada`.

4. **Promotores (catálogo)**  
   `GET /api/users/promotores`

---

### 2.4 Director (`DIRECTOR`)

1. **Resumen / KPIs**  
   `GET /api/director/overview`

2. **Reportes**  
   `GET /api/director/reports`  
   Query opcionales: `from`, `to` (fecha `YYYY-MM-DD`), `coordinatorId`.

Ejemplo:  
`GET /api/director/reports?from=2026-06-01&to=2026-06-30&coordinatorId=5`

3. **Visitas globales**  
   `GET /api/visitas?scope=all` o `scope=director`

---

## 3. Evidencias (cualquier usuario autenticado)

Orden típico: tener `visitId` válido → subir evidencia → listar.

**POST** `/api/visit-evidences`

```json
{
  "visitId": "3",
  "imageBase64": "<base64 o data:image/jpeg;base64,...>",
  "latitude": -12.04,
  "longitude": -77.04,
  "observation": "Foto de actividad",
  "userRole": "PEC",
  "eventType": "MANUAL"
}
```

`userRole` debe coincidir con un valor del enum: `PEC`, `COORDINADOR`, `ESPECIALISTA`, `DIRECTOR`.

**GET** `/api/visit-evidences/visit/{visitId}`

`imageUrl` se devuelve como URL accesible de la API (no ruta local de disco), por ejemplo:
`http://<host>:<puerto>/api/files/evidencias/evidencia_<uuid>.jpg`.

Si no hay imagen, `imageUrl` puede venir `null`.

**GET** `/api/files/evidencias/{fileName}`  
Sirve la imagen (JPEG) guardada para que cliente móvil/web la renderice directamente.

---

## 4. Estados de visita (referencia)

Valores usados en el dominio (pueden mostrarse en `GET /visitas/{id}`):

| Estado (ejemplos) | Significado breve |
|-------------------|-------------------|
| `Programada` | Creada, sin acciones de campo |
| `EnCurso` | Promotor registró llegada |
| `PromotorCerrado` | Promotor cerró su parte |
| `Completa` | Coordinador cerró (flujo completo a nivel coordinador) |

La lógica exacta evoluciona con los PATCH; usa el campo `status` del detalle como fuente en cliente.

---

## 5. Ejemplo cURL (login + lista visitas)

```bash
BASE="http://localhost:8080/api"

# Login
TOKEN=$(curl -s -X POST "$BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usuario":"mlopez","password":"secreto12","perfil":"PEC"}' \
  | jq -r '.token')

# Listar visitas del promotor
curl -s "$BASE/visitas" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## 6. Resumen de endpoints por método

| Método | Ruta | Notas |
|--------|------|--------|
| POST | `/auth/register` | Público |
| POST | `/auth/login` | Público |
| GET | `/auth/me` | JWT |
| POST | `/devices/fcm-token` | JWT |
| GET | `/users/promotores` | JWT + roles |
| PUT | `/users/{id}` | JWT |
| POST | `/visitas` | JWT; `COORDINADOR` o `DIRECTOR` |
| GET | `/visitas` | JWT; `?scope=` opcional |
| GET | `/visitas/{id}` | JWT |
| GET | `/visitas/{id}/validation` | JWT |
| PUT | `/visitas/{id}` | JWT; `COORDINADOR` o `DIRECTOR` |
| PATCH | `/visitas/{id}/promoter-arrival` | JWT; `PEC` |
| PATCH | `/visitas/{id}/promoter-close` | JWT; `PEC` |
| PATCH | `/visitas/{id}/coordinator-checkin` | JWT; `COORDINADOR` |
| PATCH | `/visitas/{id}/coordinator-close` | JWT; `COORDINADOR` |
| POST | `/visitas/{id}/registrar-entrada` | JWT; `PEC` (legacy) |
| POST | `/visit-evidences` | JWT |
| GET | `/visit-evidences/visit/{visitId}` | JWT |
| GET | `/files/evidencias/{fileName}` | JWT; descarga/renderiza imagen de evidencia |
| POST | `/visit-reviews` | JWT; `ESPECIALISTA` |
| GET | `/visit-reviews/visit/{visitId}` | JWT |
| GET | `/director/overview` | JWT; `DIRECTOR` |
| GET | `/director/reports` | JWT; `DIRECTOR` |

---

## 7. CORS y app móvil

La API está preparada para cabeceras CORS amplias. Las apps **nativas** no usan CORS como el navegador; si hay WebView o front web, revisa `app.cors.allowed-origin-patterns` en `application.properties`.

---

## 8. Deploy con PostgreSQL en Render

Para publicar la API con persistencia real de datos (PostgreSQL administrado en Render), revisa:
`docs/DEPLOY_RENDER_POSTGRES.md`.

---

*Generado a partir del código del proyecto `api-gestion-visitas`.*
