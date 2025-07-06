@echo off
echo Создание тестового JAR файла для Garden Plugin
echo.

echo Создаем временную папку...
if exist "temp" rmdir /s /q "temp"
mkdir temp
mkdir temp\classes

echo Копируем ресурсы...
xcopy "src\main\resources\*" "temp\classes\" /s /y

echo Создаем JAR файл...
cd temp\classes
jar cf ../../garden-plugin-test.jar *
cd ..\..

echo Очищаем временные файлы...
rmdir /s /q "temp"

echo.
echo Тестовый JAR создан: garden-plugin-test.jar
echo.
echo ВАЖНО: Это тестовый JAR только с ресурсами!
echo Для полной функциональности нужна Java 22 и компиляция.
echo.
echo Для тестирования:
echo 1. Поместите garden-plugin-test.jar в папку plugins
echo 2. Запустите сервер
echo 3. Проверьте загрузку плагина в логах
echo.
pause 