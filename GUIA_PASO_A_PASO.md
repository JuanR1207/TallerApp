# TallerApp – CRUD Android con Compose, Room, Hilt, Navigation 3 y Firebase

Proyecto basado en la guía **"CRUD Android profesional con Jetpack Compose, Firebase y Room" (InventarioApp)**, siguiendo los mismos 12 capítulos y el mismo orden. En vez de productos de un inventario, la app registra **vehículos que ingresan a un taller**.

## Equivalencias con la guía

| Guía (InventarioApp)              | Este proyecto (TallerApp)             |
|-----------------------------------|---------------------------------------|
| `com.example.inventarioapp`       | `com.example.tallerapp`               |
| `InventoryApplication`            | `TallerApplication`                   |
| `feature/product/`                | `feature/vehicle/`                    |
| `Product`                         | `Vehicle`                             |
| `name`                            | `plate` (placa)                       |
| `description`                     | `service` (servicio realizado)        |
| `priceCop: Long`                  | `costCop: Long` (costo del servicio)  |
| `quantity: Int`                   | `mileage: Int` (kilometraje)          |
| —                                 | `brand` (marca), `ownerName` (propietario) |
| tabla `products` / `inventory.db` | tabla `vehicles` / `taller.db`        |
| `users/{uid}/products/{id}`       | `users/{uid}/vehicles/{id}`           |

Todo lo demás (arquitectura, capas, sincronización, tombstone, pending, reglas por UID) es igual a la guía.

---

## ▶ Arranque rápido (proyecto terminado)

1. Descomprime el ZIP y abre la carpeta `TallerApp` en Android Studio (**File → Open**). El proyecto usa las mismas versiones de tu plantilla (AGP 9.5.2, SDK 37).
2. Si ya habías creado un proyecto TallerApp vacío, ciérralo o bórralo para no confundirte con dos carpetas iguales.
3. Crea tu proyecto en Firebase y descarga `google-services.json` (capítulo 09, pasos 1–2). Cópialo en `TallerApp/app/`.
4. En Firebase activa **Authentication → Anónimo** y crea **Firestore** (capítulo 09, pasos 5–6).
5. Publica las reglas de `firestore.rules` (capítulo 09, paso 8).
6. **Sync Project with Gradle Files** y ejecuta la app ▶.
7. Prueba unitaria: clic derecho en `app/src/test/...` → **Run Tests** (son 6 pruebas de `SaveVehicleUseCase`).

El resto de este documento explica cada capítulo como en la guía, para que puedas sustentar el proyecto.

---

## Ruta de aprendizaje (igual que la guía)

- **Fase A:** proyecto, interfaz Compose, Navigation 3 y modelos.
- **Fase B:** Room, repositorio, ViewModel, Hilt y CRUD local.
- **Fase C:** Firebase, reglas de seguridad y sincronización.
- **Fase D:** revisar el flujo completo, pruebas y mejoras.

Al final de cada fase hay un **commit sugerido** para subir el proyecto a GitHub por partes.

---

## 01 · Fundamentos MVVM

Antes de escribir código, ten clara la responsabilidad de cada capa:

| Capa | Debe hacer | No debería hacer |
|------|------------|------------------|
| Presentación | Dibujar la UI y exponer el estado | Ejecutar SQL o leer Firestore |
| Dominio | Definir `Vehicle`, contratos y reglas | Importar Context o Compose |
| Datos | Persistir y sincronizar; mapear modelos | Mostrar AlertDialog o navegar |
| DI (Hilt) | Crear/proveer dependencias | Tener reglas de negocio |

Flujo: Compose → ViewModel → `VehicleRepository` (contrato) → `VehicleRepositoryImpl` → Room / Firestore. Los datos vuelven por `Flow → StateFlow → Compose`.

**Estructura de carpetas:**

```
com/example/tallerapp/
├── TallerApplication.kt
├── MainActivity.kt
├── core/
│   ├── database/AppDatabase.kt
│   ├── di/ (DatabaseModule, FirebaseModule, RepositoryModule)
│   ├── navigation/AppNavigation.kt
│   └── ui/theme/Theme.kt   (colores del taller, extra)
└── feature/vehicle/
    ├── domain/   (model, repository, usecase)
    ├── data/     (local, remote, mapper, repository)
    └── presentation/ (screen, section, composables, state, viewmodel)
```

