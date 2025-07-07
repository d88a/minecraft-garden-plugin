# 🚀 Автоматический деплой на сервер

## 📋 Что нужно настроить

### 1. **GitHub Secrets**
Перейдите в ваш репозиторий: `Settings` → `Secrets and variables` → `Actions`

Добавьте следующие секреты:

| Секрет | Описание | Пример |
|--------|----------|---------|
| `SERVER_HOST` | IP адрес или домен сервера | `192.168.1.100` или `minecraft.example.com` |
| `SERVER_USERNAME` | Имя пользователя на сервере | `minecraft` |
| `SERVER_SSH_KEY` | Приватный SSH ключ | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `SERVER_PORT` | SSH порт (обычно 22) | `22` |
| `PLUGIN_PATH` | Путь к папке plugins | `/home/minecraft/server/plugins` |
| `SERVER_DIR` | Папка с сервером (для полного деплоя) | `/home/minecraft/server` |

### 2. **SSH ключи**

#### Генерация ключей:
```bash
ssh-keygen -t rsa -b 4096 -C "github-actions"
```

#### Добавление публичного ключа на сервер:
```bash
# На вашем компьютере
cat ~/.ssh/id_rsa.pub

# На сервере
echo "ваш_публичный_ключ" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

#### Добавление приватного ключа в GitHub:
1. Скопируйте содержимое `~/.ssh/id_rsa`
2. Вставьте в секрет `SERVER_SSH_KEY`

### 3. **Выбор workflow**

У вас есть два варианта:

#### 🟢 **Простой деплой** (`simple-deploy.yml`)
- ✅ Быстрый деплой
- ✅ Без перезапуска сервера
- ✅ Использует `plugman reload`
- ⚠️ Требует установленный PlugMan

#### 🔴 **Полный деплой** (`deploy.yml`)
- ✅ Полная перезагрузка сервера
- ✅ Гарантированная загрузка плагина
- ⚠️ Останавливает сервер на 10-15 секунд
- ⚠️ Отключает всех игроков

## 🛠️ Настройка сервера

### 1. **Установка PlugMan (для простого деплоя)**
```bash
# Скачайте PlugMan.jar
wget https://github.com/PlugMan/PlugMan/releases/download/2.1.2/PlugMan.jar

# Поместите в папку plugins
mv PlugMan.jar /path/to/plugins/
```

### 2. **Проверка прав доступа**
```bash
# Проверьте, что папка plugins доступна для записи
ls -la /path/to/plugins/

# Если нужно, измените владельца
sudo chown minecraft:minecraft /path/to/plugins/
```

### 3. **Настройка screen сессии**
```bash
# Создайте screen сессию для сервера
screen -S minecraft

# Запустите сервер
java -Xmx2G -Xms1G -jar server.jar nogui

# Отключитесь от screen (Ctrl+A, затем D)
```

## 🔧 Альтернативные варианты

### 1. **Деплой через FTP/SFTP**
Если SSH недоступен, используйте FTP:

```yaml
- name: Deploy via FTP
  uses: SamKirkland/FTP-Deploy-Action@v4.3.4
  with:
    server: ${{ secrets.FTP_SERVER }}
    username: ${{ secrets.FTP_USERNAME }}
    password: ${{ secrets.FTP_PASSWORD }}
    local-dir: ./target/
    server-dir: ./plugins/
```

### 2. **Деплой на хостинг**
Для популярных хостингов:

#### **Aternos:**
```yaml
- name: Deploy to Aternos
  uses: appleboy/ssh-action@v1.0.3
  with:
    host: ${{ secrets.ATERNOS_HOST }}
    username: ${{ secrets.ATERNOS_USERNAME }}
    password: ${{ secrets.ATERNOS_PASSWORD }}
    script: |
      # Загружаем файл через веб-интерфейс
      curl -X POST -F "file=@target/plugin.jar" \
        "https://aternos.org/panel/ajax/plugins/upload"
```

#### **SpigotMC:**
```yaml
- name: Deploy to SpigotMC
  uses: appleboy/scp-action@v0.1.7
  with:
    host: ${{ secrets.SPIGOT_HOST }}
    username: ${{ secrets.SPIGOT_USERNAME }}
    key: ${{ secrets.SPIGOT_SSH_KEY }}
    source: "target/*.jar"
    target: "/home/spigot/plugins/"
```

## 🧪 Тестирование

### 1. **Проверка подключения**
```bash
# Проверьте SSH подключение
ssh -i ~/.ssh/id_rsa minecraft@your-server.com

# Проверьте доступ к папке plugins
ls -la /path/to/plugins/
```

### 2. **Тестовый деплой**
1. Создайте тестовую ветку
2. Внесите небольшое изменение
3. Запушьте изменения
4. Проверьте GitHub Actions

### 3. **Логи деплоя**
Проверьте логи в GitHub:
- `Actions` → выберите workflow → `View logs`

## ⚠️ Важные моменты

### 1. **Безопасность**
- ✅ Используйте SSH ключи вместо паролей
- ✅ Ограничьте права доступа на сервере
- ✅ Регулярно обновляйте ключи

### 2. **Резервное копирование**
```bash
# Создайте бэкап перед деплоем
cp /path/to/plugins/your-plugin.jar /path/to/backup/
```

### 3. **Мониторинг**
- Следите за логами сервера
- Проверяйте работоспособность плагина
- Настройте уведомления об ошибках

## 🎯 Готово!

После настройки:

1. **Запушьте изменения:**
```bash
git add .
git commit -m "Add deployment workflow"
git push origin main
```

2. **Проверьте GitHub Actions:**
- Перейдите в `Actions` в вашем репозитории
- Убедитесь, что workflow запустился
- Проверьте логи на ошибки

3. **Проверьте сервер:**
- Подключитесь к серверу
- Проверьте, что плагин загрузился
- Протестируйте функциональность

Теперь каждый пуш в ветку `main` будет автоматически деплоить плагин на сервер! 🚀 