# AssetsLoaderMod

AssetsLoaderMod reemplaza la pantalla de carga del mundo de Minecraft por un indicador pequeño de estilo vanilla mientras se carga un mundo individual o una conexión a un servidor.

## Diseño

- No modifica la pantalla inicial que aparece al abrir Minecraft.
- Recuadro blanco con baja opacidad en la esquina inferior izquierda.
- Fuente y estética vanilla de Minecraft.
- `Cargando Assets`
- `0/1 Assets cargados`
- Barra de progreso debajo del texto.
- Sin bordes redondeados, iconos, sombras decorativas ni colores llamativos.
- El indicador desaparece cuando termina la carga.

## Compatibilidad

La compilación automática se limita exclusivamente a estas dos combinaciones:

- Fabric para Minecraft 1.20.1.
- Forge para Minecraft 1.20.1 con Forge 47.4.20.

Cada loader genera su propio paquete ZIP, que contiene el JAR correspondiente. No se compilan otras versiones ni NeoForge.

## Compilación local

Fabric:

```text
gradle build -Ploader=fabric -Pminecraft_version=1.20.1
```

Forge:

```text
gradle build -Ploader=forge -Pminecraft_version=1.20.1 -Pforge_version=47.4.20
```

Los JAR generados identifican el loader y la versión de Minecraft en su nombre.
