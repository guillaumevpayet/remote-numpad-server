/*
 * Remote Numpad Server - a server for the Remote Numpad.
 * Copyright (C) 2016-2020 Guillaume Payet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.guillaumepayet"
version = "1.5.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.4.1")
    implementation("io.ultreia:bluecove:2.1.1")
//    implementation("com.github.hypfvieh:dbus-java:3.2.3")
    implementation("org.scijava:native-lib-loader:2.3.4")
}

tasks.shadowJar {
    exclude("DebugProbesKt.bin")

    manifest {
        attributes["Main-Class"] = "com.guillaumepayet.remotenumpadserver.MainKt"
    }
}
