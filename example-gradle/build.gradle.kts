plugins {
    base
}

allprojects {
    group = "com.example.library"
    version = providers.gradleProperty("tapikVersion").getOrElse("0.6.0")
}
