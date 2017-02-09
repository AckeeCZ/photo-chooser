# Android Image Photo Chooser
## Purpose of library

This library allows to choose pictures from system gallery or from camera, save to local cache and use them in your application. Optional cropping to square photos is available.

## Usage

### Android Manifest
Its important to add this to your manifest. Library uses `FileProvider` and its specification needs to be in your `AndroidManifest.xml`

```java
 <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.choose_photo"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider_path"/>
        </provider>
```

And that should be it. Do not change anything here, please.

### Java
Core class of library is `ChoosePhotoHelper`. First you must create instance of this helper
```java
     choosePhotoHelper = new ChoosePhotoHelper(this, new ChoosePhotoHelper.OnPhotoPickedListener() {
             @Override
             public void onPhotoPicked(Observable<File> fileObservable) {
                //listener called when photo is available
             }
         }, new ChoosePhotoHelper.OnPhotoCopyingListener() {
             @Override
             public void photoCopying(boolean isCopying) {
                // called when copying of photo is in progress.
                // When picture is in some remote location (google drive), downloading and copying can take some time
             }
         });
```

When you want to show dialog with camera/gallery options, you call
```java
choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID).show(getSupportFragmentManager());
```
Its importat to pass application id (package name) because it needs to match with the one in manifest.


The crop funcionality is disabled by default, you can enable it by passing attribute to `getChoosePhotoDialogBuilder` like
```java
choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID, true).show(getSupportFragmentManager());
```

Crop screen has Done button, that is tinted with colorPrimary from your theme. If you want to pass custom color, there is another overloaded method `getChoosePhotoDialogBuilder`

```java
choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID, true, Color.BLUE).show(getSupportFragmentManager());
```

If you have UI that requires to call directly camera/gallery without prompt dialog, you can call
```java
choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID, true).showCamera(getActivity());
choosePhotoHelper.getChoosePhotoDialogBuilder(BuildConfig.APPLICATION_ID, true).showGallery(getActivity());
```
but you need to handle permissions request for yourself.

The `ChoosePhotoDialogBuilder` that is returned by `getChoosePhotoDialogBuilder` accepts several properties for settings texts in dialog and even custom file name for photos. This is useful if you need to pick multiple files and not just a single one.

Usage with this properties

## Sample

Sample app with choosing profile picture can be found in `app` module. All mentioned methods for picking images are available.

## Dependencies

Use
```groovy
compile "cz.ackee:photo-chooser:1.0.1"
```
to add it tou your project.

This library is in development and requires javadoc, tests and cleaning.