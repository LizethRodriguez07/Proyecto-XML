# Gestion de Ventas Online STORE_DANY

Aplicación móvil Android para la gestión de ventas en línea de **STORE DANY**, una tienda especializada en calzado para hombre. El proyecto centraliza un flujo completo de compra: catálogo de productos, selección de tallas, carrito de compras, captura de datos personales y confirmación de pedido.

## Descripción general

STORE DANY es un negocio dedicado a la venta de calzado masculino de las marcas más reconocidas del entorno comercial (Nike, Adidas, Puma, New Balance y Reebok). Esta aplicación actúa como su plataforma de gestión de ventas online, brindando una asesoría integral sobre los productos y acompañando al cliente durante todo el proceso de compra.

El flujo de la app guía al usuario desde un pantalla de bienvenida con términos y condiciones, pasando por un catálogo visual de productos, hasta la confirmación de un pedido. Actualmente los datos del catálogo son locales (en memoria), y se proyecta integrar el envío del pedido a un asesor comercial.

## Características principales

- **Pantalla de bienvenida (Splash):** presentación de la tienda, mensaje de la propietaria y aceptación de términos y condiciones mediante casilla obligatoria antes de continuar.
- **Catálogo de productos:** lista de calzado (Nike Air Trainer, Adidas Forum, Puma Street, New Balance 1300) con imagen, descripción, precio en pesos colombianos y selector de tallas (37–42).
- **Carrito de compras:** añadir o quitar productos, contador actualizado en tiempo real en el botón flotante y en el menú superior, con avisos mediante Toast.
- **Resumen de compra:** pantalla que lista los artículos seleccionados con su talla, permite eliminar ítems y muestra el total a pagar calculado automáticamente.
- **Formulario de datos personales:** captura de nombre, apellidos, cédula, celular, email y dirección con validación de campos vacíos.
- **Confirmación de pedido:** pantalla de éxito con mensaje de agradecimiento y botón para volver al inicio (reinicia la navegación y limpia el carrito).
- **Diseño premium:** interfaz oscura con acentos dorados, fondos negros, tarjetas Material con bordes redondeados y componentes Material Components (MaterialButton, MaterialCardView, TextInputLayout, MaterialCheckBox).

## Stack tecnológico

| Capa | Tecnología |
| --- | --- |
| Lenguaje | Kotlin 1.9.0 |
| UI | XML layouts + ViewBinding |
| Entorno de compilación | Android Gradle Plugin 8.2.0, Gradle (wrapper) |
| SDK mínimo / objetivo | minSdk 24 (Android 7.0) / targetSdk y compileSdk 34 (Android 14) |
| JVM | Java 11 |
| Framework UI | Material Components 1.10.0, AppCompat 1.6.1, ConstraintLayout 2.1.4, RecyclerView |
| Extensiones | AndroidX Core KTX 1.12.0 |
| Arquitectura base | Activities (UI) + Adapters (RecyclerView) + Modelo `Producto` (Serializable) |

## Arquitectura y diseño

### Navegación (flujo de pantallas)

La navegación se realiza mediante `Intent`s explícitos. La pantalla de entrada es `SplashActivity` (con `intent-filter` de LAUNCHER):

```
SplashActivity
   │  (acepta términos)
   ▼
MainActivity ──────────────► CarroComprasActivity
   │  (catálogo)                       │
   │                                   ▼
   └──────────► DatosPersonalesActivity ──► PedidoActivity ──► (vuelve a MainActivity)
```

