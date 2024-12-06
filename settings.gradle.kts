pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://maven.google.com/") // Google Maven deposu açıkça belirtiliyor

        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven ("https://maven.google.com/")
        maven("https://jitpack.io")

        mavenCentral()
    }
}

rootProject.name = "Whispers Nearby"
include(":app")
 