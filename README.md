# Remote Numpad Server

## Introduction

This project is a part of a trio of projects:

* [Remote Numpad](https://github.com/theolizard/remote-numpad): Written in
Kotlin, this is the client which runs on an Android device and sends the
user's inputs to the computer.
* Remote Numpad Server (this project): Written in Kotlin, this is the server
that runs on the computer and receives the inputs from the Android device and
simulates the key presses.
* [Cocoa Native Server](https://github.com/theolizard/cocoa-native-server):
Written in Objective-C, this is the Bluetooth server library for MacOS X. It
receives the Bluetooth data and passes it on to the Remote Numpad Server.

## Description

This is a cross-platform server written in Kotlin and compatible with Windows,
Linux and MacOS X. It is made up of a *VirtualNumpad* class which execute the
inputs and a set of *IConnectionInterface* implementations which receive the
inputs to be executed.

The only two supported connection interfaces so far are wifi and classic
Bluetooth (RFCOMM) but BLE and Wifi-Direct are intended to be implemented in
the future.

Bluecove is used for the Bluetooth implementation but in the case of MacOS X,
the APIs that Bluecove uses have been obsoleted so a native library has been
written using the JNI (see the project Cocoa Native Server).

## Compilation

The project is set up with Gradle so it can be compiled using Gradle or
imported by any Gradle-capable and Android-capable IDE (e.g.: Android Studio,
IntelliJ, Eclipse) and then compiled.

Bluecove is included in the repo and the other dependencies are downloaded
through Gradle.

The file *src/main/resources/libNativeServer.dylib* is obtained by compiling
the [Cocoa Native Server](https://github.com/theolizard/cocoa-native-server)
project and is only required to run the server on MacOS X.

## Contributing

This is not a main project for me so help is very apreciated. Anyone is
welcome to contribute to this project (issues, requests, pull requests).
