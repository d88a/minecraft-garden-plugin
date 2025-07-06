@echo off
echo ========================================
echo Сборка и тестирование GardenPlugin
echo ========================================
echo.

echo 1. Очистка и сборка проекта...
call mvn clean package
if %errorlevel% neq 0 (
    echo ОШИБКА: Сборка не удалась!
    pause
    exit /b 1
)
echo ✓ Сборка завершена успешно
echo.

echo 2. Проверка JAR файла...
if not exist "target\GardenPlugin-*.jar" (
    echo ОШИБКА: JAR файл не найден!
    pause
    exit /b 1
)
echo ✓ JAR файл найден
echo.

echo 3. Проверка содержимого JAR...
jar -tf target\GardenPlugin-*.jar | findstr "plugin.yml"
if %errorlevel% neq 0 (
    echo ОШИБКА: plugin.yml не найден в JAR!
    pause
    exit /b 1
)
echo ✓ plugin.yml найден в JAR
echo.

echo 4. Проверка основного класса...
jar -tf target\GardenPlugin-*.jar | findstr "GardenPlugin.class"
if %errorlevel% neq 0 (
    echo ОШИБКА: GardenPlugin.class не найден в JAR!
    pause
    exit /b 1
)
echo ✓ GardenPlugin.class найден в JAR
echo.

echo 5. Копирование в папку plugins (если существует)...
if exist "plugins" (
    copy "target\GardenPlugin-*.jar" "plugins\GardenPlugin.jar"
    echo ✓ JAR скопирован в plugins/
) else (
    echo Папка plugins не найдена, пропускаем копирование
)
echo.

echo 6. Инструкции по установке:
echo.
echo Для установки плагина:
echo 1. Скопируйте target\GardenPlugin-*.jar в папку plugins/
echo 2. Переименуйте в GardenPlugin.jar
echo 3. Перезапустите сервер
echo 4. Проверьте команды: /garden, /g
echo.

echo 7. Команды для тестирования:
echo    /plugins - проверить загрузку плагина
echo    /garden - главное меню
echo    /g - алиас команды
echo    /garden help - справка
echo.

echo ========================================
echo Готово! Плагин собран и готов к установке
echo ========================================
pause 