- `MainActivity` es el catálogo principal; administra la lista de productos y el carrito en memoria.
- `CarroComprasActivity` recibe el carrito vía `Intent.getSerializableExtra` (con manejo compatible para Android 13+).
- `DatosPersonalesActivity` valida los campos y redirige a la confirmación.
- `PedidoActivity` limpia la pila de actividades (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`) al regresar.

### Modelo

`Producto` es un `data class` `Serializable` con los campos:

- `nomProducto`, `descripcion`, `precio` (Double), `imagen` (recurso drawable), `tallaSeleccionada` (String).

### Estructura del código

```
app/src/main/java/com/example/gestiondeventasonlinestore_dany/
├── SplashActivity.kt            # Bienvenida + términos y condiciones
├── MainActivity.kt              # Catálogo y control del carrito
├── AdaptadorProducto.kt         # Adapter del RecyclerView de catálogo
├── CarroComprasActivity.kt      # Resumen de compra
├── AdaptadorCarroCompras.kt     # Adapter del carrito
├── DatosPersonalesActivity.kt   # Formulario de datos del cliente
├── PedidoActivity.kt            # Confirmación de pedido
├── Producto.kt                  # Modelo del producto
└── data/                        # (Plantilla de login sin usar)
```

### Patrón de arquitectura

El proyecto usa un patrón **MVC-ligero**: las `Activity` actúan como controladores y vistas, el modelo `Producto` como datos, y los `Adapter` se encargan de la representación en `RecyclerView`. No hay capa de persistencia ni red todavía: los datos viven en memoria durante la sesión y viajan entre pantallas mediante `Intent` extras.

### Diseño visual

- **Tema:** oscuro (`Theme.Material3.DayNight.NoActionBar`) con fondo `#000000`.
- **Color de acento:** dorado `#D4AF37` para botones principales, títulos y bordes de formularios.
- **Tarjetas:** `MaterialCardView` translúcidas (`#1AFFFFFF`) con esquinas de 16dp.
- **Tipografía:** condicional (`sans-serif-condensed-medium`, `serif`) y colores claros (`#E0E0E0`, `#FFFFFF`).
- **Formato de precios:** pesos colombianos con separador de miles (`$,.0f`).

### Recursos

- Diseños XML en `app/src/main/res/layout/` (`activity_*`, `item_rv_*`, `catalogo.xml`).
- Imágenes de productos y logotipo en `app/src/main/res/drawable/`.
- Textos centralizados en `app/src/main/res/values/strings.xml`; menú superior en `res/menu/main_menu.xml`.

## Puesta en marcha

### Requisitos

- Android Studio Hedgehog o posterior (o una versión compatible con AGP 8.2.0).
- JDK 11 o superior.
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
   ./gradlew assembleDebug
   ```

   El APK de depuración se generará en `app/build/outputs/apk/debug/`.

4. Ejecuta la aplicación (`Run ▶`) sobre un emulador o dispositivo físico.

> **Nota:** `local.properties` (ruta del SDK) es local y no se versiona; Android Studio lo genera automáticamente.

## Roadmap

**Corto plazo**
- [ ] Enviar el resumen del pedido (productos + datos del cliente) por **WhatsApp** a un asesor comercial.
- [ ] Persistencia local de productos y pedidos con **Room** (base de datos SQLite).
- [ ] Gestión de cantidades y tallas por producto desde el carrito.

**Mediano plazo**
- [ ] Catálogo dinámico consumido desde una **API/backend** (en lugar de datos fijos en memoria).
- [ ] Autenticación de usuarios (registro e inicio de sesión) con sesión persistente.
- [ ] Estado de pedidos y seguimiento (pendiente, aprobado, enviado, entregado).

**Largo plazo**
- [ ] Pasarela de pagos (PSE, tarjeta de crédito/débito, NEQUI/Daviplata).
- [ ] Módulo de administración para gestionar productos, stock, precios y promociones.
- [ ] Búsqueda, filtros por talla/marca y favoritos.
- [ ] Notificaciones push para confirmación y envío de pedidos.
- [ ] Soporte de temas claro/oscuro e internacionalización.
- [ ] Cobertura de pruebas unitarias e instrumentadas.

## Licencia

Este proyecto se distribuye bajo la **Licencia MIT**.

MIT License

Copyright (c) 2026 STORE DANY (Katherine Rodríguez)

Se otorga permiso, sin cargo, a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados (el "Software"), para utilizarlo sin restricción, incluyendo, sin limitación, los derechos a usar, copiar, modificar, fusionar, publicar, distribuir, sublicenciar y/o vender copias del Software, y a permitir a las personas a quienes se les proporcione el Software que hagan lo mismo, siempre que se incluya el aviso de derechos de autor anterior y este aviso de permiso en todas las copias o partes sustanciales del Software.

EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O IMPLÍCITA, INCLUYENDO PERO NO LIMITADO A GARANTÍAS DE COMERCIABILIDAD, IDONEIDAD PARA UN PROPÓSITO PARTICULAR Y NO INFRACCIÓN. EN NINGÚN CASO LOS AUTORES O TITULARES DE LOS DERECHOS DE AUTOR SERÁN RESPONSABLES POR CUALQUIER RECLAMO, DAÑO U OTRA RESPONSABILIDAD, YA SEA EN UNA ACCIÓN DE CONTRATO, AGRAVIO O DE OTRA MANERA, QUE SURJA DE O EN RELACIÓN CON EL SOFTWARE O SU USO U OTRO TIPO DE ACCIONES EN EL SOFTWARE.