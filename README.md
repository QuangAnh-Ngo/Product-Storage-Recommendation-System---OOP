# Product Storage and Recommendation System

This is a simple Java project applying Object-Oriented Programming (OOP) principles to build a desktop application for managing and recommending products (like laptops and phones).

The user interface (UI) is built using **JavaFX** and **Scene Builder**.

## 1. Demo Video

You can watch a demo video showcasing the application's functionality and main features here:

**(https://youtu.be/l9ifsw2xOOI)**

## 2. Features and Work Completed

This project focuses on the correct application of OOP techniques and a clear project structure:

* **💻 Core OOP Structure:**
    * Built an abstract `Product` class as the base class for all products.
    * Implemented concrete classes like `Laptop` and `Phone` that inherit from `Product`.
    * **Use of Generics:** Applied Generics (`<D extends IDetails>`) in the `Product` class to manage different types of product details (e.g., `LaptopDetails`, `PhoneDetails`) in a flexible and type-safe way.
* **📐 MVC Pattern (Model-View-Controller):**
    * **Model:** The `product` and `details` packages contain business logic and data structures.
    * **View:** The `.fxml` files (e.g., `ShowMainScreen.fxml`, `ShowProduct.fxml`) define the user interface.
    * **Controller:** The `controller` package (e.g., `ControlMainScreen`, `ProductController`) handles event logic and links the Model and View.
* **📄 Data Handling:**
    * The application reads product data from **JSON** files (e.g., `laptop_filtered.json`, `phones_filtered.json`).
    * The `com.fasterxml.jackson` library is used for JSON parsing (inferred from `pom.xml` and crawl files).
* **🤖 Recommendation Chatbot:**
    * Integrated a simple advisory bot that reads conversation data from `data_bot.json` (inferred from the existence of `Bot.java`).
* **UI Features:**
    * Display a list of products.
    * View product details.
    * Search and compare products (inferred from `SearchCompareController`).

## 3. Areas for Improvement

While the project applies basic OOP principles well, there are several areas that could be improved in the future:

* **Exception Handling:**
    * `try-catch` blocks should be implemented more consistently, especially when reading files (I/O) or parsing JSON.
    * Avoid printing to the console (`System.out.println`) from within Model classes (like in `Product.java`). Instead, exceptions should be thrown for the Controller to handle and notify the user via the UI.
* **Data Source:**
    * Currently, the project uses static JSON files. The next step should be to connect to a **Database** (e.g., MySQL, PostgreSQL, or MongoDB) to manage data more flexibly and persistently.
* **Testing:**
    * The project currently lacks test cases. Adding **Unit Tests** (using JUnit) for the Model classes and controller logic would help ensure the program's correctness during maintenance and future development.

---

*This is an academic project intended to practice object-oriented design and programming with Java.*
