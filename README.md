# Simple Multidexer
Add DEX files into application and use them immediately

## Install

Gradle:
```
compile 'com.gusya.mv:simple-multidexer:1.0.0'
```

Code:
build simple-multidexer module as jar/aar dependency

## Usage

### Prepare list of DEX files

Define a String array that points to DEX files. DEX files also can be packed into JAR\APK files.
SimpleMultidexer searches for provided files in some predefined locations:
* assets folder
* app's private 'files' directory
* app's private 'cache' directory
* by absolute path
* by package name (you can use some package name and if it is installed on the same device, you'll get it's classes)

For example:
```java
String[] dexes = {
                /*from assets*/sampleJar,
                /*from getFilesDir()*/"from-files-dir.jar",
                /*from getCacheDir()*/"from-cache-dir.jar",
                /*absolute path*/st4AbsolutePath,
                /*from package name*/"com.android.calculator2"};
```

### Call DEX loading synchronously

Simply invoke static `SimpleMultidexer.loadDex(Activity, String[])`
All DEX files will be loaded on Activity's thread.

```java
SimpleMultiDexer.loadDex(this, dexes);
```

### or asynchronously

Simply invoke static `SimpleMultidexer.loadDexAsync(Context, String[], SimpleMultidexer.Callback)`
All DEX files will be loaded on a separate thread and you will receive callbacks with results.

```java
SimpleMultiDexer.loadDexAsync(getApplicationContext(), dexes,
                new SimpleMultiDexer.Callback() {

                    @Override
                    public void onLoad(String dexFile) {
                        // dexFile was successfully loaded
                    }

                    @Override
                    public void onLoadAll() {
                        // dex files loading has finished
                    }

                    @Override
                    public void onException(Exception exception, String dexFile) {
			// exception occured while loading dexFile
                    }
                });
```

# License
```
MIT License

Copyright (c) 2017 Gusya

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
