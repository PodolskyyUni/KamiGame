📄 Kami Game
Kami Game — це JavaFX додаток-головоломка, натхненна оригінальною грою Kami. Гравець має за мету заповнити ігрове поле одним кольором за мінімальну кількість ходів. Гра реалізована з використанням JavaFX для фронтенду, Spring Boot для бекенду та PostgreSQL для збереження даних гравців і лідерборду.

⚙️ Технології
Java 17 — основна мова програмування.
JavaFX — для створення графічного інтерфейсу користувача.
Spring Boot — для реалізації бекенду та обробки запитів.
PostgreSQL — реляційна база даних для збереження гравців, їхніх результатів і лідерборду.
Maven — система управління проєктом.
Hibernate — ORM для роботи з базою даних.
📂 Структура проєкту
src/main/java/org/example
├── databaseShenanigans
│   ├── Leaderboard.java         # Логіка лідерборду
│   ├── Player.java              # Сутність гравця
│   ├── PlayerController.java    # Контролер для обробки запитів
│   └── PlayerRepository.java    # Репозиторій для роботи з БД
├── game
│   ├── Color.java               # Перелік можливих кольорів
│   ├── GameEngine.java          # Ігрова логіка
│   ├── Sector.java              # Сектор на ігровому полі
│   └── KamiGameUI.java          # Інтерфейс гри
├── Main.java                    # Точка входу в додаток
└── MainUI.java                  # Головне меню гри
💾 Структура Бази Даних
Таблиця Players:

id — унікальний ідентифікатор гравця.
name — ім'я гравця.
password — пароль гравця.
maxScore — найвищий набраний бал.
Основні SQL функції:

add_player — додає нового гравця, якщо такого ще немає.
login_player — перевіряє наявність гравця та правильність пароля.
show_leaderboard — виводить топ-10 гравців за їхнім максимальним рахунком.
update_max_score — оновлює рекорд гравця, якщо новий бал вищий за попередній.🧩 Геймплей
Реєстрація/Логін:
Гравець повинен зареєструватися або увійти для початку гри.

Головне меню:

Start: Перехід до гри.
Show Leaderboard: Перегляд лідерборду.
Exit: Вихід з програми.
Ігровий процес:

Клікніть по сектору, щоб вибрати його.
Виберіть новий колір для заповнення.
Завершіть хід, щоб оновити ігрове поле.
Кінець гри:
Гра завершується, коли поле заповнено одним кольором. Після цього результат додається до лідерборду.