Ejercicio mental (adaptado):
- ¿Qué archivo cambias si la tarjeta de un vehículo cambia de diseño? → `VehicleCard.kt`
- ¿Y si cambia el nombre de la colección `vehicles`? → `FirestoreVehicleDataSource.kt`
- ¿Dónde va la validación de la placa? → `SaveVehicleUseCase.kt`

---

## 02 · Crear el proyecto y configurar Gradle

### Paso 1. Proyecto limpio
1. Android Studio → **New Project → Empty Activity** (Jetpack Compose).
2. Nombre: **TallerApp**; package: **com.example.tallerapp**.
3. Lenguaje Kotlin, Min SDK 26 o superior.
4. Deja el AGP y Kotlin que trae la plantilla.
5. **Ejecuta la app una vez** antes de tocar nada (debe salir el "Hello Android").

### Paso 2. Plugins (catálogo de versiones)
Todas las versiones están en `gradle/libs.versions.toml`; se conservaron AGP y Kotlin de la plantilla de Android Studio, como pide la guía. El `build.gradle.kts` raíz declara con `apply false`: Android, Compose, Kotlin Serialization, KSP, Hilt y Google Services. AGP 9 trae Kotlin integrado, por eso **no** aparece `org.jetbrains.kotlin.android`.

En `app/build.gradle.kts` se aplican esos plugins y se fija `applicationId = "com.example.tallerapp"`.

### Paso 3. Dependencias

| Librería | Versión |
|----------|---------|
| AGP / Gradle | 9.5.2 / 9.6.0 |
| Kotlin (plugins Compose y Serialization) / KSP | 2.2.10 / 2.3.12 |
| compileSdk / minSdk / targetSdk | 37 / 30 / 37 |
| Compose BoM | 2026.09.00 |
| Room | 2.8.5 |
| Lifecycle | 2.11.1 |
| Navigation 3 | 1.2.0 |
| Hilt / androidx.hilt | 2.60.1 / 1.4.0 |
| Firebase BoM | 35.1.0 |

### Paso 4. Manifest
`app/src/main/AndroidManifest.xml` ya tiene:
- `android:name=".TallerApplication"`
- `<uses-permission android:name="android.permission.INTERNET" />`
- icono y tema propios (`@style/Theme.TallerApp`).

> ⚠️ El proyecto **no compila sin `app/google-services.json`** (es tuyo, se descarga de tu Firebase). Si quieres seguir el orden de la guía y probar Room primero, comenta temporalmente `id("com.google.gms.google-services")` en `app/build.gradle.kts`; pero la app se cerrará al abrir, porque Hilt crea `FirebaseAuth` al inicio. Lo más práctico: haz de una vez los pasos 1–3 del capítulo 09.

**Punto de comprobación:** Gradle Sync sin errores.

> 💾 **Commit 1:** `chore: crear proyecto TallerApp y configurar Gradle (Hilt, KSP, Room, Nav3, Firebase)`

---

## 03 · UI Compose: screen, section y composables

Orden de construcción visual (igual que la guía):
1. `VehicleListScreen` – estructura general y conexión al ViewModel.
2. `VehicleCard` – componente reutilizable, sin ViewModel.
3. `VehicleListSection` – LazyColumn de `VehicleCard`.
4. `VehicleFormSection` y `VehicleFormField`; `VehicleFormScreen` conecta eventos con el ViewModel.
5. `VehicleDeleteDialog`.

Componentes usados: `Scaffold`, `LazyColumn`, `Card`, `OutlinedTextField`, `AlertDialog`, `collectAsStateWithLifecycle`.

El costo se maneja en pesos colombianos enteros (`costCop: Long`), igual que `priceCop` en la guía. No se usa `Double` para dinero.

> Los archivos de UI dependen de los ViewModels y del modelo `Vehicle`, por eso se pegan en el capítulo 08. En este capítulo basta con entender qué hace cada uno.

