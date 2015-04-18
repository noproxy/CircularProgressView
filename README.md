#CircularProgressView

A custom progress view/button for Android. It combines button and progress view. You can custom button image resource by XML.

---

**Video**: https://youtu.be/iPLf9Gc7b_Y

![](https://github.com/a642500/CircularProgressView/blob/master/sample.gif)


## Usage:

### XML:

All these tag is optional. You needn't keep ratio of width and height. It use the min value of width and height as the circular progress's diameter.

```XML
    <me.toxz.circularprogressview.library.CircularProgressView
            android:id="@+id/circularProgressView"
            android:layout_width="64dp"
            android:layout_height="64dp"
            android:layout_centerInParent="true"
            app:startDrawableMargins="0dp"
            app:progressDrawableMargins="0dp"
            app:endDrawableMargins="0dp"
            app:strokeSize="3dp"
            app:strokeColor="@android:color/white"
            app:startDrawable="@android:drawable/ic_media_play"
            app:progressDrawable="@android:drawable/ic_media_pause"
            app:endDrawable="@android:drawable/ic_menu_share"
            />

```

### Java code:

You should not to use ```CircularProgressView.setOnClickListener()```, but use ```CircularProgressView.setOnStateListener()``` instead.

```Java

CircularProgressView circularProgressView = (CircularProgressView) findViewById(R.id.circularProgressView);
circularProgressView.setOnStateListener(new CircularProgressView.OnStatusListener() {
            @Override
            public void onStatus(CircularProgressView.Status status) {
                switch (status) {
                    case START:
                        //This mean view was clicked before progress view displays, and you should start do some thing cost time there.
                        break;
                    case PROGRESS:
                        //view was clicked when progress is updating. In most cases, you needn't to handle this status.
                        break;
                    case END:
                        //view was clicked after progress has reached 100%. You can reset view here.
                        break;
                }
            }
        });

```

Call ```CircularProgressView.reset()``` to reset this view to origin state or call ```CircularProgressView.resetSmoothly()``` to reset with animation.

This view doesn't support indeterminate mode, you must call ```CircularProgressView.setProgress(int progress)``` to update progress, or call ```CircularProgressView.setDuration(long millis)``` to set progress duration.

By default, once it's clicked it will start animation from beginDrawable to progressDrawable. If you want to start animation manually, you can set ```app:cpv_startAnimationAuto="false"``` in xml or call ```setStartAnimationAuto(boolean)```
 in java code.And then you can call ```startAnimation()``` at any time you want.
 
 And you can also set ```app:cpv_clickAnimationDelayMillis="100"``` or call ```setClickedAnimationDelay(int)``` to set start animation delay time.
 
## Import to your project:

Gradle:

```gradle
buildscript {
    repositories {
        jcenter()
    }
}


dependencies {
    compile 'me.toxz.circularprogressview:library:1.0.3'
}
```
