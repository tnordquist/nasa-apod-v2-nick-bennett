# NASA APOD Client & Viewer App

## Summary

This project includes the Java source files, resources, and Gradle files to build an Android app that is a client to the NASA APOD web service, and a viewer for the media content retrieved from that service.

This app is used in a series of exercises in the Deep Dive Coding Java + Android Bootcamp. In these exercises, we explore Android resources (including layouts, menus, colors, styles, strings, and dimensions), activities &amp; fragments, navigation concepts &amp; techniques, model-view-controller (MVC) architecture, fluent &amp; functional coding styles, database integration using Room &amp; SQLite, 

## Requirements

* **`deepdive-utils`**

In addition to the files in this project, there are a number of external compile-time and run-time dependencies. These are specified in the `dependencies` section of the `app` module-level `build.gradle` file; however, one of these is not satisfiable through the Maven central or Google repositories: the `edu.cnm.deepdive:deepdive-utils:1.0.0` library must be built by cloning the repository located at <https://github.com/deep-dive-coding-java/deepdive-utils>, and running `mvn install` (which can be done from the Maven tool window of IntelliJ IDEA) to install the artifact in your local Maven repository. (The project-level `build.gradle` file of this project is already configured to include the local Maven repository in its dependencies search.)

* **`nasa.properties`**

During the build process, a file with the path (relative to the project directory) and name `../../services/nasa.properties` must be found (i.e. the `services` directory must be located in the parent directory of the parent directory of this project), and must be readable as a Java properties file. It must contain (minimally) the property `api_key`, with a value obtained from ["NASA Open API: Get Your API Key"](https://api.nasa.gov/index.html#apply-for-an-api-key.)
