[![Release](https://jitpack.io/v/ashLikun/AnimationMenu.svg)](https://jitpack.io/#ashLikun/Commonanimmenu)


# **AnimationMenu**
动画菜单viewgroup
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
     implementation 'com.github.ashLikun.AnimationMenu:{latest version}'//没有databind
}
```
### 1.用法
```xml
 <com.ashlikun.animmenu.AnimMenu
        android:id="@+id/animMenu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:am_autoOpen="false"
        app:am_badgeColor="#ff5688ff"
        app:am_buttonNormalColor="#ff999999"
        app:am_buttonPressColor="#ff999999"
        app:am_icon="@drawable/close"></com.ashlikun.animmenu.AnimMenu>
```
```java
  animMenu.addView(animMenu.getDefaultItem()
                .strokeWidth(3)
                .strokeColor(Color.BLACK)
                .iconId(R.drawable.add));
        animMenu.addView(animMenu.getDefaultItem()
                .strokeWidth(3)
                .strokeColor(Color.BLACK)
                .badge("1")
                .iconId(R.drawable.like));

        animMenu.setItemClickListener(new OnMenuItemClickListener() {


            @Override
            public void onItemClick(int index, String tag) {

            }
        });
```

### 混肴


