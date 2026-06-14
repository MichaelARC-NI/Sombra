# 🌑 Michael Sombra

**Sombra lateral personalizable para Android**

**Funciona Con O Sin Acceso Root No Es Necesario Tener Root**

Aplicación que dibuja un degradado oscuro en los bordes laterales de la pantalla, simulando una sombra estilo notch o curvatura. Totalmente personalizable con 8 controles en tiempo real.

---

## 🎯 Funcionalidades

- **Sombra lateral izquierda y derecha** con degradado negro semitransparente
- **8 controles**:
  1. **Ancho** — grosor horizontal de la sombra (10–210dp)
  2. **Altura** — largo vertical en porcentaje de la pantalla (30–100%)
  3. **Subir arriba** — desplaza la sombra hacia arriba para cubrir barra de estado (0–80dp)
  4. **Bajar inicio** — mueve el punto de inicio hacia abajo desde el tope (0–400dp)
  5. **Bajar abajo** — extiende la sombra hacia la parte inferior (0–80dp)
  6. **Invertir** — invierte la dirección del degradado
  7. **Espacio desde bordes** — separa la sombra del borde de la pantalla (0–100dp)
  8. **Oscuridad** — controla la opacidad del degradado (0–100%)
- **Persistencia**: todos los valores se guardan al cerrar la app
- **Servicio en primer plano (Foreground Service)** con notificación silenciosa para que el sistema no la cierre
- **La sombra es "intocable"**: no bloquea los toques ni el enfoque (flags `FLAG_NOT_TOUCHABLE` + `FLAG_NOT_FOCUSABLE`)
- **Cobertura total**: usa `FLAG_LAYOUT_IN_SCREEN` + `FLAG_LAYOUT_NO_LIMITS` para cubrir la barra de estado y bordes

---

## ⚠️ Instalación

> **IMPORTANTE**: En algunos dispositivos (especialmente Android 13+ con políticas de seguridad restrictivas como HyperOS, MIUI, ColorOS, OneUI, etc.) el instalador de paquetes predeterminado **puede bloquear la instalación** mostrando errores como *"No se puede instalar"*, *"Archivo dañado"* o simplemente no dejando instalar.

Si te ocurre eso, usa uno de estos métodos alternativos:

### Método 1 — ADB (recomendado)

Conecta el teléfono a la PC con depuración USB activada:

```bash
adb install MichaelSombra.apk
```

Si ya está instalada y quieres actualizar:

```bash
adb install -r MichaelSombra.apk
```

### Método 2 — Shizuku + MT Manager

1. Activa **Shizuku** en tu dispositivo
2. Abre **MT Manager** con permisos de Shizuku
3. Navega a la carpeta donde tienes el APK
4. Mantén presionado el archivo → **Instalar**

### Método 3 — Instalador de sistema (si funciona)

1. Descarga el APK
2. Ábrelo desde el gestor de archivos
3. Concede el permiso "Instalar apps desconocidas" si lo pide
4. Toca **Instalar**

---

## 🔧 Permisos necesarios

Al abrir la app por primera vez, te pedirá:

- **Mostrar sobre otras apps** (`SYSTEM_ALERT_WINDOW`) — necesario para dibujar la sombra sobre cualquier pantalla
- **Notificaciones** — para mantener el servicio en primer plano

Si no concedes el permiso de superposición, la app te guiará a la pantalla de configuración correspondiente.

---

## 📱 Cómo usar

1. Abre la app **Michael Sombra**
2. Ajusta los controles a tu gusto
3. Presiona **Activar Sombra**
4. Para desactivar, presiona **Desactivar Sombra**
5. Los cambios se aplican en tiempo real mientras la sombra está activa

### Combinaciones sugeridas

| Efecto | Ancho | Altura | Subir | Inicio | Bajar | Invertir | Espacio | Oscuridad |
|--------|-------|--------|-------|--------|-------|----------|---------|-----------|
| Sombra sutil tipo notch | 30dp | 80% | 20dp | 0 | 0 | OFF | 0 | 40% |
| Sombra completa lateral | 80dp | 100% | 30dp | 0 | 10dp | OFF | 0 | 60% |
| Sombra solo esquinas | 50dp | 40% | 0 | 0 | 0 | OFF | 20dp | 70% |
| Sombra invertida | 60dp | 90% | 20dp | 0 | 0 | ON | 5dp | 50% |
| Marco oscuro | 100dp | 100% | 40dp | 0 | 20dp | OFF | 10dp | 80% |

---

## 🛠 Compilar desde código

```bash
git clone https://github.com/MichaelARC-NI/Sombra.git
cd Sombra
./gradlew assembleRelease
```

El APK firmado estará en `app/build/outputs/apk/release/`.

### Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK 35
- Kotlin 2.1+

---

## 📋 Notas técnicas

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 + (Android 15 +)
- **Lenguaje**: Kotlin
- **Arquitectura**: Activity + Foreground Service + WindowManager
- **Ofuscación**: R8 (minifyEnabled = true)
- **Package**: `com.michael.sombra`

---

## 📸 Captura


[Es Asi](./Screenshot_2026-06-14-06-14-04-881_com.google.android.apps.docs.jpg) [Cofiguracionees](https://github.com/MichaelARC-NI/Sombra/blob/main/SCREENSHOT_2026-06-14-06-14-13-804_COM.MICHAEL.SOMBRA.JPG)


---

## 🔗 Enlaces

- **Repositorio**: https://github.com/MichaelARC-NI/Sombra
- **Reportar errores**: https://github.com/MichaelARC-NI/Sombra/issues
