# spring-demo — Pinaka Java SDK test app

Spring Boot app on port **4002** wired to the local api-gateway at `http://localhost:8080/v1/ingest`.

## Prerequisites

- api-gateway running on `:8080` (`cd garuda/services/api-gateway && go run .`)
- Java 17+, Maven 3.8+

## Setup

Install the Pinaka SDK to your local Maven repo first:

```bash
cd ../../
mvn install -DskipTests
cd examples/spring-demo
```

## Run

```bash
mvn spring-boot:run
```

## Routes

| Route | What it triggers | `handled` |
|---|---|---|
| `GET /` | Health check — no error | — |
| `GET /crash-exception` | `RuntimeException` — caught by `PinakaFilter` | `false` |
| `GET /crash-npe` | `NullPointerException` — caught by `PinakaFilter` | `false` |
| `GET /crash-manual` | `Pinaka.captureError(...)` — returns 200 | `true` |
| `GET /crash-assertion` | `AssertionError` — caught by `PinakaFilter` | `false` |

## What to look for in api-gateway logs

```
ingest: payload received   service=java-demo language=java errorType=RuntimeException ...
ingest: api key validated  orgId=...
ingest: error event stored successfully  eventId=...
```

## Note on PinakaFilter

`PinakaFilter` is a Jakarta Servlet Filter registered via Spring Boot autoconfiguration.
It captures the exception after the response is committed, so the HTTP error response
is still returned to the caller — the SDK never swallows it.