---

## 04 · Modelo del dominio y contrato

Crea estos archivos (en este orden):

1. `feature/vehicle/domain/model/Vehicle.kt` – data class sin anotaciones de Room.
2. `feature/vehicle/domain/repository/VehicleRepository.kt` – interfaz con `observeVehicles`, `findById`, `save`, `delete`, `syncNow` y la clase `SyncReport`.
3. `feature/vehicle/domain/usecase/SaveVehicleUseCase.kt` – validaciones centralizadas:
   - Placa con formato colombiano: `ABC123` (carro) o `ABC12D` (moto).
   - Marca y propietario obligatorios.
   - Servicio obligatorio (máx. 500 caracteres).
   - Costo mayor a 0.
   - Kilometraje mayor o igual a 0.

> Regla de diseño (igual que la guía): la UI puede avisar, pero la regla real vive en el dominio.

---

## 05 · Room: entidad, DAO y base de datos

1. `feature/vehicle/data/local/VehicleEntity.kt` – tabla `vehicles` con los campos extra `pending` e `isDeleted`.
2. `feature/vehicle/data/local/VehicleDao.kt` – `observeVisible()` (Flow), `find`, `getPending`, `upsert`, `markSynced`.
3. `core/database/AppDatabase.kt` – `@Database(entities = [VehicleEntity::class], version = 1)`.
4. `feature/vehicle/data/mapper/VehicleMapper.kt` – `toDomain()` y `toEntity()`.

**Comprobación con Database Inspector** (cuando la app ya corra): View → Tool Windows → App Inspection → Database Inspector → `taller.db` → `vehicles`.

No se usa `allowMainThreadQueries()` ni `fallbackToDestructiveMigration()`.

---

## 06 · Hilt y repositorio

1. `TallerApplication.kt` con `@HiltAndroidApp`.
2. `MainActivity.kt` con `@AndroidEntryPoint` y `AppNavigation()` dentro de `setContent` (envuelto en `TallerAppTheme`, que reemplaza el `MaterialTheme` de la guía).
3. `core/di/DatabaseModule.kt` – crea `AppDatabase` (`taller.db`) y provee `VehicleDao`.
4. `core/di/FirebaseModule.kt` – provee `FirebaseAuth` y `FirebaseFirestore`.
5. `core/di/RepositoryModule.kt` – `@Binds` de `VehicleRepositoryImpl` a `VehicleRepository`.
6. `feature/vehicle/data/repository/VehicleRepositoryImpl.kt` – guarda primero en Room, borra con tombstone y sincroniza con `syncNow()`.

`VehicleRepositoryImpl` necesita `FirestoreVehicleDataSource`, `VehicleDto` y `VehicleRemoteMapper` (capítulo 10). **Pégalos de una vez** para que compile:
- `feature/vehicle/data/remote/VehicleDto.kt`
- `feature/vehicle/data/remote/FirestoreVehicleDataSource.kt`
- `feature/vehicle/data/mapper/VehicleRemoteMapper.kt`

> Si aparece **MissingBinding**: revisa `@InstallIn`, `@Binds`, `@HiltAndroidApp` y que KSP esté aplicado.

> 💾 **Commit 2:** `feat: dominio Vehicle, Room (entity, dao, database) y modulos Hilt`

---

## 07 · ViewModels y estado de UI

| Operación | ViewModel | Repositorio | Room |
|-----------|-----------|-------------|------|
| Listar | Observa StateFlow | Convierte Flow de entidad a dominio | SELECT reactivo |
| Crear/editar | Valida y envía Vehicle | Marca pending y guarda | @Upsert |
| Eliminar | Confirma desde UI | Crea tombstone | @Upsert isDeleted=true |
| Sincronizar | Muestra carga y mensajes | Sube y descarga | Persiste resultado |

