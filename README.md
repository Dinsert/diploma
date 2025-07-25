# Проект "Платформа для перепродажи вещей"

Это веб-приложение для создания и управления объявлениями с функциями регистрации, авторизации, добавления комментариев и работы с картинками. Пользователи могут создавать объявления, редактировать их и просматривать профили.

## Участники команды
- Корольков Максим (бэкенд-разработчик)

## Используемые технологии
- **Язык**: Java 17
- **Фреймворк**: Spring Boot 3.4.1
- **База данных**: PostgreSQL
- **Миграции**: Liquibase
- **ORM**: Hibernate
- **Документация API**: Swagger
- **Библиотека для упрощения кода (геттеры, сеттеры)**: Lombok
- **Библиотека для маппинга DTO**: Mapstruct
- **Аутентификация и авторизация**: Spring Security

## Как запустить
1. Установи Java 17 и PostgreSQL.
2. Клонируй репозиторий: `git clone <твой-репозиторий>`.
3. Настрой файл `application.yml` с данными базы данных.
4. Выполни `mvn clean install` и запусти приложение: `mvn spring-boot:run`.

## Структура проекта
- `config`: Конфигурации приложения.
- `controller`: Контроллеры для обработки запросов.
- `dto`: DTO объекты на вход и на выход.
- `entity`: Сущности базы данных.
- `exception`: Кастомные исключения и обработчик ошибок.
- `mapper`: Мапперы для преобразования DTO объектов в сущности и наоборот.
- `repository`: Интерфейсы для работы с базой данных.
- `service`: Логика бизнес-услуг.

## Дополнительно
- API-документация доступна по адресу `/swagger-ui.html` после запуска.