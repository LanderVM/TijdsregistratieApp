import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.andr.app) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.jlleitschuh.gradle.ktlint) apply true
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.daggerHilt.android) apply false
    alias(libs.plugins.benManes.version) apply true
    alias(libs.plugins.versionCatalog) apply true
    alias(libs.plugins.owasp) apply false
}

allprojects {
    apply(plugin = "org.owasp.dependencycheck")
}

configure<org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension> {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
}

dependencies {
    ktlintRuleset(libs.ktlint.composeRules)
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

configurations {
    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version) // disallow non stable versions
        }
    }
}
