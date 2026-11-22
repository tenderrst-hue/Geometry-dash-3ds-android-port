# Chicken Gun Android Port — work in progress

This branch repurposes the Android scaffold as a touch-friendly **Chicken Gun menu prototype**, based on the user-provided `chickengun-ps2-menu.zip` source.

## Current state

- Android app renamed to Chicken Gun Android (`com.chickengun.android`).
- Portrait touch interface based on the supplied PS2 menu flow.
- Main menu: Play Team Battle, Shop, Servers and Settings.
- Create-match screen: Deathmatch / Team Fight, map selection and player count.
- Settings screen includes the vibration toggle.
- The original PS2 menu source lists eight maps, but marks only `Proto07` as converted/ready.
- GitHub Actions builds a debug APK on this branch.

**Important limitation:** the supplied PS2 archive contains menu/UI/input code and placeholder loading/game screens. It does not contain Chicken Gun gameplay, player models, weapons, maps, physics, bots or networking. This Android branch therefore ports the menu flow only; it is not yet a playable Chicken Gun game. The match screen explains this rather than pretending gameplay works.

## Build

Open in Android Studio or run:

```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Next porting work

1. Add actual Chicken Gun gameplay source and licensed game assets.
2. Replace the match placeholder with a 3D renderer and player controller.
3. Port weapons, animation, collision, audio and map loading.
4. Add bots or networking only when corresponding source/protocols are available.
5. Test performance and touch controls on Android devices.
