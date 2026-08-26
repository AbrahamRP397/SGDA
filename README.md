# SGDA — Sistema de Gestión de Almacén

SGDA es una aplicación web para administrar las operaciones de un almacén. Permite controlar usuarios, áreas, proveedores, productos, unidades de medida, existencias, entradas y salidas, además de consultar indicadores y generar reportes.

El proyecto utiliza una arquitectura MVC adaptada a Jakarta EE: las vistas JSP y el código JavaScript consumen controladores Servlet; estos coordinan la lógica de negocio y los DAO, responsables del acceso a Oracle Database.

## Integrantes

| Integrante | Matrícula | Usuario de GitHub | Responsabilidad principal |
|---|---|---|---|
| Abraham Ríos Peña | 20233TN162 | [AbrahamRP397](https://github.com/AbrahamRP397) | Controladores, filtros, servicios y JavaScript |
| Daniel Jared Flores Beltrán | 20243DS034 | [jaredFloresBeltran](https://github.com/jaredFloresBeltran) | Modelos, DAO, utilidades, plantillas y pruebas de persistencia |
| Dulce Janet Ríos Aguilar | 20243DS048 | [janetrios](https://github.com/janetrios) | Vistas JSP, componentes, estilos CSS y recursos web |

## Funciones principales

- Inicio y cierre de sesión, recuperación de contraseña y cambio obligatorio de credenciales temporales.
- Administración de usuarios y control de acceso.
- Catálogos de áreas, proveedores, productos y unidades de medida.
- Registro de entradas y salidas de productos.
- Consulta de existencias y movimientos del almacén.
- Panel con métricas e indicadores.
- Generación de reportes.
- Manejo de errores HTTP, protección CSRF, autenticación y limitación de solicitudes.

## Tecnologías

- Java 21 y Jakarta EE 10.
- Servlets, JSP y JSTL.
- Maven y empaquetado WAR.
- Oracle Database con Oracle JDBC y HikariCP.
- JavaScript, HTML y CSS.
- Gson, BCrypt, Jakarta Mail y OpenPDF.
- JUnit 5 y Oracle Free en Docker para pruebas.
- Apache Tomcat 10.1 o compatible con Jakarta Servlet 6.

## Estructura del código

```text
integradora/
├── docker/oracle/init/        Esquema Oracle aislado para las pruebas
├── src/main/java/com/almacen/integradora/
│   ├── controllers/           Servlets y puntos de entrada HTTP
│   ├── filters/               Seguridad, autenticación y control de solicitudes
│   ├── models/                Entidades y objetos DAO de persistencia
│   ├── services/              Servicios de recuperación y acceso temporal
│   ├── templates/             Contratos reutilizables, como la interfaz DAO
│   └── utils/                 Conexión SQL, correo, políticas y reportes
├── src/main/resources/        Plantilla de configuración local
├── src/main/webapp/
│   ├── assets/                CSS, JavaScript, imágenes y recursos estáticos
│   ├── components/            Fragmentos JSP reutilizables
│   ├── views/                 Vistas organizadas por módulo
│   ├── META-INF/              Contexto de despliegue de Tomcat
│   └── WEB-INF/               Descriptor web y configuración de errores
├── src/test/java/             Pruebas JUnit de los DAO
├── compose.test.yml           Oracle Free para pruebas en Docker
└── pom.xml                    Dependencias y construcción Maven
```
