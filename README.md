# Lexicron

Lexicron es un juego interactivo con interfaz gráfica en JavaFX para aprender a formar frases básicas en francés. En cada ronda el jugador recibe una frase en español y debe construir su equivalente en francés seleccionando el sujeto, verbo y complemento correctos.

## Autores

- **Alexander David Gualdron Chaparro**
- **Daniel Grande Gordillo**
- **Laura Sofia Gutiérrez Rico**
- **Leonardo Holguin Arias**
- **Sandra Milena Gutiérrez Madrigal**

## Librerías no estándar

El proyecto utiliza Maven para la gestión de dependencias. Las librerías externas son:

| Librería | Versión | Propósito |
|---|---|---|
| [JavaFX](https://openjfx.io/) | 24 | Interfaz gráfica |
| [Jackson](https://github.com/FasterXML/jackson) | 2.17.1 | Carga de archivos JSON |
| [JUnit](https://junit.org/junit5/) | 5.11.0 | Tests unitarios |

## Requisitos previos

- **Java 24** o superior instalado y configurado en la variable de entorno `JAVA_HOME`
- **Maven 3.9+** instalado

### Instalación de Maven

1. Descargar Maven desde https://maven.apache.org/download.cgi
2. Extraer el contenido en una carpeta (ej. `C:\apache-maven-3.9.9`)
3. Agregar la carpeta `bin` al `PATH` del sistema (ej. `C:\apache-maven-3.9.9\bin`)
4. Verificar la instalación ejecutando en una terminal:

```bash
mvn --version
```

## Ejecución del proyecto

Compilar el proyecto:

```bash
mvn compile
```

Ejecutar la aplicación gráfica:

```bash
mvn javafx:run
```

El comando anterior inicia la interfaz gráfica con la pantalla de bienvenida, desde donde se puede acceder al juego.

## Tests unitarios

Ejecutar todos los tests:

```bash
mvn test
```

## Estructura principal

- `com.example.lexicron.ui` — punto de entrada gráfico y navegación entre vistas
- `com.example.lexicron.ui.welcome` — pantalla de bienvenida
- `com.example.lexicron.ui.game` — pantalla del juego
- `com.example.lexicron.ui.results` — pantalla de resultados finales
- `com.example.lexicron.ui.viewmodel` — ViewModels de la interfaz gráfica
- `com.example.lexicron.model` — records del dominio (`Subject`, `Verb`, `Complement`, `Round`, etc.)
- `com.example.lexicron.service` — lógica de negocio (`RoundGenerator`, `RoundValidator`, `ScoreCalculator`, `GameSession`, etc.)

## Documentación Javadoc

Generar la documentación:

```bash
mvn javadoc:javadoc
```

La documentación se genera en el directorio `docs/api/`. Para consultarla, abrir el archivo:

```
docs/api/index.html
```
