# Deploy en Render con PostgreSQL

Esta guía deja la API corriendo en Render con base de datos persistente (PostgreSQL administrado), evitando pérdida de datos por reinicios del contenedor.

## 1) Prerrequisitos

- Repositorio en GitHub actualizado.
- Servicio web en Render conectado al repo.
- Este proyecto ya incluye:
  - perfil `local` (H2) para desarrollo,
  - perfil `prod` (PostgreSQL),
  - `render.yaml` con web service + database.

## 2) Crear infraestructura en Render

Si usas Blueprint (`render.yaml`):

1. En Render: **New +** → **Blueprint**.
2. Selecciona el repo `api-gestion-visitas`.
3. Render creará:
   - Web service: `api-gestion-visitas`
   - PostgreSQL: `api-gestion-visitas-db`

Si ya tienes un servicio creado manualmente, crea también una base PostgreSQL administrada en la misma región.

## 3) Variables de entorno (producción)

El `render.yaml` ya define:

- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL` desde `connectionString` de la DB
- `SPRING_DATASOURCE_USERNAME` desde `user` de la DB
- `SPRING_DATASOURCE_PASSWORD` desde `password` de la DB
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`

Además, configura/valida:

- `security.jwt.secret` (obligatorio, no usar secreto de ejemplo)
- `security.jwt.expiration-ms` (si deseas sobrescribir default)

## 4) Build y arranque

El deploy usa Docker:

- `Dockerfile` compila con Maven.
- Comando final: `java -jar target/api-gestion-visitas-0.0.1-SNAPSHOT.jar`

No uses H2 en producción.

## 5) Verificación posterior al deploy

1. Revisar logs: no debe aparecer `jdbc:h2:`.
2. Probar health funcional:
   - `POST /api/auth/register`
   - `POST /api/auth/login`
   - `GET /api/visitas`
3. Crear un registro de prueba y reiniciar el servicio en Render.
4. Verificar que el dato persiste luego del reinicio.

## 6) Desarrollo local

Para local no necesitas PostgreSQL:

- perfil por defecto: `local`
- datasource: H2 archivo (`./data/visitasdb`)

Para ejecutar local explícito:

```bash
SPRING_PROFILES_ACTIVE=local
```

## 7) Troubleshooting rápido

- **Falla de conexión a DB**:
  - validar que `SPRING_PROFILES_ACTIVE=prod`
  - revisar `SPRING_DATASOURCE_*` y que apunten a la DB correcta.
- **Errores SSL**:
  - en Render usa `connectionString` administrada (incluye parámetros requeridos).
- **Datos no persisten**:
  - confirmar que estás en PostgreSQL y no H2.
- **Conflictos de esquema**:
  - revisar logs Hibernate al arranque.
  - como mejora futura, migrar a Flyway/Liquibase.
