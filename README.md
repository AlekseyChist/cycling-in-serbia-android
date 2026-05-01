# Cycling in Serbia — Android (Kotlin)

Native Android port of the **Cycling in Serbia** PWA.
Stack: Kotlin · Jetpack Compose · Material 3 · Hilt · Coroutines · Supabase (`supabase-kt`) · osmdroid · Room · Coil.

The Vite/React web project (`Uaecyclinghubappdesign`) is **not** abandoned — we keep it alive while migrating to native. Same Supabase backend on both sides.

---

## 1. First open in Android Studio

1. **Studio version**: Android Studio Ladybug (2024.2.x) or newer.
2. `File → Open` → выбрать папку `C:\Users\prost\CyclingInSerbia`.
3. Studio → "Trust project" → подождать первый Gradle Sync (5–10 минут, качается AGP 8.7, Kotlin 2.1, Compose, Supabase, osmdroid).
4. Если предложит установить Android SDK Platform 35 / Build-Tools — соглашаемся.
5. Если предложит JDK 17 — соглашаемся (Android Studio обычно ставит свой Embedded JDK).
6. Создаём AVD: `Tools → Device Manager → Create Device` → Pixel 7, system image API 35.
7. `Run ▶` (Shift+F10).

При первом запуске должен открыться Onboarding-экран → Get Started → список реальных треков из Supabase → клик по треку → детальный экран с картой OSM и polyline маршрута.

---

## 2. Конфигурация

### Supabase
Креды лежат в `local.properties` (НЕ в git). Используется тот же проект, что и в вебе:
```
SUPABASE_URL=https://umaeqrhjnfawmqdtovjt.supabase.co
SUPABASE_ANON_KEY=sb_publishable_...
```
Они прокидываются в код через `BuildConfig` (см. `app/build.gradle.kts`).

### Карты
osmdroid использует raster-тайлы OpenStreetMap — те же, что и Leaflet в вебе. API key не нужен.

---

## 3. Что готово

| Слой | Статус |
|---|---|
| Gradle skeleton + version catalog | ✅ |
| Material 3 тема (light/dark + Material You dynamic colors на API 31+) | ✅ |
| Single-Activity + Navigation Compose | ✅ |
| BottomNav (Tracks / Events / Shops / Rules) | ✅ |
| Onboarding-заглушка | ✅ |
| Supabase-kt клиент + Hilt DI | ✅ |
| `TrackRepository` — `getPublishedTracks()`, `getTrackByLegacyId()`, `gpxPublicUrl()` | ✅ |
| `TracksScreen` — реальный список из Supabase, карточки с difficulty-цветом | ✅ |
| `TrackDetailScreen` — детали + osmdroid-карта с polyline и стартовым маркером | ✅ |
| EventsScreen / ShopsScreen / RegulationsScreen | заглушки |

---

## 4. Что портировать дальше (по приоритету)

1. **EventsScreen + EventDetailScreen** — таблица `events` в Supabase, использовать `useClubEvents.ts` из веба как образец.
2. **ShopsScreen** — карточки магазинов, аналогично `ShopCard.tsx`.
3. **RegulationsScreen** — статический контент.
4. **MapView с группировкой маркеров** — портировать `groupTracksByStartPoint` из `src/app/components/map/MapView.tsx`. Сейчас на `TrackDetailScreen` карта показывает только один трек; общая карта на TracksScreen — следующий шаг.
5. **GPX-парсер** — порт `src/utils/gpxParser.ts` на Kotlin (или просто использовать `route_points` из БД, как сейчас).
6. **Strava интеграция** — `src/services/stravaService.ts` → нативный OAuth.
7. **BottomSheet** — Material 3 `ModalBottomSheet`.
8. **Офлайн-кэш** — Room для треков (DAO + entity), синхронизация через repository.
9. **Push-уведомления** — Firebase Cloud Messaging.
10. **Иконки приложения и сплэш-скрин** — пока стоят placeholder-векторы; заменить на дизайн-иконки.

---

## 5. Архитектурные решения

- **Single-Activity + NavHost** — один `MainActivity`, навигация полностью через Compose. Стандарт Google.
- **MVVM**: `ViewModel` владеет `StateFlow<UiState>`, экран собирает через `collectAsStateWithLifecycle()`.
- **Hilt**: SupabaseClient — singleton, репозитории — `@Singleton`. ViewModels — `@HiltViewModel`.
- **Material 3**: dynamic colors включены на API 31+; иначе — emerald-палитра, повторяющая веб.
- **osmdroid вместо Google Maps** — open source, без API-key, OSM-тайлы, прямой аналог Leaflet.
- **supabase-kt вместо REST вручную** — реиспользует Postgrest/Auth/Storage паттерны как в JS-клиенте.
- **`legacyId` как навигационный ID** — тот же подход, что в вебе; UUID `id` остаётся как primary key.

---

## 6. Структура проекта

```
app/src/main/java/com/cyclinginserbia/app/
├── MainActivity.kt              — Compose host
├── CyclingApp.kt                — Application + Hilt + osmdroid init
├── di/AppModule.kt              — Hilt graph
├── data/
│   ├── model/Track.kt           — domain model
│   ├── supabase/
│   │   ├── SupabaseClientProvider.kt
│   │   └── TrackDto.kt          — wire format → domain mapper
│   └── repository/TrackRepository.kt
└── ui/
    ├── theme/                   — Color, Type, Theme
    ├── navigation/              — Destination, RootNavigation, BottomBar
    ├── components/TrackMap.kt   — osmdroid wrapper
    └── screens/
        ├── onboarding/
        ├── tracks/              — list + ViewModel
        ├── trackdetail/         — detail + ViewModel + map
        ├── events/
        ├── shops/
        └── regulations/
```

---

## 7. Команды (из терминала)

```powershell
# сборка
.\gradlew :app:assembleDebug

# установка на подключённое устройство/AVD
.\gradlew :app:installDebug

# чистка
.\gradlew clean
```

---

## 8. Git

Репозиторий ещё не инициализирован. Когда будем готовы — `git init` + первый коммит, потом создадим отдельный GitHub-репозиторий `cycling-in-serbia-android` и запушим.
