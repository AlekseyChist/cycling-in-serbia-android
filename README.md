# Cycling in Serbia — Android (Kotlin)

Нативное Android-приложение для велоклуба **DBB** (Belgrade Cycling Club) — список туров по Сербии, ближайшие групповые покатушки от Strava-клуба, веломагазины Белграда и правила дорожного движения для велосипедистов.

Это нативный порт PWA-версии (`Uaecyclinghubappdesign`) — обе версии живут параллельно и используют один и тот же бэкенд Supabase.

---

## Что внутри

### 🛣️ Tracks — карта с маршрутами
- **Map-first экран:** osmdroid-карта во весь экран с polyline'ами всех 114 маршрутов из Supabase.
- Цвет линии = сложность маршрута (Easy/Medium/Hard).
- Кластеры в местах, где несколько маршрутов начинаются рядом (~200 м); тап на кластер раскрывает все треки в нижнем листе.
- **Поиск + фильтры в Bottom Sheet:** Difficulty / Surface / Ride Type (Coffee / Dark / Sun / DBB+ / Misc) / Region + переключатель «Только избранное».
- **Избранное** — сердечко на карточке. Сохраняется через DataStore, переживает перезапуск.
- Тап по polyline или кластеру с одним треком → лист фокусируется на этом треке, peek-высота шторки плавно растёт чтобы показать карточку без перекрытия карты. Тап в пустое место — фокус снимается, шторка опускается обратно.
- **Камера** не дёргается на deselect / смене фильтров — пользователь сохраняет место на карте.
- **Офлайн-кэш:** SSoT через Room. UI рендерится мгновенно из кэша, при каждом заходе на Tracks-таб фоновый refresh подтягивает свежие данные из Supabase (классический SWR).
- **Стартовая позиция** — Белград, zoom 9.5 (город + пригороды), большинство DBB-маршрутов сразу в кадре.

### 🗺️ TrackDetail — карточка маршрута
- Hero-фото 280 dp + плавающие круглые кнопки Back/Share поверх.
- Сетка статов 2×2: Distance / Elevation / Time / Difficulty.
- About + блок Safety Notes (амбер-предупреждение).
- Превью-карта 180 dp с polyline'ом маршрута.
- DBB tip-карточка с подсказкой.
- **Действия:**
  - **Download GPX** — качает `.gpx` в `Downloads/` через системный DownloadManager.
  - **Navigate to Start** — пробует `google.navigation:` (велорежим) → fallback на универсальный `maps.google.com/dir` → fallback на `geo:`.
  - **Share** — стандартный Intent.ACTION_SEND с текстом и GPX-ссылкой.

### 📅 Events — групповые покатушки
- **Live-фид от Strava-клуба DBB** через серверный proxy веб-приложения (`/api/strava/club-events`). Без OAuth для пользователя и без `client_secret` в APK.
- Если proxy недоступен — fallback на локальный генератор повторяющихся ивентов клуба (Dark-On-Draft четверг, Coffee Ride суббота, Rekafary среда, Burekfast вторник).
- Фильтры по типу + поиск.
- **EventDetail:** hero-карточка, инфо-блок, секции *What to bring* и *Timeline*, кнопки *Add to Calendar* и *Open in Strava*.

### 🏪 Shops — веломагазины Белграда
- 4-табовый segmented control: All / Shops / Services / Friends.
- Карточки магазинов с FlowRow-футером (адаптивно ломается на узких экранах).
- Личные контакты (Mihail, Marko, Denis, Lena & Artur) — с круглым логотипом-аватаркой.
- Inline-ссылки на Instagram / сайт / телефон.

### 📜 Regulations — ПДД для велосипедистов
- 4 категории, 18 правил.
- Sticky search + раскрывающиеся карточки.
- Закладки (в памяти текущей сессии).
- Inline-ссылки `[label](url)` и пояснительные пиктограммы (`__distance`, `__overlap`, `__overshoot` и т. д.).

### 👋 Onboarding
- Cream-градиентный hero, 88 dp orange-логотип, 3 строки фич, кнопка *Get Started*, мелкая подпись автора.
- Показывается **только при первом запуске** + после version-bump'а (DataStore-флаг `last_completed_version_code` сравнивается с `BuildConfig.VERSION_CODE`). Сплэш удерживается до тех пор, пока `RootViewModel` не определит стартовый маршрут.
- Под капотом параллельно греет два кэша: `TrackRepository.refreshIfStale()` и `EventRepository.getEvents()` — чтобы Tracks-карта и Events-лист открывались без паузы на сеть.

