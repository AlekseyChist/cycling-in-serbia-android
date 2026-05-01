# Keep kotlinx.serialization metadata (Supabase uses it)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class com.cyclinginserbia.app.**$$serializer { *; }
-keepclassmembers class com.cyclinginserbia.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.cyclinginserbia.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
