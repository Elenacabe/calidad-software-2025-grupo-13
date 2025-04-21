# Informe de cobertura

Cobertura obtenida: 41%

### ¿Qué clases/métodos crees que faltan por cubrir con pruebas? 

En `es.codeurjc.web.nitflex.model`: Métodos relacionados con las entidades del modelo, como validaciones o lógica adicional
En `es.codeurjc.web.nitflex.controller.rest`: createFilm, updateFilm, deleteFilm òdrían tenr mas casos límite o erores de recursos que no existen a la hora de acceder a ellos.
En `es.codeurjc.web.nitflex.service` : Métodos de servicios como `FilmService` y `UserService` necesitan más pruebas para casos límite y errores.


### ¿Qué clases/métodos crees que no hace falta cubrir con pruebas? 

- Getters y setters de las entidades
- Clases de configuración 
- Excepciones personalizadas 
