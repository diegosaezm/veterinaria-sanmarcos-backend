
# Veterinaria San Marcos - Backend

API REST desarrollada con Spring Boot para el sistema
de gestión de Veterinaria San Marcos.

## Tecnologías utilizadas

- Java 25
- Spring Boot
- Spring Data JPA
- MySQL
- Hibernate
- Maven
- Swagger / OpenAPI

## Arquitectura

El proyecto utiliza una arquitectura por capas:

- Controller: recibe las solicitudes HTTP.
- Service: implementa la lógica de negocio.
- Repository: permite acceder a la base de datos.
- Model: representa las entidades persistentes.
- DTO: transporta los datos recibidos por la API.

## Funcionalidades

### Gestión de citas
- Registrar citas veterinarias.
- Consultar las citas registradas.
- Actualizar citas.
- Eliminar citas.

### Gestión de productos
- Registrar productos.
- Consultar el catálogo.
- Actualizar productos.
- Eliminar productos.

### Gestión de compras
- Registrar compras con varios productos.
- Consultar el historial de compras.
- Validar disponibilidad de stock.
- Calcular totales desde los precios almacenados.
- Descontar inventario mediante transacciones.

## Requisitos

- JDK 25
- MySQL
- Base de datos `veterinaria_sanmarcos`
- Usuario MySQL `sanmarcos_app` con permisos
  sobre la base de datos

## Configuración

La aplicación utiliza el archivo:

src/main/resources/application.properties

La contraseña de MySQL se obtiene desde la
variable de entorno DB_PASSWORD.

Ejemplo para PowerShell:

    $clave = Read-Host "Contrasena MySQL" -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
    Remove-Variable clave

## Ejecución

Desde la carpeta raíz del backend:

    .\mvnw.cmd spring-boot:run

La API estará disponible en:

http://localhost:8080

## Documentación Swagger

http://localhost:8080/swagger-ui.html

## Endpoints principales

### Citas
GET     /api/v1/citas
GET     /api/v1/citas/{id}
POST    /api/v1/citas
PUT     /api/v1/citas/{id}
DELETE  /api/v1/citas/{id}

### Productos
GET     /api/v1/productos
GET     /api/v1/productos/{id}
POST    /api/v1/productos
PUT     /api/v1/productos/{id}
DELETE  /api/v1/productos/{id}

### Compras
GET     /api/v1/compras
POST    /api/v1/compras

## Persistencia

Se utiliza MySQL como base de datos relacional.

Las entidades principales son:
- Cita
- Producto
- Compra
- DetalleCompra

Las compras y sus detalles se relacionan mediante
claves foráneas.

La lógica de compra utiliza @Transactional para
mantener la consistencia entre el registro de la
compra, sus detalles y la actualización del stock.

Para desarrollo local, Hibernate está configurado
con spring.jpa.hibernate.ddl-auto=update.

## Frontend

Repositorio React:

https://github.com/diegosaezm/Veterinaria_react

El frontend utiliza React, Bootstrap y Fetch API
para consumir los endpoints REST.

## Alcance

Proyecto académico de desarrollo fullstack.

Las compras se registran en la base de datos,
pero no incluyen procesamiento de pagos reales.
