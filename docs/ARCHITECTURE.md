# FarNDone Architecture

This document describes the architectural layout of **FarNDone**, a high-performance cross-platform PC remote-control system built for low latency, modularity, and clean separation of concerns.

---

## 1. High-Level System Architecture

The following ASCII diagram illustrates the end-to-end data flow from physical user interaction on the Android smartphone to native OS input injection on the PC.

```
+-----------------------------------------------------------------------------------------+
|                                    ANDROID MOBILE CLIENT                                |
|                                                                                         |
|  [Touch Screen / Gyro] ---> [Gestures & Sensors] ---> [Activities (ViewBinding)]        |
|                                                              |                          |
|                                                              v                          |
|                                                    [ConnectionManager] (Singleton)      |
|                                                              |                          |
|                                                              v                          |
|                                                     [OkHttp WebSocket]                  |
+--------------------------------------------------------------|--------------------------+
                                                               |
                                                 ws://<ip>:8765/ws (JSON v1)
                                                               |
+--------------------------------------------------------------v--------------------------+
|                                  DESKTOP SERVER (.NET 10)                               |
|                                                                                         |
|       +-------------------------------------------------------------------------+       |
|       | Kestrel WebSocket Server (ASP.NET Core, Raw WebSocket Middleware)       |       |
|       +-------------------------------------------------------------------------+       |
|                                              |                                          |
|                                     (Raw JSON Message)                                  |
|                                              v                                          |
|       +-------------------------------------------------------------------------+       |
|       | Pairing & Authentication Manager (Token Hashes, Rate Limiter, Cooldown) |       |
|       +-------------------------------------------------------------------------+       |
|                                              |                                          |
|                                  (Validated Command Object)                             |
|                                              v                                          |
|       +-------------------------------------------------------------------------+       |
|       | Command Dispatcher                                                      |       |
|       +-------------------------------------------------------------------------+       |
|                               /                              \                          |
|              (IInputInjector)                                  (IAudioController)       |
|                      |                                                 |                |
+----------------------|-------------------------------------------------|----------------+
                       |                                                 |
         +-------------+-------------+                     +-------------+-------------+
         |                           |                     |                           |
         v                           v                     v                           v
+-----------------+         +-----------------+   +-----------------+         +-----------------+
|  Linux Driver   |         | Windows Driver  |   |  Linux Audio    |         |  Windows Audio  |
|  (/dev/uinput)  |         |   (SendInput)   |   | (PipeWire/wpctl)|         | (NAudio/Core)   |
| Wayland & X11   |         |    Win32 API    |   | Master Volume   |         | Endpoint Volume |
+-----------------+         +-----------------+   +-----------------+         +-----------------+
```

---

## 2. Desktop Solution Layout (.NET 10)

The desktop codebase is organized into modular assemblies with strict dependency boundaries:

```
FarNDone.Desktop (Avalonia UI & Entry Point)
  ├── References: FarNDone.Core, FarNDone.Server
  ├── References (conditional/runtime): FarNDone.Platform.Linux, FarNDone.Platform.Windows
  │
  ├── FarNDone.Server (WebSocket, Auth & Dispatching)
  │     └── References: FarNDone.Core ONLY
  │
  ├── FarNDone.Platform.Linux (uinput + wpctl)
  │     └── References: FarNDone.Core ONLY
  │
  ├── FarNDone.Platform.Windows (SendInput + NAudio)
  │     └── References: FarNDone.Core ONLY
  │
  ├── FarNDone.Core (Protocol Models & Interfaces)
  │     └── Zero dependencies on other projects
  │
  └── FarNDone.Tests (xUnit Tests)
        └── References: FarNDone.Core, FarNDone.Server
```

### Dependency Rules:
1. **`FarNDone.Core`** defines:
   - All protocol models (`AuthMessage`, `MoveMessage`, `ClickMessage`, etc.).
   - Service interfaces: `IInputInjector`, `IAudioController`, `ITransport`.
   - Has **zero** project dependencies and **no platform-specific code**.
2. **`FarNDone.Server`**:
   - Implements ASP.NET Core Kestrel raw WebSocket handler.
   - Manages pairing state, 6-digit PIN verification, rate limiting, and trusted device token hashing.
   - Dispatches validated incoming messages to `IInputInjector` and `IAudioController`.
   - Has **no knowledge of Linux or Windows**; it programs strictly against Core interfaces.
