# Keep our service and activity
-keep class com.michael.sombra.** { *; }

# Keep Android components
-keep class * extends android.app.Service { *; }
-keep class * extends android.app.Activity { *; }

# Keep WindowManager related stuff
-keep class android.view.WindowManager$LayoutParams { *; }
-keep class android.graphics.PixelFormat { *; }
-keep class android.view.Gravity { *; }

# Keep notification related stuff
-keep class android.app.Notification { *; }
-keep class android.app.NotificationChannel { *; }
-keep class android.app.NotificationManager { *; }
