package com.michael.sombra

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnActivar: Button
    private lateinit var btnDesactivar: Button
    private lateinit var btnInvertir: Button
    private lateinit var tvEstado: TextView
    private lateinit var seekAncho: SeekBar
    private lateinit var seekAltura: SeekBar
    private lateinit var seekTopExt: SeekBar
    private lateinit var seekInitDown: SeekBar
    private lateinit var seekBotExt: SeekBar
    private lateinit var seekMargin: SeekBar
    private lateinit var seekOpacity: SeekBar
    private lateinit var tvAncho: TextView
    private lateinit var tvAltura: TextView
    private lateinit var tvTopExt: TextView
    private lateinit var tvInitDown: TextView
    private lateinit var tvBotExt: TextView
    private lateinit var tvMargin: TextView
    private lateinit var tvOpacity: TextView
    private lateinit var prefs: SharedPreferences
    private var invertido = false
    private val OVERLAY_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnActivar = findViewById(R.id.btnActivar)
        btnDesactivar = findViewById(R.id.btnDesactivar)
        btnInvertir = findViewById(R.id.btnInvertir)
        tvEstado = findViewById(R.id.tvEstado)
        seekAncho = findViewById(R.id.seekAncho)
        seekAltura = findViewById(R.id.seekAltura)
        seekTopExt = findViewById(R.id.seekTopExt)
        seekInitDown = findViewById(R.id.seekInitDown)
        seekBotExt = findViewById(R.id.seekBotExt)
        seekMargin = findViewById(R.id.seekMargin)
        seekOpacity = findViewById(R.id.seekOpacity)
        tvAncho = findViewById(R.id.tvAncho)
        tvAltura = findViewById(R.id.tvAltura)
        tvTopExt = findViewById(R.id.tvTopExt)
        tvInitDown = findViewById(R.id.tvInitDown)
        tvBotExt = findViewById(R.id.tvBotExt)
        tvMargin = findViewById(R.id.tvMargin)
        tvOpacity = findViewById(R.id.tvOpacity)
        prefs = getSharedPreferences("sombra_prefs", MODE_PRIVATE)

        // Cargar valores guardados
        val anchoGuardado = prefs.getInt("shadow_width", 100)
        seekAncho.progress = (anchoGuardado - 10).coerceIn(0, 200)
        tvAncho.text = "Ancho: ${anchoGuardado}dp"

        val alturaGuardada = prefs.getInt("shadow_height_pct", 100)
        seekAltura.progress = (alturaGuardada - 30).coerceIn(0, 70)
        tvAltura.text = "Altura: ${alturaGuardada}%"

        val topExtGuardado = prefs.getInt("shadow_top_ext", 0)
        seekTopExt.progress = topExtGuardado.coerceIn(0, 80)
        tvTopExt.text = "Subir: ${topExtGuardado}dp"

        val initDownGuardado = prefs.getInt("shadow_init_down", 0)
        seekInitDown.progress = initDownGuardado.coerceIn(0, 400)
        tvInitDown.text = "Inicio: ${initDownGuardado}dp abajo"

        val botExtGuardado = prefs.getInt("shadow_bot_ext", 0)
        seekBotExt.progress = botExtGuardado.coerceIn(0, 80)
        tvBotExt.text = "Bajar: ${botExtGuardado}dp"

        val marginGuardado = prefs.getInt("shadow_margin", 0)
        seekMargin.progress = marginGuardado.coerceIn(0, 100)
        tvMargin.text = "Espacio: ${marginGuardado}dp"

        val opacityGuardada = prefs.getInt("shadow_opacity", 50)
        seekOpacity.progress = opacityGuardada.coerceIn(0, 100)
        tvOpacity.text = "Oscuridad: ${opacityGuardada}%"

        invertido = prefs.getBoolean("shadow_invert", false)
        actualizarBotonInvertir()

        btnActivar.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                guardarYPulsarSombra()
            } else {
                solicitarPermisoOverlay()
            }
        }

        btnDesactivar.setOnClickListener {
            detenerSombra()
        }

        btnInvertir.setOnClickListener {
            invertido = !invertido
            prefs.edit().putBoolean("shadow_invert", invertido).apply()
            actualizarBotonInvertir()
            if (OverlayService.activo) actualizarServicio()
        }

        // SeekBar listeners
        seekAncho.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            val ancho = (progress + 10).coerceIn(10, 210)
            tvAncho.text = "Ancho: ${ancho}dp"
            prefs.edit().putInt("shadow_width", ancho).apply()
        })

        seekAltura.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            val altura = (progress + 30).coerceIn(30, 100)
            tvAltura.text = "Altura: ${altura}%"
            prefs.edit().putInt("shadow_height_pct", altura).apply()
        })

        seekTopExt.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            tvTopExt.text = "Subir: ${progress}dp"
            prefs.edit().putInt("shadow_top_ext", progress).apply()
        })

        seekInitDown.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            tvInitDown.text = "Inicio: ${progress}dp abajo"
            prefs.edit().putInt("shadow_init_down", progress).apply()
        })

        seekBotExt.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            tvBotExt.text = "Bajar: ${progress}dp"
            prefs.edit().putInt("shadow_bot_ext", progress).apply()
        })

        seekMargin.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            tvMargin.text = "Espacio: ${progress}dp"
            prefs.edit().putInt("shadow_margin", progress).apply()
        })

        seekOpacity.setOnSeekBarChangeListener(simpleSeekbar { progress ->
            tvOpacity.text = "Oscuridad: ${progress}%"
            prefs.edit().putInt("shadow_opacity", progress).apply()
        })

        actualizarEstado()
        setupSocialButtons()
    }

    // Helper para evitar repetir código en los SeekBars
    private fun simpleSeekbar(onChange: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) onChange(progress)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (OverlayService.activo) actualizarServicio()
        }
    }

    private fun actualizarBotonInvertir() {
        if (invertido) {
            btnInvertir.text = "Invertir: ON 🔄"
            btnInvertir.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF8E44AD.toInt()))
        } else {
            btnInvertir.text = "Invertir: OFF"
            btnInvertir.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF555555.toInt()))
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarEstado()
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Si la sombra está activa, reenviar configuración al servicio
        // para que recalcule posiciones con las nuevas dimensiones de pantalla
        if (OverlayService.activo) {
            actualizarServicio()
        }
    }

    private fun guardarYPulsarSombra() {
        val ancho = (seekAncho.progress + 10).coerceIn(10, 210)
        val altura = (seekAltura.progress + 30).coerceIn(30, 100)
        prefs.edit()
            .putInt("shadow_width", ancho)
            .putInt("shadow_height_pct", altura)
            .putInt("shadow_top_ext", seekTopExt.progress)
            .putInt("shadow_init_down", seekInitDown.progress)
            .putInt("shadow_bot_ext", seekBotExt.progress)
            .putInt("shadow_margin", seekMargin.progress)
            .putInt("shadow_opacity", seekOpacity.progress)
            .putBoolean("shadow_invert", invertido).apply()
        iniciarSombra()
    }

    private fun actualizarEstado() {
        val sombraActiva = OverlayService.activo
        tvEstado.text = if (sombraActiva) "Sombra: ACTIVA ✅" else "Sombra: INACTIVA ❌"
        btnActivar.isEnabled = !sombraActiva
        btnDesactivar.isEnabled = sombraActiva
    }

    private fun actualizarServicio() {
        try {
            val intent = Intent(this, OverlayService::class.java).apply { action = "ACTUALIZAR" }
            startService(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarSombra() {
        try {
            val intent = Intent(this, OverlayService::class.java).apply { action = "ACTIVAR" }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
            else startService(intent)
            actualizarEstado()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun detenerSombra() {
        try {
            val intent = Intent(this, OverlayService::class.java).apply { action = "DESACTIVAR" }
            startService(intent)
            actualizarEstado()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al detener: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun solicitarPermisoOverlay() {
        AlertDialog.Builder(this)
            .setTitle("Permiso necesario")
            .setMessage("Michael Sombra necesita permiso para dibujar sobre otras aplicaciones.\n\nEn la siguiente pantalla, activa el interruptor \"Permitir mostrar sobre otras apps\".")
            .setPositiveButton("Ir a Configuración") { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), OVERLAY_PERMISSION_REQUEST)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupSocialButtons() {
        try {
            val btnFacebook: Button = findViewById(R.id.btnFacebook)
            val btnTelegram: Button = findViewById(R.id.btnTelegram)
            val btnWhatsApp: Button = findViewById(R.id.btnWhatsApp)
            val btnYouTube: Button = findViewById(R.id.btnYouTube)

            btnFacebook.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/share/1D1pfVdbXE/"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir Facebook: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            btnTelegram.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Michael_Antonio_Rodriguez"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir Telegram: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            btnWhatsApp.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/50500000000?text=Hola%20Michael"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            btnYouTube.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@MichaelAntonioRodriguezCondega"))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir YouTube: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            // Si no se encuentran los botones (vista de contacto no disponible), ignorar silenciosamente
            android.util.Log.w("MainActivity", "Botones de redes sociales no disponibles: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST && Settings.canDrawOverlays(this)) {
            guardarYPulsarSombra()
        } else if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            Toast.makeText(this, "Permiso de superposición no concedido", Toast.LENGTH_LONG).show()
        }
    }
}
