# Cycling in Serbia — Android (Kotlin)

Native Android port of the **Cycling in Serbia** PWA.
Stack: Kotlin · Jetpack Compose · Material 3 · Hilt · Coroutines · Supabase (`supabase-kt`) · osmdroid · Room · Coil.

The Vite/React web project (`Uaecyclinghubappdesign`) is **not** abandoned — we keep it alive while migrating to native. Same Supabase backend on both sides.

---

## 1. First open in Android Studio

0. **Клонировать репозиторий** (если ещё нет локально):
   ```powershell
   git clone https://github.com/AlekseyChist/cycling-in-serbia-android.git
   cd cycling-in-serbia-android
   ```
1. **Создать `local.properties`** в корне проекта с кредами Supabase — см. §2 (после `clone` этого файла нет, он в `.gitignore`).
2. **Studio version**: Android Studio Ladybug (2024.2.x) or newer.
3. `File → Open` → выбрать папку проекта.
4. Studio → "Trust project" → подождать первый Gradle Sync (5–10 минут, качается AGP 8.7, Kotlin 2.1, Compose, Supabase, osmdroid).
5. Если предложит установить Android SDK Platform 35 / Build-Tools — соглашаемся.
6. Если предложит JDK 17 — соглашаемся (Android Studio обычно ставит свой Embedded JDK).
7. Создаём AVD: `Tools → Device Manager → Create Device` → Pixel 7, system image API 35.
8. `Run ▶` (Shift+F10).

При первом запуске должен открыться Onboarding-экран → Get Started → список реальных треков из Supabase → клик по треку → детальный экран с картой OSM и polyline маршрута.

---

## 2. Конфигурация

### Supabase
Креды лежат в `local.properties` — он в `.gitignore` и в репозиторий **не коммитится**. После `git clone` его НЕТ — нужно создать вручную в корне проекта по шаблону:

```properties
# путь к Android SDK (свой)
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk

# Supabase — тот же проект, что у веб-версии
SUPABASE_URL=https\://umaeqrhjnfawmqdtovjt.supabase.co
SUPABASE_ANON_KEY=sb_publishable_...
```

Anon key — публичный, безопасно держать в клиентском коде (RLS на стороне Supabase). Значения прокидываются в Kotlin через `BuildConfig` (см. `app/build.gradle.kts`).

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

### 7.1 Gradle

```powershell
# сборка
.\gradlew :app:assembleDebug

# установка на подключённое устройство/AVD
.\gradlew :app:installDebug

# чистка
.\gradlew clean
```

### 7.2 Git workflow

```powershell
# 1. начать новую фичу: всегда от dev
git checkout dev
git pull
git checkout -b feat/<short-name>

# 2. в процессе — обычные коммиты (один логический блок = один коммит)
git add <files>
git commit -m "feat: <message>"

# 3. когда готово — пуш и PR в dev (НЕ в main!)
git push -u origin feat/<short-name>
gh pr create --base dev --fill

# 4. после мержа PR на GitHub — забираем себе и удаляем локальную ветку
git checkout dev
git pull
git branch -d feat/<short-name>
```

Префиксы веток: `feat/...` — новая функциональность, `fix/...` — багфикс, `chore/...` — рутина (доки, конфиги, рефакторинг без видимых изменений).

---

## 8. Git и ветвление

**Репозиторий:** https://github.com/AlekseyChist/cycling-in-serbia-android (public).

### 8.1 Схема веток (gitflow-lite)

```
main   ●──────  только стабильные / проверенные снапшоты (релизы)
        │
dev    ●──────  ежедневная интеграция: сюда сливаются все фичи
        │
feat/* ──────  короткоживущие ветки от dev (одна фича = одна ветка)
```

- **`main`** — то, что считается «работающим». Сюда попадает только через PR из `dev`, когда состояние явно зафиксировано как стабильное.
- **`dev`** — рабочая ветка. Все feature-ветки нарезаются от неё и сливаются обратно в неё через PR.
- **`feat/*` / `fix/*` / `chore/*`** — одна ветка = одна логическая правка. После мержа ветка не реюзается, для следующей задачи нарезается новая.

### 8.2 Branch protection на `main`

На ветке `main` включена защита (см. *Settings → Branches* на GitHub):

| Правило | Значение |
|---|---|
| Прямой `git push` в `main` | ❌ запрещён, только через PR |
| `git push --force` | ❌ запрещён |
| Удаление ветки `main` | ❌ запрещено |
| `enforce_admins` | ✅ правила распространяются и на админа репо |

Если случайно попробуешь пушнуть напрямую — GitHub откажет с `GH006: Protected branch update failed`.

### 8.3 Default branch и PR

Default branch на GitHub — `main`, поэтому при создании PR из feature-ветки **обязательно** указывать базу:

```powershell
gh pr create --base dev --fill
```

Без `--base dev` PR пойдёт в `main`, и protection (правильно) его не пропустит.

### 8.4 Релиз (`dev → main`)

Когда `dev` стабилен и хочется зафиксировать релиз:

```powershell
gh pr create --base main --head dev --title "release: <version>" --body "<changelog>"
```

После проверки — мерж кнопкой `Merge pull request` на GitHub (или `gh pr merge --merge` без squash, чтобы сохранить историю фич в `main`).
