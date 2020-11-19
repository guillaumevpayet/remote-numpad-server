plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.guillaumepayet"
version = "1.4.0"

repositories {
    mavenCentral()

    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.4.1")
    implementation("net.sf.bluecove:bluecove:2.1.1-SNAPSHOT")
//    implementation("net.sf.bluecove:bluecove-gpl:2.1.1-SNAPSHOT")
//    implementation("net.sf.bluecove:bluecove-bluez:2.1.1-SNAPSHOT")
//    implementation("com.github.hypfvieh:dbus-java:3.2.3")
//    implementation("org.scijava:native-lib-loader:2.3.4")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.guillaumepayet.remotenumpadserver.MainKt"
    }
}

//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
