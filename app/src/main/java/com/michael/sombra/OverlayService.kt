package com.michael.sombra

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView

class OverlayService : Service() {

    companion object {
        var activo = false
            private set
        private const val TAG = "OverlayService"
    }

    private lateinit var windowManager: WindowManager
    private val overlayViews = mutableListOf<View>()
    private var foregroundOk = false

    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "michael_sombra_channel"

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTIVAR" -> activarSombra()
            "ACTUALIZAR" -> actualizarSombra()
            "DESACTIVAR" -> detenerSombra()
        }
        return START_STICKY
    }

    private fun actualizarSombra() {
        if (!activo) return
        detenerSombraInternal(false)
        activarSombraInternal()
    }

    private fun activarSombra() {
        if (activo) return
        activarSombraInternal()
    }

    private fun activarSombraInternal() {
        try {
            val prefs = getSharedPreferences("sombra_prefs", MODE_PRIVATE)
            val anchoDp = prefs.getInt("shadow_width", 100)
            val alturaPct = prefs.getInt("shadow_height_pct", 100)
            val topExtDp = prefs.getInt("shadow_top_ext", 0)
            val initDownDp = prefs.getInt("shadow_init_down", 0)
            val botExtDp = prefs.getInt("shadow_bot_ext", 0)
            val marginDp = prefs.getInt("shadow_margin", 0)
            val opacityPct = prefs.getInt("shadow_opacity", 50)
            val invertido = prefs.getBoolean("shadow_invert", false)

            // Foreground service
            if (!foregroundOk) {
                try {
                    crearCanalNotificacion()
                    startForeground(NOTIFICATION_ID, crearNotificacion())
                    foregroundOk = true
                } catch (e: Exception) {
                    Log.w(TAG, "No se pudo iniciar foreground: ${e.message}")
                    foregroundOk = false
                }
            }

            val type: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE

            val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            val density = resources.displayMetrics.density
            val anchoPx = (anchoDp * density).toInt()
            val marginPx = (marginDp * density).toInt()
            val topExtPx = (topExtDp * density).toInt()
            val initDownPx = (initDownDp * density).toInt()
            val botExtPx = (botExtDp * density).toInt()

            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            val baseHeightPx = (screenHeight * alturaPct / 100)

            val yPos = -topExtPx + initDownPx
            val totalHeight = baseHeightPx + topExtPx + botExtPx
            val alphaFloat = opacityPct / 100f

            val drawableIzquierdo = if (invertido) R.drawable.gradient_right else R.drawable.gradient_left
            val drawableDerecho = if (invertido) R.drawable.gradient_left else R.drawable.gradient_right

            // --- SOMBRA IZQUIERDA ---
            // Gravity.LEFT + x = margin → separado del borde izquierdo
            val paramsIzq = WindowManager.LayoutParams(
                anchoPx,
                totalHeight,
                type,
                flags,
                PixelFormat.TRANSLUCENT
            )
            paramsIzq.gravity = Gravity.TOP or Gravity.LEFT
            paramsIzq.x = marginPx
            paramsIzq.y = yPos

            val viewIzq = ImageView(this)
            viewIzq.setImageResource(drawableIzquierdo)
            viewIzq.scaleType = ImageView.ScaleType.FIT_XY
            viewIzq.alpha = alphaFloat
            windowManager.addView(viewIzq, paramsIzq)
            overlayViews.add(viewIzq)

            // --- SOMBRA DERECHA ---
            // Gravity.RIGHT + x = margin para mantener posicion al rotar pantalla
            val paramsDer = WindowManager.LayoutParams(
                anchoPx,
                totalHeight,
                type,
                flags,
                PixelFormat.TRANSLUCENT
            )
            paramsDer.gravity = Gravity.TOP or Gravity.RIGHT
            paramsDer.x = marginPx
            paramsDer.y = yPos

            val viewDer = ImageView(this)
            viewDer.setImageResource(drawableDerecho)
            viewDer.scaleType = ImageView.ScaleType.FIT_XY
            viewDer.alpha = alphaFloat
            windowManager.addView(viewDer, paramsDer)
            overlayViews.add(viewDer)

            activo = true
            Log.i(TAG, "Sombra: ${anchoDp}dp, margin=$marginPx, screen=${screenWidth}x${screenHeight}, izq.x=${paramsIzq.x}, der.x=${paramsDer.x}")
        } catch (e: Exception) {
            Log.e(TAG, "Error al activar sombra: ${e.message}", e)
            detenerSombraInternal(true)
        }
    }

    private fun detenerSombra() { detenerSombraInternal(true) }

    private fun detenerSombraInternal(pararServicio: Boolean) {
        for (v in overlayViews) {
            try { windowManager.removeView(v) } catch (_: Exception) {}
        }
        overlayViews.clear()
        if (pararServicio) {
            activo = false
            foregroundOk = false
            try { stopForeground(STOP_FOREGROUND_REMOVE) } catch (_: Exception) {}
            stopSelf()
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val ch = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_MIN)
                ch.description = getString(R.string.channel_desc)
                ch.setSound(null, null)
                ch.enableVibration(false)
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
            } catch (_: Exception) {}
        }
    }

    private fun crearNotificacion(): Notification {
        val b = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            b.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        return b.build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
