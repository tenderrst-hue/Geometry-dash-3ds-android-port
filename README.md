# Geometry Dash 3DS — Android port (work in progress)

This repository is the Android porting workspace for [gd3ds](https://github.com/AleFunky/gd3ds).

## Current state

- Android app module and reproducible debug-APK workflow.
- Portrait layout with two vertically stacked logical screens: gameplay area above, secondary 3DS-style interface below.
- Touch-down / touch-hold / touch-release events are captured on the upper screen.
- A lightweight 60 Hz presentation loop drives the current visual/input prototype.

**Important:** this is a porting scaffold, not yet the original GD3DS game running on Android. The upstream game uses libctru, citro2d and citro3d; its rendering, input, audio, filesystem and platform code still need Android replacements. The current screen is a visual test harness, not the original gameplay renderer.

## Build

Open in Android Studio or run:

```bash
gradle assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Porting roadmap

1. Replace the prototype Canvas renderer with an Android renderer (OpenGL ES) and map the upstream sprite/texture pipeline.
2. Port the 3DS input abstraction to Android touch and key events while preserving press/hold/release semantics.
3. Replace libctru filesystem, timing, audio and system calls.
4. Port the Citro2D/Citro3D drawing paths, including sprite batching, tinting, blending, particles and the two-screen UI.
5. Bring over the game source in dependency-sized steps, then test physics and frame pacing on real devices.

The upstream source and original project assets are under CC BY-SA 4.0 as described by the upstream project. Geometry Dash trademarks and RobTop-owned assets remain their respective owners' property.
