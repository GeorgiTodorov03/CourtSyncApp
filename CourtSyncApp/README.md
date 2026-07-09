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
- **Material Components 3** - dark theme, primary color `#10E5B2`
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
| Sport Hall Details | Hero photo, rating/hours/price, description, an embedded **Google Map** with a marker at the hall's location, favorite toggle, share, "Open in Maps" (external), and a date/time picker booking flow |
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

### Google Maps API key

The key is declared in [`res/values/google_maps_api.xml`](app/src/main/res/values/google_maps_api.xml) and wired into the manifest via a `<meta-data android:name="com.google.android.geo.API_KEY">` entry. **Do not commit a real key to source control** - this file currently holds a real key for convenience during development; replace it with your own and consider moving it to a non-committed `local.properties`-based `resValue`/`BuildConfig` field, or restrict the key in the Google Cloud Console to your app's package name + SHA-1 fingerprint.

## Building & running

1. Open the `CourtSyncApp/` folder in Android Studio (not the repo root).
2. Let Gradle sync — dependencies are resolved via `gradle/libs.versions.toml`.
3. Start the backend first (see [backend README](../courtsync-backend/README.md)).
4. Run the `app` configuration on an emulator (API 24+) or physical device.

From the command line:

```bash
# from CourtSyncApp/
./gradlew assembleDebug
# or, to install directly on a connected device/emulator
./gradlew installDebug
```

### Gradle properties

`gradle.properties` sets `android.useAndroidX=true` and `android.nonTransitiveRClass=true` — required for AndroidX dependency resolution; don't remove these.

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
