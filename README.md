# NASA APOD Client & Viewer App

## Summary

This project includes the Java source files, resources, and Gradle files to build an Android app that is a client to the NASA APOD web service, and a viewer for the media content retrieved from that service.

This app is used in a series of exercises in the Deep Dive Coding Java + Android Bootcamp. In these exercises, we explore Android resources (including layouts, menus, colors, styles, strings, and dimensions), activities &amp; fragments, navigation concepts &amp; techniques, model-view-controller (MVC) architecture, fluent &amp; functional coding styles, database integration using Room &amp; SQLite, 

## Requirements

* **`deepdive-utils`**

In addition to the files in this project, there are a number of external compile-time and run-time dependencies. These are specified in the `dependencies` section of the `app` module-level `build.gradle` file; however, one of these is not satisfiable through the Maven central or Google repositories: the `edu.cnm.deepdive:deepdive-utils:1.0.0` library must be built by cloning the repository locates at <https://github.com/deep-dive-coding-java/deepdive-utils>, and running `mvn install` to install the artifact in your local Maven repository. (The project-level `build.gradle` file of this project is already configured to include the local Maven repository in its dependencies search.)

* **`nasa.properties`**


