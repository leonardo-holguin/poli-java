# Lexicron

Lexicron es un juego interactivo para aprender conjugaciones de verbos en francés. El juego presenta al jugador un sujeto, un verbo y un complemento, y debe elegir la conjugación correcta del verbo entre varias opciones.

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

Ejecutar el juego por consola:

```bash
mvn exec:java -Dexec.mainClass=com.example.lexicron.LexicronApp
```

## Tests unitarios

Ejecutar todos los tests:

```bash
mvn test
```

## Documentación Javadoc

Generar la documentación:

```bash
mvn javadoc:javadoc
```

La documentación se genera en el directorio `docs/api/`. Para consultarla, abrir el archivo:

```
docs/api/index.html
```
