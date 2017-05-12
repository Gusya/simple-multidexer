# Simple Multidexer
Add DEX files into application and use them immediately

## Install

Gradle:
```
compile 'com.gusya.mv:simple-multidexer:1.0.0'
```

Code:
Copy `SimpleAdId.java` from library module into your project or build this project as jar\aar file.

## Usage

Simply invoke static `SimpleAdId.getAdInfo` method while supplying application context and a listener object.
Be aware that listener's methods will be invoked on UI thread.

```java
SimpleAdId.getAdInfo(getApplicationContext(), new SimpleAdId.SimpleAdListener() {

            @Override
            public void onSuccess(SimpleAdId.AdIdInfo info) {
		String adId = info.getAdId();
		boolean adTrackingEnabled = info.isAdTrackingEnabled();	
            }

            @Override
            public void onException(Exception exception) {
		Log.e("SimpleAdId", exception.getMessage());
		exception.printStackTrace();	
            }
        })
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