Archivos:
1. `presentation/state/VehicleListUiState.kt`
2. `presentation/state/VehicleFormUiState.kt`
3. `presentation/viewmodel/VehicleListViewModel.kt` – escucha Room en `init` e intenta sincronizar; si no hay red muestra *"Sin conexión: datos locales disponibles"*.
4. `presentation/viewmodel/VehicleFormViewModel.kt` – funciones `updatePlate`, `updateBrand`, `updateOwnerName`, `updateService`, `updateCost`, `updateMileage` y `save()`. La placa se pasa a mayúsculas y se le quitan espacios y guiones antes de validar.

Ejercicio (igual que la guía): explica por qué `saved = true` se activa después de guardar en Room y no después de la red. Apaga el Wi-Fi y comprueba que el vehículo sigue apareciendo.

---

## 08 · Navigation 3 y pantallas

1. `core/navigation/AppNavigation.kt` – rutas `VehicleListRoute` y `VehicleFormRoute(id: String? = null)`, `rememberNavBackStack`, `NavDisplay`, decoradores y `hiltViewModel()` (de `androidx.hilt.lifecycle.viewmodel.compose`).
2. `presentation/screen/VehicleListScreen.kt`
3. `presentation/section/VehicleListSection.kt`
4. `presentation/composables/VehicleCard.kt`
5. `presentation/composables/VehicleDeleteDialog.kt`
6. `presentation/screen/VehicleFormScreen.kt`
7. `presentation/section/VehicleFormSection.kt`
8. `presentation/composables/VehicleFormField.kt` (tiene un parámetro extra `capitalization` para que la placa salga en mayúsculas).

### Demostración a realizar (CRUD local)
- Registrar 3 vehículos, por ejemplo:
  - `ABC123` · Chevrolet · Juan Pérez · Cambio de aceite · 180000 · 45000
  - `XYZ45F` · Yamaha · Laura Gómez · Cambio de pastillas de freno · 95000 · 12000
  - `KLM789` · Renault · Carlos Ruiz · Alineación y balanceo · 120000 · 78000
- Cerrar la app y abrirla: la lista sigue ahí.
- Editar costo/kilometraje y revisar el cambio.
- Eliminar con confirmación y comprobar que desaparece.
- Probar una placa inválida (ej. `12ABC`): debe salir el error y no guardar.
- Verificar que el botón Atrás regresa a la lista.

> En este punto el CRUD funciona **solo con Room**. El botón Sincronizar mostrará "Sin conexión" porque Firebase aún no está configurado; eso es normal.

> 💾 **Commit 3:** `feat: ViewModels, Navigation 3 y pantallas Compose (CRUD local con Room)`

---

## 09 · Firebase Console paso a paso

### Paso 1 · Crear el proyecto Firebase
- Entra a https://console.firebase.google.com con tu cuenta Google.
- **Agregar proyecto** → nombre: `TallerApp-Aula`.
- Analytics no es necesario.

### Paso 2 · Registrar la app Android
- En la descripción general, icono de Android → **Agregar app**.
- Package: **com.example.tallerapp** (exacto).
- Alias opcional; SHA-1 no es necesario para Auth anónima + Firestore.
- **Registrar app** y descargar `google-services.json`.

### Paso 3 · Colocar google-services.json
```
TallerApp/
├── build.gradle.kts
├── settings.gradle.kts
└── app/
    ├── build.gradle.kts
    ├── google-services.json   <- AQUI
    └── src/main/...
```
No va en `src/main/java` ni se escribe a mano.

### Paso 4 · Conectar Gradle
El plugin `com.google.gms.google-services` ya está aplicado en `app/build.gradle.kts` (si lo comentaste, descoméntalo). Haz **Sync Project with Gradle Files**.

Si sale *No matching client found for package name*, compara `applicationId` con `package_name` dentro del JSON.

### Paso 5 · Crear Firestore Database
- Build → **Firestore Database** → **Crear base de datos**.
- Edición Standard, id `(default)`.
- Ubicación cercana (ej. `southamerica-east1` o `us-east1`).
- **Modo producción**.
- No crees la colección a mano: el SDK crea `users/{uid}/vehicles/{vehicleId}` al sincronizar.

### Paso 6 · Activar Authentication anónima
- Build → **Authentication** → Comenzar.
- Pestaña **Sign-in method** → **Anónimo** → habilitar → guardar.

