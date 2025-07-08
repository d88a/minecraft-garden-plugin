@echo off
echo === GardenPlugin - Информация о сборке ===
echo.

echo 📦 Сборка проекта происходит автоматически на GitHub Actions
echo.
echo 🔄 При каждом push в main/master ветку:
echo    - Проект автоматически собирается
echo    - Создается новый релиз
echo    - JAR файл загружается в релиз
echo.

echo 📋 Для локальной сборки:
echo    1. Установите Maven: https://maven.apache.org/download.cgi
echo    2. Добавьте Maven в PATH
echo    3. Запустите: mvn clean package
echo.

echo 🌐 GitHub Actions:
echo    - Автоматическая сборка при push
echo    - Проверка сборки при pull requests
echo    - Создание релизов с версиями
echo.

echo 📥 Скачать готовый плагин:
echo    https://github.com/[username]/minecraft-garden-plugin/releases
echo.

echo ✅ Статус проекта: 65%% реализовано
echo 📊 Подробности: см. IMPLEMENTATION_STATUS.md
echo.

pause 