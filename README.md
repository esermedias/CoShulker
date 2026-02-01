# CoShulker

Minecraft Paper sunucularinda shulker box'lari havada sag tiklayarak acmanizi saglayan hafif bir eklenti.

## Ozellikler
- Havada sag tik ile shulker acma
- Offhand destegi (istege bagli)
- Shulker icine shulker koymayi engelleme (istege bagli)
- Sneak zorunlulugu (istege bagli)
- Ozellestirilebilir envanter basligi ve mesajlar
- Basit komutlar ve izinler

## Gereksinimler
- Paper veya Paper tabanli bir sunucu (api-version: 1.20)
- Java 17+ (Maven) veya Java 21 (Gradle) ile derleme

## Kurulum
1. `build/libs/CoShulker-1.0.0.jar` dosyasini `plugins/` klasorune atin.
2. Sunucuyu yeniden baslatin.
3. `plugins/CoShulker/config.yml` dosyasini ihtiyaciniza gore duzenleyin.

## Komutlar
- `/cos help` - Yardim
- `/cos reload` - Ayarlari yeniler

## Izinler
- `cos.use` - Havada shulker acma izni (varsayilan: true)
- `cos.reload` - Ayar yenileme izni (varsayilan: op)

## Yapilandirma (config.yml)

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
  not-shulker: "{prefix}<yellow>elinde shulker yok.</yellow>"
  already-open: "{prefix}<yellow>zaten acik.</yellow>"
  open: "{prefix}<green>shulker acildi.</green>"
  unknown-command: "{prefix}<red>bilinmeyen komut.</red>"
  help:
    - "{prefix}<gray>komutlar:</gray>"
    - "<gold>/cos help</gold> <gray>- yardim</gray>"
    - "<gold>/cos reload</gold> <gray>- ayarlari yenile</gray>"
```

## Kaynaktan Derleme

Maven:
```bash
mvn clean package
```

Gradle:
```bash
./gradlew build
```

## Katki
PR ve issue'lar her zaman acik. Fikirlerinizi paylasabilirsiniz.

## Lisans
MIT License. 2026
