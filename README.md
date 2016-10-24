This library allows to add pictures from system gallery or from camera, save to local cache and use in your application.

Use
```groovy
compile "cz.ackee:photo-chooser:0.0.2"
```
to add it tou your project.

This library is in development and requires javadoc, tests and cleaning.

For now the library creates images names with timestamps. In this version, the programmer should handle cleaning by himself. Just call `clear()' method when you don't need this temp photos anymore.