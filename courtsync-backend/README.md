# CourtSync Backend

Spring Boot 3 REST API за платформата за резервация на спортни зали CourtSync. Осигурява автентикация, търсене/резервация на спортни зали, управление на резервации и chat endpoint за AI треньор, задвижван от OpenAI.

## Технологичен стек

- **Java 17**, **Spring Boot 3.2.0**
- **Spring Web** - REST контролери
- **Spring Data JPA / Hibernate** - постоянство на данните, с MySQL 8 като целеви диалект
- **Spring Security 6** - stateless JWT автентикация (lambda DSL конфигурация)
- **jjwt 0.12.3** (`io.jsonwebtoken`) - издаване/парсиране на JWT
- **MySQL** (`mysql-connector-j`) - база данни
- **Lombok** — намаляване на шаблонния код (`@Data`, `@Builder`, `@RequiredArgsConstructor`, …)
- **Bean Validation** (`spring-boot-starter-validation`) - валидация на request DTO-та
- **OpenAI API** (GPT-4o-mini) - чат за AI треньора, извикван директно през `RestTemplate`/HTTP, не чрез SDK

## Структура на проекта

```
src/main/java/com/courtsync/
├── CourtSyncApplication.java     Входна точка на Spring Boot
├── config/                       SecurityConfig, CorsConfig
├── security/                     JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── entity/                       JPA ентитети (User, SportHall, Sport, Reservation, Review, Favorite, AIConversation, AIMessage)
├── repository/                   Spring Data JPA хранилища (repositories)
├── service/                      Бизнес логика (AuthService, SportHallService, ReservationService, UserService, AIService)
├── controller/                   REST контролери
├── dto/                          DTO-та за заявки/отговори
└── exception/                    GlobalExceptionHandler (@RestControllerAdvice)

src/main/resources/
├── application.properties        Конфигурация (вижте по-долу)
└── data.sql                      Начални данни (спортове + реални спортни зали от София), изпълнява се при всяко стартиране
```

## Предварителни изисквания

- JDK 17+
- Maven (или използвайте включения wrapper, ако е наличен, иначе локална инсталация на Maven)
- MySQL 8+, работещ локално (или достъпен по мрежата)
- OpenAI API ключ (опционално - AI треньорът се връща към готов отговор, ако ключът липсва/е невалиден)

## Конфигурация

Цялата конфигурация се намира в `src/main/resources/application.properties`.

```properties
spring.application.name=courtsync-backend

# MySQL DataSource
spring.datasource.url=jdbc:mysql://localhost:3306/courtsync_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Seed data — runs data.sql on every startup (upserts sports + demo halls)
spring.sql.init.mode=always
spring.sql.init.continue-on-error=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Server
server.port=8080
server.error.include-message=always

# Logging
logging.level.com.courtsync=DEBUG
logging.level.org.springframework.security=WARN

# OpenAI (AI Coach)
openai.api.key=${OPENAI_API_KEY}
openai.model=gpt-4o-mini
```

| Свойство | Описание |
|---|---|
| `spring.datasource.*` | MySQL връзка. Базата данни (`courtsync_db`) се създава автоматично, ако не съществува. |
| `jwt.secret` | Base64-кодиран HMAC-SHA ключ, използван за подписване/проверка на JWT токени (`Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))`). Трябва да е поне 256 бита след декодиране. |
| `jwt.expiration` | Времетраене на токена в милисекунди (по подразбиране: 24ч). |
| `openai.api.key` | Използва се от `AIService` за endpoint-а `/api/ai/chat`. Ако липсва или е невалиден, услугата плавно се връща към резервен отговор, вместо да провали заявката. |
| `spring.sql.init.mode=always` + `data.sql` | Зарежда/обновява таблицата `sports` и 4 реални спортни зали от София (Арена Исаев, Спортен комплекс "Мир и дружба", Тенис клуб "Про Спорт", Овергаз Арена) при всяко стартиране. Безопасно е за повторно изпълнение — използва `ON DUPLICATE KEY UPDATE`. |

Генериране на JWT таен ключ (трябва да е валиден Base64):

```bash
openssl rand -base64 32
```

## Локално стартиране

