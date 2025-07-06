@echo off
echo ========================================
echo Тестирование команды garden tp
echo ========================================
echo.

echo 1. Проверяем, что плагин загружен...
echo /plugins
echo.

echo 2. Создаем участок (если его нет)...
echo /garden create
echo.

echo 3. Проверяем информацию об участке...
echo /garden plot
echo.

echo 4. Пробуем телепортацию...
echo /garden tp
echo.

echo 5. Если телепортация не работает, проверьте:
echo    - Есть ли у вас участок (/garden plot)
echo    - Существует ли мир "world"
echo    - Есть ли права на телепортацию
echo.

echo 6. Альтернативные команды:
echo    /garden help - справка
echo    /garden - главное меню
echo.

pause 