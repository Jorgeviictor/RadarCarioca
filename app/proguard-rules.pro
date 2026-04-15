# Radar Carioca ProGuard Rules

# Manter modelos de dados (Room + Serialização)
-keep class com.radarcarioca.data.model.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Mapbox
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Serialização JSON
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
