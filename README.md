详情请参见：http://blog.csdn.net/anydrew/article/details/50969574

## 概述

本文主要讲解如何在 Android 下实现高仿 iOS 的开关按钮，并非是在 Android 自带的 ToggleButton 上修改，而是使用 API 提供的 onDraw、onMeasure、Canvas 方法，纯手工绘制。基本原理就是在 Canvas 上叠着放两张图片，上面的图片根据手指触摸情况，不断移动，实现开关效果。

本文示例代码：https://github.com/heshiweij/EasySwitchButton

效果图：

![这里写图片描述](http://img.blog.csdn.net/20160324121218248)


功能点：

1. 不滑出边界，超过一半自动切换（**边界判断**）

2. 可滑动，也可点击（**事件共存**）

3. 提供状态改变监听（**设置回调**）

3. 通过属性设置初始状态、背景图片、滑动按钮（**自定义属性**）


## 自定义View的概述
Android 在绘制 View 时，其实就像蒙上眼睛在画板上画画，它并不知道应该把 View **画多大**，**画哪儿**，**怎么画**。所以我们必须实现 View 的三个重要方法，以告诉它这些信息。即：**onMeasure**（画多大）,**onLayout**（画哪儿）,**onDraw**（怎么画）。

### View的生命周期

| 未设置点击事件  |  是否监听 ACTION_MOVE | 
| ------------- | ------------- |
|`onFinishedInflate()` |当从布局文件创建时调用，做一些初始化的操作，如创建对象等|  
|`onSizeChanged()`| 当尺寸改变时调用,做一些进一步的初始化，如：处理外部通过 set 设置的属性    |
| `onMeasure()`| 当需要测量时调用，指定 View 的大小 |
| `onLayout()`| 当需要布局时调用，指定 View 的位置 |
| `onDraw()`| 当需要绘制时调用，指定 View 的内容 |	
	

在动手写之前，必须先了解以下几个概念:

1. View 的默认不支持 WRAP_CONTENT，必须重写 onMeasure 方法，通过 setMeasuredDimension() 设置尺寸

2. 基本的事件分发机制：onClickListener 一定是在 onTouchEvent 之后执行

### 自定义View的流程

自定义 View 一般遵循如下流程：

![这里写图片描述](http://img.blog.csdn.net/20160324094155273)


## 开始动手

下面就开始动手来实现了

### 初始化成员

```java
/* 画笔 */
Paint mPaint; 
/* 背景图片 */
Bitmap mSwitchBackground;
/* 滑动图片 */
Bitmap mSlideButton;
/* 最大滑动距离 */
int mMaxLeft;
/* 当前滑动距离 */
int mCurrLeft;
/* 当前状态 */
boolean isOpen = false;
```

```
/* 初始化各种组件 */
private void init(AttributeSet attrs) {
	// 初始化画笔
	mPaint = new Paint();
	mPaint.setColor(Color.BLUE);

	// 初始化背景图片
	mSwitchBackground = BitmapFactory.decodeResource(getResources(),R.drawable.switch_background);

	mSlideButton = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
		
	// 计算最大可滑动距离
	mMaxLeft = mSwitchBackground.getWidth() - mSlideButton.getWidth();
		
	// 设置开关事件
	// 已将点击事件的逻辑，移至 onTouchEvent 的 ACTION_UP 中
}
```

封装设置状态的方法：
```java
/* 设置状态，此方法只改变 mCurrLeft ，不引起重绘*/
private void setStatus(boolean status){
	if (status){
		mCurrLeft = mMaxLeft;
		isOpen = true;
	} else {
		mCurrLeft = 0;
		isOpen = false;
	}
}
```
### 测量并绘制

设置 View 的宽高为背景图的宽高

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// 将 View 的宽高设置为背景图的宽高
	setMeasuredDimension(mSwitchBackground.getWidth(), mSwitchBackground.getHeight());
}
```

测量完成后，需要实现 onDraw ，在 View 提供的 Canvas 画板绘制两个图片。 其中 `mCurrLeft` 是关键，滑动的原理就是不断改变 `mCurrLeft` 的值，调用 `invalidate()` 引起重绘，不断重新执行 onDraw，从而发生位移的改变。

```java
@Override
protected void onDraw(Canvas canvas) {
	canvas.drawBitmap(mSwitchBackground, 0, 0, mPaint);
	canvas.drawBitmap(mSlideButton, mCurrLeft, 0, mPaint);
}
```

### 处理 onTouchEvent

```java
// 开始位置
int startX;
	
/**
 * event.getX() 基于控件本身
 * event.getRawX() 基于整个屏幕
 */
@Override
public boolean onTouchEvent(MotionEvent event) {
	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
		startX = (int) event.getX();
		break;
	case MotionEvent.ACTION_MOVE:
		int distance = (int) (event.getX() - startX);
		mCurrLeft += distance;
		startX = (int) event.getX();
		
		break;
	}
	
	// 边界判断，不让滑块滑出边界
	if (mCurrLeft < 0){
		mCurrLeft = 0;
	}
	
	if (mCurrLeft > mMaxLeft){
		mCurrLeft = mMaxLeft;
	}
	
	// 引起重绘（重新调用 onDraw 方法）
	invalidate();
	return true;
}
```
至此，滑块已经可以做最基本的滑动，基本像样了。

**关于 onTouchEvent 的返回值**

我们发现，当给 onTouchEvent 的返回值设为 false，就不能监听 `ACTION_MOVE` 了。这牵扯到 **View 的事件分发机制**，关于这个内容，我稍后会写一篇文章，详细阐述我的理解。

目前，暂时只需要记住下面这个规则：

| 设置点击事件  |  是否监听 ACTION_MOVE |是否响应点击事件|
| ------------- | ------------- |------------- |
| return true | YES  | NO |
| return false| NO   | NO  |
| return super.onTouchEvent| YES    | YES   |


| 未设置点击事件  |  是否监听 ACTION_MOVE | 是否响应点击事件 |
| ------------- | ------------- |------------- |
| return true |YES    |  不需要|
| return false| NO    |  不需要|
| return super.onTouchEvent| NO    | 不需要|

### 处理状态改变回调

```java
/** 定义接口 */
public interface OnOpenedListener {
	void onChecked(View v, boolean isOpened);
}

