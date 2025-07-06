@echo off
echo Простая сборка Garden Plugin без Maven
echo.

echo Создаем временную папку для сборки...
if exist "temp" rmdir /s /q "temp"
mkdir temp
mkdir temp\classes

echo Компилируем Java файлы...
javac -cp "lib/*" -d temp\classes src\main\java\com\minecraft\garden\*.java src\main\java\com\minecraft\garden\commands\*.java src\main\java\com\minecraft\garden\listeners\*.java src\main\java\com\minecraft\garden\managers\*.java

if errorlevel 1 (
    echo ОШИБКА: Не удалось скомпилировать код!
    echo Убедитесь, что у вас установлена Java 22 и Paper API
    pause
    exit /b 1
)

echo Копируем ресурсы...
xcopy "src\main\resources\*" "temp\classes\" /s /y

echo Создаем JAR файл...
cd temp\classes
jar cf ../../garden-plugin.jar *
cd ..\..

echo Очищаем временные файлы...
rmdir /s /q "temp"

echo.
echo Сборка завершена!
echo JAR файл: garden-plugin.jar
echo.
echo Для тестирования:
echo 1. Поместите garden-plugin.jar в папку plugins вашего сервера
echo 2. Запустите сервер
echo 3. Используйте команду /garden
echo.
pause 