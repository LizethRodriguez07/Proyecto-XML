# Gestion de Ventas Online STORE_DANY

Aplicación móvil Android para la gestión de ventas en línea de **STORE DANY**, una tienda especializada en calzado para hombre. El proyecto centraliza un flujo completo de compra: catálogo de productos, selección de tallas, carrito de compras, captura de datos personales y confirmación de pedido.

## Descripción general

STORE DANY es un negocio dedicado a la venta de calzado masculino de las marcas más reconocidas del entorno comercial (Nike, Adidas, Puma, New Balance y Reebok). Esta aplicación actúa como su plataforma de gestión de ventas online, brindando una asesoría integral sobre los productos y acompañando al cliente durante todo el proceso de compra.

El flujo de la app guía al usuario desde un pantalla de bienvenida con términos y condiciones, pasando por un catálogo visual de productos, hasta la confirmación de un pedido. Actualmente los datos del catálogo son locales (en memoria), y se proyecta integrar el envío del pedido a un asesor comercial.

## Características principales

**Compra**
- **Bienvenida:** splash con mensaje de la propietaria y términos y condiciones de aceptación obligatoria.
- **Catálogo:** cinco modelos (Nike, Adidas, Puma, New Balance y Reebok) con búsqueda, filtros por marca y modo Favoritos.
- **Detalle:** imagen grande, selectores de talla y color, descripción ampliada y añadir al carrito.
- **Carrito:** cantidades (−/+), talla editable, subtotales, total y método de pago (NEQUI, Daviplata o Efectivo contra entrega).
- **Factura:** al confirmar el pedido se genera con datos del cliente, envío, líneas por producto y resumen del pago.

**Cuenta**
- **Mi Perfil:** datos del cliente persistentes con autocompletado, validaciones y selector de departamento/municipio en cascada.
- **Mis Pedidos:** historial persistente con detalle ampliable, avance de estado (Pendiente → Enviado → Entregado) y eliminación con confirmación.
- **Favoritos:** corazón en catálogo y detalle, con filtro propio y acceso desde el menú lateral.

**Información**
- **Ayuda:** llamada y WhatsApp directos, horario con indicador Abierto/Cerrado, punto de venta y preguntas frecuentes.
- **Acerca de:** misión, visión y ubicación del negocio.

**Diseño**
- Interfaz oscura con acentos dorados, tarjetas Material, animación escalonada en el catálogo e iconografía dorada.

## Stack tecnológico

| Capa | Tecnología |
| --- | --- |
| Lenguaje | Kotlin 1.9.0 |
| UI | XML layouts (ViewBinding) |
| Entorno de compilación | Android Gradle Plugin 8.2.0, Gradle (wrapper) |
| SDK mínimo / objetivo | minSdk 24 (Android 7.0) / targetSdk y compileSdk 34 (Android 14) |
| JVM | Java 11 |
| Framework UI | Material Components 1.10.0, AppCompat 1.6.1, ConstraintLayout 2.1.4, RecyclerView, DrawerLayout (NavigationView) |
| Extensiones | AndroidX Core KTX 1.12.0 |
| Arquitectura base | Activities (UI) + Adapters (RecyclerView) + Modelo `Producto` (Serializable) |

## Arquitectura y diseño

### Navegación (flujo de pantallas)

La navegación se realiza mediante `Intent`s explícitos. La pantalla de entrada es `SplashActivity` (con `intent-filter` de LAUNCHER):

```
SplashActivity
   │  "Ver términos"            Puede cancelar
   ├────► TerminosCondicionesActivity
   │  (acepta términos y continúa)
   ▼
MainActivity (Inicio + Catálogo + menú lateral / Drawer)
   │  tocar un producto ──► DetalleProductoActivity ──► (Añadir al carrito, vuelve)
   │  "Mi Carrito" ──► CarroComprasActivity ──► DatosPersonalesActivity (compra) ──► PedidoActivity ──► (vuelve a MainActivity)
   │  "Mis Pedidos" ──► MisPedidosActivity (historial, estado y eliminación de pedidos)
   │  "Mi Perfil" ──► DatosPersonalesActivity (modo perfil: guarda y vuelve)
```