```bash
# from courtsync-backend/
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -base64 32)
export OPENAI_API_KEY=sk-...           # optional

mvn spring-boot:run
```

API-то стартира на `http://localhost:8080`. От Android емулатор е достъпно на `http://10.0.2.2:8080/` (вижте [Android README](../CourtSyncApp/README.md)).

Компилиране на jar файл:

```bash
mvn clean package
java -jar target/courtsync-backend-1.0.0.jar
```

## Модел на данните

| Ентитет | Бележки |
|---|---|
| `User` | `Role` enum: `USER`, `HALL_OWNER`, `ADMIN`. BCrypt-хеширана парола, баланс от кредити, статистика за резервации. |
| `Sport` | Справочна таблица (Баскетбол, Футбол, Тенис, Падел, Волейбол, Бадминтон). |
| `SportHall` | `HallType` enum: `INDOOR`, `OUTDOOR`, `PREMIUM`, `CLAY`, `GRASS`. Принадлежи към един `Sport`, има географска ширина/дължина, цена/час, рейтинг, работно време. |
| `Reservation` | `Status` enum: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`. Изчислена `totalPrice = pricePerHour × часове`. |
| `Review` | Рейтинг/коментар за зала (1–5), свързан с потребител и зала. |
| `Favorite` | Връзка много-към-много потребител↔зала, уникална за всяка двойка (потребител, зала). |
| `AIConversation` / `AIMessage` | Запазена история на чата за AI треньора, `Role` enum: `USER`, `ASSISTANT`. |

## Сигурност

- Stateless JWT автентикация — хедър `Authorization: Bearer <token>`, валидиран от `JwtAuthFilter` (`OncePerRequestFilter`) преди `UsernamePasswordAuthenticationFilter` на Spring Security.
- Паролите се хешират с `BCryptPasswordEncoder`.
- `/api/auth/**` и `/api/halls`, `/api/halls/**` са публични; всичко останало изисква валиден токен (вижте `SecurityConfig`).
- CORS е разрешителен (`CorsConfig`), за да улесни локалната разработка срещу Android емулатора — затегнете това преди каквото и да е продукционно внедряване.

## API справочник

Базов път: `/api`

### Автентикация (публично)

| Метод | Път | Описание |
|---|---|---|
| `POST` | `/auth/register` | Създаване на акаунт. Тяло: `fullName`, `email`, `password`. Връща JWT + потребителски профил. |
| `POST` | `/auth/login` | Автентикация. Тяло: `email`, `password`. Връща JWT + потребителски профил. |

### Спортни зали (публично)

| Метод | Път | Описание |
|---|---|---|
| `GET` | `/halls/recommended` | Топ 10 зали по рейтинг. |
| `GET` | `/halls` | Търсене/странициране на зали. Query параметри: `query`, `sportId`, `sortBy` (`rating_asc/desc`, `price_asc/desc`, `name_asc/desc`), `page`, `size`. |
| `GET` | `/halls/{id}` | Детайли за зала. |
| `POST` | `/halls/{id}/favorite` | Превключва любимо за автентикирания потребител (изисква автентикация). |

### Резервации (изисква автентикация)

| Метод | Път | Описание |
|---|---|---|
| `GET` | `/reservations/upcoming` | Предстоящи резервации на автентикирания потребител. |
| `GET` | `/reservations/past` | Минали резервации на автентикирания потребител. |
| `POST` | `/reservations` | Създаване на резервация. Тяло: `hallId`, `date`, `startTime`, `endTime`. |
| `DELETE` | `/reservations/{id}` | Отказ на резервация. |

### Потребители (изисква автентикация)

| Метод | Път | Описание |
|---|---|---|
| `GET` | `/users/me` | Профил на текущия потребител. |
| `PUT` | `/users/me` | Обновяване на полета от профила. |

### AI Треньор (изисква автентикация)

| Метод | Път | Описание |
|---|---|---|
| `POST` | `/ai/chat` | Изпраща съобщение до AI треньора; връща отговор от асистента, опционално с предложена зала, като историята се запазва за всеки потребител. |

Всички отговори за грешки са оформени от `GlobalExceptionHandler` (`@RestControllerAdvice`), покривайки грешки при валидация, невалидни идентификационни данни и общи runtime грешки с последователни JSON тела за грешки.

## Бележки относно формата на JWT тайния ключ

`JwtUtil` декодира `jwt.secret` от Base64 и изгражда ключа за подписване с `Keys.hmacShaKeyFor(...)`, а парсира/проверява токени чрез `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)` — API-то на jjwt 0.12.x. Ако смените тайния ключ, всички издадени преди това токени стават невалидни.

---

# CourtSync Backend

Spring Boot 3 REST API for the CourtSync sports hall booking platform. Provides authentication, sport hall search/booking, reservation management, and an AI coach chat endpoint backed by OpenAI.

## Tech stack

- **Java 17**, **Spring Boot 3.2.0**
- **Spring Web** - REST controllers
- **Spring Data JPA / Hibernate** - persistence, with MySQL 8 as the target dialect
- **Spring Security 6** - stateless JWT authentication (lambda DSL configuration)
- **jjwt 0.12.3** (`io.jsonwebtoken`) - JWT issuing/parsing
- **MySQL** (`mysql-connector-j`) - database
- **Lombok** — boilerplate reduction (`@Data`, `@Builder`, `@RequiredArgsConstructor`, …)
- **Bean Validation** (`spring-boot-starter-validation`) - request DTO validation
- **OpenAI API** (GPT-4o-mini) - AI Coach chat, called directly via `RestTemplate`/HTTP, not an SDK

## Project layout

```
src/main/java/com/courtsync/
├── CourtSyncApplication.java     Spring Boot entry point
├── config/                       SecurityConfig, CorsConfig
├── security/                     JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── entity/                       JPA entities (User, SportHall, Sport, Reservation, Review, Favorite, AIConversation, AIMessage)
├── repository/                   Spring Data JPA repositories
├── service/                      Business logic (AuthService, SportHallService, ReservationService, UserService, AIService)
├── controller/                   REST controllers
├── dto/                          Request/response DTOs
└── exception/                    GlobalExceptionHandler (@RestControllerAdvice)

src/main/resources/
├── application.properties        Configuration (see below)
└── data.sql                      Seed data (sports + real Sofia sport halls), runs on every startup
```

## Prerequisites

- JDK 17+
- Maven (or use the included wrapper if present, otherwise a local Maven install)
- MySQL 8+ running locally (or reachable over network)
- An OpenAI API key (optional - the AI Coach falls back to a canned response if the key is missing/invalid)

## Configuration

All configuration lives in `src/main/resources/application.properties`. **Do not commit real credentials to this file.** Use environment variable placeholders and provide the actual values via your shell, an untracked `application-local.properties`, or your IDE's run configuration.

```properties
spring.application.name=courtsync-backend

# MySQL DataSource
spring.datasource.url=jdbc:mysql://localhost:3306/courtsync_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Seed data — runs data.sql on every startup (upserts sports + demo halls)
spring.sql.init.mode=always
spring.sql.init.continue-on-error=true

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Server
server.port=8080
server.error.include-message=always

# Logging
logging.level.com.courtsync=DEBUG
logging.level.org.springframework.security=WARN

# OpenAI (AI Coach)
openai.api.key=${OPENAI_API_KEY}
openai.model=gpt-4o-mini
```

| Property | Description |
|---|---|
| `spring.datasource.*` | MySQL connection. The DB (`courtsync_db`) is auto-created if it doesn't exist. |
| `jwt.secret` | Base64-encoded HMAC-SHA key used to sign/verify JWTs (`Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))`). Must be at least 256 bits once decoded. |
| `jwt.expiration` | Token lifetime in milliseconds (default: 24h). |
| `openai.api.key` | Used by `AIService` for the `/api/ai/chat` endpoint. If omitted or invalid, the service degrades gracefully to a fallback response instead of failing the request. |
| `spring.sql.init.mode=always` + `data.sql` | Seeds/upserts the `sports` lookup table and 4 real Sofia sport halls (Арена Исаев, Спортен комплекс "Мир и дружба", Тенис клуб "Про Спорт", Овергаз Арена) on every boot. Safe to re-run — uses `ON DUPLICATE KEY UPDATE`. |

Generate a JWT secret (must be valid Base64):

```bash
openssl rand -base64 32
```

## Running locally

```bash
# from courtsync-backend/
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -base64 32)
export OPENAI_API_KEY=sk-...           # optional

mvn spring-boot:run
```

The API starts on `http://localhost:8080`. From an Android emulator, it's reachable at `http://10.0.2.2:8080/` (see the [Android README](../CourtSyncApp/README.md)).

Build a jar:

```bash
mvn clean package
java -jar target/courtsync-backend-1.0.0.jar
```

## Data model

| Entity | Notes |
|---|---|
| `User` | `Role` enum: `USER`, `HALL_OWNER`, `ADMIN`. BCrypt-hashed password, credits balance, booking stats. |
| `Sport` | Lookup table (Basketball, Football, Tennis, Padel, Volleyball, Badminton). |
| `SportHall` | `HallType` enum: `INDOOR`, `OUTDOOR`, `PREMIUM`, `CLAY`, `GRASS`. Belongs to one `Sport`, has lat/lng, price/hour, rating, opening hours. |
| `Reservation` | `Status` enum: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`. Computed `totalPrice = pricePerHour × hours`. |
| `Review` | Hall rating/comment (1–5), tied to a user and a hall. |
| `Favorite` | User↔hall many-to-many join, unique per (user, hall). |
| `AIConversation` / `AIMessage` | Persisted chat history for the AI Coach, `Role` enum: `USER`, `ASSISTANT`. |

## Security

- Stateless JWT auth — `Authorization: Bearer <token>` header, validated by `JwtAuthFilter` (a `OncePerRequestFilter`) before Spring Security's `UsernamePasswordAuthenticationFilter`.
- Passwords hashed with `BCryptPasswordEncoder`.
- `/api/auth/**` and `/api/halls`, `/api/halls/**` are public; everything else requires a valid token (see `SecurityConfig`).
- CORS is permissive (`CorsConfig`) to simplify local development against the Android emulator — tighten this before any production deployment.

## API reference

Base path: `/api`

### Auth (public)

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/register` | Create an account. Body: `fullName`, `email`, `password`. Returns a JWT + user profile. |
| `POST` | `/auth/login` | Authenticate. Body: `email`, `password`. Returns a JWT + user profile. |

### Sport halls (public)

| Method | Path | Description |
|---|---|---|
| `GET` | `/halls/recommended` | Top 10 halls by rating. |
| `GET` | `/halls` | Search/paginate halls. Query params: `query`, `sportId`, `sortBy` (`rating_asc/desc`, `price_asc/desc`, `name_asc/desc`), `page`, `size`. |
| `GET` | `/halls/{id}` | Hall details. |
| `POST` | `/halls/{id}/favorite` | Toggle favorite for the authenticated user (requires auth). |

### Reservations (requires auth)

| Method | Path | Description |
|---|---|---|
| `GET` | `/reservations/upcoming` | Authenticated user's upcoming reservations. |
| `GET` | `/reservations/past` | Authenticated user's past reservations. |
| `POST` | `/reservations` | Create a reservation. Body: `hallId`, `date`, `startTime`, `endTime`. |
| `DELETE` | `/reservations/{id}` | Cancel a reservation. |

### Users (requires auth)

| Method | Path | Description |
|---|---|---|
| `GET` | `/users/me` | Current user's profile. |
| `PUT` | `/users/me` | Update profile fields. |

### AI Coach (requires auth)

| Method | Path | Description |
|---|---|---|
| `POST` | `/ai/chat` | Send a message to the AI coach; returns an assistant reply, optionally with a suggested hall, with history persisted per user. |

All error responses are shaped by `GlobalExceptionHandler` (`@RestControllerAdvice`), covering validation errors, bad credentials, and generic runtime failures with consistent JSON error bodies.

## Notes on the JWT secret format

`JwtUtil` decodes `jwt.secret` from Base64 and builds the signing key with `Keys.hmacShaKeyFor(...)`, and parses/verifies tokens via `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)` — the jjwt 0.12.x API. If you rotate the secret, all previously issued tokens become invalid.
