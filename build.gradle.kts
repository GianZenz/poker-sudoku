// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Top-level configuration that applies to all modules
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
