# Руководство по устранению неполадок

## Проблема: "Такой команды не существует"

### 🔍 **Пошаговая диагностика:**

#### 1. **Проверьте, загружен ли плагин**
```
/plugins
```
**Ищите:** `GardenPlugin` в списке плагинов
**Если нет:** Плагин не загружен

#### 2. **Проверьте логи сервера**
В файле `logs/latest.log` ищите:
- `[GardenPlugin]` - сообщения от плагина
- `ERROR` или `SEVERE` - ошибки
- `✗ ОШИБКА: Команда 'garden' не найдена в plugin.yml!`

#### 3. **Проверьте файл plugin.yml**
Убедитесь, что в JAR файле есть правильный `plugin.yml`:
```yaml
commands:
  garden:
    description: Главная команда для управления садом
    usage: /garden [create|plot|tp|shop|sell|invite|expand|help]
    aliases: [g]
    permission: garden.use
```

#### 4. **Проверьте права доступа**
```
/garden help
```
**Если команда не работает:** У вас нет прав `garden.use`

### 🛠️ **Решения проблем:**

#### **Проблема 1: Плагин не загружен**
1. **Проверьте JAR файл:**
   - Убедитесь, что файл `GardenPlugin.jar` находится в папке `plugins/`
   - Проверьте, что файл не поврежден

2. **Перезапустите сервер:**
   ```
   /stop
   ```
   Затем запустите сервер заново

3. **Проверьте совместимость:**
   - Minecraft версия: 1.21+
   - Paper/Spigot сервер
   - Java 17+

#### **Проблема 2: Ошибки в логах**
1. **Откройте файл `logs/latest.log`**
2. **Ищите ошибки:**
   ```
   [SEVERE] [GardenPlugin] ✗ КРИТИЧЕСКАЯ ОШИБКА
   [SEVERE] [GardenPlugin] ✗ ОШИБКА: Команда 'garden' не найдена
   ```

3. **Исправьте ошибки и перезапустите сервер**

#### **Проблема 3: Неправильный plugin.yml**
1. **Извлеките JAR файл:**
   ```bash
   jar -xf GardenPlugin.jar
   ```

2. **Проверьте файл `plugin.yml`:**
   ```yaml
   name: GardenPlugin
   version: '${project.version}'
   main: com.minecraft.garden.GardenPlugin
   api-version: '1.21'
   
   commands:
     garden:
       description: Главная команда для управления садом
       usage: /garden [create|plot|tp|shop|sell|invite|expand|help]
       aliases: [g]
       permission: garden.use
   ```

3. **Пересоберите плагин:**
   ```bash
   mvn clean package
   ```

#### **Проблема 4: Конфликт с другими плагинами**
1. **Попробуйте загрузить только этот плагин**
2. **Проверьте конфликты с другими плагинами**
3. **Измените алиас команды в plugin.yml**

### 📋 **Чек-лист для проверки:**

- [ ] JAR файл в папке `plugins/`
- [ ] Плагин загружен (`/plugins`)
- [ ] Нет ошибок в логах
- [ ] Правильный `plugin.yml`
- [ ] Совместимая версия Minecraft
- [ ] Права доступа `garden.use`

### 🔧 **Команды для диагностики:**

```bash
# Проверка плагинов
/plugins                    # Список загруженных плагинов
/reload                     # Перезагрузить плагины
/stop                       # Остановить сервер

# Тестирование команд
/garden                     # Главное меню
/g                          # Алиас команды
/garden help               # Справка
/garden create             # Создать участок
/garden tp                 # Телепорт на участок
```

### 📁 **Структура файлов:**

```
plugins/
├── GardenPlugin.jar       # Основной файл плагина
└── GardenPlugin/
    ├── config.yml         # Конфигурация
    └── data.yml          # Данные игроков

logs/
└── latest.log            # Логи сервера
```

### 🆘 **Экстренные решения:**

#### **Решение 1: Полная переустановка**
1. Удалите `plugins/GardenPlugin.jar`
2. Удалите папку `plugins/GardenPlugin/`
3. Загрузите новый JAR файл
4. Перезапустите сервер

#### **Решение 2: Проверка JAR файла**
```bash
# Проверьте содержимое JAR
jar -tf GardenPlugin.jar

# Должно содержать:
# META-INF/
# META-INF/MANIFEST.MF
# plugin.yml
# com/minecraft/garden/
# com/minecraft/garden/GardenPlugin.class
```

#### **Решение 3: Отладка**
1. Включите отладку в `server.properties`:
   ```properties
   debug=true
   ```

2. Проверьте логи на наличие ошибок
3. Исправьте найденные проблемы

### 📞 **Поддержка:**

Если проблема не решается, предоставьте:
- Полный лог сервера (последние 100 строк)
- Версию Minecraft и сервера
- Список загруженных плагинов (`/plugins`)
- Содержимое файла `plugin.yml` из JAR 