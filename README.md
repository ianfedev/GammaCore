# GammaCore
Plugins Bukkit/Bungee personalizados utilizados en Seocraft Network.

# Redistribución
El código dentro de este repositorio no debe ser difundido a cualquier persona ajéna a Seocraft Network.
Una vez otorgado el acceso al mismo, el usuario aceptará todas las responsabilidades legales/administrativas
de incumplir con lo anterior Cualquier intento de divulgación a personas externas a Seocraft Network y/o personas autorizadas por la misma entidad, será penalizada legalmente de acuerdo a la [Decisión 486 del año 2000 - Artículo 257](http://www.wipo.int/edocs/lexdocs/laws/es/can/can012es.pdf).

# Estado

Actualmente los plugins de este repositorio requieren para funcionar directamente una conexión con una instancia
funcional de la API HTTP conocida como [GammaBackend](http://github.com/TomateDeveloper/GammaBackend), de lo
contrario no funcionarán.

# Obtener ayuda

Podrás encontrar *algún* tipo de ayuda hablando con rangos superiores de la División Developement.

También podrás encontrar una guía detallada acerca de como iniciar un servidor de GammaCore en la [Wiki](https://github.com/TomateDeveloper/GammaCore/wiki).

[#division-developement](https://discord.gg/XGryyjB) Es el canal de Discord de tu división, donde podrás encontrar ayuda.

Recuerda que toda la documentación necesaria para iniciar un servidor se encuentra a tu disposición. En caso de necesitar
ayuda de un rango superior, solicitala gentilmente y ten en cuenta que ellos también tienen cosas que hacer.

# Build del proyecto

Necesitarás la versión más reciente de [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
Las versiones antiguas de JDK tienen algunos bugs que evitarán que el código se compile.
También necesitarás [Maven](http://maven.apache.org).

Para construir todo solo necesitas ejecutar `mvn clean package`.
Esto descargará todas las dependencias de nuestro [repositorio](https://gamma.seocraft.net) y creará archivos .jar en la carpeta `target` de cada módulo.
Si la build falla, puedes reportarlo a algún rango superior.

# Ejecución

Los plugins de nuestro repositorio funcionarán únicamente con [fdSpigot](https://github.com/FixedDev/fdSpigot)
y la versión estándar de [BungeeCord](https://github.com/SpigotMC/BungeeCord).
Ellos no funcionarán con la versión por defecto de Spigot/CraftBukkit.

En cualquier servidor de GammaCore se deben utilizar los siguientes plugins:

* api-bukkit (construido en `API/bukkit/target/api-bukkit-#-SNAPSHOT.jar`)
* commons-bukkit (construido en `Commons/bukkit/target/commons-bukkit-#-SNAPSHOT.jar`)

Para un servidor de juegos, debes realizar la configuración necesaria para que sea reconocido por el [GameAPI](https://github.com/TomateDeveloper/GammaCore/wiki).

Para un servidor de Lobby, necesitas del plugin de Lobby. Si deseas añadir soporte para un juego, debes descargar el `hook`
para cada uno de ellos:

* Lobby (construido en `Lobby/target/Lobby-#-SNAPSHOT.jar`)
* Hook (construido en `Game/hook/target/game-hook-#-SNAPSHOT.jar`)

Recuerda que `Game` hace alusión al juego que vas a configurar.

Para un proxy bungee, solo necesitas de estos dos:

* api-bungee (construido en `API/bungee/target/api-bungee-#-SNAPSHOT.jar`)
* commons-bungee (construido en `Commons/bungee/target/commons-bungee-#-SNAPSHOT.jar`)

# Contenido

* `API`
  * `core` Codigo que comparte interfaces de Bungee y Bukkit
  * `bukkit` Plugin con interfaces diseñadas para Bukkit
  * `bungee` Plugin con interfaces diseñadas para Bungee
* `Commons` Funcionalidad básica de los servidores (Ej. Amigos, Chat, grupos)
  * `core` Codigo necesario para compartir funcionalidad de Bungee y Bukkit.
  * `bukkit` Plugin de Bukkit
  * `bungee` Plugin de Bungee
* `Lobby` Plugin de Bukkit principal para cada servidor de Lobby.

El plugin API es utilizado por los demás plugins para realizar la conexión al (backend)[http://github.com/TomateDeveloper/GammaBackend].
El proyecto se encuentra dividido en interfaces las cuales se encuentran en  el módulo API, mientras que las implementaciones 
se encuentran en el módulo Commons.

# Guías de programación

* General
  * Siempre utiliza tu juicio. Puedes romper las reglas siempre y cuando sea necesario. Nunca sigas un patrón si desconoces su función.
  * Escribe código legible por encima de todo. Piensa en como otro desarrollador verá tu código.
  * Evita el código repetitivo, a menos de que haya una razón justa para hacerlo.
  * Evita el legacy coding. Ten cuidado de construir sistemas obsoletos en un entorno actualizado.
* Formato
  * Para cosas simples como la longitud de las tab o brackets, sigue el código actual.
  * Preferiblemente sigue el [Principio de substitución de Liskov](https://es.wikipedia.org/wiki/Principio_de_sustituci%C3%B3n_de_Liskov) en tus códigos.
  * Debes seguir las [Java Naming Conventions](https://www.geeksforgeeks.org/java-naming-conventions/) para asegurar un modelo más conciso.
  * Puedes asignar un nombre concreto a cualquier implementación, siempre y cuando no difiera mucho de la interfaz base.
  * Puedes usar formatos concisos en donde sea necesario (e.j. Getters de una linea).
* Comentarios
  * Intenta escribir código que sea lo suficientemente obvio para evitar que se deban usar comentarios.
  * En lugares donde el lector se pueda sentir confundido, utiliza comentarios para orientarlo.
  * No pongas comentarios obvios o redundantes en los Javadocs, a excepción de que sea una API.
  * Asegurate de que tu IDE no inserte comentarios generados automáticamente.
* Nulls
  * Utiliza preferiblemente `java.util.Optional` por encima de `null`, cuando sea posible.
  * Utiliza colecciones vacías en lugar de valores nulos.
  * Utiliza `@Nullable` donde se admitan valores nulos. Escríbelo antes del tipo en métodos y antes de la capacidad de acceso en propiedades de un modelo.
  * Utiliza `@NotNull` para asegurar que todo lo que no sea `@Nullable` siempre tenga un valor.
  * No revises valores `@Inject`, ellos no pueden ser nulos.
* Estructura
  * Diseña clases para hacer [una cosa a la vez](https://en.wikipedia.org/wiki/Single_responsibility_principle).
    Si una clase proveé multiples funcionalidades, dividela en varios pedazos.
  * Utiliza `final` y crea datos inmutables, donde sea posible.
  * No crees getter y setter innecesarios, solo aquellos que se usen.
  * No utilices colecciones o propiedades privadas (Salvo por pequeñas excepciones como cache y `ThreadLocal`).
* Inyección
  * Utiliza Guice siempre. Necesitarás [entenderlo perfectamente](https://github.com/google/guice/wiki/Motivation).
  * Sígue las [buenas prácticas](https://github.com/google/guice/wiki/InjectOnlyDirectDependencies) de Guice.
  * Evita nombrar partes de tu plugin como "Module", ya que este nombre es reservado para los modulos de Guice.
  * Utilizamos de manera correcta el soporte Guice que trae fdSpigot:
    * Cada plugin tiene su propio modulo privado, con acceso al Binder publico, el cual todos los plugins pueden consultar.
    * Cada instancia de plugin está enlazado a `org.bukkit.plugin.Plugin` dentro de su módulo privado.
    * Cualquier cosa que requiera directamente del `Plugin` necesita estar enlazada en algún modulo del plugin.
    * Si otros plugins necesitan acceso a algo, deben ser [expuestos](https://google.github.io/guice/api-docs/latest/javadoc/com/google/inject/PrivateModule.html#expose-com.google.inject.Key-).
    * Evita depender del `Plugin` directamente. Haz bindings especificos para cualquier cosa de tu plugin. (e.j. `Configuración`),
     además de que todas las interfaces se encuentran enlazadas a una implementación (e.j. `UserProvider - GammaUserProvider`).
    * Si realmente necesitas `Plugin`, siempre inyecta la interface Base, nunca el plugin directo, a menos que sea necesario.
    * Un enlace `@Singleton` en un ambiente privado (e.j. un ecosistema privado del plugin) debe ser único en ese ecosistema,
     no en el proceso entero, así evitarás tener duplicados del Singleton.
* Excepciones
  * Detecta los errores lo antes posibles, incluyendo los internos y errores de usuario.
  * Maneja las excepciones a las que puedes responser correctamente. No escondas excepciones de las que se deben conocer.
  * Evita hacer catch a las excepciones comunes como `IllegalArgumentException`, ya que es dificil saber de donde vienen.
    Si necesitas manejarlas, mantén el bloque `try` lo más pequeño posible.
* Localización
  * Todo el texto in-game debe ser localizado.
  * Para añadir un texto, debes escribir su traducción en los archivos `.yml` de commons.
  * Utiliza `TranslatableField` para enviar el mensaje traducido. En caso de no encontrarse se enviará el mensaje en español.
