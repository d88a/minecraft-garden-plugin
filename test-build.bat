@echo off
echo Проверка структуры проекта Minecraft Garden Plugin
echo.

echo Проверяем наличие основных файлов...
if exist "pom.xml" (
    echo [OK] pom.xml найден
) else (
    echo [ERROR] pom.xml не найден
)

if exist "src\main\resources\plugin.yml" (
    echo [OK] plugin.yml найден
) else (
    echo [ERROR] plugin.yml не найден
)

if exist "src\main\resources\config.yml" (
    echo [OK] config.yml найден
) else (
    echo [ERROR] config.yml не найден
)

if exist "src\main\java\com\minecraft\garden\GardenPlugin.java" (
    echo [OK] GardenPlugin.java найден
) else (
    echo [ERROR] GardenPlugin.java не найден
)

if exist ".github\workflows\build.yml" (
    echo [OK] GitHub Actions workflow найден
) else (
    echo [ERROR] GitHub Actions workflow не найден
)

echo.
echo Проверяем структуру директорий...
if exist "src\main\java\com\minecraft\garden\managers" (
    echo [OK] Папка managers найдена
) else (
    echo [ERROR] Папка managers не найдена
)

if exist "src\main\java\com\minecraft\garden\commands" (
    echo [OK] Папка commands найдена
) else (
    echo [ERROR] Папка commands не найдена
)

if exist "src\main\java\com\minecraft\garden\listeners" (
    echo [OK] Папка listeners найдена
) else (
    echo [ERROR] Папка listeners не найдена
)

echo.
echo Проверка завершена!
echo.
echo Для сборки проекта нужно:
echo 1. Установить Java 22
echo 2. Установить Maven
echo 3. Запустить: mvn clean package
echo.
echo Или использовать GitHub Actions для автоматической сборки.
pause 