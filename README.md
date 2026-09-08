# YouTube RU

Версия SmartTube для Android TV с исправлениями воспроизведения и оформлением YouTube RU.

- Исправлена обработка отказов доступа и выбор резервного источника для видео с возрастным ограничением.
- Для TV-потоков используется отдельный декодер TCL-плеера и соответствующий signature timestamp.
- Добавлены тесты выбора форматов, декодера и загрузки аудио/видео на устройстве.
- Название YouTube RU, чёрные значки и баннеры, пакет `org.smarttube.ru`.
- Обновления из каналов исходного SmartTube отключены для этой сборки.

## Сборка

Нужны JDK 11 и Android SDK с платформой 34. Путь к SDK задаётся через `ANDROID_HOME` или `local.properties`.
Все вложенные модули включены в репозиторий; загрузка git submodules не требуется.
Исходные сценарии GitHub Actions сохранены в `.github/upstream-workflows/` для справки.

```sh
bash ./gradlew :smarttubetv:assembleStruDebug
```

APK находятся в `smarttubetv/build/outputs/apk/stru/debug/`.
Проверенная на Xiaomi Android TV сборка: `YouTube_ru_32.44-ru.1_armeabi-v7a.apk`.

## ByeByeDPI

ByeByeDPI устанавливается и настраивается отдельно. В режиме VPN добавьте YouTube RU
(`org.smarttube.ru`) в список приложений, использующих подключение.
Включите подключение при запуске и автозапуск ByeByeDPI, если они нужны.
Стратегия зависит от провайдера; настройки VPN не встраиваются в APK.

## Исходные проекты

- [SmartTube](https://github.com/yuliskov/SmartTube), commit `f23438ba2`.
- [MediaServiceCore](https://github.com/yuliskov/MediaServiceCore), commit `0b01a01730f256b3dde3cc9eb4d8ac90e48f3946` с локальными исправлениями.
- [SharedModules](https://github.com/yuliskov/SharedModules), commit `86f032738e3a24f6ee85c7a4ccd9524b0d20b7aa`.

Лицензии и уведомления об авторских правах исходных проектов сохранены.
Документация исходного SmartTube доступна в [его репозитории](https://github.com/yuliskov/SmartTube#readme).
