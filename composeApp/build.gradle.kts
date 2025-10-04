import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation("io.github.vinceglb:confettikit:0.6.0")

            implementation(libs.bundles.ktor.common)
            implementation("io.ktor:ktor-client-logging:2.3.8")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

tasks.register<Exec>("generateConfig") {
    // Aquesta tasca es crea per executar-se SEMPRE abans de la compilació
    // Llegeix les variables d'entorn i crea el fitxer Config.kt
    workingDir = file("${project.projectDir}/src/commonMain/kotlin/cat/happyband/mot/data")
    commandLine("bash", "-c", """
        echo "package cat.happyband.mot.data" > Config.kt
        echo "const val SUPABASE_URL = \"${System.getenv("SUPABASE_URL")}\"" >> Config.kt
        echo "const val SUPABASE_ANON_KEY = \"${System.getenv("SUPABASE_ANON_KEY")}\"" >> Config.kt
        echo "const val TABLE_NAME = \"mot_results\"" >> Config.kt
    """.trimIndent())
}

// Fes que la tasca de compilació depengui d'aquesta generació
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("generateConfig")
}


