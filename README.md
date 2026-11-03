# Partiful Party Invite & Poster Maker - Native Android

Ứng dụng Native Android chuyên nghiệp thiết kế thiệp mời, poster tiệc với phong cách trẻ trung, hiện đại tương tự Partiful.

---

## 1. Công nghệ & Thư viện sử dụng
- **Ngôn ngữ:** Kotlin 2.2+
- **Giao diện:** Jetpack Compose Material 3 (100% Declarative UI)
- **Kiến trúc:** MVVM (Model - View - ViewModel) + Repository Pattern + Clean Architecture
- **Xử lý đồ họa:** Android `Canvas`, `Paint`, `ColorMatrixColorFilter`, `Bitmap`
- **Chọn ảnh:** Android Photo Picker (`ActivityResultContracts.PickVisualMedia`)
- **Xuất PDF in ấn:** `android.graphics.pdf.PdfDocument`
- **Tích hợp Lịch & Lời nhắc:** `CalendarContract.Events`, `Intent.ACTION_INSERT` an toàn không cần quyền nguy hiểm
- **Chia sẻ:** Android Sharesheet (`Intent.ACTION_SEND` kết hợp `FileProvider`)
- **Lưu trữ cục bộ:** `Internal Storage`, JSON serialization, `SharedPreferences`
- **Build System:** Gradle 9.1 + Android Gradle Plugin 9.0+

---

## 2. Cấu trúc thư mục (Folder Structure)

```
android/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml          # Khai báo permissions và FileProvider
│       │   ├── res/                         # Values, xml, icons, file_paths.xml
│       │   └── kotlin/com/partiful/invitemaker/
│       │       ├── MainActivity.kt          # Single Activity entry point
│       │       ├── Navigation.kt            # Trình điều hướng ScreenState
│       │       ├── core/
│       │       │   ├── designsystem/        # Color, Theme, Components (NeonButton, GlassCard)
│       │       │   └── canvas/              # CanvasRendererView, OffscreenCardRenderer, HistoryManager
│       │       ├── domain/model/            # CardDesign, CanvasLayer, LayerType, PartyEvent, Template
│       │       ├── data/
│       │       │   ├── repository/          # TemplateRepository (11 danh mục mẫu thiệp)
│       │       │   └── service/             # CalendarService, PdfExportService, ImageProcessor, DraftStorage, ShareService
│       │       └── feature/                 # 18 Màn hình chức năng
│       │           ├── splash/              # SplashScreen.kt
│       │           ├── onboarding/          # OnboardingScreen.kt
│       │           ├── home/                # HomeScreen.kt
│       │           ├── templates/           # TemplateDetailScreen.kt
│       │           ├── editor/              # CardEditorScreen.kt, CardEditorViewModel.kt, EditorSheets.kt
│       │           ├── preview/             # CardPreviewScreen.kt
│       │           ├── export/              # ExportOptionsScreen.kt
│       │           ├── calendar/            # EventFormScreen.kt
│       │           ├── projects/            # SavedProjectsScreen.kt
│       │           └── settings/            # SettingsScreen.kt
│       └── test/kotlin/com/partiful/invitemaker/ # Unit Tests
│           └── CardDesignModelTest.kt
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 3. Hướng dẫn Build & Chạy dự án (Android Studio & CLI)

### Yêu cầu hệ thống:
- JDK 17 hoặc JDK 21.
- Android SDK với API 34, 35 hoặc 36.
- Android Studio Ladybug / Koala hoặc chạy trực tiếp bằng dòng lệnh qua `gradlew`.

### Chạy kiểm thử Unit Tests:
```powershell
cd android
.\gradlew.bat testDebugUnitTest
```

### Build file APK Debug:
```powershell
cd android
.\gradlew.bat assembleDebug
```
File APK đầu ra nằm tại: `android/app/build/outputs/apk/debug/app-debug.apk`

### Build file APK Release / AAB:
```powershell
cd android
.\gradlew.bat bundleRelease
```
File AAB đầu ra nằm tại: `android/app/build/outputs/bundle/release/app-release.aab`
