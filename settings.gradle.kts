pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()        // VERY IMPORTANT for ML Kit
        mavenCentral()
    }
}

rootProject.name = "LearningApp"
include(":app")
