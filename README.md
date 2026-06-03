# Mensis 🌸

**Mensis** è un'app Android per il monitoraggio del ciclo mestruale, della fertilità e della
gravidanza, in lingua italiana e con un forte accento sulla **privacy**: tutti i dati di salute
restano sul dispositivo, in un database locale, senza account né cloud.

Ispirata ad app come Flo, ma pensata per chi vuole tenere i propri dati **solo per sé**.

> ⚠️ Mensis fornisce informazioni a scopo divulgativo e **non sostituisce un parere medico**.
> Le previsioni del ciclo sono stime statistiche.

---

## ✨ Funzionalità

### Ciclo & fertilità
- Previsione di mestruazioni, ovulazione e **finestra fertile** in base allo storico.
- Riconoscimento automatico della **fase** (mestruale, follicolare, ovulatoria, luteale) con
  home dinamica che cambia contenuti e colori.
- **Calendario** che copre ±6 mesi con fasi proiettate ciclo per ciclo e marcatore di ovulazione.
- **Quick-log** contestuale alla fase + diario completo (flusso, dolore, muco, temperatura
  basale, test LH, umore, sintomi, rapporti, peso…).
- **Insights** con grafici (lunghezza dei cicli, andamenti) disegnati con Compose Canvas.

### Gravidanza
- Modalità gravidanza con sviluppo **settimana per settimana dalla 0**.
- Illustrazioni 2D dello sviluppo fetale (immagini di **pubblico dominio**, vedi crediti).
- **Contamovimenti** (kick counter) con cronologia delle sessioni.
- **Curva del peso** con range di aumento consigliato.
- Traguardi e contenuti gestazionali.

### Academy
- Articoli completi su ciclo, contraccezione, salute femminile e gravidanza.
- Video curati (player YouTube integrato).

### Assistente AI locale
- Chat con un modello **LLM eseguito interamente on-device** tramite
  [LiteRT-LM](https://github.com/google-ai-edge/LiteRT-LM)
  (`Qwen2.5-1.5B-Instruct`, formato `.litertlm`).
- Il modello viene **scaricato automaticamente** al primo avvio (~1,6 GB) e poi gira offline.
- Caricato in memoria all'apertura della chat e rilasciato alla chiusura, così l'app resta reattiva.

### Altro
- **Promemoria** per gli eventi imminenti (notifiche locali, ora configurabile, riprogrammate al boot).
- **Export PDF** "per il medico" e **export JSON** (anche da incollare nell'AI).
- Tema **chiaro/scuro**.
- **Blocco app** con PIN e sblocco biometrico.

---

## 🔐 Privacy

I dati del ciclo, del diario e della gravidanza sono salvati **solo localmente** (Room + DataStore)
e non lasciano mai il dispositivo. La connessione a Internet è usata **esclusivamente** per:

- scaricare il modello AI al primo avvio;
- riprodurre i video dell'Academy e le relative anteprime.

Nessun dato personale viene inviato a server di terze parti.

---

## 🏗️ Stack tecnico

| Ambito        | Tecnologia |
|---------------|------------|
| UI            | Jetpack Compose + Material 3 |
| Architettura  | MVVM (ViewModel + StateFlow) |
| Persistenza   | Room (`mensis_v2.db`) + DataStore Preferences |
| AI on-device  | LiteRT-LM `0.12.0` (Qwen2.5-1.5B `.litertlm`) |
| Media/Academy | androidyoutubeplayer, Markwon, Coil |
| Build         | Gradle 9.x, AGP 8.13, Kotlin 2.1.20, KSP |
| SDK           | minSdk 26 · target/compile 36 |

---

## 🚀 Build & installazione

> Mensis **non è pubblicata sul Play Store**: si installa tramite APK.

### Prerequisiti
- Android SDK installato; imposta il percorso in `local.properties`:
  ```properties
  sdk.dir=/percorso/al/tuo/Android/Sdk
  ```

### Build di debug
```bash
./gradlew :app:assembleDebug
# APK in app/build/outputs/apk/debug/
```

### Build di release (firmata)
1. Genera un keystore di release:
   ```bash
   keytool -genkeypair -v -keystore mensis-release.jks -alias mensis \
           -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Copia `keystore.properties.example` in `keystore.properties` e inserisci le tue password.
3. Compila:
   ```bash
   ./gradlew :app:assembleRelease
   # APK pronto: app/build/outputs/apk/release/Mensis-<versionName>.apk
   ```

Senza `keystore.properties` la build di release ripiega automaticamente sulla firma di debug.

### Installazione su dispositivo
```bash
adb install -r app/build/outputs/apk/release/Mensis-2.0.0.apk
```

> Al **primo avvio** l'app mostra una schermata introduttiva, chiede il permesso per le notifiche
> e avvia in background il download del modello AI (~1,6 GB). Serve una connessione a Internet
> solo per questo passaggio.

---

## 📁 Struttura del progetto

```
app/src/main/java/com/mensis/app/
├── MensisApp.kt / MainActivity.kt    # Application + entrypoint Compose
├── MensisEngine.kt / Models.kt       # Dominio: previsioni ciclo/fertilità (Kotlin puro)
├── BabyDevelopment.kt / PregnancyContent.kt
├── ai/                               # Chat locale + download/gestione modello LiteRT-LM
├── academy/                          # Catalogo articoli/video + UI
├── data/                             # Room (db/), repository, export PDF/JSON, settings
├── notifications/                    # Promemoria (AlarmManager) + BootReceiver
└── ui/                               # Compose: dashboard, calendario, insights,
                                      #   gravidanza, logging, settings, theme, componenti
```

---

## 🖼️ Crediti immagini

Le illustrazioni dello sviluppo fetale derivano dal file
**"Fetus proposal.svg"** di Wikimedia Commons (**pubblico dominio**), ritagliate per le varie
settimane. I crediti sono riportati anche in *Impostazioni → Crediti immagini* nell'app.

---

## 👥 Autori

- **Ak1r4Yuk1**
- **Paranoid**

## 📄 Licenza

Distribuito con licenza **MIT** — vedi il file [LICENSE](LICENSE).

Sei libero di usare, modificare e ridistribuire il codice, **a condizione di mantenere i crediti**:
la nota di copyright e di licenza (© 2026 Ak1r4Yuk1 and Paranoid) va conservata in ogni copia o
porzione sostanziale del software.

> Le immagini dello sviluppo fetale sono di pubblico dominio (vedi *Crediti immagini*) e non
> rientrano nel copyright del codice.
