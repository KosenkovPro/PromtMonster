# PromtMonster

Android app for browsing AI-generated images with their prompts in 13 languages.

## Features

- Photo grid (3 columns, portrait orientation)
- Tap to view full image + English prompt + localized prompt
- 13 languages: EN, ZH, HI, ES, FR, AR, BN, PT, RU, UR, ID, DE, JA
- Dark theme

## Requirements

- Android Studio Ladybug (2024.2.x) or newer
- JDK 21
- AGP 8.7.3 / Gradle 8.11.1

## How to Add Photos

### Option 1: Bundled in app (assets)
Place `.jpg`/`.png`/`.webp` files in:
```
app/src/main/assets/images/
```

Add prompts to `app/src/main/assets/prompts.json`:
```json
[
  {
    "filename": "my_photo.jpg",
    "prompt_en": "Your English prompt here",
    "translations": {
      "ru": "Промпт на русском",
      "zh": "中文提示词",
      "de": "Deutsches Prompt"
    }
  }
]
```

### Option 2: User-added photos (runtime)
Copy photos to the device folder:
```
Android/data/pro.kosenkov.promtmonster/files/
```
Accessible via any file manager. Photos without prompts will show no text.

## Language Codes for prompts.json

| Language   | Code |
|------------|------|
| English    | en   |
| Chinese    | zh   |
| Hindi      | hi   |
| Spanish    | es   |
| French     | fr   |
| Arabic     | ar   |
| Bengali    | bn   |
| Portuguese | pt   |
| Russian    | ru   |
| Urdu       | ur   |
| Indonesian | id   |
| German     | de   |
| Japanese   | ja   |

## Build

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Package

`pro.kosenkov.promtmonster`
