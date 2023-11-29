pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.mozilla.org/maven2")
        maven(url = "https://plugins.gradle.org/m2/")
    }

    resolutionStrategy {
        eachPlugin {
            // Manually resolve Glean plugin ID to Maven coordinates,
            // because the Maven repository is missing plugin marker artifacts.
            // See: https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_resolution_rules
            if (requested.id.id == "org.mozilla.telemetry.glean-gradle-plugin") {
                useModule("org.mozilla.telemetry:glean-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.mozilla.org/maven2")
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "TikTokReporter"
include(":app")
