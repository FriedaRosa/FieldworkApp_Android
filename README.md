# Fieldwork Data Mask

Fieldwork Data Mask is an Android application designed to streamline the digitization of botanical field notes. It leverages on-device OCR and cloud-based AI to transform handwritten or printed field records into structured, standardized data compatible with international biodiversity standards.

## 🌿 Key Features

*   **CameraX Integration**: Capture high-quality images of field notes and specimens directly within the app.
*   **ML Kit OCR**: Fast, on-device text recognition to extract raw text from captured images.
*   **Gemini AI Extraction**: Uses Google's Gemini 1.5 Flash to parse unstructured OCR text into structured fields using a botanical-context-aware prompt.
*   **In-App API Configuration**: Easily switch between development and production API keys via the Settings menu within the app, with secure fallback to build-time secrets.
*   **Darwin Core (DwC) Standardization**: Automatically maps extracted data to Darwin Core terms (e.g., `scientificName`, `recordedBy`, `eventDate`, `decimalLatitude`) for immediate compatibility with platforms like GBIF.
*   **Room Database Persistence**: Reliable local storage for all field records, supporting offline workflows.
*   **Location Services**: Capture precise GPS coordinates and altitude automatically using Google Play Services.
*   **CSV Export**: Export your digitized database to a Darwin Core-compliant CSV file for easy sharing and integration with desktop GIS or herbarium management software.

## 🛠 Tech Stack

*   **Language**: Kotlin (2.1.10)
*   **UI**: Jetpack Compose with Material 3
*   **Database**: Room (2.7.0-alpha13) with KSP
*   **AI/ML**: Google Generative AI (Gemini) & ML Kit Text Recognition
*   **Image Loading**: Coil
*   **Asynchronous Work**: Kotlin Coroutines & Flow

## 🚀 Getting Started

### Prerequisites

*   **Android Studio**: Ladybug (2024.2.1) or newer.
*   **Android Device**: Physical device or emulator running API 26 (Oreo) or higher.
*   **Google AI API Key**: Required for the Gemini extraction features. Obtain one for free at [Google AI Studio](https://aistudio.google.com/).
*   **Google Play Services**: Required on the device for Location Services (GPS) and ML Kit functionality.

### Installation & Development Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/FriedaRosa/FieldworkApp_Android.git
    ```

2.  **Set up the API Key (Choose one)**:
    *   **Option A (Build-time)**: Create/Open `local.properties` in the project root and add:
        ```properties
        GEMINI_API_KEY=your_actual_key_here
        ```
    *   **Option B (In-App)**: Leave the property blank, build the app, and enter the key via the **Settings (⚙️)** menu after launch.

3.  **Sync & Build**:
    Open the project in Android Studio and perform a **Gradle Sync**. This generates the `BuildConfig` required for secure API key management.

4.  **Deploy**:
    Run the `:app` module on your connected device.

### Using your own API Key in-app

If you are using a pre-built version of the app (APK) or want to change keys on the fly:
1. Tap the **Settings** (⚙️) icon in the top app bar.
2. Enter your **Gemini API Key**.
3. Tap **Save**.
The app will prioritize this key over any key provided during the build process.

## 📊 Data Standard

This project prioritizes data interoperability. All internal models and exports follow the [Darwin Core Quick Reference Guide](https://dwc.tdwg.org/terms/). 

**Standardized Fields include:**
- `scientificName`
- `recordedBy`
- `recordNumber`
- `eventDate`
- `locality`
- `decimalLatitude` / `decimalLongitude`
- `minimumElevationInMeters`
- `occurrenceRemarks`

## 🤝 Contributing

Contributions are welcome! Whether it's improving the AI prompt, adding support for more Darwin Core terms, or enhancing the UI, feel free to open an issue or submit a pull request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
