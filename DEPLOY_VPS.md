# MyChat VPS deployment

Нормальный публичный вариант для курсовой:

1. Купить VPS с Ubuntu, публичным IP и доменом.
2. В DNS домена создать `A`-запись на IP сервера.
3. Установить Docker, Docker Compose, Nginx и Certbot.
4. Скопировать проект на сервер, например в `/var/www/mychat`.
5. Настроить `.env` с нормальными паролями.
6. Запустить сервисы через `docker compose up --build -d`.
7. Положить `web-client` в `/var/www/mychat/web-client`.
8. Скопировать `deploy/nginx/mychat.conf` в `/etc/nginx/sites-available/mychat`, заменить `example.com` на свой домен.
9. Выпустить HTTPS-сертификат:

```bash
sudo certbot --nginx -d example.com -d www.example.com
```

После этого пользователи смогут открыть `https://example.com`, пройти регистрацию и работать с приложением через публичный HTTPS-адрес.

Для отправки кодов подтверждения email настрой SMTP-переменные у `mychat-authentication`:

```env
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=no-reply@example.com
MAIL_PASSWORD=strong-password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

Если SMTP не настроен, `MyChat-Auth` не падает: код подтверждения пишется в лог сервиса.