### 👤 Profile (5-й боттом-таб)
- **Appearance** — System / Light / Dark переключатель темы (`SingleChoiceSegmentedButtonRow`). Дефолт `System`, выбор персистится в DataStore (`theme_prefs`).
- **Your favorites · N** — компактный список лайкнутых треков, реактивно собирается из `SyncPreferences.favoriteTrackIds` + `TrackRepository.observePublishedTracks()`. Тап на карточке открывает соответствующий TrackDetail. Empty-state, если пусто.
- **About-футер** — логотип DBB, версия + build number (для багрепортов), подпись автора.

### 🎨 Дизайн-система
- Strava-orange brand color (`#FC5200`), Material 3, **light + dark тема**.
- Единый theme-aware источник цветов: `AppColorPalette` через `LocalAppColors` CompositionLocal. Top-level façade `val AppColors @Composable @ReadOnlyComposable get() = LocalAppColors.current` сохраняет существующие call-site'ы (`AppColors.Primary` и т.д.).
- Brand и saturated accent colors одинаковые в обеих темах; surface/text/gray-scale + soft chip-tints — флипаются.
- `ChipColors / DifficultyColors / SurfaceTypeColors` — тоже @Composable getters над `AppColors`. `DifficultyMapColors` остаётся плоским (используется в osmdroid overlay-коде, не в @Composable).
- Канонические компоненты: `SearchField`, `PillChip`, `EmptyState`.
- Все экраны рендерятся в общий `Scaffold` из `RootNavigation` с `BottomNavigation` (Tracks / Events / Shops / Rules / Profile).
- Splash screen → Onboarding (только при первом запуске) → главный экран. Statusbar appearance меняется по теме.

---

## Стек

