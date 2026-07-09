# CourtSync Android приложение

Нативно Android приложение за CourtSync, написано на Java с MVVM архитектура. Комуникира с [CourtSync бекенда](../courtsync-backend) чрез REST.

## Технологичен стек

- **Java 17**, Android **minSdk 24 / targetSdk 34 / compileSdk 34**
- **MVVM** - `AndroidViewModel` → Repository → Retrofit → REST API, изложено към UI чрез `LiveData`
- **Navigation Component** (единична `Activity`, `NavHostFragment` + `BottomNavigationView`, `nav_graph.xml`)
- **Retrofit 2 + OkHttp** - мрежова комуникация, с `AuthInterceptor`, който прикачва JWT bearer токена, и `HttpLoggingInterceptor` за дебъгване
- **Gson** - (де)сериализация на JSON
- **Glide** - зареждане на изображения (аватари, снимки на зали)
- **Google Maps SDK for Android** (`play-services-maps`) - вградена карта + маркер на екрана с детайли за залата
- **Material Components 3** - тъмна тема
- **ViewBinding** - активиран
- **Room** - зависимост, налична за бъдещо офлайн кеширане (все още не е свързана)
- Gradle **version catalog** (`gradle/libs.versions.toml`)

## Структура на проекта

```
app/src/main/java/com/courtsync/app/
├── activities/        SplashActivity, LoginActivity, RegisterActivity, MainActivity
├── fragments/          HomeFragment, SearchFragment, SortFilterBottomSheet,
│                        SportHallDetailsFragment, ReservationsFragment, AIFragment, ProfileFragment
├── viewmodels/         По един AndroidViewModel за всеки екран (Home, Search, SportHallDetails, Reservations, AI, Auth, Profile)
├── repositories/       Хранилища, работещи с Retrofit, публикуващи резултати в LiveData
├── network/            ApiService (Retrofit интерфейс), RetrofitClient, AuthInterceptor
├── models/             SportHall, User, Reservation, AuthResponse, AIMessage, PagedResponse<T>
├── adapters/            RecyclerView адаптери (базирани на DiffUtil): SportHallCardAdapter, SportHallListAdapter,
│                        ReservationAdapter, AIMessageAdapter
└── utils/               Constants (BASE_URL, ключове за настройки), SessionManager (съхранение на JWT/сесия)

app/src/main/res/
├── navigation/nav_graph.xml     Единичен NavHostFragment граф, 5 дестинации в долната навигация + детайли за зала
├── menu/bottom_nav_menu.xml     Home, Search, Bookings, AI Coach, Profile
├── values/themes.xml            Theme.CourtSync (тъмна Material3 тема), стилове за карти/бутони/полета за въвеждане
├── values/colors.xml            Тъмна палитра (background #0D0D0D, primary #10E5B2, …)
└── layout/                       XML оформления за всеки екран
```

## Екрани

| Екран | Бележки |
|---|---|
| Splash | Проверява `SessionManager.isLoggedIn()`, пренасочва към Login или Main след кратко забавяне |
| Login / Register | Валидация на формата във ViewModel-а преди извикване на API-то |
| Home | Карусел с препоръчани зали, преки пътища "Ask AI Coach" и "View All" |
| Search | Търсене с дебаунс (400мс), чипове за филтриране по спорт и **долен лист за сортиране/филтриране** — сортиране по рейтинг, цена или име (по азбучен ред), всяко с опция за "обръщане на реда" |
| Sport Hall Details | Снимка на залата, рейтинг/работно време/цена, описание, вградена **Google карта** с маркер на местоположението на залата, превключвател за любимо, споделяне, "Open in Maps" (външно приложение), и процес на резервация с избор на дата/час |
| Reservations | Табове Upcoming/Past, отказ с диалог за потвърждение |
| AI Coach | Чат интерфейс, задвижван от `/api/ai/chat` на бекенда (OpenAI), с чипове за бързи предложения |
| Profile | Статистика за резервации, кредити, изход от профила |

## Конфигурация

### URL на бекенда

Задава се в [`utils/Constants.java`](app/src/main/java/com/courtsync/app/utils/Constants.java):

```java
public static final String BASE_URL = "http://10.0.2.2:8080/";
```

