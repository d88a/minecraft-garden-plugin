#!/bin/bash

# Проверка наличия sshpass
if ! command -v sshpass &> /dev/null; then
    echo "sshpass не установлен. Установите его командой: sudo apt-get install sshpass"
    exit 1
fi

# Проверка переменных окружения
if [[ -z "$SFTP_SERVER" || -z "$SFTP_USERNAME" || -z "$SFTP_PASSWORD" ]]; then
  echo "Не заданы переменные окружения SFTP_SERVER, SFTP_USERNAME или SFTP_PASSWORD."
  exit 1
fi

# Параметры
SFTP_PORT=${SFTP_PORT:-22}
LOCAL_DIR="./target"
REMOTE_DIR="/root/aurorix/plugins/"

# Загрузка всех .jar файлов из LOCAL_DIR на сервер
for file in $LOCAL_DIR/*.jar; do
  if [[ -f "$file" ]]; then
    echo "Загружаю $file на $SFTP_SERVER:$REMOTE_DIR"
    sshpass -p "$SFTP_PASSWORD" sftp -oBatchMode=no -P $SFTP_PORT "$SFTP_USERNAME@$SFTP_SERVER" <<EOF
put "$file" "$REMOTE_DIR"
EOF
  fi
done

echo "Деплой завершён." 