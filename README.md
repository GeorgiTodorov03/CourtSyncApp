# CourtSync

CourtSync е пълноценна платформа за резервация на спортни зали: нативно Android приложение и Spring Boot REST API, работещо с MySQL. Потребителите могат да разглеждат и търсят спортни зали (баскетбол, футбол, тенис, падел, волейбол, бадминтон), да резервират часови интервали, да управляват своите резервации и да получават препоръки от AI треньор, задвижван от OpenAI.

Проектът е разделен на два независими модула в това хранилище:

| Модул | Описание | Документация |
|---|---|---|
| [`courtsync-backend/`](courtsync-backend) | Spring Boot 3 REST API, JWT автентикация, MySQL/JPA | [Backend README](courtsync-backend/README.md) |
| [`CourtSyncApp/`](CourtSyncApp) | Нативно Android приложение (Java, MVVM) | [Android README](CourtSyncApp/README.md) |

## Функционалности

- **Автентикация** - регистрация и вход с имейл/парола, JWT bearer токени, BCrypt хеширане на пароли
- **Откриване на спортни зали** - препоръчани зали, търсене по ключова дума/град/спорт, сортиране по рейтинг/цена/име (с опция за обръщане на реда), филтърни чипове по спорт
- **Детайли за залата** - снимки, работно време, цени, описание, вградена Google карта с маркер на местоположението на обекта, добавяне в любими
- **Резервации** - процес на резервация по дата/часови интервал, списъци с предстоящи/минали резервации, отказ на резервация
- **AI Треньор** - чат асистент (OpenAI GPT-4o-mini), който препоръчва зали въз основа на съобщението на потребителя, като историята на разговора се запазва за всеки потребител
- **Профил** - статистика за резервации, кредити, изход от профила

## Архитектура

```
┌─────────────────────────┐        HTTPS/JSON (JWT Bearer)        ┌──────────────────────────┐
│   Android App (Java)    │  ───────────────────────────────────▶│  Spring Boot REST API    │
│  MVVM + Retrofit/OkHttp │ ◀─────────────────────────────────── │  Spring Security + JWT   │
└─────────────────────────┘                                       └───────────┬──────────────┘
                                                                                │ JPA/Hibernate
                                                                    ┌───────────▼──────────────┐
                                                                    │        MySQL             │
                                                                    └──────────────────────────┘
                                                                                │
                                                                    ┌───────────▼──────────────┐
                                                                    │   OpenAI API (AI Coach)  │
                                                                    └──────────────────────────┘
```

## Стъпки за започване

Ще ви трябват и двата модула, работещи, за да използвате приложението от край до край:

1. Първо настройте и стартирайте бекенда — вижте [Backend README](courtsync-backend/README.md) за настройка на MySQL, конфигурация на средата и как да го стартирате.
2. След това компилирайте и стартирайте Android приложението срещу него — вижте [Android README](CourtSyncApp/README.md) за конфигурация на емулатор/устройство.

## Лиценз

MIT - вижте [LICENSE](LICENSE).

---

# CourtSync

CourtSync is a full-stack sports hall booking platform: a native Android client and a Spring Boot REST API backed by MySQL. Users can browse and search sports halls (basketball, football, tennis, padel, volleyball, badminton), book time slots, manage their reservations, and get recommendations from an AI coach powered by OpenAI.

The project is split into two independent modules in this repository:

| Module | Description | Docs |
|---|---|---|
| [`courtsync-backend/`](courtsync-backend) | Spring Boot 3 REST API, JWT auth, MySQL/JPA | [Backend README](courtsync-backend/README.md) |
| [`CourtSyncApp/`](CourtSyncApp) | Native Android client (Java, MVVM) | [Android README](CourtSyncApp/README.md) |

## Features

- **Auth** - email/password registration and login, JWT bearer tokens, BCrypt password hashing
- **Sport hall discovery** - recommended halls, keyword/city/sport search, sort by rating/price/name (with reverse toggle), filter chips by sport
- **Hall details** - photos, opening hours, pricing, description, an embedded Google Map with a marker at the venue's location, favoriting
- **Bookings** - date/time-slot reservation flow, upcoming/past reservation lists, cancellation
- **AI Coach** - chat-based assistant (OpenAI GPT-4o-mini) that recommends halls based on the user's message, with conversation history persisted per user
- **Profile** - booking stats, credits, sign out

## Architecture

```
┌─────────────────────────┐        HTTPS/JSON (JWT Bearer)        ┌──────────────────────────┐
│   Android App (Java)    │  ───────────────────────────────────▶│  Spring Boot REST API   │
│  MVVM + Retrofit/OkHttp │ ◀─────────────────────────────────── │  Spring Security + JWT   │
└─────────────────────────┘                                       └───────────┬──────────────┘
                                                                                │ JPA/Hibernate
                                                                    ┌───────────▼──────────────┐
                                                                    │        MySQL             │
                                                                    └──────────────────────────┘
                                                                                │
                                                                    ┌───────────▼──────────────┐
                                                                    │   OpenAI API (AI Coach)  │
                                                                    └──────────────────────────┘
```

## Getting started

You'll need both modules running to use the app end-to-end:

1. Set up and start the backend first — see the [Backend README](courtsync-backend/README.md) for MySQL setup, environment configuration, and how to run it.
2. Then build and run the Android app against it — see the [Android README](CourtSyncApp/README.md) for emulator/device configuration.

## License

MIT - see [LICENSE](LICENSE).