### Paso 7 · ¿Para qué sirve el UID?
Cada vehículo se guarda en `users/UID/vehicles/vehicleId`. Así un usuario no puede leer los vehículos de otro.

### Paso 8 · Publicar reglas
- Firestore Database → pestaña **Rules**.
- Pega el contenido de `firestore.rules` de este proyecto.
- **Publicar**.

Las reglas validan, además, que la placa tenga el formato colombiano y que costo y kilometraje sean enteros ≥ 0.

### Campos de cada documento

| Campo | Tipo | Ejemplo |
|-------|------|---------|
| id | string | UUID generado por la app |
| plate | string | ABC123 |
| brand | string | Chevrolet |
| ownerName | string | Juan Pérez |
| service | string | Cambio de aceite |
| costCop | integer | 180000 |
| mileage | integer | 45000 |
| updatedAt | integer | timestamp en milisegundos |
| isDeleted | boolean | false |

`pending` solo existe en Room, no se sube a Firestore.

### Paso 9 · Guardar el primer registro
- Abre la app con Internet.
- Pulsa **+** y registra: Placa `ABC123`, Marca `Chevrolet`, Propietario `Juan Pérez`, Servicio `Cambio de aceite`, Costo `180000`, Kilometraje `45000`.
- Guardar → aparece en la lista (Room).
- Pulsa **Sincronizar** → crea la sesión anónima.
- En Firebase → Authentication → Users copia el UID.

### Paso 10 · Verificar la ruta en Firestore
```
users
  {tu-uid}
    vehicles
      {id-del-vehiculo}
        plate: "ABC123"
        brand: "Chevrolet"
        ...
        isDeleted: false
```

### Paso 11 · Verificar Editar y Eliminar
- Edita el kilometraje a `46000`, guarda y sincroniza → cambia en Firestore sobre el mismo id.
- Elimina el vehículo y sincroniza → el documento sigue existiendo pero con `isDeleted: true`.

### Paso 12 · Diagnóstico

| Síntoma | Qué revisar |
|---------|-------------|
| permission-denied | Anónimo activado, reglas publicadas, UID correcto, placa válida |
| No matching client found | applicationId distinto al package registrado |
| Sin documentos | No pulsaste Sincronizar o falló |
| Vehículo reaparece | Revisar isDeleted y pendientes |
| Cuenta anónima ausente | La primera vez necesita Internet |
| JSON no detectado | google-services.json en app/ y plugin activo |

> 💾 **Commit 4:** `feat: integrar Firebase Auth anonima, Firestore y reglas de seguridad`
>
> ⚠️ Antes de hacer push, decide con tu instructor si subes `google-services.json`. No contiene contraseñas, pero muchos prefieren agregarlo a `.gitignore`.

---

## 10 · Sincronización Room / Firestore

Estrategia (igual que la guía): **Room manda en la UI**. Todo se guarda en Room y queda `pending`. `syncNow()`:
1. Asegura sesión anónima.
2. Sube los pendientes y los marca sincronizados (solo si no cambiaron mientras tanto).
3. Descarga los documentos del usuario y los guarda en Room si son más nuevos y no hay una edición local pendiente.

Archivos de este capítulo (ya los pegaste en el 06):
- `data/remote/VehicleDto.kt`
- `data/mapper/VehicleRemoteMapper.kt`
- `data/remote/FirestoreVehicleDataSource.kt`

### Prueba offline / online
- Con Internet, agrega `KLM789` y compruébalo en Firestore.
- Activa modo avión y edita su kilometraje: la UI se actualiza desde Room.
- Elimina otro vehículo sin Internet: desaparece de la lista, queda el tombstone.
- Quita modo avión, pulsa Sincronizar y revisa la consola.
- Cierra la app a la fuerza y vuelve a abrirla.

**Limitaciones** (igual que la guía): `updatedAt` usa el reloj del celular, no hay WorkManager, ni tiempo real, ni manejo de varios dispositivos. Es didáctico.

---

## 11 · Auditar y resolver errores

