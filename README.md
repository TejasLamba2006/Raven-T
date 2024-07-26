# Raven T

Raven T builds on the foundation of Raven b++, a comprehensive PvP and utility mod for Minecraft 1.8.x. Supporting both Forge and Feather, Raven T enhances your gameplay with quality of life improvements, bug fixes, and more. It incorporates features from Raven Bs and Raven XD, designed specifically for discreet (closet) cheating.

Official Raven T Discord: [https://discord.gg/RV6sWX9d7p](https://discord.gg/RV6sWX9d7p)

## Installation & Download

1. Download the latest version [here](https://github.com/TejasLamba2006/Raven-T/releases/latest).
2. Download Forge for 1.8.9 [here](https://maven.minecraftforge.net/net/minecraftforge/forge/1.8.9-11.15.1.2318-1.8.9/forge-1.8.9-11.15.1.2318-1.8.9-installer.jar) and run the installer.
3. If you can't find the Forge version, close the Minecraft launcher using the Task Manager. For further assistance, join the [Discord](https://discord.gg/UqJ8ngteud).
4. After launching the Forge version once, navigate to `%appdata%\.minecraft\mods` (Linux users will need to find the `.minecraft` folder location). Place the downloaded mod from [here](https://github.com/TejasLamba2006/Raven-T/releases/latest) in the `mods` folder.
5. Launch Forge and enjoy! For additional help, join our [Discord](https://discord.gg/RV6sWX9d7p).

## User Information

- [Latest Version](https://github.com/TejasLamba2006/Raven-T/releases/latest): This may have minor bugs but no game-breaking ones.
- [Stable Versions](https://github.com/TejasLamba2006/Raven-T/releases): All previous versions can be found in the releases section.

## How to Build it Yourself

1. [Download](https://gradle.org/next-steps/?version=2.7&format=bin) and [install](https://docs.gradle.org/current/userguide/installation.html) Gradle.
2. Clone the repository to a folder.
3. Open a terminal/command prompt and type `./gradlew build`.
4. Find the build in `builds/libs` in the directory you cloned.

## Improved/Added Modules

### Render

- 🆕 **Indicators** - Shows arrows indicating the direction of players, ender pearls, fireballs, and other projectiles.
- 🔥 **Projectiles** - Displays the landing spot of projectiles! (New options include highlighting on hit and disabling for uncharged bows).
- 🆕 **BedPlates** - Shows what the bed is covered with.
- 🔥 **Chams** - Renders players through walls. (Now includes AntiBot).
- 🆕 **BreakProgress** - Show block progress in modes like percentage, seconds and decimals.

### Player

- 🆕 **AntiFireball** - Automatically shoots fireballs back at the player who shot them.
- 🔥 **AutoTools** - Automatically selects the best tool for the block you're mining. (Now less buggy, detects enchants and has more options)

### Other

- 🆕 **RotationHandler** - Allows you to manage rotation for the client.
- 🔥 **SlotHandler** - Allows you to manage inventory slots for the client.
## Contributing

To contribute code, follow [this guide](https://gist.github.com/MarcDiethelm/7303312#file-contributing-md). Ideally, work on the latest `dev` branch. If you have any questions, feel free to ask in the [Discord](https://discord.gg/RV6sWX9d7p).
