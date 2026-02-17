# Food Delivery Backend

This is the **backend** for the Food Delivery Application built with **Spring Boot**.  
It handles **user authentication, restaurant and menu management, order processing, and billing**.

---

## 🛠️ **Technologies Used**
- Java 17  
- Spring Boot  
- Spring Data JPA / Hibernate  
- Mongodb 
- Maven  
- Docker (for containerization)  
- Git / GitHub (version control)

---

## ⚡ **Features**
### User Module
- Customer registration and login  
- Profile management (edit details, addresses, payment info)

### Restaurant Module
- Admin can **add/update/remove restaurants**  
- Admin can **add/update/remove menu items**  

### Order & Cart
- Add items to cart  
- Place orders  

### Admin Features
- View all customers, restaurants, and orders  

---

## 🚀 **Setup / Installation**
1. Clone the repository:

```bash
git clone https://github.com/KhaddyX/food-delivery-backend.git
cd food-delivery-backend
```
Add your database configuration in application.properties :
```bash

spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

Build the project:
```bash
mvn clean install
```

Run the backend:
```bash

mvn spring-boot:run
```


The backend will run on http://localhost:8080 by default.