/** 定义成员变量 */
private onOpenedListener mOpenedkListener;

/** 提供设置回调的方法 */
public void setOnCheckChangedListener(onOpenedListener checkedkListener) {
	this.mOpenedkListener = checkedkListener;
}
```

接着在状态改变的时刻，添加如下代码即可：

**onTouchEvent 的 ACTION_UP**

```java
//处理回调
if (mOpenedkListener != null){
	mOpenedkListener.onChecked(this, isOpen);
}
```

### 事件共存

本案例中，我们需要实现的效果是，用户既能点击切换，也能滑动切换。但是我们知道，如果设置了点击事件，并且 `onTouchEvent` 返回 `return super.onTouchEvent(envent)`，点击事件必然在 `onTouchEvent` 的 `ACTION_UP` 后执行，由于 `onTouchEvent`  和 `onClickListener` 共用同一个状态，将导致冲突。具体表现是：无法将滑块滑到打开位置（一移动，自动弹回来）。

这时，我们就需要增加一个变量 moveX，记录用户从手指按下到抬起滑过的距离，如果 `moveX <5`，则认为点击，如果 `moveX >= 5`，则认为滑动。

代码如下：

在initView() 中添加点击事件
```java
setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
		if (isClick ){
			setStatus(!isOpen);
			// 引起重绘（重新调用 onDraw 方法）
			invalidate();
		}
		
	}
});
```

```java
booleal isClick;
@Override
public boolean onTouchEvent(MotionEvent event) {
	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
		startX = (int) event.getX();
		break;
	case MotionEvent.ACTION_MOVE:
		int distance = (int) (event.getX() - startX);
		mCurrLeft += distance;
		startX = (int) event.getX();
		
		// 移动的距离必须用绝对值，避免往回滑，moveX反向减小
		moveX += Math.abs(distance);
		break;
	case MotionEvent.ACTION_UP:
		if (moveX >= 5){
			isClick = false;
			// 用户的本意是滑动
			setStatus(mCurrLeft >= mMaxLeft / 2);
			
			//处理回调
			if (mOpenedkListener != null){
				mOpenedkListener.onChecked(this, isOpen);
			}
			
		} else {
			isClick = true;
			// 用户的本意是点击，交给 onClickListener
			
		}
		
		// 完成一次滑动，则 moveX 必须清零
		moveX = 0;
		break;
	} 
	
	// 边界判断
	...
	
	// 引起重绘
	invalidate();
	return true;
}
```

### 自定义属性

为了方便用户在 XML 中设置属性，需要添加自定义属性

**attr.xml**
```xml
<declare-styleable name="SwitchButton">
       <attr name="isOpened" format="boolean" />
       <attr name="slide_button" format="reference" />
       <attr name="switch_background" format="reference" />
</declare-styleable>
```

**命名空间**

```xml
xmlns:ifavor="http://schemas.android.com/apk/res/res-auto"
```

**定义控件**

```xml
 <com.example.customeview.switchbutton.SwitchButton
   android:layout_centerInParent="true"
   android:id="@+id/sb_button"
     
   ifavor:isOpened="true"
   ifavor:slide_button="@drawable/slide_button"
   ifavor:switch_background="@drawable/switch_background"
     
   android:layout_width="wrap_content"
   android:layout_height="wrap_content" />
```


附录：

本文示例代码：https://github.com/heshiweij/EasySwitchButton

图片资源来源：

[SwitchButton 开关按钮 的多种实现方式 （附源码DEMO）](http://blog.csdn.net/vipzjyno1/article/details/23707149)



