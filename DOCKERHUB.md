# ODM Platform Marketplace Server

The Marketplace Server is the product-plane marketplace service of [Open Data Mesh Platform](https://github.com/opendatamesh-initiative) V2. It receives access requests to data products and their output ports, stores them, and forwards them to marketplace executors: adapters, such as the [Jira](https://github.com/opendatamesh-initiative/odm-platform-adapter-marketplace-executor-jira) and [ServiceNow](https://github.com/opendatamesh-initiative/odm-platform-adapter-marketplace-executor-servicenow) executors, that run the approval and provisioning process. When an executor sends back its result, the Marketplace stores it and dispatches a `MARKETPLACE_EXECUTOR_RESULT_RECEIVED` event to the Notification service.

- **Source code:** [opendatamesh-initiative/odm-platform-pp-marketplace-server](https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server)
- **Documentation:** [README](https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server/blob/master/README.md)
- **Issues:** [GitHub issues](https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server/issues)

## Tags

Each [GitHub release](https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server/releases) publishes one image tagged with its version, without the `v` prefix:

```bash
docker pull opendatamesh/odm-platform-pp-marketplace:<version>
```

There is no `latest` tag: always pin a version.

## Requirements

| Dependency | Required | Notes |
| --- | --- | --- |
| PostgreSQL | Yes | Flyway creates and migrates the schema on startup. |
| Marketplace executor | Yes, to submit access requests | At least one active executor must accept a request, otherwise the submission fails. Executors must be able to reach the Marketplace to send results back. |
| [Notification service](https://hub.docker.com/r/opendatamesh/odm-platform-notification) | No | Receives an event for each executor result. When it's disabled, events are only logged. |

## Quick start

Starts the Marketplace against an existing PostgreSQL database, with Notification disabled and no executors. Use it to check that the container starts and connects to the database: without executors, access request submissions fail.

```bash
docker run --name odm-marketplace -p 8080:8080 \
  -e DB_JDBC_URL=jdbc:postgresql://db.example.com:5432/marketplace \
  -e DB_USERNAME=marketplace \
  -e DB_PASSWORD=change-me \
  -e ODM_PRODUCT_PLANE_NOTIFICATION_SERVICE_ACTIVE=false \
  -e ODM_PRODUCT_PLANE_NOTIFICATION_SERVICE_ADDRESS=http://localhost:8006 \
  opendatamesh/odm-platform-pp-marketplace:<version>
```

The Notification address is required even when Notification is disabled; any value works then.

When it's up, the API (prefix `/api/v1/pp/marketplace/`) is documented at `http://localhost:8080/swagger-ui.html` (OpenAPI at `/v3/api-docs`).

## Production-like run

Spring Boot reads nested configuration from the `SPRING_APPLICATION_JSON` environment variable. This example enables Notification and configures one executor:

```bash
docker run --name odm-marketplace -p 8080:8080 \
  -e DB_JDBC_URL=jdbc:postgresql://db.example.com:5432/marketplace \
  -e DB_USERNAME=marketplace \
  -e DB_PASSWORD=change-me \
  -e SPRING_APPLICATION_JSON='{
    "odm": {
      "product-plane": {
        "notification-service": { "active": true, "address": "http://notification.example.com:8006" }
      },
      "utility-plane": {
        "marketplace-executors": [
          { "name": "jira", "address": "http://executor.example.com:8080", "active": true }
        ]
      }
    }
  }' \
  opendatamesh/odm-platform-pp-marketplace:<version>
```

The Marketplace sends each new access request to every active executor, at `<address>/api/v1/up/marketplace-executor/requests`. The submission succeeds if at least one executor accepts it. Executors send their result to `POST /api/v1/pp/marketplace/requests/{uuid}/results`.

## Environment variables

### Image

| Variable | Description | Default |
| --- | --- | --- |
| `DB_JDBC_URL` | JDBC URL of the PostgreSQL database (`spring.datasource.url`). | none, required |
| `DB_USERNAME` | Database user (`spring.datasource.username`). | none, required |
| `DB_PASSWORD` | Database password (`spring.datasource.password`). | none, required |
| `PROFILES_ACTIVE` | Spring profiles to activate. The `docker` profile maps the three `DB_*` variables above. It overrides `SPRING_PROFILES_ACTIVE`. | `docker` |
| `JAVA_OPTS` | Extra JVM options, for example `-Xmx512m`. | empty |
| `SPRING_PROPS` | Extra arguments added to the `java` command line before `-jar`, such as `-Dserver.port=9000`. It does not accept JSON: use `SPRING_APPLICATION_JSON` for that. | empty |

### Application

Every property can be set with `SPRING_APPLICATION_JSON` or as an environment variable, using Spring Boot relaxed binding (for example, `odm.product-plane.notification-service.active` becomes `ODM_PRODUCT_PLANE_NOTIFICATION_SERVICE_ACTIVE`). The executor list is easier to set with `SPRING_APPLICATION_JSON`.

| Property | Description | Default |
| --- | --- | --- |
| `server.port` | HTTP port. | `8080` |
| `spring.jpa.properties.hibernate.default_schema` | Database schema used by Hibernate and Flyway. Lowercase only: the server doesn't start otherwise. | `odm_marketplace` |
| `odm.product-plane.notification-service.active` | Dispatch events to the Notification service (`true`) or only log them (`false`). | none, required |
| `odm.product-plane.notification-service.address` | Base URL of the Notification service. | none, required |
| `odm.utility-plane.marketplace-executors` | List of marketplace executors. Each entry has `name` (used in logs), `address` (base URL of the executor) and `active` (`false` if omitted: inactive executors are skipped). | empty |

## Ports and volumes

- **Port:** `8080` (HTTP), or the value of `server.port`.
- **Volumes:** none. All state is in PostgreSQL.

## Learn more

- [Access request flow](https://github.com/opendatamesh-initiative/odm-platform-pp-marketplace-server/blob/master/README.md#access-request-management)
- [Marketplace executor starter](https://github.com/opendatamesh-initiative/odm-platform-adapter-marketplace-executor-starter), to build your own executor
- [Notification service](https://github.com/opendatamesh-initiative/odm-platform-pp-notification-server)

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