- `10.0.2.2` е специалният адрес, който **Android емулаторът** използва, за да достигне до `localhost` на хост машината — използвайте това, ако бекендът работи на вашата машина за разработка и тествате в емулатора.
- Ако тествате на **физическо устройство**, сменете това с LAN IP адреса на вашата машина (напр. `http://192.168.1.50:8080/`) и се уверете, че устройството е в същата мрежа и вашият firewall позволява връзката.
- `android:usesCleartextTraffic="true"` е зададено в манифеста, за да позволи обикновен HTTP по време на локална разработка — това трябва да се премахне/затегне преди каквото и да е продукционно/HTTPS внедряване.

## Компилиране и стартиране

1. Отворете папката `CourtSyncApp/` в Android Studio.
2. Изчакайте Gradle да синхронизира — зависимостите се разрешават чрез `gradle/libs.versions.toml`.
3. Първо стартирайте бекенда (вижте [backend README](../courtsync-backend/README.md)).
4. Стартирайте конфигурацията `app` на емулатор (API 24+) или физическо устройство.

## Архитектурни бележки

- **Единично Activity**: `MainActivity` съдържа един `NavHostFragment`, свързан с `BottomNavigationView` чрез `NavigationUI.setupWithNavController`. Екраните за автентикация (`LoginActivity`, `RegisterActivity`) и splash екранът са отделни activity-та извън navigation графа.
- **Сесия**: JWT токенът и основната потребителска информация се запазват в `SharedPreferences` чрез `SessionManager`; `AuthInterceptor` чете токена и прикачва `Authorization: Bearer <token>` към всяка изходяща заявка.
- **Еднократни събития**: еднократни сигнали (напр. "резервацията е отказана", "любимото е превключено") са моделирани като `MutableLiveData`, която изрично се нулира след консумиране, за да се избегне класическият бъг на Android, при който "залепналата" LiveData възпроизвежда старо събитие при пресъздаване на фрагмента — вижте `ReservationsViewModel.clearCancelResult()` за модела.
- **Хигиена на back stack-а**: действия, които водят до дестинация, хоствана от долната навигация, от друго място в графа (напр. след резервация, при навигация от детайлите за залата към Reservations), използват `popUpTo`/`launchSingleTop`, за да остане back stack-ът еквивалентен на обикновено превключване на таб, вместо да натрупва остарели междинни фрагменти.

## Известни ограничения / TODO

- Room е включен като зависимост за бъдещо офлайн кеширане, но все още не се използва.
- "Payment methods" и "Personal info editor" в Profile са placeholder действия (показват toast).
- Няма поток за обновяване (refresh) на JWT токена - токените просто изтичат след `jwt.expiration` (24ч по подразбиране) и изискват повторен вход.
- Съпоставянето на име на спорт → `sportId` в `SearchFragment` е твърдо закодирано (Basketball=1, Football=2, Tennis=3), за да съответства на заредената таблица `sports` на бекенда — поддържайте и двете синхронизирани, ако промените началните данни.

---

# CourtSync Android App

Native Android client for CourtSync, written in Java with an MVVM architecture. Talks to the [CourtSync backend](../courtsync-backend) over REST.

## Tech stack

- **Java 17**, Android **minSdk 24 / targetSdk 34 / compileSdk 34**
- **MVVM** - `AndroidViewModel` → Repository → Retrofit → REST API, exposed to the UI via `LiveData`
- **Navigation Component** (single-`Activity`, `NavHostFragment` + `BottomNavigationView`, `nav_graph.xml`)
- **Retrofit 2 + OkHttp** - networking, with an `AuthInterceptor` that attaches the JWT bearer token and an `HttpLoggingInterceptor` for debugging
- **Gson** - JSON (de)serialization
- **Glide** - image loading or (avatars, hall photos)
- **Google Maps SDK for Android** (`play-services-maps`) - embedded map + marker on the hall details screen
- **Material Components 3** - dark theme
- **ViewBinding** enabled
- **Room** - dependency present for future offline caching (not yet wired up)
- Gradle **version catalog** (`gradle/libs.versions.toml`)

## Project layout

