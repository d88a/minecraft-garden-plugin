@echo off
echo ========================================
echo Minecraft Garden Plugin - Quick Build
echo ========================================
echo.

echo Проверяем наличие Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ОШИБКА] Java не установлена или не найдена в PATH
    echo.
    echo Для установки Java 22:
    echo 1. Скачайте с https://adoptium.net/temurin/releases/?version=22
    echo 2. Установите и добавьте в PATH
    echo 3. Перезапустите командную строку
    echo.
    echo Альтернативно, используйте GitHub Actions для сборки:
    echo 1. Загрузите код в GitHub
    echo 2. Перейдите в Actions вкладку
    echo 3. Запустите workflow "Build Plugin"
    echo.
    pause
    exit /b 1
)

echo [OK] Java найдена
java -version

echo.
echo Проверяем наличие Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ПРЕДУПРЕЖДЕНИЕ] Maven не найден
    echo.
    echo Для установки Maven:
    echo 1. Скачайте с https://maven.apache.org/download.cgi
    echo 2. Распакуйте в C:\Program Files\Apache\maven
    echo 3. Добавьте C:\Program Files\Apache\maven\bin в PATH
    echo.
    echo Или используйте GitHub Actions для автоматической сборки!
    echo.
    pause
    exit /b 1
)

echo [OK] Maven найден
mvn -version

echo.
echo Собираем проект...
mvn clean package

if errorlevel 1 (
    echo.
    echo [ОШИБКА] Сборка не удалась!
    echo Проверьте ошибки выше и исправьте их.
    pause
    exit /b 1
)

echo.
echo [УСПЕХ] Сборка завершена!
echo JAR файл: target\garden-plugin-1.0.0.jar
echo.
echo Для тестирования:
echo 1. Скопируйте JAR файл в папку plugins вашего сервера
echo 2. Перезапустите сервер
echo 3. Используйте команду /garden
echo.
pause 