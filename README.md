## WaveView波浪进度波形图
最近在做项目时需要实现这样一种动画，类似于波浪形的进度动画，粗略的看了一下，发现好像类似于正余弦曲线实现的，但是Android 没有相关的API，所以需要我们动手画出来，所以现在在此记录一下学习过程，方便总结、学习。

### 效果图
![这里写图片描述](http://img.blog.csdn.net/20161222144547445?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


### 代码地址
关于实现的具体代码可以请移步[ github: excellentWaveView](https://github.com/crazyandcoder/WaveView)

### 使用方法
gradl依赖

```
compile 'liji.library.dev:excellentwaveview:0.4.0'
```

自定义的方法属性有;

```
xmlns:app="http://schemas.android.com/apk/res-auto"

//进度条的文字颜色
app:wave_progress_text_color="#ffffff"
//是否显示进度条，默认不显示
app:wave_progress_text_show="true"
//波浪波形的填充颜色
app:wave_wavecolor="@color/red" 



```

### 实现原理

在讲解实现这个波浪动画的原理之前，我们需要一些前提知识，防止到时候看的云里来雾里去的。嘿嘿~

#### **前提知识**
**1、关于自定义view的一些基本用法，例如canvas、Paint的用法等，可以自寻相关文章进行学习。**

**2、Path的用法，也就是路径的意思，我们这个动画也主要是通过path来完成的。**

接下来我们简单的介绍一下关于path的用法。详细文章请参考：[https://github.com/GcsSloop/AndroidNote](https://github.com/GcsSloop/AndroidNote)

 前面我们知道了波浪图形的样子，那么是怎么画出来的呢？
![wave1.gif](http://upload-images.jianshu.io/upload_images/676457-4fa912d2c421ea81.gif?imageMogr2/auto-orient/strip) ![wave2.gif](http://upload-images.jianshu.io/upload_images/676457-16f5e189ffc84536.gif?imageMogr2/auto-orient/strip) ![wave3.gif](http://upload-images.jianshu.io/upload_images/676457-92e0f876d827c036.gif?imageMogr2/auto-orient/strip)

我们实现这个波浪图呢主要运用二阶贝塞尔曲线来完成，那么什么是贝塞尔曲线呢？有二阶，是不是还有三阶、四阶。。。。呢？想知道答案请继续往下看。

**1、一阶贝塞尔曲线**

![一阶贝塞尔曲线示例图](https://camo.githubusercontent.com/71496535e042ea905274315ddafda0defd1de8e1/68747470733a2f2f75706c6f61642e77696b696d656469612e6f72672f77696b6970656469612f636f6d6d6f6e732f302f30302f422543332541397a6965725f315f6269672e676966)

>  其实一阶贝塞尔曲线就是Path方法中的lineTo方法。

**2、二阶贝塞尔曲线**

![二阶贝塞尔曲线示例图](https://camo.githubusercontent.com/e42e6067ba0955631e3207a6fbd4b24f4a26132f/68747470733a2f2f75706c6f61642e77696b696d656469612e6f72672f77696b6970656469612f636f6d6d6f6e732f332f33642f422543332541397a6965725f325f6269672e676966)

> 二阶曲线对应的方法是quadTo

**3、三阶贝塞尔曲线**

![三阶贝塞尔曲线示例图](https://camo.githubusercontent.com/e8db61bc38f255f4d1b701126f6920688c8e5de4/68747470733a2f2f75706c6f61642e77696b696d656469612e6f72672f77696b6970656469612f636f6d6d6f6e732f642f64622f422543332541397a6965725f335f6269672e676966)

> 三阶曲线对应的方法是cubicTo

下面还有4、5、6、7阶的贝塞尔曲线。。。。
那么看完二阶的贝塞尔曲线示例图，是不是感觉跟我们实现的波浪图很相似？答案是正确的，我们就是用二阶的贝塞尔曲线完成的，下面开始进入波浪图的实现原理讲解过程。

首先在View上根据View宽计算可以容纳几个完整波形，不够一个的算一个，然后在View的不可见处预留一个完整的波形；然后波动开始的时候将所有点同时在x方向上移动相同的距离，这样隐藏的波形就会被平移出来，当平移距离达到一个波长时，这时候将所有点的x坐标又恢复到平移前的值，这样就可以一个波形一个波形地往外传输。用草图表示如下：
![](http://img.blog.csdn.net/20161222152831938?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


介绍一个重要知识点，贝塞尔曲线的控制点的概念。
 ![](http://img.blog.csdn.net/20161222153034926?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbGlqaV94Yw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

对于二阶的贝塞尔曲线的控制点也就是正弦曲线的峰点，从上图可以很容看出来，控制点为：**P[2n+1]，n>=0都是贝塞尔曲线的控制点。**，有了控制点和数据点，我们就可以按照path的二阶贝塞尔曲线的方法进行绘制贝塞尔曲线了。

**第一步、设置贝塞尔曲线的数据点**
我们首先设置一种数据结构进行存储贝塞尔曲线的数据点和控制点的信息。

```
 class Point {
        private float x;

        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }
```

其次呢，在设置初始的数据点和控制点。

```
// 水位线从最底下开始上升
                mLevelLine = mViewHeight;
                {//计算水位线
                    mLevelLine = mLevelLine * progress / 100;
                    if (mLevelLine < 0)
                        mLevelLine = 0;
                }
                // 根据View宽度计算波形峰值
                mWaveHeight = mViewWidth / 20f;
                // 波长等于四倍View宽度也就是View中只能看到四分之一个波形，这样可以使起伏更明显
                mWaveWidth = mViewWidth;
                // 左边隐藏的距离预留一个波形
                mLeftSide = -mWaveWidth;
                // 这里计算在可见的View宽度中能容纳几个波形，注意n上取整
                int n = (int) Math.round(mViewWidth / mWaveWidth + 0.5);
                // n个波形需要4n+1个点，但是我们要预留一个波形在左边隐藏区域，所以需要4n+5个点
                for (int i = 0; i < (4 * n + 5); i++) {
                    // 从P0开始初始化到P4n+4，总共4n+5个点
                    float x = i * mWaveWidth / 4 - mWaveWidth;
                    float y = 0;
                    switch (i % 4) {
                        case 0:
                        case 2:
                            // 零点位于水位线上
                            y = mLevelLine;
                            break;
                        case 1:
                            // 往下波动的控制点
                            y = mLevelLine + mWaveHeight;
                            break;
                        case 3:
                            // 往上波动的控制点
                            y = mLevelLine - mWaveHeight;
                            break;
                    }
                    mPointsList.add(new Point(x, y));
                 }
```

**第二步，设置进度**

```
     /**
     * 设置进度
     **/
    public void setProgress(int chargePercent) {
        if (chargePercent < 0 || chargePercent > 100) {
            progress = 100;
        } else {
            // 水位百分比,progress越大水位线越低
            progress = 100 - (chargePercent);
        }
        mLevelLine = mViewHeight;
        mLevelLine = mLevelLine * progress / 100;
        if (mLevelLine < 0)
            mLevelLine = 0;
    }

```

**第三步，进行绘制贝塞尔曲线**

```
 int i = 0;
        mWavePath.moveTo(mPointsList.get(0).getX(), mPointsList.get(0).getY());
        for (; i < mPointsList.size() - 2; i = i + 2) {
            mWavePath.quadTo(mPointsList.get(i + 1).getX(), mPointsList.get(i + 1).getY(),
                    mPointsList.get(i + 2).getX(), mPointsList.get(i + 2).getY());
        }
        mWavePath.lineTo(mPointsList.get(i).getX(), mViewHeight);
        mWavePath.lineTo(mLeftSide, mViewHeight);
        mWavePath.close();

        // mPaint的Style是FILL，会填充整个Path区域
        canvas.drawPath(mWavePath, mPaint);

```
通过以上几个步骤，我们就可以绘出一个简单的波浪图了，具体详细的代码可参考github:

#### **github地址为：**
[https://github.com/crazyandcoder/WaveView](https://github.com/crazyandcoder/WaveView)


----------

### 关于我

简书 http://www.jianshu.com/users/18281bdb07ce/latest_articles

博客 http://crazyandcoder.github.io/

github https://github.com/crazyandcoder


### 参考资料
[1、http://blog.csdn.net/zhongkejingwang/article/details/38556891](http://blog.csdn.net/zhongkejingwang/article/details/38556891)

[2、https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B06%5DPath_Bezier.md](https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B06%5DPath_Bezier.md)