Ruta si no ves un vehículo en Firebase:
1. ¿Está en Room? (Database Inspector).
2. Si no está: revisa formulario, `SaveVehicleUseCase` y `VehicleDao.upsert`.
3. Si está: pulsa Sincronizar con Internet y lee el mensaje.
4. Si falla: revisa FirebaseAuth, reglas, UID y ruta en `FirestoreVehicleDataSource`.
5. Si está en Firebase pero no en la app: revisa mappers, `observeVisible`, StateFlow y `VehicleListSection`.

| Síntoma | Archivo |
|---------|---------|
| El formulario se ve mal | VehicleFormSection.kt / VehicleFormField.kt |
| Los botones no responden | VehicleFormScreen.kt / VehicleListScreen.kt |
| Se guardan datos incorrectos | SaveVehicleUseCase.kt / VehicleFormViewModel.kt |
| No aparece en Room | VehicleRepositoryImpl.kt / VehicleDao.kt |
| No aparece en Firebase | FirestoreVehicleDataSource.kt / firestore.rules |
| Room tiene datos, UI vacía | observeVehicles() → VehicleListViewModel → VehicleListSection |

---

## 12 · Pruebas y evaluación

### Matriz de pruebas

| Caso | Acción | Esperado |
|------|--------|----------|
| Crear sin Internet | Guardar vehículo en modo avión | Aparece en la lista |
| Leer persistente | Cerrar y abrir la app | Se conservan los vehículos |
| Editar | Cambiar costo y kilometraje | Muestra datos nuevos |
| Eliminar | Confirmar borrado | Oculto en la lista |
| Cancelar borrado | Cerrar el diálogo | El vehículo se mantiene |
| Sincronizar | Reconectar y pulsar Sincronizar | Firestore actualizado |
| Validación | Placa `12ABC`, costo con letras o marca vacía | Error; no guarda |
| Seguridad | Acceder con otro uid | Firestore niega el acceso |

### Diagnóstico rápido

| Síntoma | Corrección |
|---------|------------|
| MissingBinding | Módulo sin @InstallIn o KSP no aplicado |
| Permission denied | Auth desactivado, uid incorrecto o reglas sin publicar |
| No matching client found | Package distinto en google-services.json |
| Unresolved reference NavDisplay | Faltan dependencias de Navigation 3 |
| Room schema changed | Subir versión de BD y crear Migration |

### Evidencias para entregar
Capturas de: lista, formulario, error de validación, Database Inspector (`taller.db`), documento en Firestore, reglas publicadas, cambio sin Internet y sincronización al reconectar. Más un diagrama MVVM propio con la explicación de cada capa.

> 💾 **Commit 5:** `docs: evidencias, pruebas y README`

### Retos opcionales (adaptados)
- Buscar vehículos por placa en `VehicleListSection` sin consultar Firebase.
- Mostrar en cada tarjeta si está "pendiente de sincronizar".
- Evitar registrar dos veces la misma placa.
- Worker con WorkManager para sincronizar con conexión.
- ✅ Ya incluido: pruebas unitarias de `SaveVehicleUseCase` en `app/src/test/.../SaveVehicleUseCaseTest.kt` (placa de carro y moto, placa inválida, costo 0, kilometraje negativo, marca vacía), con un `FakeVehicleRepository`.
- Pantalla de detalle del vehículo con una ruta extra en Navigation 3.

---

## Anexo · Checklist

- [ ] Crear Empty Activity y compilar.
- [ ] Configurar Gradle, KSP, Hilt, Navigation 3 y Room.
- [ ] Crear dominio `Vehicle` y `VehicleRepository`.
- [ ] Implementar Entity, DAO y AppDatabase.
- [ ] Crear módulos Hilt.
- [ ] Construir ViewModels y pantallas Compose.
- [ ] Conectar rutas de Navigation 3.
- [ ] Verificar CRUD Room sin Internet.
- [ ] Registrar Firebase, colocar google-services.json y habilitar Anónimo.
- [ ] Publicar reglas y probar permisos.
- [ ] Sincronizar y verificar Firestore.
- [ ] Aplicar matriz de pruebas y entregar evidencias.
