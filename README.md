[ ![Download](https://api.bintray.com/packages/ackeecz/photo-chooser/photo-chooser/images/download.svg) ](https://bintray.com/ackeecz/photo-chooser/photo-chooser/_latestVersion)

# Android Image Photo Chooser
## Purpose of library

This library allows to choose pictures from system gallery or from camera, save to local cache and use them in your application. Optional cropping to square photos is available.

Based on RxJava2
## Usage
Core class of library is `ChoosePhotoHelper`. First you must create instance of this helper
```kotlin
val choosePhotoHelper = ChoosePhotoHelper(this,
    object : ChoosePhotoHelper.OnPhotoPickedListener() {
        override fun onPhotoPicked(fileObservable: Observable<File>) {
            //listener called when photo is available
        }
    },
    object : ChoosePhotoHelper.OnPhotoCopyingListener() {
    
        override fun photoCopying(isCopying: Boolean) {
            // called when copying of photo is in progress.
            // When picture is in some remote location (i.e. google drive), downloading and copying can take some time
        }
    })
```

When you want to show dialog with camera/gallery options, you call
```kotlin
choosePhotoHelper.getChoosePhotoDialogBuilder().show(getSupportFragmentManager())
```
It is important to pass application id (package name) because it needs to match with the one in manifest.


The crop functionality is disabled by default, you can enable it by passing attribute to `getChoosePhotoDialogBuilder` like
```kotlin
choosePhotoHelper.getChoosePhotoDialogBuilder(true).show(getSupportFragmentManager())
```

Crop screen has Done button, that is tinted with colorPrimary from your theme. If you want to pass custom color, there is another overloaded method `getChoosePhotoDialogBuilder`

```kotlin
choosePhotoHelper.getChoosePhotoDialogBuilder(true, Color.BLUE).show(getSupportFragmentManager())
```

If you have UI that requires to call directly camera/gallery without prompt dialog, you can call
```kotlin
choosePhotoHelper.getChoosePhotoDialogBuilder(true).showCamera(getActivity())
choosePhotoHelper.getChoosePhotoDialogBuilder(true).showGallery(getActivity())
```
but you need to handle permissions request for yourself.

The `ChoosePhotoDialogBuilder` that is returned by `getChoosePhotoDialogBuilder` accepts several properties for settings texts in dialog and even custom file name for photos. This is useful if you need to pick multiple files and not just a single one.

Usage with this properties

## Sample

Sample app with choosing profile picture can be found in `app` module. All mentioned methods for picking images are available.

## Dependencies

Use
```groovy
compile "cz.ackee.photochooser:photo-chooser:x.x.x"
```
to add it tou your project.

## License
Copyright 2018 Ackee, s.r.o.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
