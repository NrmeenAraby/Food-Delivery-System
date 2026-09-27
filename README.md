# Food Delivery System 

**Food Delivery System** is a Java-based food delivery platform built as a step between learning core Java and moving into framework-based development.

The goal was to take Java beyond isolated exercises and build a **non-trivial, backend-oriented system** using Java itself — focusing on domain modeling, business logic, architecture, extensibility, and design patterns before introducing frameworks such as Spring.

The console is intentionally a **thin interface**. The main focus of the project is the system underneath it.

##  Architecture

```text
Console
   ↓
Services → Business Logic
   ↓
Repositories → Data Access
   ↓
Domain → Core Models & Behaviour

Additional layers/components:
Builders • Factories • Strategies • Filters
Events & Observers • Validation • Exceptions
```

The structure keeps business logic, data access, and domain behaviour separated, making the core system easier to extend or replace with another interface later.
A major focus of the project was making the system open to extension without constantly modifying existing business logic.

##  Engineering Highlights

* **Strategy Pattern** — promotions & rider dispatching
* **Builder Pattern** — order construction
* **Factory** — menu item creation
* **Observer/Event system** — order lifecycle events
* **Polymorphism** — menu item pricing
* **Composable search filters** — combine district, cuisine, rating, and price criteria without editing existing filter code
* **Repository layer** — centralized, efficient lookups
* **BigDecimal** — monetary calculations
* **Streams & Collectors** — reporting and analytics
* **Custom exception hierarchy** — business-level failures
* Defensive collection handling and modern Java APIs

##  Features

**Customers** — restaurant search, menus, ordering, wallet payments, promotions, tracking, cancellation, history.

**Restaurants** — menu & stock management, order processing, availability, revenue.

**Riders** — duty management, capability-based dispatching, pickup/delivery, statistics.

**Platform** — administration, promotions, audit logging, event-driven notifications, and reports.

##  Not Yet Done

The current scope is intentionally **in-memory and single-threaded**.

* No database/persistence yet
* No concurrency handling yet 
* Dispatch currently assigns the **first eligible rider**, rather than selecting by distance/speed

These are known boundaries of the current Java-focused implementation, rather than hidden limitations.

##  Built With

**Java • OOP • Collections • Streams • Generics • BigDecimal • Date/Time API • Design Patterns**


