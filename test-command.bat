@echo off
echo ========================================
echo Тестирование команд плагина
echo ========================================
echo.

echo 1. Проверяем загрузку плагина...
echo /plugins
echo.

echo 2. Проверяем доступные команды...
echo /help garden
echo.

echo 3. Пробуем основную команду...
echo /garden
echo.

echo 4. Пробуем алиас...
echo /g
echo.

echo 5. Пробуем справку...
echo /garden help
echo.

echo 6. Тестирование участков:
echo    /garden create - создать участок
echo    /garden plot - информация об участке
echo    /garden tp - телепорт на участок
echo    /garden delete - удалить участок
echo.

echo 7. Если команды не работают, проверьте:
echo    - Плагин загружен в /plugins
echo    - Нет ошибок в логах сервера
echo    - Файл plugin.yml корректный
echo    - JAR файл не поврежден
echo.

echo 8. Команды для диагностики:
echo    /reload - перезагрузить плагины
echo    /stop - остановить сервер
echo.

pause 