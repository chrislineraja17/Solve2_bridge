# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.ktx.**
-dontwarn com.google.firebase.installations.ktx.**

# Keep your models used with Firestore
-keep class com.solve_bridge.app.Post { *; }
-keep class com.solve_bridge.app.SolutionModel { *; }
-keep class com.solve_bridge.app.UserModel { *; }

# Keep names for PropertyName annotation
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
}

# AndroidX and Material components
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }

# Credential Manager
-keep class androidx.credentials.** { *; }
