# CourtSync

CourtSync is a full-stack sports hall booking platform: a native Android client and a Spring Boot REST API backed by MySQL. Users can browse and search sports halls (basketball, football, tennis, padel, volleyball, badminton), book time slots, manage their reservations, and get recommendations from an AI coach powered by OpenAI.

The project is split into two independent modules in this repository:

| Module | Description | Docs |
|---|---|---|
| [`courtsync-backend/`](courtsync-backend) | Spring Boot 3 REST API, JWT auth, MySQL/JPA | [Backend README](courtsync-backend/README.md) |
| [`CourtSyncApp/`](CourtSyncApp) | Native Android client (Java, MVVM) | [Android README](CourtSyncApp/README.md) |

## Features

- **Auth** — email/password registration and login, JWT bearer tokens, BCrypt password hashing
- **Sport hall discovery** — recommended halls, keyword/city/sport search, sort by rating/price/name (with reverse toggle), filter chips by sport
- **Hall details** — photos, opening hours, pricing, description, an embedded Google Map with a marker at the venue's location, favoriting
- **Bookings** — date/time-slot reservation flow, upcoming/past reservation lists, cancellation
- **AI Coach** — chat-based assistant (OpenAI GPT-4o-mini) that recommends halls based on the user's message, with conversation history persisted per user
- **Profile** — booking stats, credits, sign out

## Architecture

```
┌─────────────────────────┐        HTTPS/JSON (JWT Bearer)        ┌──────────────────────────┐
│   Android App (Java)    │  ───────────────────────────────────▶ │  Spring Boot REST API    │
│  MVVM + Retrofit/OkHttp │ ◀─────────────────────────────────── │  Spring Security + JWT    │
└─────────────────────────┘                                       └───────────┬──────────────┘
                                                                                │ JPA/Hibernate
                                                                    ┌───────────▼──────────────┐
                                                                    │        MySQL              │
                                                                    └────────────────────────────┘
                                                                                │
                                                                    ┌───────────▼──────────────┐
                                                                    │   OpenAI API (AI Coach)   │
                                                                    └────────────────────────────┘
```

## Getting started

You'll need both modules running to use the app end-to-end:

1. Set up and start the backend first — see the [Backend README](courtsync-backend/README.md) for MySQL setup, environment configuration, and how to run it.
2. Then build and run the Android app against it — see the [Android README](CourtSyncApp/README.md) for emulator/device configuration.

## ⚠️ Security note

This repository's `courtsync-backend/src/main/resources/application.properties` has, at various points, contained real credentials (a MySQL password and an OpenAI API key) committed directly to git. **If you're setting this project up, treat any credentials currently in that file as compromised — rotate them and switch to environment variables** as described in the [Backend README](courtsync-backend/README.md#configuration). Never commit real secrets to `application.properties`; use `${ENV_VAR}` placeholders instead.

## License

MIT — see [LICENSE](LICENSE).