3. **`FarNDone.Platform.Linux`**:
   - `LinuxInputInjector`: Interacts directly with Linux kernel virtual input via `/dev/uinput` ioctl calls. This is fully compatible with modern Wayland compositors (Fedora GNOME, Omarchy/Hyprland) as well as X11.
   - `LinuxAudioController`: Controls master volume and mute state via `wpctl` (standard on modern PipeWire systems).
4. **`FarNDone.Platform.Windows`**:
   - `WindowsInputInjector`: Uses Win32 P/Invoke to call `SendInput` for smooth mouse and keyboard injection.
   - `WindowsAudioController`: Controls master volume using Windows CoreAudio APIs via NAudio.
5. **`FarNDone.Desktop` (Avalonia UI)**:
   - Application entry point.
   - **Crucial Rule**: Platform resolution happens **ONLY** here at startup:
     ```csharp
     IInputInjector inputInjector;
     IAudioController audioController;

     if (OperatingSystem.IsWindows())
     {
         inputInjector = new WindowsInputInjector();
         audioController = new WindowsAudioController();
     }
     else if (OperatingSystem.IsLinux())
     {
         inputInjector = new LinuxUinputInjector();
         audioController = new LinuxAudioController();
     }
     else
     {
         throw new PlatformNotSupportedException();
     }
     ```
   - Displays QR code, manual connection details, trusted devices list, and pairing approval dialogs.

---

## 3. Android Client Layout (Java)

The Android application is built with vanilla Java (minSdk 26, targetSdk 35) following standard Android patterns:

```
app/src/main/java/com/farndone/android/
├── activities/
│   ├── ConnectActivity.java       # Main dashboard: Scan QR button, Wi-Fi/BT switch, saved PCs list
│   ├── ScanQrActivity.java        # CameraX + ML Kit Barcode scanner viewfinder
│   ├── ManualCodeActivity.java    # Fallback: Manual IP, port, and 6-digit code entry
│   ├── TrackpadActivity.java      # Virtual trackpad, scroll strip, and mouse click buttons
│   ├── PresentationActivity.java  # Slide navigation, timer, notes, and gyro laser pointer
│   └── MediaActivity.java         # Media playback buttons, volume controls, app settings
│
├── network/
│   ├── ConnectionManager.java     # Thread-safe Singleton managing the active OkHttp WebSocket
│   ├── MessageParser.java         # JSON serialization and deserialization
│   └── NsdDiscoveryManager.java   # mDNS / Network Service Discovery for finding PCs on LAN
│
├── services/
│   └── KeepAliveService.java      # Foreground service keeping the WebSocket alive when screen is off
│
└── sensors/
    └── GyroscopePointer.java      # SensorEventListener applying calibration and exponential smoothing
```

### Navigation & State Sharing:
- **Navigation Flow**:
  - `ConnectActivity` is the entry point.
  - Tapping **"Scan QR code"** launches `ScanQrActivity`.
  - From `ScanQrActivity`, the user can tap **"Enter code manually"** to open `ManualCodeActivity`.
  - Both `ScanQrActivity` and `ManualCodeActivity` return the address, port, and code back to `ConnectActivity`.
  - Bottom navigation bar allows switching between:
    - **Connect** (`ConnectActivity`)
    - **Trackpad** (`TrackpadActivity`)
    - **Slides** (`PresentationActivity`)
    - **Media** (`MediaActivity`)
- **Singleton Connection**:
  - `ConnectionManager` holds the single active `WebSocket` instance.
  - When the user switches between `TrackpadActivity`, `PresentationActivity`, and `MediaActivity`, the connection stays persistently open and does not reconnect.
- **Background Execution**:
  - Android aggressively sleeps background tasks. `KeepAliveService` runs as a foreground service with a persistent notification while connected, ensuring uninterrupted control during long presentations.

---

## 4. Concurrency & Threading Model

- **Desktop**:
  - Kestrel processes incoming WebSocket frames asynchronously on the .NET thread pool.
  - Message parsing and security checks occur on the worker thread.
  - Input injection calls (`uinput` / `SendInput`) execute immediately with minimal latency (<1 ms).
  - UI updates (e.g., showing the pairing prompt, updating trusted devices) are dispatched to the Avalonia UI thread via `Dispatcher.UIThread.Post()`.
- **Android**:
  - OkHttp runs WebSocket frame reading and network I/O on its dedicated background background thread.
  - Touch motion events from `GestureDetector` / `onTouchEvent` generate messages posted asynchronously to the WebSocket.
  - Incoming server messages (`state`, `paired`, `authFail`) are posted to the Android Main Looper (`runOnUiThread`) to update UI widgets.
