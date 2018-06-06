# MicrosoftFaceSwiping

[![](https://jitpack.io/v/HaoHaoP/Microsoft-Face-Swiping.svg)](https://jitpack.io/#HaoHaoP/Microsoft-Face-Swiping)

An Android project using Microsoft Face API to swiping face

## Gradle

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```gradle
dependencies {
    implementation 'com.github.HaoHaoP:Microsoft-Face-Swiping:0.0.1'
}
```

## Use

```java
/**
 * FaceSwiping
 *
 * @param context     context
 * @param surfaceView surfaceView
 * @param url         https://[location].api.cognitive.microsoft.com/face/v1.0/
 * @param key         Subscription key which provides access to Face API.
 * @param name        compare GroupName
 */
```

```java
faceSwiping = new FaceSwiping(this, surfaceView, baseUrl, key, groupName)
    .setConfidence(0.8)
    .setLoopTime(2000)
    .setTimes(10)
    .setFaceCallBack(new FaceSwiping.FaceCallBack() {

        @Override
        public void onError(int error) {
            switch (error) {
                case TIME_OUT:
                    Toast.makeText(FaceSwipingActivity.this, "TimeOut", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case CALL_BACK_ERROR:
                break;
                case VALUE_ERROR:
                    break;
            }
        }

        @Override
        public void onResponse(byte[] img, String name, String userData) {
            Intent intent = new Intent();
            intent.putExtra("img", img);
            setResult(RESULT_OK, intent);
            finish();
        }
    }).start();
```

```java
@Override
protected void onRestart() {
    super.onRestart();
    faceSwiping.start();
}
```

```java
@Override
protected void onPause() {
    super.onPause();
    faceSwiping.release();
}
```

## License

MicrosoftFaceSwiping is released under the [MIT License](LICENSE).

```
MIT License

Copyright (c) 2018 皓皓P

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