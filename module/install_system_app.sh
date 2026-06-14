#!/sbin/sh
# Instalador directo para Michael Sombra como app de sistema
# Ejecutar con: su -c sh /sdcard/Download/install_sombra_system.sh

echo ""
echo "  ╔══════════════════════════════╗"
echo "  ║    Michael Sombra v1.3       ║"
echo "  ║  Instalador directo          ║"
echo "  ╚══════════════════════════════╝"
echo ""

# Ruta del APK en este mismo directorio
APK_DIR=$(dirname "$0")
APK_SRC="$APK_DIR/MichaelSombra.apk"

if [ ! -f "$APK_SRC" ]; then
    # Try alternative paths
    if [ -f "/sdcard/Download/MichaelSombra.apk" ]; then
        APK_SRC="/sdcard/Download/MichaelSombra.apk"
    else
        echo "❌ ERROR: No se encuentra MichaelSombra.apk"
        echo "   Copia el APK a /sdcard/Download/ y vuelve a intentar"
        exit 1
    fi
fi

echo "  📍 APK encontrado: $APK_SRC"
echo ""

# Mount system as writable
echo "  🔧 Montando /system como escriturable..."
mount -o rw,remount /system 2>/dev/null
mount -o rw,remount / 2>/dev/null

# Try to remount with magisk if available
if command -v magisk >/dev/null 2>&1; then
    magisk --mount 2>/dev/null
fi

# Create priv-app directory
echo "  📁 Creando directorio..."
mkdir -p /system/priv-app/MichaelSombra 2>/dev/null

# Copy APK
echo "  📦 Copiando APK..."
cp "$APK_SRC" /system/priv-app/MichaelSombra/MichaelSombra.apk 2>/dev/null

if [ $? -ne 0 ]; then
    echo "❌ ERROR: No se pudo copiar el APK"
    echo "   ¿Tienes root y permisos de escritura en /system?"
    exit 1
fi

# Set permissions
echo "  🔧 Configurando permisos..."
chmod 0755 /system/priv-app/MichaelSombra 2>/dev/null
chmod 0644 /system/priv-app/MichaelSombra/MichaelSombra.apk 2>/dev/null
chown root:root /system/priv-app/MichaelSombra 2>/dev/null
chown root:root /system/priv-app/MichaelSombra/MichaelSombra.apk 2>/dev/null

# Remount as read-only
mount -o ro,remount /system 2>/dev/null
mount -o ro,remount / 2>/dev/null

echo ""
echo "  ✅ App instalada como sistema"
echo "  🔄 Reinicia el dispositivo"
echo ""
echo "  📍 Ubicación: /system/priv-app/MichaelSombra/MichaelSombra.apk"
echo ""