```
app/src/main/java/com/courtsync/app/
├── activities/        SplashActivity, LoginActivity, RegisterActivity, MainActivity
├── fragments/          HomeFragment, SearchFragment, SortFilterBottomSheet,
│                        SportHallDetailsFragment, ReservationsFragment, AIFragment, ProfileFragment
├── viewmodels/         One AndroidViewModel per screen (Home, Search, SportHallDetails, Reservations, AI, Auth, Profile)
├── repositories/       Retrofit-backed repositories, posting results to LiveData
├── network/            ApiService (Retrofit interface), RetrofitClient, AuthInterceptor
├── models/             SportHall, User, Reservation, AuthResponse, AIMessage, PagedResponse<T>
├── adapters/            RecyclerView adapters (DiffUtil-based): SportHallCardAdapter, SportHallListAdapter,
│                        ReservationAdapter, AIMessageAdapter
└── utils/               Constants (BASE_URL, pref keys), SessionManager (JWT/session storage)

app/src/main/res/
├── navigation/nav_graph.xml     Single NavHostFragment graph, 5 bottom-nav destinations + hall details
├── menu/bottom_nav_menu.xml     Home, Search, Bookings, AI Coach, Profile
├── values/themes.xml            Theme.CourtSync (Material3 dark), card/button/input styles
├── values/colors.xml            Dark palette (background #0D0D0D, primary #10E5B2, …)
└── layout/                       XML layouts for every screen
```

## Screens

| Screen | Notes |
|---|---|
| Splash | Checks `SessionManager.isLoggedIn()`, routes to Login or Main after a short delay |
| Login / Register | Form validation in the ViewModel before calling the API |
| Home | Recommended halls carousel, "Ask AI Coach" and "View All" shortcuts |
| Search | Debounced text search (400ms), sport filter chips, and a **sort/filter bottom sheet** — sort by rating, price, or name (alphabetical), each with a "reverse order" toggle |
| Sport Hall Details | Hall photo, rating/hours/price, description, an embedded **Google Map** with a marker at the hall's location, favorite toggle, share, "Open in Maps" (external), and a date/time picker booking flow |
| Reservations | Upcoming/Past tabs, cancel with confirmation dialog |
| AI Coach | Chat UI backed by the backend's OpenAI-powered `/api/ai/chat`, with quick-suggestion chips |
| Profile | Booking stats, credits, sign out |

## Configuration

### Backend URL

Set in [`utils/Constants.java`](app/src/main/java/com/courtsync/app/utils/Constants.java):

```java
public static final String BASE_URL = "http://10.0.2.2:8080/";
```

- `10.0.2.2` is the special alias the **Android Emulator** uses to reach `localhost` on the host machine — use this if the backend is running on your dev machine and you're testing on the emulator.
- If you're testing on a **physical device**, change this to your machine's LAN IP (e.g. `http://192.168.1.50:8080/`) and make sure the device is on the same network and your firewall allows the connection.
- `android:usesCleartextTraffic="true"` is set in the manifest to allow plain HTTP during local development — this should be removed/tightened before any production/HTTPS deployment.

## Building & running

1. Open the `CourtSyncApp/` folder in Android Studio.
2. Let Gradle sync — dependencies are resolved via `gradle/libs.versions.toml`.
3. Start the backend first (see [backend README](../courtsync-backend/README.md)).
4. Run the `app` configuration on an emulator (API 24+) or physical device.

## Architecture notes

- **Single-Activity**: `MainActivity` hosts one `NavHostFragment` wired to a `BottomNavigationView` via `NavigationUI.setupWithNavController`. Auth screens (`LoginActivity`, `RegisterActivity`) and the splash screen are separate activities outside the nav graph.
- **Session**: JWT + basic user info are persisted in `SharedPreferences` via `SessionManager`; `AuthInterceptor` reads the token and attaches `Authorization: Bearer <token>` to every outgoing request.
- **State events**: one-shot signals (e.g. "reservation cancelled", "favorite toggled") are modeled as `MutableLiveData` that's explicitly reset after being consumed, to avoid Android's classic "sticky LiveData replays a stale event on fragment recreation" bug - see `ReservationsViewModel.clearCancelResult()` for the pattern.
- **Back stack hygiene**: actions that land on a bottom-nav-hosted destination from elsewhere in the graph (e.g. after booking, navigating from hall details to Reservations) use `popUpTo`/`launchSingleTop` so the back stack stays equivalent to a normal tab switch, instead of accumulating stale intermediate fragments.

## Known limitations / TODO

- Room is included as a dependency for future offline caching but isn't used yet.
- "Payment methods" and "Personal info editor" in Profile are placeholder actions (show a toast).
- No JWT refresh-token flow - tokens simply expire after `jwt.expiration` (24h by default) and require re-login.
- Sport-name → `sportId` mapping in `SearchFragment` is hardcoded (Basketball=1, Football=2, Tennis=3) to match the backend's seeded `sports` table — keep both in sync if you change the seed data.
