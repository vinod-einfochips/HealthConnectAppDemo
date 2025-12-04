plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("jacoco")
}

android {
    namespace = "com.eic.healthconnectdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eic.healthconnectdemo"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "ENVIRONMENT", "\"DEV\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            resValue("string", "app_name", "HealthConnect DEV")
        }
        create("qa") {
            dimension = "environment"
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            buildConfigField("String", "ENVIRONMENT", "\"QA\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            resValue("string", "app_name", "HealthConnect QA")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"PRODUCTION\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            resValue("string", "app_name", "HealthConnect")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    lint {
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = false
        checkReleaseBuilds = true
        checkDependencies = true
        baseline = file("lint-baseline.xml")
        htmlReport = true
        xmlReport = true
        textReport = true
        htmlOutput = file("${layout.buildDirectory.get()}/reports/lint/lint-results.html")
        xmlOutput = file("${layout.buildDirectory.get()}/reports/lint/lint-results.xml")
        textOutput = file("${layout.buildDirectory.get()}/reports/lint/lint-results.txt")

        // Disable specific checks if needed
        // InvalidPackage: JaCoCo uses java.lang.management which isn't available on Android
        disable +=
            listOf(
                "ObsoleteLintCustomCheck",
                "GradleDependency",
                "InvalidPackage",
            )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // UI Components
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Health Connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha10")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Kotlin DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Hilt - Using KSP instead of kapt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("app.cash.turbine:turbine:1.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

// KSP Configuration
ksp {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
}

// JaCoCo Configuration
jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// JaCoCo Report Task
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDevDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/data/model/*",
            "**/di/*",
            "**/*_Factory.*",
            "**/*_MembersInjector.*",
            "**/*Module.*",
            "**/*Dagger*.*",
            "**/*Hilt*.*",
            "**/*_Provide*Factory*.*",
        )

    val debugTree =
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/devDebug") {
            exclude(fileFilter)
        }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "jacoco/testDevDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec",
            )
        },
    )
}

// Coverage Verification Task
tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")
    group = "Verification"
    description = "Verify code coverage meets minimum thresholds"

    violationRules {
        rule {
            limit {
                minimum = "0.60".toBigDecimal()
            }
        }

        rule {
            enabled = true
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.50".toBigDecimal()
            }
            excludes =
                listOf(
                    "*.BuildConfig",
                    "*.di.*",
                    "*.*Module",
                    "*.*_Factory",
                    "*.*_MembersInjector",
                )
        }
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/data/model/*",
            "**/di/*",
        )

    val debugTree =
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/devDebug") {
            exclude(fileFilter)
        }

    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "jacoco/testDevDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec",
            )
        },
    )
}

// Detekt Configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
}

// Combined Quality Check Task
tasks.register("qualityCheck") {
    group = "verification"
    description = "Run all code quality checks including tests, lint, detekt, ktlint, and coverage"
    dependsOn(
        "testDevDebugUnitTest",
        "detekt",
        "ktlintCheck",
        "lintDevDebug",
        "jacocoTestReport",
        "jacocoTestCoverageVerification",
    )
}

// Pre-commit Quality Check (faster, without coverage)
tasks.register("preCommitCheck") {
    group = "verification"
    description = "Run quick quality checks before committing (no coverage)"
    dependsOn(
        "ktlintCheck",
        "detekt",
        "testDevDebugUnitTest",
    )
}
