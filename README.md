<p align="center">
  <img src="assets/icons/logo.jpg" alt="FarNDone Logo" width="180"/>
</p>

<h1 align="center">FarNDone</h1>

<p align="center">
  <strong>Cross-Platform PC Remote Control System</strong><br>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-GPL--3.0-blue" alt="License"></a>
  <img src="https://img.shields.io/badge/.NET-10.0%20LTS-512BD4" alt=".NET 10">
  <img src="https://img.shields.io/badge/Android-Java%20(minSdk%2026)-3DDC84" alt="Android Java">
  <img src="https://img.shields.io/badge/UI-Avalonia%20XAML-9B51E0" alt="Avalonia UI">
  <a href="https://github.com/SBAI-Youness/FarNDone/pulls"><img src="https://img.shields.io/badge/PRs-welcome-brightgreen" alt="Contributions"></a>
</p>

---

## Overview

**FarNDone** turns an Android smartphone into a responsive, wireless remote control for your PC. The mobile client captures user interactions - such as trackpad swipes, mouse clicks, presentation clicks, media controls, and gyroscope orientation - and transmits lightweight JSON messages over WebSocket to the PC server. The PC application translates these events into native operating system input injection on both **Linux**<!-- (Wayland/X11 via `/dev/uinput` and PipeWire)--> and **Windows**<!-- (via `SendInput` and CoreAudio)-->.

---

## Key Features

1. **Secure Pairing & Connection**:
   - Pair quickly using a QR code scanned via CameraX + ML Kit, or enter PC address, port, and a 6-digit one-time PIN manually.
   - Wi-Fi transport (raw WebSocket) by default, with an architecture designed to support Bluetooth as a secondary transport.
   - PC user approval prompt with SHA-256 hashed persistent device tokens for seamless reconnection.
2. **Trackpad & Scrolling**:
   - Smooth relative cursor movement batched at ~60 FPS (~16 ms).
   - Tap to click, double tap, two-finger tap for right click, dedicated left/right click buttons.
   - Vertical scroll strip or two-finger scroll with optional natural scrolling.
   - Tap-and-drag / button-hold for drag-and-drop operations.
3. **Presentation Controller**:
   - Previous / Next slide buttons with prominent touch targets.
   - Start presentation, End presentation, and Black screen.
   - Speaker notes area and presentation timer.
4. **Digital Laser Pointer (Air Mouse)**:
   - Uses the phone's gyroscope (`SensorManager`) to steer an on-screen pointer while holding a trigger button.
   - Includes real-time sensor calibration and exponential smoothing.
5. **Media & Volume**:
   - PC volume up, volume down, and mute toggles.
   - Play/pause, next track, and previous track media keys.
   - Real-time volume state feedback from PC to phone.

---

## Monorepo Layout

```
FarNDone/
├── android/            # Android Mobile Application (Java, Gradle, ViewBinding)
├── assets/             # Branding assets, logos, and UI graphics
├── desktop/            # .NET 10 Solution (C#, Avalonia UI, Kestrel Server)
├── docs/               # Architecture, Protocol, and Decision records
│   ├── ARCHITECTURE.md # System architecture, ASCII diagrams, data flows
│   ├── DECISIONS.md    # Architecture Decision Records (ADRs)
│   └── PROTOCOL.md     # Full JSON-over-WebSocket protocol specification
├── .gitignore          # Comprehensive ignore rules for .NET, Android, & IDEs
├── CONTRIBUTING.md     # Branching, conventional commits, peer review rules
└── README.md           # Project overview and navigation
```

---

## Technology Stack

### Mobile Client (`/android`)
- **Language**: Java (Android SDK minSdk 26, targetSdk 35)
- **Architecture**: Single-responsibility Activities (exactly 6), ViewBinding, Material Components
- **Networking**: OkHttp WebSocket client managed via a singleton `ConnectionManager`
- **Vision & Sensors**: CameraX with Google ML Kit Barcode Scanning, Android `SensorManager`
- **Background**: Foreground service to sustain WebSocket connection when the screen is turned off

### Desktop Server & UI (`/desktop`)
- **Language & Runtime**: C# on .NET 10 (LTS)
- **UI Framework**: Avalonia UI (cross-platform XAML, system tray, light theme)
- **Server**: ASP.NET Core Kestrel raw WebSocket server (no heavy SignalR overhead)
- **Linux Platform**: `/dev/uinput` for Wayland-compatible input injection; `wpctl` for PipeWire audio
- **Windows Platform**: Win32 `SendInput` (P/Invoke) for input injection; NAudio / CoreAudio for audio
- **Testing**: xUnit tests for protocol serialization, auth rate limiting, and command dispatching

---

## Documentation Links

- [Protocol Specification (PROTOCOL.md)](docs/PROTOCOL.md): Exact message formats, ranges, handshake sequences, and error codes.
- [System Architecture (ARCHITECTURE.md)](docs/ARCHITECTURE.md): Component diagrams, OS abstraction layers, and threading model.
- [Architectural Decisions (DECISIONS.md)](docs/DECISIONS.md): Justifications for Avalonia, raw WebSockets, `/dev/uinput`, and Java on Android.
- [Contribution Guidelines (CONTRIBUTING.md)](CONTRIBUTING.md): Git branch conventions, conventional commits, and peer review policy.
