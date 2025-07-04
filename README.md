# Práctica Calidad Software - Grupo 13
## Miembros del grupo

| Nombre                 | Usuario GitHub | Correo                                 |
|------------------------|----------------|----------------------------------------|
| Elena Ceinos Abeijón  | Elenacabe      | e.ceinos@alumnos.urjc.es               |
| David Esteban Bernardo| Daviid24x      | d.estebanb.2022@alumnos.urjc.es        |
## Práctica 1
### Asignación de tareas
Fue 50/50, totalmente dividida.


##Crónicas
Un miembro abandona el grupo y no realiza la práctica dos. Nueva tabla

## Miembros del grupo

| Nombre                 | Usuario GitHub | Correo                                 |
|------------------------|----------------|----------------------------------------|
| Elena Ceinos Abeijón  | Elenacabe      | e.ceinos@alumnos.urjc.es               |

## Enlaces

- Repositorio GitHub: https://github.com/Elenacabe/calidad-software-2025-grupo-13  
- Aplicación desplegada en producción: http://nitflexproduction-elenacabe.spaincentral.azurecontainer.io:8080/


## Práctica 2

### Asignación de tareas

| Tarea                          | Responsable   |
|--------------------------------|---------------|
| Arreglo botón "Cancel"        | Elena Ceinos  |
| Validación del año inválido   | Elena Ceinos  |
| Implementación de test E2E    | Elena Ceinos  |
| Configuración y ejecución de workflows | Elena Ceinos |
| Despliegue Docker + Azure     | Elena Ceinos  |

## Capturas

- Captura del dashboard de Azure con la versión desplegada 1.0.1:  
 ![image](https://github.com/user-attachments/assets/f43061f4-b697-47c2-bc4b-d172333a38a3)


- Captura de la aplicación desplegada con la URL visible:  
 ![image](https://github.com/user-attachments/assets/c9d6616f-3e7d-4f47-851b-04f7b91eafee)


---


### Pasos seguidos y acciones realizadas en el repositorio

#### Ramas creadas

Se crearon dos ramas separadas a partir de `main` para gestionar el bug y la nueva funcionalidad por separado:

```bash
git checkout -b fix-1
````

En `fix-1` se solucionó el bug del botón "Cancel" que no redirigía correctamente al cancelar la creación de una película.
Además, se añadió un test de regresión automatizado en Selenium para comprobar que dicho botón funcionaba.
Esta rama lanzaba el `Workflow 2 - Feature` automáticamente al hacer push.

```bash
git checkout -b feature-1
```

En `feature-1` se añadió una validación para que no se pudieran crear películas anteriores a 1895.
También se implementó un test E2E con Selenium para validar la visualización del mensaje de error.
Al igual que `fix-1`, esta rama también ejecutaba `Workflow 2` al hacer push.

---

#### Push de cambios

```bash
git add .
git commit -m "Fix cancel button in form and add validation for release year"
git push origin fix-1
git push origin feature-1
```

---

#### Pull requests y fusión

Se crearon dos pull requests en GitHub:

1. PR de `fix-1` a `main`
2. PR de `feature-1` a `main`


Una vez revisados, se realizó merge de ambas ramas a `main`:

```bash
git checkout main
git pull origin main
git merge fix-1
git merge feature-1
git push origin main
```

Esto desencadenó el `Workflow 1` de tests automáticos y, posteriormente, el `Workflow 3` de despliegue.

---

### Workflows ejecutados

* Workflow 1 - Test unitarios y Selenium
  [https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow1-tests.yml](https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow1-tests.yml)

* Workflow 2 - Lanzado en cada push a `fix-1` y `feature-1`
  [https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow2-feature.yml](https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow2-feature.yml)

* Workflow 3 - Despliegue a producción
  [https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow3-deploy.yml](https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow3-deploy.yml)

---

### Imagen publicada en DockerHub

* Imagen `1.0.1`: [https://hub.docker.com/repository/docker/elenacabe/nitflex/general](https://hub.docker.com/repository/docker/elenacabe/nitflex/general)

También se generaron imágenes asociadas a cada commit y una versión nightly:

* nightly-2025-07-04
* d50adea
* 1.0.1
* ff3b394
* 1.0.0

Captura:
![Captura Docker Nightly](capturas/captura-nightly-tag.png)

---

### Workflow de nightly

* Nombre: Workflow 4 - Nightly
* Se ejecuta automáticamente cada noche
* Acciones: Ejecuta pruebas multibrowser, pruebas de carga, smoke test, genera una nueva imagen con tag `nightly-fecha`
* Última ejecución: 2025-07-04
* Enlace:
  [https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow4-nightly.yml](https://github.com/Elenacabe/calidad-software-2025-grupo-13/actions/workflows/workflow4-nightly.yml)


```
