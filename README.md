# Alarm Walutowy

Monorepo mikroserwisowej aplikacji do monitorowania kursów walut i powiadamiania użytkowników o istotnych zmianach.

---

## Spis treści

* [Cel projektu](#cel-projektu)
* [Architektura](#architektura)
* [Wybór technologii](#wybór-technologii)
* [Wymagania funkcjonalne](#wymagania-funkcjonalne)
* [Model domeny i schemat danych](#model-domeny-i-schemat-danych)
* [Kontrakty komunikacji](#kontrakty-komunikacji)
* [API DataProvider (REST)](#api-dataprovider-rest)
* [Uruchomienie lokalne (Docker Compose)](#uruchomienie-lokalne-docker-compose)
* [Konfiguracja i zmienne środowiskowe](#konfiguracja-i-zmienne-środowiskowe)
* [Bezpieczeństwo](#bezpieczeństwo)
* [Monitoring i logowanie](#monitoring-i-logowanie)
* [Testy](#testy)
* [CI/CD](#cicd)
* [Plan wdrożenia / Roadmapa](#plan-wdrożenia--roadmapa)
* [FAQ dla deweloperów](#faq-dla-deweloperów)

---

## Cel projektu

„Alarm Walutowy” dostarcza w (niemal) czasie rzeczywistym informacje o zmianach kursów walut oraz wysyła powiadomienia, gdy zmiana przekroczy zadany próg procentowy skonfigurowany przez użytkownika.

---

## Architektura

Dwa mikroserwisy + infrastruktura wspólna.

```
┌──────────────────┐      ┌────────────────────┐
│  Zewn. dostawcy  │      │  DataGatherer      │
│  kursów (API)    │──▶──▶│  (scheduler + MQ)  │
└──────────────────┘      └─────────┬──────────┘
                                     │
                                RabbitMQ (MQ)
                                     │
                            ┌─────────▼──────────┐
                            │   DataProvider     │
                            │ (REST + consumer)  │
                            └─────────┬──────────┘
                                      │
                         PostgreSQL    │     Mail (SMTP)
                                      ▼
                                 Klienci (REST)
```

* **DataGatherer** – cyklicznie odpytuje wybrane API kursów (plan: co 1 h) i przy istotnych zmianach publikuje komunikat do kolejki.
* **DataProvider** – konsumuje komunikaty, zapisuje dane do bazy, zarządza użytkownikami/subskrypcjami, wystawia REST API i wysyła e‑maile.
* **Brak widoków** – wyłącznie REST API.

---

## Wybór technologii

Poniżej rekomendowana (spójna) ścieżka w Javie. Alternatywy wskazane kursywą.

**Język/Frameworki**

* Java 21, **Spring Boot 3.3+** (Security, Web, Validation, AMQP, Data JPA, Mail)
* Konsument/producent MQ: **RabbitMQ** (AMQP 0.9.1) *(alternatywnie: Kafka)*
* Baza danych: **PostgreSQL 16**
* Migracje schematu: **Flyway**
* Dokumentacja API: **springdoc-openapi** (Swagger UI)
* JWT: **Spring Security + jjwt**
* Harmonogram: **Spring Scheduling** *(lub Quartz)*
* E‑mail: **Spring Mail + MailHog (dev)** *(prod: dowolny SMTP)*
* Build: **Maven** *(lub Gradle)*
* Konteneryzacja: **Docker / Docker Compose**
* Observability: **Micrometer + Prometheus + Grafana**, logi w **JSON**

Struktura repo (monorepo):

```
/infra/                # docker-compose, pliki konfig.
/data-gatherer/        # Spring Boot app (producer)
/data-provider/        # Spring Boot app (consumer + REST)
/docs/                 # diagramy, ADR, OpenAPI
```

---

## Wymagania funkcjonalne

### DataGatherer

* **Pobieranie danych**: harmonogram co 1h (cron `0 0 * * * *`), zapytania do wybranego dostawcy (np. Open Exchange Rates).
* **Analiza zmian**: porównanie ostatniego znanego kursu z nowym; jeśli `|Δ%| ≥ próg_globalny` lub kurs dotyczy aktywnego instrumentu – publikacja do MQ.
* **Wiadomość**: zawiera co najmniej `base`, `symbol`, `rate`, `timestamp_source`.

### DataProvider

* **Konsumpcja danych**: zapis do tabel `exchange_rate` i `exchange_rate_history` z idempotencją (deduplikacja po `symbol+timestamp`).
* **Użytkownicy**: rejestracja/logowanie, JWT, role `USER`/`ADMIN`.
* **Subskrypcje**: CRUD subskrypcji (`symbol`, `threshold_percent`, `direction` [UP/DOWN/ANY]).
* **Powiadomienia**: e‑mail przy przekroczeniu progu; możliwość `digest daily` i `instant`.
* **API publiczne**: odczyt aktualnych kursów i historii.

---

## Model domeny i schemat danych

Minimalny schemat relacyjny (PostgreSQL):

```
users (
  id uuid PK,
  email citext UNIQUE NOT NULL,
  password_hash text NOT NULL,
  created_at timestamptz NOT NULL,
  is_active boolean NOT NULL DEFAULT true,
  role text NOT NULL DEFAULT 'USER'
)

subscriptions (
  id uuid PK,
  user_id uuid FK -> users(id),
  symbol text NOT NULL,          -- np. "EUR/PLN"
  threshold_percent numeric(6,3) NOT NULL, -- np. 1.5 = 1.5%
  direction text NOT NULL CHECK (direction IN ('UP','DOWN','ANY')),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL
)

exchange_rate (
  id uuid PK,
  base text NOT NULL,            -- np. "USD"
  symbol text NOT NULL,          -- np. "PLN"
  rate numeric(18,8) NOT NULL,
  as_of timestamptz NOT NULL,    -- czas zródła
  created_at timestamptz NOT NULL,
  UNIQUE(base, symbol)           -- aktualny ostatni znany kurs
)

exchange_rate_history (
  id uuid PK,
  base text NOT NULL,
  symbol text NOT NULL,
  rate numeric(18,8) NOT NULL,
  as_of timestamptz NOT NULL,
  ingested_at timestamptz NOT NULL,
  UNIQUE(base, symbol, as_of)
)

notifications (
  id uuid PK,
  user_id uuid FK -> users(id),
  symbol text NOT NULL,
  change_percent numeric(8,4) NOT NULL,
  direction text NOT NULL,
  rate_before numeric(18,8) NOT NULL,
  rate_after numeric(18,8) NOT NULL,
  triggered_at timestamptz NOT NULL,
  sent_at timestamptz NULL,
  channel text NOT NULL DEFAULT 'EMAIL'
)

message_outbox (
  id uuid PK,
  aggregate_type text NOT NULL,
  aggregate_id uuid NOT NULL,
  payload jsonb NOT NULL,
  created_at timestamptz NOT NULL,
  sent_at timestamptz NULL
)
```

Uwagi:

* `exchange_rate` przechowuje ostatni znany kurs, a pełna historia w `exchange_rate_history`.
* `message_outbox` do wzorca **Transactional Outbox** (opcjonalne, przydatne przy skalowaniu powiadomień).

---

## Kontrakty komunikacji

### Wiadomość z DataGatherer → MQ → DataProvider (JSON)

```json
{
  "event": "EXCHANGE_RATE_CHANGED",
  "base": "USD",
  "symbol": "PLN",
  "rate": 3.9876,
  "timestamp_source": "2025-09-25T10:00:00Z",
  "provider": "openexchangerates",
  "trace_id": "c7b3f4c2-..."
}
```

Nagłówki AMQP (zalecane): `content-type: application/json`, `message-id`, `x-trace-id`, `x-dedup-key = base|symbol|timestamp_source`.

Idempotencja po `base+symbol+timestamp_source`.

---

## API DataProvider (REST)

Dokumentacja w Swagger UI: `/swagger-ui.html` (springdoc-openapi).

### Autoryzacja

`Authorization: Bearer <JWT>` dla endpointów chronionych.

### Endpoints (skrót)

```
POST   /api/v1/auth/register         # rejestracja
POST   /api/v1/auth/login            # zwrot JWT
GET    /api/v1/rates                 # aktualne kursy (query: base, symbols[])
GET    /api/v1/rates/{base}/{symbol}/history?from=&to=&limit=

GET    /api/v1/subscriptions         # lista moich subskrypcji
POST   /api/v1/subscriptions         # {symbol, threshold_percent, direction}
PATCH  /api/v1/subscriptions/{id}    # modyfikacja
DELETE /api/v1/subscriptions/{id}    # usunięcie

POST   /api/v1/notifications/test    # wysyłka testowa (dla zalog.
                                     # użytkownika) – środ.
```

### Kody błędów (przykłady)

* `400` – walidacja, niepoprawne parametry
* `401` – brak/niepoprawny JWT
* `403` – brak uprawnień
* `404` – nie znaleziono
* `409` – konflikt (duplikat subskrypcji)
* `429` – rate limit
* `500` – błąd serwera

### Przykładowe żądania

```bash
# rejestracja
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"S3cret!pass"}'

# logowanie
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"S3cret!pass"}'

# dodanie subskrypcji (Authorization: Bearer <JWT>)
curl -X POST http://localhost:8080/api/v1/subscriptions \
  -H 'Content-Type: application/json' -H 'Authorization: Bearer <JWT>' \
  -d '{"symbol":"EUR/PLN","threshold_percent":1.5,"direction":"ANY"}'
```

---

## Uruchomienie lokalne (Docker Compose)

Minimalny stack dev: RabbitMQ, Postgres, MailHog, oba serwisy.

```yaml
# infra/docker-compose.yml
version: '3.9'
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: alarmwalutowy
      POSTGRES_USER: app
      POSTGRES_PASSWORD: app
    ports: ["5432:5432"]
    volumes:
      - pgdata:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"   # UI

  mailhog:
    image: mailhog/mailhog
    ports:
      - "1025:1025"     # SMTP
      - "8025:8025"     # UI

  data-provider:
    build: ../data-provider
    env_file:
      - ../data-provider/.env.dev
    depends_on: [postgres, rabbitmq, mailhog]
    ports: ["8080:8080"]

  data-gatherer:
    build: ../data-gatherer
    env_file:
      - ../data-gatherer/.env.dev
    depends_on: [rabbitmq]

volumes:
  pgdata: {}
```

Uruchomienie:

```bash
cd infra && docker compose up --build
# DataProvider -> http://localhost:8080/swagger-ui.html
# RabbitMQ UI -> http://localhost:15672 (guest/guest)
# MailHog UI -> http://localhost:8025
```

---

## Konfiguracja i zmienne środowiskowe

### DataGatherer – `.env.dev`

```
SPRING_PROFILES_ACTIVE=dev
DG_PROVIDER=openexchangerates
DG_BASE=USD
DG_SYMBOLS=PLN,EUR,GBP
DG_FETCH_CRON=0 0 * * * *
DG_API_URL=https://openexchangerates.org/api/latest.json
DG_API_KEY=changeme
AMQP_HOST=rabbitmq
AMQP_PORT=5672
AMQP_USERNAME=guest
AMQP_PASSWORD=guest
AMQP_EXCHANGE=fx.events
AMQP_ROUTING_KEY=rate.changed
AMQP_QUEUE=fx.rate.changed
```

### DataProvider – `.env.dev`

```
SPRING_PROFILES_ACTIVE=dev
DB_HOST=postgres
DB_PORT=5432
DB_NAME=alarmwalutowy
DB_USER=app
DB_PASS=app
AMQP_HOST=rabbitmq
AMQP_PORT=5672
AMQP_USERNAME=guest
AMQP_PASSWORD=guest
AMQP_QUEUE=fx.rate.changed
JWT_SECRET=please-change
JWT_EXP_MIN=60
MAIL_HOST=mailhog
MAIL_PORT=1025
MAIL_FROM=no-reply@alarmwalutowy.local
RATE_LIMIT_PER_MIN=120
```

---

## Bezpieczeństwo

* **JWT** dla autoryzacji, rotacja sekretu (env per środowisko), krótkie TTL + refresh (opcjonalnie).
* **Rate limiting** (np. Bucket4j) na endpointach publicznych.
* **Walidacja** payloadów (Jakarta Validation), sanity‑check dla `symbol`.
* **Sekrety** poza repo (env / secret manager).
* **TLS** (prod za ingress/proxy), HSTS, CORS whitelist.
* **Idempotencja** konsumenta MQ + deduplikacja.

---

## Monitoring i logowanie

* **Micrometer** → Prometheus (metryki: czas pobierania, opóźnienia MQ, liczba notyfikacji, błędy 4xx/5xx).
* **Grafana** – dashboardy usług.
* Logi strukturalne (JSON), korelacja `trace_id` z MQ → REST.

---

## Testy

* **Jednostkowe**: JUnit 5 + Mockito (serwisy, walidacja).
* **Integracyjne**: Spring Boot Test + Testcontainers (Postgres, RabbitMQ).
* **Kontraktowe**: Spring Cloud Contract (DataGatherer ↔ DataProvider, REST API ↔ klienci).
* **End‑to‑end (opcjonalnie)**: scenariusze Gherkin (Cucumber).

Przykładowy scenariusz e2e:

1. Rejestracja użytkownika i logowanie.
2. Utworzenie subskrypcji `EUR/PLN, 1.5%, ANY`.
3. Wysłanie do MQ komunikatu z `Δ% = 2%`.
4. Sprawdzenie, że e‑mail trafił do MailHog i zapisano rekord w `notifications`.

---

## CI/CD

* **CI**: GitHub Actions/ GitLab CI – kroki: build, test, kontrakty, obrazy Docker, skany (OWASP Dependency‑Check, Trivy).
* **CD**: push obrazów do rejestru, deployment na środowiska (Helm chart dla K8s lub ECS), migracje Flyway on‑startup.
* Tagowanie wersji: `vMAJOR.MINOR.PATCH`, migracje `V__*.sql`.

---

## Plan wdrożenia / Roadmapa

**Milestone 1 – Szkielet projektu** (1–2 dni)

* Monorepo, moduły, Docker Compose, puste aplikacje Spring Boot, health‑checki.
* Konfiguracja RabbitMQ, Postgres, MailHog.

**Milestone 2 – Autoryzacja i użytkownicy** (2–3 dni)

* Rejestracja/logowanie, JWT, testy.

**Milestone 3 – Subskrypcje + modele danych** (2–3 dni)

* Tabele, CRUD, walidacja, ograniczenia unikalności, testy repozytoriów.

**Milestone 4 – Integracja MQ i historia kursów** (3–4 dni)

* Konsumpcja komunikatów, idempotencja, zapis historii, metryki.

**Milestone 5 – DataGatherer (scheduler + provider)** (3–4 dni)

* Klient HTTP do dostawcy, harmonogram, logika progów, publikacja MQ.

**Milestone 6 – Powiadomienia** (2–3 dni)

* Reguły triggerów, e‑maile, MailHog, preferencje kanału (na start: EMAIL), testy e2e.

**Milestone 7 – Twardnienie i obserwowalność** (2 dni)

* Rate limiting, dashboardy, alerty, cleanup.

---

## FAQ dla deweloperów

* **Skąd kurs bazowy?** Trzymamy `base=USD` (konfigurowalne); pary raportujemy jako `BASE/SYMBOL`, np. `USD/PLN`.
* **Ile API dostawców?** Na start jeden (Open Exchange Rates). Interfejs `RateProvider` umożliwia dołączanie kolejnych (NBP, ECB) z fallbackiem.
* **Czy potrzebny cache?** W DataProvider można dodać krótki cache GET (`/rates`) na 30–60 s (Caffeine), by odciążyć DB.
* **Czy potrzebny outbox?** Przy wzroście ruchu tak – ułatwia niezawodną wysyłkę wieloma kanałami (e‑mail/SMS/push).

---

## Załączniki

* `/docs/openapi.yaml` – definicja API (do uzupełnienia w trakcie prac).
* Diagramy C4/Sequence (PlantUML / draw.io) – opcjonalnie.