- **Язык / UI:** Kotlin 2.1.0, Jetpack Compose, Material 3
- **DI:** Hilt 2.51.1 + kapt _(см. quirks ниже)_
- **Async:** Coroutines + Flow
- **Backend:** [supabase-kt 3.0](https://github.com/supabase-community/supabase-kt) — реиспользуем тот же Supabase-проект, что у веба.
- **Карты:** [osmdroid 6.1](https://github.com/osmdroid/osmdroid) — OSM-тайлы без API-key, прямой аналог Leaflet из веба.
- **Локальное хранилище:** Room 2.7.0 (треки SSoT) + DataStore Preferences (sync timestamp, избранное).
- **HTTP:** Ktor (под капотом supabase-kt + кастомный `StravaService`).
- **Сериализация:** kotlinx.serialization
- **Картинки:** Coil
- **Min SDK:** 26 (Android 8.0), **Target/Compile SDK:** 35 (Android 15)

---

## Первый запуск

### 1. Клон и настройка

```powershell
git clone https://github.com/AlekseyChist/cycling-in-serbia-android.git
cd cycling-in-serbia-android
```

### 2. `local.properties`

Файл в `.gitignore` — после `clone` его НЕТ, нужно создать вручную в корне проекта:

```properties
# путь к Android SDK (свой)
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk

# Supabase — тот же проект, что у веб-версии
SUPABASE_URL=https\://umaeqrhjnfawmqdtovjt.supabase.co
SUPABASE_ANON_KEY=sb_publishable_...
```

`SUPABASE_ANON_KEY` — публичный ключ, безопасно держать в клиенте (RLS на стороне Supabase). Значения прокидываются в Kotlin через `BuildConfig`.

### 3. Android Studio

- Studio Ladybug (2024.2.x) или новее.
- `File → Open` → выбрать папку проекта → *Trust project*.
- Подождать первый Gradle Sync (5–10 мин — качает AGP 8.13, Kotlin 2.1, Compose, Supabase, osmdroid, Hilt, Room).
- Если предложит SDK 35 / Build-Tools / JDK 17 — соглашаемся.
- Создать AVD: Pixel 7, system image API 35.
- `Run ▶` (Shift+F10).

### 4. Карты

osmdroid использует raster-тайлы OpenStreetMap — те же, что Leaflet в вебе. **API key не нужен.**

### 5. Supabase keep-alive (одноразовая настройка)

Free-tier ставит проект на паузу после ~7 дней без **активности БД**. Один read-GET в сутки оказался недостаточным сигналом, поэтому GitHub Action (`.github/workflows/supabase-keepalive.yml`) дважды в день делает реальную **запись** в строку-пустышку. Запись идёт под публичным `anon`-ключом через узкую RLS-политику — service_role key не нужен.

Перед тем как это заработает, один раз выполни в Supabase (Dashboard → SQL Editor):

```sql
-- строка-пустышка, которую трогает GitHub Action
create table if not exists public.keepalive (
  id        int primary key,
  last_ping timestamptz not null default now()
);

insert into public.keepalive (id, last_ping)
values (1, now())
on conflict (id) do nothing;

-- RLS: anon может ТОЛЬКО обновлять эту таблицу (не читать/вставлять/удалять)
alter table public.keepalive enable row level security;

create policy "anon may touch keepalive"
  on public.keepalive
  for update
  to anon
  using (true)
  with check (true);
```

GitHub-секреты `SUPABASE_URL` и `SUPABASE_ANON_KEY` уже заведены в репозитории. Проверка: вкладка Actions → *Supabase keep-alive* → **Run workflow** → оба шага зелёные.

---

## Сборка из терминала

```powershell
# debug APK (пойдёт в app/build/outputs/apk/debug/app-debug.apk)
.\gradlew :app:assembleDebug

# установка на подключённое устройство/AVD
.\gradlew :app:installDebug

# чистка
.\gradlew clean
```

---

## Архитектура

### Single-Activity + NavHost
Один `MainActivity`, навигация полностью через Compose Navigation. Стандарт Google.

### MVVM + StateFlow
- `@HiltViewModel` владеет `StateFlow<UiState>`.
- Экран собирает через `collectAsStateWithLifecycle()`.
- `UiState` — обычная `data class` с флагами `isInitialLoading / isSyncing / syncError` (без sealed Loading/Ready/Error).

### SSoT через Room
- `TrackRepository` отдаёт `Flow<List<Track>>` из Room — UI не ходит напрямую в Supabase.
- `refresh()` пишет результаты Supabase в Room и стампит `tracks_last_sync_at` в DataStore.
- `TracksViewModel` вызывает `refresh()` на каждом заходе на таб (классический SWR — Room мгновенно отдаёт кэш, сеть подтягивает свежее в фоне). `refreshIfStale()` (TTL 6 ч) остался для prefetch из `OnboardingViewModel` — там это one-shot.
- `EventRepository` держит process-lifetime in-memory cache (mutex для расы), prefetch-ится тоже из `OnboardingViewModel`.

### Strava через Vercel proxy
Веб-приложение (`uaecyclinghubappdesign.vercel.app`) держит Strava `client_id` / `client_secret` / `refresh_token` на сервере и отдаёт events клуба JSON-ом по `/api/strava/club-events`. Android просто бьёт этот endpoint через ktor — никаких токенов в APK.

### DI
- Hilt `@Singleton` на репозитории и Supabase-клиент.
- ViewModels — `@HiltViewModel`.

### Серверная часть
**Supabase содержит ТОЛЬКО** таблицу `tracks` (114 опубликованных) + Storage bucket `gpx-files`. Events / Shops / Regulations / Onboarding — НЕ в Supabase: либо генерятся клиентом (events fallback), либо статически зашиты, либо приходят от внешнего API (Strava через proxy).

---

## Структура проекта

```
app/src/main/java/com/cyclinginserbia/app/
├── MainActivity.kt              — Compose host
├── CyclingApp.kt                — Application + Hilt + osmdroid init
├── di/AppModule.kt              — Hilt graph
├── data/
│   ├── model/                   — domain models (Track, Event, Shop, Regulation)
│   ├── supabase/                — wire DTOs + клиент
│   ├── strava/                  — StravaService через Vercel proxy
│   ├── local/db/                — Room (entities, DAO, mapper)
│   ├── local/datastore/         — SyncPreferences (timestamps, favorites)
│   └── repository/              — TrackRepository, EventRepository, ShopRepository, RegulationRepository
└── ui/
    ├── theme/                   — AppColors, Theme, Type
    ├── navigation/              — Destination, RootNavigation, BottomBar
    ├── components/              — SearchField, PillChip, EmptyState, TrackMap
    └── screens/
        ├── onboarding/
        ├── tracks/              — map-first list + filters modal sheet + favorites
        ├── trackdetail/         — hero, stats, route preview, GPX/Navigate/Share
        ├── events/              — Strava live + recurring fallback
        ├── shops/               — segmented tabs + cards
        └── regulations/         — categorized rules with bookmarks
```

---

## Git workflow

### Схема веток (gitflow-lite)

```
main   ●──────  стабильные релизы (только PR из dev, защищена)
        │
dev    ●──────  ежедневная интеграция: сюда сливаются все фичи
        │
feat/* ──────  короткоживущие feature-ветки от dev
```

- **`main`** — то, что считается «работающим». Только через PR из `dev`.
- **`dev`** — рабочая ветка. Все фичи нарезаются от неё и через PR возвращаются обратно.
- **`feat/*` / `fix/*` / `chore/*`** — одна логическая правка = одна ветка. После мержа ветка не реюзается.

### Branch protection на `main`

| Правило | Значение |
|---|---|
| Прямой `git push` в `main` | ❌ запрещён |
| `git push --force` | ❌ запрещён |
| Удаление ветки `main` | ❌ запрещено |
| `enforce_admins` | ✅ правила распространяются и на админа |

### Default branch

GitHub default — `main`, поэтому при создании PR из feature-ветки **обязательно** указывать базу `dev`:

```powershell
gh pr create --base dev --fill
```

### Релиз (`dev → main`)

Когда `dev` стабилен:

```powershell
gh pr create --base main --head dev --title "release: vX.Y.Z" --body "<changelog>"
gh pr merge --merge   # без squash, чтобы сохранить историю фич
```

---

## Build environment quirks (Windows + Kotlin 2.1.0)

- **Hilt пинуется на 2.51.1 + kapt** (НЕ 2.52+, НЕ KSP). Причина: 2.52+ ввёл LazyClassKey ProGuard-rule generator, эмитит Windows-style `META-INF\\proguard\\...` пути → `JavaFileManager` отвергает их с `Invalid relative name`. Plus, Hilt KSP regression на Kotlin 2.1.0. Room — на KSP, всё ок.
- **JBR 21** из Android Studio (`C:\Program Files\Android\Android Studio\jbr`) — `JAVA_HOME` для CLI-сборок. Project compile target — JDK 17, но Gradle сам бежит на JBR.

---

## Что осталось / Roadmap

- ✅ Все основные экраны портированы и обведены под единую дизайн-систему.
- ✅ Strava live events через proxy.
- ✅ Room offline cache + аггрессивный SWR на каждом заходе.
- ✅ Splash screen + brand launcher icon.
- ✅ Region + favorites filters в Tracks.
- ✅ Onboarding — только первый запуск / после версионного апдейта.
- ✅ Beta feedback batch 1 + 2 (route_points data fix, ETA, navigate, sheet UX, tab flicker и т.д. — см. историю v0.2.0 → v0.3.0).
- ✅ Тёмная тема (System/Light/Dark) + Profile-таб с фаворитами.
- ✅ Магазины и партнёры на карте треков (за тоглом Storefront).
- 🚧 Локализация ru / sr (sr-Latn) — переключатель языка в Profile; готовы Profile/навигация, Tracks, Events; осталось TrackDetail, Shops, Regulations.
- ✅ Кнопки зума +/- убраны с карт (только pinch-to-zoom).
- ✅ Supabase keep-alive (GitHub Action) против автопаузы free-tier.
- ⏳ Performance-аудит скролла Tracks-листа (image downsample, LazyColumn keys).
- ⏳ Release signing config (для подписанных APK + Play Store).
- ⏳ ProGuard/R8 minification (сейчас release без minify, APK ~30 MB).
- ⏳ Push-уведомления о ближайших ивентах.
- ⏳ Офлайн-кэш OSM-тайлов в видимых регионах.

---

## Сестринский проект

Веб-версия живёт в [`Uaecyclinghubappdesign`](https://github.com/AlekseyChist) (Vite + React + Supabase). Схема и имена полей там — авторитетный источник: `src/services/trackService.ts`, `src/app/components/map/MapView.tsx`, `src/utils/gpxParser.ts`. Один и тот же Supabase, на двух фронтах — пока существуют параллельно.