- `SplashActivity` lanza `TerminosCondicionesActivity` con `registerForActivityResult` y marca la casilla automáticamente si acepta (`RESULT_OK`).
- `MainActivity` es el catálogo principal; administra la lista de productos, la búsqueda/filtros y el carrito en memoria, y está envuelta en un `DrawerLayout` con `NavigationView`.
- `CarroComprasActivity` recibe el carrito vía `Intent.getSerializableExtra` (con manejo compatible para Android 13+) y lo devuelve actualizado.
- `DetalleProductoActivity` recibe un `Producto`, muestra su información ampliada con selector de talla y devuelve con `RESULT_OK` el producto con la talla elegida; `MainActivity` lo añade (o fusiona) al carrito.
- `MisPedidosActivity` es accesible desde el menú lateral y muestra el historial de pedidos persistente, permite ampliar el detalle de cada tarjeta, avanzar su estado (Pendiente→Enviado→Entregado) y eliminarlo con confirmación (o muestra el estado vacío si aún no hay compras).
- `DatosPersonalesActivity` valida los campos del cliente (cédula y celular de 10 dígitos, email con formato), selecciona departamento/municipio en cascada y redirige a la confirmación; en modo perfil ("Mi Perfil") guarda los datos y regresa.
- `PedidoActivity` limpia la pila de actividades (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`) al regresar.
- El perfil del cliente se persiste con `SharedPreferences` (JSON) mediante `RepositorioPerfil` y autocompleta los formularios futuros.

### Modelo

`Producto` es un `data class` `Serializable` con los campos:

- `nomProducto`, `descripcion`, `precio` (Double), `imagen` (recurso drawable), `marca` (String), `tallaSeleccionada` (String), `cantidad` (Int).

`Cliente` es un `data class` `Serializable` que agrupa los datos del formulario (nombre, apellidos, cédula, celular, email, departamento, municipio, dirección) y se guarda como JSON en `SharedPreferences` a través de `RepositorioPerfil`.

### Estructura del código

```
app/src/main/java/com/example/gestiondeventasonlinestore_dany/
├── SplashActivity.kt             # Bienvenida rediseñada + enlace a términos
├── TerminosCondicionesActivity.kt # Documento de términos (Aceptar/Cancelar)
├── MainActivity.kt               # Inicio + Catálogo, búsqueda, filtros y control del carrito
├── AdaptadorProducto.kt          # Adapter del RecyclerView de catálogo
├── DetalleProductoActivity.kt    # Detalle del producto (imagen grande, talla, descripción)
├── CarroComprasActivity.kt       # Carrito completo (resumen + total)
├── AdaptadorCarroCompras.kt      # Adapter del carrito (marca, precio, cantidad, talla)
├── DatosPersonalesActivity.kt    # Formulario de compra y perfil (modo perfil)
├── PedidoActivity.kt             # Confirmación de pedido
├── MisPedidosActivity.kt         # Historial de pedidos (detalle, estado y eliminación)
├── AdaptadorPedidos.kt           # Adapter del historial (detalle ampliable + acciones)
├── AyudaActivity.kt              # Canales de atención, horario dinámico y FAQ
├── AcercaDeActivity.kt           # Hero de bienvenida + Misión y Visión
├── Producto.kt                   # Modelo del producto
├── Pedido.kt                     # Modelo del pedido (cliente, productos, total, fecha, estado)
├── Cliente.kt                    # Modelo del cliente/perfil
├── RepositorioPedidos.kt         # Persistencia del historial (SharedPreferences/JSON)
├── RepositorioPerfil.kt          # Persistencia del perfil (SharedPreferences/JSON)
├── UbicacionColombia.kt          # Departamentos y municipios del selector
└── Ui.kt                         # Extensión ajustarBarrasSistema()
```

Recursos del menú lateral: `res/menu/drawer_menu.xml` y `res/layout/header_drawer.xml`.

### Patrón de arquitectura

El proyecto usa un patrón **MVC-ligero**: las `Activity` actúan como controladores y vistas, el modelo `Producto` como datos, y los `Adapter` se encargan de la representación en `RecyclerView`. Los datos del catálogo y el carrito viven en memoria durante la sesión y viajan entre pantallas mediante `Intent` extras; la persistencia actual usa `SharedPreferences` (JSON) para el perfil del cliente y el historial de pedidos. No hay red todavía.

### Diseño visual

- **Tema:** oscuro fijo (`Theme.Material3.Dark.NoActionBar`) con fondo `#0E0E0E`.
- **Color de acento:** dorado `#D4AF37` para botones principales, títulos y bordes de formularios.
- **Sistema de diseño centralizado:** paleta de tokens en `res/values/colors.xml` (fondos, superficies, dorados de marca `#D4AF37`/`#9C7C1E`, textos claro/medio/gris, error y éxito) y estilos reutilizables en `themes.xml` (`Style.BotonDorado`, `Style.BotonContorno`, `Style.BotonAccion`, `Style.CampoTexto`, `Style.Buscador`, `Style.ChipMarca`, `Style.Tarjeta`, `Style.TarjetaResaltada`, `Style.ToolbarApp` y `TextAppearance.*`).
- **Tarjetas:** `MaterialCardView` con fondo `@color/surface` (`#161616`), bordeadas y esquinas de 16dp.
- **Tipografía:** estilos `TextAppearance.*` (títulos, marcas, subtítulos) con acentos dorados y colores claros (`#F5F5F5`, `#B0B0B0`).
- **Barras de sistema:** padding automático sobre el contenido mediante la extensión `ajustarBarrasSistema()` aplicada en todas las pantallas.
- **Formato de precios:** pesos colombianos con separador de miles (`$,.0f`).

### Recursos

- Diseños XML en `app/src/main/res/layout/` (`activity_*`, `item_rv_*`, `drawer_menu.xml`, `header_drawer.xml`).
- Imágenes de productos y logotipo en `app/src/main/res/drawable/`.
- Textos centralizados en `app/src/main/res/values/strings.xml`; menú lateral en `res/menu/drawer_menu.xml`.
- Selectores de estado (chips de marca y ítems del drawer) en `res/color/`.
- Iconos vectoriales dorados para el menú lateral, búsqueda, cantidades y flecha de retorno.

## Puesta en marcha

### Requisitos

- Android Studio Hedgehog o posterior (o una versión compatible con AGP 8.2.0).
- JDK 11 o superior. Nota: con JDK 26 el build puede fallar; se recomienda el JBR 21 incluido con Android Studio (o el JDK que configure el propio IDE).
- Android SDK con API 34 instalada.
- SDK Manager: configura un emulador o conecta un dispositivo físico con Android 7.0+.

### Pasos

1. Clona el repositorio:

   ```bash
   git clone https://github.com/LizethRodriguez07/Proyecto-XML.git
   ```

2. Abre el proyecto en Android Studio y espera a que Gradle sincronice las dependencias.

3. (Opcional) Desde la terminal:

   ```bash
   $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
   .\gradlew.bat :app:compileDebugKotlin --console=plain
   ```

   El APK de depuración se generará ejecutando `assembleDebug` (desde Android Studio: `Run ▶`).

4. Ejecuta la aplicación (`Run ▶`) sobre un emulador o dispositivo físico conectado por USB.

> **Nota:** `local.properties` (ruta del SDK) es local y no se versiona; Android Studio lo genera automáticamente.

## Roadmap

**Corto plazo**
- [ ] Enviar el resumen del pedido (productos + datos del cliente) por **WhatsApp** a un asesor comercial.
- [ ] Persistencia del catálogo con **Room** (SQLite); el perfil del cliente y el historial de pedidos ya usan SharedPreferences.

**Mediano plazo**
- [ ] Catálogo dinámico consumido desde una **API/backend** (en lugar de datos fijos en memoria).
- [ ] Autenticación de usuarios (registro e inicio de sesión) con sesión persistente.

**Largo plazo**
- [ ] Módulo de administración para gestionar productos, stock, precios y promociones.
- [ ] Notificaciones push para confirmación y envío de pedidos.
- [ ] Cobertura de pruebas unitarias e instrumentadas.

> Nota sobre pagos: la gestión del pago queda cubierta con los métodos elegidos en el carrito (NEQUI, Daviplata y Efectivo contra entrega), guardados en cada pedido. Una pasarela de pagos en línea (PSE/tarjeta) **no es necesaria** en el modelo de negocio actual; tampoco se requieren temas claro/oscuro ni internacionalización.

## Licencia

Este proyecto se distribuye bajo la **Licencia MIT**.

MIT License

Copyright (c) 2026 STORE DANY (Katherine Rodríguez)

Se otorga permiso, sin cargo, a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados (el "Software"), para utilizarlo sin restricción, incluyendo, sin limitación, los derechos a usar, copiar, modificar, fusionar, publicar, distribuir, sublicenciar y/o vender copias del Software, y a permitir a las personas a quienes se les proporcione el Software que hagan lo mismo, siempre que se incluya el aviso de derechos de autor anterior y este aviso de permiso en todas las copias o partes sustanciales del Software.

EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O IMPLÍCITA, INCLUYENDO PERO NO LIMITADO A GARANTÍAS DE COMERCIABILIDAD, IDONEIDAD PARA UN PROPÓSITO PARTICULAR Y NO INFRACCIÓN. EN NINGÚN CASO LOS AUTORES O TITULARES DE LOS DERECHOS DE AUTOR SERÁN RESPONSABLES POR CUALQUIER RECLAMO, DAÑO U OTRA RESPONSABILIDAD, YA SEA EN UNA ACCIÓN DE CONTRATO, AGRAVIO O DE OTRA MANERA, QUE SURJA DE O EN RELACIÓN CON EL SOFTWARE O SU USO U OTRO TIPO DE ACCIONES EN EL SOFTWARE.