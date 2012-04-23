Magic Text View - Bringing the magic of Word Art to Android
======

Featuring:

* **Crisp Outer Shadows** as many as you like!
* **Inner Shdows** again, hundreds if you so desire
* **Outlines** just one for now, add more and send a pull request for extra credit
* **Text Backgrounds** "Fire" made of fire!


If anybody figures out how to do text paths, please let me know!


Installation
===

If you like you can import the project into eclipse, and use it as a library project
[This is a decent tutorial on using library projects](http://www.vogella.com/blog/2011/03/03/android-library-projects/)

Realistically, just copy MagicTextView.java & attrs.xml into your project and use them as your own.


Usage
===

From Xml:

    <com.qwerjk.better_text.MagicTextView
        xmlns:qwerjk="http://schemas.android.com/apk/res/com.qwerjk.better_text"
        android:textSize="78dp"
        android:textColor="#ff333333"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:drawableLeft="@android:drawable/btn_star"
        android:textStyle="bold"
        android:padding="10dp"
        qwerjk:foreground="@drawable/fake_luxury_tiled"
        qwerjk:innerShadowDy="2"
        qwerjk:innerShadowColor="#FF000000"
        qwerjk:innerShadowRadius="1"
        qwerjk:outerShadowDy="3"
        qwerjk:outerShadowColor="#FF0088ff"
        qwerjk:outerShadowRadius="10"
        qwerjk:strokeColor="#FFff0000"
        qwerjk:strokeJoinStyle="miter"
        qwerjk:strokeWidth="5"
        android:text="Magic" />

From Java:

    view = new MagicTextView(context);
    view.addInnerShadow(0, -1, 0, 0xFFffffff);
    view.addOuterShadow(0, -1, 0, 0xff000000);
    view.setStroke(4, 0xFFff0000);
    view.setForegroundDrawable(getResources().getDrawable(R.drawable.fake_luxury_tiled);

