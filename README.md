# AssetsLoaderMod

AssetsLoaderMod replaces Minecraft's world loading screen with a small vanilla-style indicator while a singleplayer world or server connection is loading.

## Diseño

- No modifica la pantalla inicial que aparece al abrir Minecraft.
- Recuadro blanco con baja opacidad en la esquina inferior izquierda.
- Fuente y estética vanilla de Minecraft.
- `Cargando Assets`
- `0/1 Assets cargados`
- Barra de progreso debajo del texto.
- Sin bordes redondeados, iconos, sombras decorativas ni colores llamativos.
- El indicador desaparece cuando termina la carga.

## Compatibilidad objetivo

El proyecto conserva builds separadas por loader y solo contempla estas versiones de Minecraft:

- Minecraft 1.20, 1.20.1, 1.20.2, 1.20.3, 1.20.4, 1.20.5 y 1.20.6.
- Minecraft 26.3.

Cada combinación válida se compila como un JAR independiente. No se crea un JAR universal entre Forge, NeoForge y Fabric.

### Builds

- Fabric: 1.20–1.20.6 y 26.3.
- Forge: 1.20–1.20.6.
- NeoForge: 1.20.4, 1.20.6 y 26.3.

Las diferencias internas entre 1.20.x y 26.3 se mantienen en adaptadores separados; la apariencia y el comportamiento del indicador son los mismos.

## Compilación local

La compilación se selecciona con propiedades de Gradle:

```text
gradle build -Ploader=fabric -Pminecraft_version=1.20.1
gradle build -Ploader=forge -Pminecraft_version=1.20.1 -Pforge_version=47.4.26
gradle build -Ploader=neoforge -Pminecraft_version=1.20.6 -Pneoforge_version=20.6.62
```

El nombre del artefacto identifica Minecraft y loader, por ejemplo:

```text
AssetsLoaderMod-1.0.0+mc1.20.1-forge.jar
AssetsLoaderMod-1.0.0+mc1.20.6-neoforge.jar
AssetsLoaderMod-1.0.0+mc26.3-fabric.jar
```
