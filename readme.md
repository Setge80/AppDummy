# AppDummy v1 — G-APPDUMMY-V1
Sergio Jávega Porcel · 10086949

## Qué hace
Aplicación Android desarrollada en Jetpack Compose para la gestión y visualización de un catálogo de libros.
Permite visualizar, filtrar reactivamente por texto y autor, y gestionar el estado de cada obra (marcar como leído y favorito).
Incluye integración con el sistema para compartir información de los libros (título y autor) mediante intents 
implícitos y gestión de permisos en tiempo de ejecución para el acceso a la cámara.

## Estructura
- `PantallaBienvenida.kt`: Pantalla de acceso inicial que valida la longitud del nombre de usuario y contiene el botón de entrada.
- `PantallaListado.kt`: Pantalla principal que gestiona la cuadrícula de libros (`LazyVerticalGrid`), el buscador en tiempo real y el filtrado por autor con `FilterChip`.
- `ItemLibro.kt`: Componente reutilizable que representa cada libro y expone las acciones de marcar leído, favorito y compartir.
- `LibroUI.kt`: Modelo de datos (`data class`) y lista de prueba (`librosTest`) que definen las propiedades y estados de cada libro.

## Dependencias añadidas
| Librería | Para qué |
|---|---|
| `material-icons-extended` | Iconos adicionales de Material Design |
| `coil-compose` / `coil-network-okhttp` | Carga asíncrona de imágenes remotas |

## Permisos declarados
| Permiso | Por qué es necesario                                                       |
| :--- |:---------------------------------------------------------------------------|
| `android.permission.INTERNET` | Permitir la descarga en red de las imágenes de las portadas mediante Coil. |
| `android.permission.CAMERA` | Para capturar o escanear imágenes de portadas.                             |

## Decisiones propias
1. Modularización y separación de responsabilidades: Extraer LibroUI e ItemLibro fuera de PantallaListado para facilitar el mantenimiento y la reutilización de componentes.
2. Scroll vertical adaptativo en Bienvenida: Añadir verticalScroll para garantizar la usabilidad en orientación horizontal (evitando que el botón quede inaccesible).
3. Permiso de Internet explícito: Configurar el permiso para la carga asíncrona de portadas remotas en la app.
## Limitaciones conocidas
- **Persistencia de datos en memoria :** Los cambios de estado (leído/favorito) y el catálogo se reinician al cerrar la app; con más tiempo se podria integrar persistencia local a nivel de dispositivo.
- **Integración de la cámara sin implementar :** El permiso de cámara se solicita en tiempo de ejecución, pero no realiza ninguna acción; el siguiente paso sería integrar alguna API con metodos/funciones para escanear las portadas de los libros y añadirlas al catalogo/lista.
- **Modelo de datos simplificado :** Los autores se manejan actualmente como cadenas de texto simples; se beneficiaría de un modelo relacional con una entidad `Autor` dedicada para gestionar biografías y relaciones más complejas.
