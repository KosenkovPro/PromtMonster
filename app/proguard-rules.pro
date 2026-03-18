# Keep data classes for Gson
-keep class pro.kosenkov.promtmonster.data.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Gson
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
