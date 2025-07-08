@echo off
echo === Сборка GardenPlugin ===
echo.

echo 1. Очистка проекта...
call mvn clean
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось очистить проект!
    pause
    exit /b 1
)

echo 2. Компиляция...
call mvn compile
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось скомпилировать проект!
    pause
    exit /b 1
)

echo 3. Сборка JAR файла...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось собрать JAR файл!
    pause
    exit /b 1
)

echo.
echo === Сборка завершена успешно! ===
echo JAR файл находится в: target/garden-plugin-1.0.0.jar
echo.
pause 