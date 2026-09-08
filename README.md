# youtube-ru — YouTube RU

**[youtube-ru](https://github.com/Sergei-Gerasimenko/youtube-ru)** — проект YouTube RU для Android TV и ТВ-приставок, адаптированный для использования в российском регионе.

**Автор доработок и сопровождающий проекта — @nx_mercy.**
Проект развивается на базе открытого исходного кода SmartTube.

## Возможности этой версии

- Название **YouTube RU**, значки и баннеры на чёрном фоне.
- Отдельный пакет приложения: `org.smarttube.ru`.
- Исправления воспроизведения TV-потоков, связанные с ошибкой HTTP 403.
- Исправлена обработка отказов доступа: вместо бесконечных повторных загрузок сохраняется причина ошибки.
- Добавлены проверки выбора форматов и загрузки аудио/видео, в том числе для видео с возрастными ограничениями при входе в аккаунт.
- Возможность работы через отдельно установленный **ByeByeDPI**.
- Обновления из каналов исходного SmartTube отключены для этой сборки.

## Установка

Сборки проекта публикуются только в разделе **[Releases этого репозитория](https://github.com/Sergei-Gerasimenko/youtube-ru/releases)**.

1. Скачайте APK, подходящий для вашей приставки.
2. Установите его через ADB или файловый менеджер Android TV.
3. Запустите **YouTube RU** и настройте подключение через ByeByeDPI, если оно необходимо.

Текущая сборка: **32.44-ru.2**, APK для ARMv7 — `YouTube_ru_32.44-ru.2_armeabi-v7a.apk`.
В релизе используется debug-подпись. Рядом с APK опубликована контрольная сумма SHA-256.

## ByeByeDPI

ByeByeDPI устанавливается отдельно; его настройки не встраиваются в YouTube RU.

1. Выберите в ByeByeDPI режим VPN.
2. Добавьте **YouTube RU** (`org.smarttube.ru`) в список приложений, использующих подключение.
3. Примените стратегию, которая работает у вашего провайдера, и включите подключение.
4. При необходимости включите автозапуск и подключение при запуске ByeByeDPI.

Если для входа в аккаунт требуется такое подключение, включите его до авторизации в YouTube RU.

## Сборка из исходников

Нужны **JDK 11** и **Android SDK с платформой 34**.
Путь к SDK задаётся через `ANDROID_HOME` или `local.properties`.

```sh
git clone https://github.com/Sergei-Gerasimenko/youtube-ru.git
cd youtube-ru
bash ./gradlew :smarttubetv:assembleStruDebug
```

APK появятся в `smarttubetv/build/outputs/apk/stru/debug/`.
Исходники MediaServiceCore и SharedModules включены в репозиторий; загружать git submodules не требуется.
Исходные сценарии GitHub Actions находятся в `.github/upstream-workflows/` как справочные файлы.

## Обратная связь

Ошибки и предложения для **youtube-ru**: **[Issues проекта](https://github.com/Sergei-Gerasimenko/youtube-ru/issues)**.
Сопровождающий: **@nx_mercy**.

## Авторство и лицензии

Доработки YouTube RU: **© 2026 @nx_mercy**.
Основной текст лицензии — [MIT](LICENSE). Авторство исходного SmartTube и лицензии сторонних компонентов сохранены в соответствующих файлах.

Исходные проекты:

- [SmartTube](https://github.com/yuliskov/SmartTube), commit `f23438ba2`.
- [MediaServiceCore](https://github.com/yuliskov/MediaServiceCore), commit `0b01a01730f256b3dde3cc9eb4d8ac90e48f3946` с доработками YouTube RU.
- [SharedModules](https://github.com/yuliskov/SharedModules), commit `86f032738e3a24f6ee85c7a4ccd9524b0d20b7aa`.
