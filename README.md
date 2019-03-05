# NASA APOD Client & Viewer App

## Summary

This project includes the Java source files, resources, and Gradle files to build an Android app that is a client to the NASA APOD web service, and a viewer for the media content retrieved from that service.

This app is used in a series of exercises in the Deep Dive Coding Java + Android Bootcamp. In these exercises, we explore Android resources (including layouts, menus, colors, styles, strings, and dimensions), activities &amp; fragments, navigation concepts &amp; techniques, model-view-controller (MVC) architecture, fluent &amp; functional coding styles, and Room/SQLite database integration. 

## Requirements

* **`deepdive-utils`**

In addition to the files in this project, there are a number of external compile-time and run-time dependencies. These are specified in the `dependencies` section of the `app` module-level `build.gradle` file; however, one of these is not satisfiable through the Maven central or Google repositories: the `edu.cnm.deepdive:deepdive-utils:1.0.0` library must be built by cloning the repository located at <https://github.com/deep-dive-coding-java/deepdive-utils>, and running `mvn install` (which can be done from the Maven tool window of IntelliJ IDEA) to install the artifact in your local Maven repository. (The project-level `build.gradle` file of this project is already configured to include the local Maven repository in its dependencies search.)

* **`nasa.properties`**

During the build process, a file with the path (relative to the project directory) and name `../../services/nasa.properties` must be found (i.e. the `services` directory must be located in the parent directory of the parent directory of this project), and must be readable as a Java properties file. It must contain (minimally) the property `api_key`, with a value obtained from ["NASA Open API: Get Your API Key"](https://api.nasa.gov/index.html#apply-for-an-api-key.)

## Programming features

### Model-View-Controller

* To the greatest practical extent, user interaction is handled by (or at least delegated to) classes in the `controller` package (typically, activities and fragments).

* As of yet, there are no custom view classes in the project; however, there are subclasses of `RecyclerView.Adapter` and `RecyclerView.ViewHolder` in the `view` package; these are responsible for binding model class instances to view objects defined in the various layout resources of the app. Any UI event listeners configured by these classes (e.g. `View.OnClickListener`, `View.OnCreateContextMenuListener`) are either set directly to instances of controller classes, or set to the adapter or holder class instances, with the corresponding methods invoking methods in the controller classes.

* The `model` package contains entity classes (in the `entity` sub-package) mapped to JSON documents (via Gson annotations) and SQLite tables (via Room annotations), as well as data access object (DAO) interfaces (in the `dao` sub-package) that declare basic CRUD operations on the entities. There's also a subclass of `RoomDatabase`, with abstract getters (implemented by the Room annotation processor) for each DAO, and with converters for entity field types that are not natively supported by Room or SQLite.

### Service layer

Rather than including Retrofit and Room client code in controller classes, the operations needed by the app have been implemented in classes in the `service` package. Many of these are subclasses of a `BaseFluentAsyncTask` class, itself a subclass of the `AsyncTask` class in the Android standard library. (See ["Fluent &amp; functional background task definition &amp; invocation"](#fluent--functional-background-task-definition--invocation) for more information.)

To simplify the invocation of fragment operations from all controller classes, a `FragmentService` class provides methods to support non-back-stack intensive fragment operations&mdash;e.g. switching between UI fragments in response to user interaction with `NavigationView` and `BottomNavigationView` objects.

### Singleton pattern used for de facto singletons (and other logical singletons)

At runtime, an Android app has only one instance of the `Application` class (or subclass); thus, it is a de facto singleton. Similarly, in an application that uses the Room ORM, there is only one instance of a `RoomDatabase` subclass for a given application context&mdash;so just one instance in a running app. In obtaining access to these de facto singletons, we often end up inventing "mutant" singletons, rather than simply implementing the classic singleton pattern.
 
In this app, the `ApodApplication` class is implemented with a `static ApodApplication getInstance()` method, for returning the single instance. Since this instance is accessible in the `Application.onCreate` method, which is invoked (only once) by Android, there's no need to do any lazy initialization or synchronization to save this in a `static` field.

The `ApodDB` class uses a hybrid approach: since there will be only one instance per application context, it is already a de facto singleton. Since the `Room.databaseBuilder` method requires an application context, it is a common practice to pass in a `Context` to the `getInstance` method; this doesn't follow the singleton pattern, however. In the implementation employed by `ApodDB` class, the `ApodApplication.getInstance()` method is used to retrieve the app context, so that `ApodDB` can be accessed using the singleton pattern as well. (Creation of the `ApodDB` singleton instance follows the [_Initialization-on-demand holder idiom_](https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom).)

There is 1 singleton class in the app that isn't already a de facto singleton: `FragmentService` (described in ["Service layer"](#service-layer) above). (The singleton instance of `FragmentService` is also initialized with the [initialization-on-demand holder idiom](https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom).)

### Fluent &amp; functional background task definition &amp; invocation

The Android `AsyncTask` is a powerful mechanism for executing short-lived background tasks, followed by UI updates. However, it does not sufficiently encourage code reuse (in my opinion). For example, there may be several points in app code that need to perform the same database operation, but with different UI updates needed after successful completion in each case. We might implement each variation in its own subclass of `AsyncTask`, but we'd end up duplicating a lot of code that way.

The alternative followed in this app is to define a subclass of `AsyncTask`, `BaseFluentAsyncTask`. It overrides most of the main lifecycle methods of the superclass, to support a listener-based lifecycle. Client code can create an instance of `BaseFluentAsyncTask` itself, and specify all of the processing as implementations (usually in the form of lambdas, if the language level permits it) of `Performer`, `Transformer`, `ProgressListener`, `ResultListener` for a successful outcome, and `ResultListener` for an unsuccessful outcome. I recommend that the core background processing of the task should be specified in the `perform` method of a `BaseFluentAsyncTask` subclass, and the other lifecycle processing should be specified using listeners. (Note that `BaseFluentAsyncTask` splits the `doInBackground` processing into an invocation of `perform`, and an invocation of the registered `Transformer`, if there is one. The purpose of this split is to allow "chaining" of different `Transformer` implementations to the core operation of `perform`, on the same background thread.)

Each of the methods for attaching a listener at a given point in the lifecycle returns the instance of `BaseFluentAsyncTask` (or a subclass), thus encouraging a fluent style.

For an example, consider the case of loading an APOD object for a given date. Normally, we would have one `AsyncTask` subclass for attempting to load the `Apod` instance from the database, then another for making a request to the NASA APOD web service, if necessary. In the latter case, we'd also need to turn around and store the retrieved `Apod` instance in the database, possibly with yet another `AsyncTask` subclass. Alternatively, we could combine all of these operations into a single `AsyncTask` subclass. From experience, both of these approaches lead to a need for "choreography" code that is excessively complicated, hard to read, and hard to maintain.

Instead, we might define 3 `BaseFluentAsyncTask` subclasses, and choreograph their actions more directly, in a fluent style. For example, the following client code (from the `loadApod` method of `edu.cnm.deepdive.nasaapod.controller.ImageFragment`) implements the above logic, consuming classes defined in the `edu.cnm.deepdive.nasaapod.service` package, as well as invoking other methods in `edu.cnm.deepdive.nasaapod.controller.ImageFragment` and `edu.cnm.deepdive.nasaapod.controller.HistoryFragment`.

```java
new SelectApodTask()
    .setTransformer((apod) -> {
      saveIfNeeded(apod);
      return apod;
    })
    .setSuccessListener(this::setApod)
    .setFailureListener((nullApod) -> {
      new GetFromNasaTask()
          .setTransformer((apod) -> {
            new InsertApodTask().execute(apod);
            saveIfNeeded(apod);
            return apod;
          })
          .setSuccessListener((apod) -> {
            historyFragment.refresh();
            setApod(apod);
          })
          .setFailureListener((anotherNullApod) -> showFailure())
          .execute(date);
    })
    .execute(date);
```

For more information, see the [`BaseFluentAsyncTask` Javadoc documentation](docs/api/edu/cnm/deepdive/nasaapod/service/BaseFluentAsyncTask.html).

## Javadoc

[**Javadoc**](docs/api)

The Javadoc HTML for this project has been generated on Windows with the following command line arguments. (Note that all arguments are specified on a single line for execution, but they're written on multiple lines for clarity here. Multiple options can also be specified in an options file, using the `@files` option.)

```
-bootclasspath "C:\Program Files\Java\jdk1.8.0_202\jre\lib\rt.jar";C:\android\sdk\platforms\android-21\android.jar 
-link https://docs.oracle.com/javase/8/docs/api/ 
-link https://google.github.io/gson/apidocs/ 
-link http://square.github.io/retrofit/2.x/converter-gson/ 
-link http://square.github.io/retrofit/2.x/retrofit/ 
-link https://square.github.io/okhttp/3.x/okhttp/ 
-linkoffline https://developer.android.com/reference C:\android\sdk\docs\reference 
-windowtitle "NASA APOD service client"
```

### Explanation of options

(There are many options that can be used with the Javadoc command. These are just the options used in this case.)

* The value of the `-bootclasspath` option must give the locations of the Java 8 JRE `rt.jar` and the `android.jar` for the minimum Android API level of the project. For Unix-based operating systems (e.g. OS X, Linux), these components will be separated by the colon character, rather than the semi-colon. (Note: Be sure to use the correct paths for your version/location of the Java JRE and the Android SDK.)

* The multiple `-link` options specify Javadoc URLs for the Java SDK and 3<sup>rd</sup>-party libraries.

* The `-linkoffline` option is used to specify the URL of the Android API reference and the (local) location of the Android SDK package list; these 2 values are separated by a space. (Note: For the second part, be sure to use the correct path for your version/location of the Android SDK.)

* `-windowtitle` specifies the title (appearing in the browser tab) of the generated HTML documents.