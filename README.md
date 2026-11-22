# CoShulker

[![Build](https://github.com/esermedias/CoShulker/actions/workflows/build.yml/badge.svg)](https://github.com/esermedias/CoShulker/actions/workflows/build.yml)
![Paper](https://img.shields.io/badge/Paper-1.20%2B-1f6feb?style=flat-square)
![Folia](https://img.shields.io/badge/Folia-supported-238636?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-f89820?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)

A small Paper plugin that lets players open a shulker box straight from their hand with a right-click, without placing it on the ground first.

## Features

- Right-click in the air to open the shulker box you're holding
- Optional off-hand support
- Optional sneak requirement
- Blocks putting shulker boxes inside the open box (configurable)
- The held box is locked while open: no dropping, swapping or moving it
- MiniMessage formatting for the inventory title and all messages
- Runs on **Folia** as well as Paper

## Requirements

- Paper 1.20 or newer, or Folia
- Java 21

## Installation

1. Download the latest jar from [Releases](../../releases), or build it yourself (see below).
2. Drop it into your server's `plugins/` folder.
3. Restart the server.
4. Adjust `plugins/CoShulker/config.yml` and run `/cos reload`.

## Commands and permissions

| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| *(right-click)* | Open the held shulker box | `cos.use` | everyone |
| `/cos help` | Show help | none | everyone |
| `/cos reload` | Reload the config | `cos.reload` | op |

The open permission can be changed with `settings.permission.open`. Leave it empty to allow everyone.

## Configuration

```yml
settings:
  open-in-air: true
  allow-offhand: true
  allow-shulker-inside: false
  require-sneak: false
  inventory-title: "<gold>cos</gold> <gray>shulker</gray>"
  permission:
    open: "cos.use"

messages:
  prefix: "<dark_gray>[</dark_gray><gold>cos</gold><dark_gray>]</dark_gray> "
  no-permission: "{prefix}<red>yetkin yok.</red>"
  reloaded: "{prefix}<green>ayarlar yenilendi.</green>"
  already-open: "{prefix}<yellow>zaten acik.</yellow>"
  open: "{prefix}<green>shulker açıldı.</green>"
  unknown-command: "{prefix}<red>bilinmeyen komut.</red>"
```

Messages ship in Turkish by default. All of them accept MiniMessage tags and the `{prefix}` and `{player}` placeholders.

## Folia

CoShulker declares `folia-supported: true`. It doesn't schedule any tasks: everything happens inside player events, which Folia already runs on the player's own region thread. Open sessions are kept in a concurrent map, since players in different regions are handled on different threads at the same time.

## Building

```bash
./gradlew build
```

The jar ends up in `build/libs/`.

## License

[MIT](LICENSE)
