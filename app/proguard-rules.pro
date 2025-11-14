# ================================================================================================
# HealthConnect ProGuard Rules - Comprehensive Security & Optimization Configuration
# ================================================================================================
# This file contains ProGuard rules for:
# - Code obfuscation and optimization
# - Security hardening
# - Library compatibility
# - Crash reporting support
# ================================================================================================

# ================================================================================================
# SECURITY & OBFUSCATION
# ================================================================================================

# Enable aggressive obfuscation
-repackageclasses ''
-allowaccessmodification
-optimizationpasses 5

# Preserve line numbers for crash reporting while hiding source file names
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove logging in release builds for security
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove debug and verbose logging from Timber (if used)
-assumenosideeffects class timber.log.Timber* {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ================================================================================================
# ANDROID FRAMEWORK
# ================================================================================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Activity subclasses and their view methods
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep View methods
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================================================================================
# HEALTH CONNECT
# ================================================================================================

# Keep all Health Connect SDK classes
-keep class androidx.health.connect.client.** { *; }
-keep interface androidx.health.connect.client.** { *; }
-keep enum androidx.health.connect.client.** { *; }

# Keep Health Connect records
-keep class androidx.health.connect.client.records.** { *; }
-keep interface androidx.health.connect.client.records.** { *; }

# Keep Health Connect permissions
-keep class androidx.health.connect.client.permission.** { *; }

# Keep Health Connect metadata
-keep class androidx.health.connect.client.metadata.** { *; }

# Keep Health Connect time classes
-keep class androidx.health.connect.client.time.** { *; }

# Keep Health Connect units
-keep class androidx.health.connect.client.units.** { *; }

# ================================================================================================
# DEPENDENCY INJECTION - HILT/DAGGER
# ================================================================================================

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt modules
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.components.SingletonComponent class * { *; }

# Keep Hilt entry points
-keep @dagger.hilt.EntryPoint interface * { *; }

# Keep classes with @Inject constructors
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# Keep classes with @Inject fields
-keepclasseswithmembernames class * {
    @javax.inject.Inject <fields>;
}

# Keep classes with @Inject methods
-keepclasseswithmembernames class * {
    @javax.inject.Inject <methods>;
}

# Keep Dagger generated classes
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_HiltModules** { *; }
-keep class **_ComponentTreeDeps { *; }

# ================================================================================================
# KOTLIN
# ================================================================================================

# Keep Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep Kotlin serialization (if used)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Kotlin DateTime
-keep class kotlinx.datetime.** { *; }

# ================================================================================================
# ANDROIDX LIBRARIES
# ================================================================================================

# Keep AndroidX annotations
-keep class androidx.annotation.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Keep LiveData
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }

# Keep ViewBinding classes
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}

# Keep RecyclerView
-keep class androidx.recyclerview.widget.RecyclerView { *; }
-keep class androidx.recyclerview.widget.RecyclerView$ViewHolder { *; }

# ================================================================================================
# DATA MODELS
# ================================================================================================

# Keep all data models (adjust package name as needed)
-keep class com.eic.healthconnectdemo.data.model.** { *; }
-keep class com.eic.healthconnectdemo.domain.model.** { *; }

# Keep data class properties
-keepclassmembers class com.eic.healthconnectdemo.data.model.** {
    <fields>;
    <methods>;
}
-keepclassmembers class com.eic.healthconnectdemo.domain.model.** {
    <fields>;
    <methods>;
}

# ================================================================================================
# NETWORKING (if used in future)
# ================================================================================================

# Retrofit (if added)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp (if added)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Gson (if added)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ================================================================================================
# CRASH REPORTING
# ================================================================================================

# Keep exception classes for crash reporting
-keep class * extends java.lang.Exception
-keep class * extends java.lang.Error
-keep class * extends java.lang.Throwable

# Keep stack traces
-keepattributes Exceptions

# ================================================================================================
# REFLECTION
# ================================================================================================

# Keep classes accessed via reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ================================================================================================
# WARNINGS SUPPRESSION
# ================================================================================================

# Suppress warnings for missing classes (common in Android)
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.reflect.jvm.internal.**

# ================================================================================================
# END OF PROGUARD RULES
# ================================================================================================
