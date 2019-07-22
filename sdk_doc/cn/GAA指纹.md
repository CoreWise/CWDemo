# GAA大指纹


* [1.GAA大指纹开发包说明](#GAA大指纹开发包说明)
* [2.二次开发说明](#二次开发说明)
  * [2.1 AndroidStudio工程配置说明](#AndroidStudio工程配置说明)
  * [2.2 AndroidManifest.xml配置说明](#AndroidManifest配置说明)
  * [2.3 接口说明](#接口说明)
  * [2.4 接口调用流程](#接口调用流程)
  * [2.5 接口调用案例](#接口调用案例)
* [3.二次开发问题汇总](#二次开发问题汇总)

### GAA大指纹开发包说明

   1.1 支持GAA大指纹模块;

   1.2 GAA大指纹功能占用了机器唯一的USB口

   1.3 GAA大指纹开发包兼容机器请查看: [GAA大指纹开发包兼容机器说明](https://github.com/CoreWise/CWDemo#user-content-zh)

   1.4 [GAA大指纹开发包下载地址](https://github.com/CoreWise/CWDemo#user-content-zh)

### 二次开发说明

   由于本机器的高通CPU只支持一个USB口,所以在使用GAA大指纹模块时，需要先调用USB管理类将USB切换到指纹模组,此时USB正常情况下不能用来充电、数据线通信等操作。
   在这样的情况下，USB数据线调试不能使用，建议网络adb调试;

   网络adb调试推荐Android Studio安装Android Wifi ADB插件;
#### AndroidStudio工程配置说明

- 1.添加开发包aar到项目libs目录下

- 2.配置Moudle的build.gradle,参考如下:


```
...
 android {
     ...
     defaultConfig {
         ...
         targetSdkVersion 22 //身份证功能必须降22，其他无所谓
         ...
     }
     ...
 }
 //2.必须2
 repositories {
     flatDir {
         dirs 'libs'   // aar目录
     }
 }

 dependencies {
     ...
    //GAA大指纹开发包(新固件开发包)
    //GAA FingerPrint SDK
    compile(name: 'fp_gaa_sdk_20190429', ext: 'aar')

 }

```

#### AndroidManifest配置说明


```xml

<uses-feature android:name="android.hardware.usb.host"
android:required="true" />

<uses-permission
android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

#### 接口说明


**USB管理类**

| USBFingerManager API接口 | 接口说明 |
| :----- | :---- |
| USBFingerManager | USB管理类,单例模式 |
| USBFingerManager.getInstance(this) | 获取USB管理类的实例，单例模式 |
| USBFingerManager.getInstance(this).setDelayMs(ms) | 设置切到指纹模组后延时，默认1000ms |
| USBFingerManager.getInstance(this).openUSB(OnUSBFingerListener()) | 将USB切到指纹模块 |
| USBFingerManager.getInstance(this).closeUSB(); | 将USB切到正常模式 |

OnUSBFingerListener回调接口说明:
- onOpenUSBFingerSuccess(String device):

    切换USB到指纹模组成功，返回当前指纹模组的名称

- onOpenUSBFingerFailure(String error):

    切换USB到指纹模组失败，返回错误码



---

**GAA大指纹类**

常量

```
    public static final int LIVESCAN_SUCCESS = 1;//操作成功
    public static final int LIVESCAN_NOTINIT = -12;//没有初始化
    public static final int LIVESCAN_AUTH_FAILED = -13;//认证模块校验失败
    public static final int LIVESCAN_AUTH_ENCRYPT_FAILED = -14;//加密算法失败
    public static final int LIVESCAN_UPIMAGE_FAILED = -15;//上传图片失败
    public static final int LIVESCAN_NO_FINGER = -16;//没有手指
    public static final int LIVESCAN_SEARCH_THRESHOLD = -17;//搜索相似度低于阀值
    public static final int LIVESCAN_CLEAR_ERROR = -18;//删除错误
    public static final int LIVESCAN_ERROR_LENGTH = -19;//入参长度不足
    public static final int LIVESCAN_NOTADD = -20;//添加指纹特征值失败

    public static final int LIVESCAN_EINVAL = -1;
    public static final int LIVESCAN_EMEMORY = -2;
    public static final int LIVESCAN_EFUN = -3;
    public static final int LIVESCAN_EDEVICE = -4;
    public static final int LIVESCAN_EINIT = -5;
    public static final int LIVESCAN_EUNKOWN = -6;
    public static final int LIVESCAN_EELSE = -9;
    public static final int LIVESCAN_SUCCESS = 1;
    public static final int LIVESCAN_NONE = 0;
    public static int LIVESCAN_FEATURE_SIZE = 512;

```


| GAA GaaApiBase API接口 | 接口说明 |
| :----- | :---- |
|GaaFingerFactory().createGAAFinger(String,Context)| 构造函数，返回GaaApiBase，GaaApiBase.ZiDevice新固件，GaaApiBase.BHMDevice老固件 |
|openGAA|打开模组|
|closeGAA|关闭模组|
|PSGetImage|GAA采集指纹图片,并保存在本地|
|PSUpImage |将GAA保存本地指纹图片上传到view层|
|PSGenChar |提取特征值,保存特征值|
|PSSearch|搜索指纹|
|PSEmpty|清空指纹库|
|DataToBmp|指纹图片byte转bitmap|

具体说明:

- public GaaApiBase createGAAFinger(String gaaSelect, Context context)
    ```
    构造函数

    String gaaSelect 选择初始化GaaApiBase具体Gaa指纹
    GaaApiBase.ZiDevice新固件 | GaaApiBase.BHMDevice老固件
    ```

- public int openGAA()
    ```
    打开模组
    成功返回:LIVESCAN_SUCCESS
    ```

- public int closeGAA()
    ```
    关闭模组
    ```

- public int PSGetImage()
    ```
    GAA模块开始采集指纹图片
    并保存在本地
    ```
- public int PSUpImage(byte[] pImageData)
    ```
    GAA模组将保存本地指纹图片上传到app

    byte[] pImageData 传入大小需要大于 256 * 360,返回指纹图片
    ```
- public int PSGenChar(byte[] mFeature,int[] id)
    ```
    提取本地保存指纹图片的特征值,保存特征值,添加指纹库

    byte[] mFeature 传入大小需要大于 512,返回生成的特征值
    int[] id 返回特征值对应的id
    ```

- public int PSGenChar(byte[] mFeature)
    ```
    提取本地保存指纹图片的特征值,不保存特征值

    byte[] mFeature 传入大小需要大于 512,返回保存指纹图片的特征值
    ```

- public int PSSearch(byte[] mFeature, int[] mId)
    ```
    搜索指纹

    byte[] mFeature 需要搜索的指纹特征值
    int[] mId 返回的指纹索引
    成功返回:LIVESCAN_SUCCESS
    ```

- public int PSEmpty()
    ```
    清空指纹库
    ```
- public Bitmap DataToBmp(byte[] fpRaw)
    ```
   指纹图片 byte转bitmap

   byte[] fpRaw 指纹图片
    ```

**错误码**

```
    public static final int LIVESCAN_NOTINIT = -12;//没有初始化
    public static final int LIVESCAN_AUTH_FAILED = -13;//认证模块校验失败
    public static final int LIVESCAN_AUTH_ENCRYPT_FAILED = -14;//加密算法失败
    public static final int LIVESCAN_UPIMAGE_FAILED = -15;//上传图片失败
    public static final int LIVESCAN_NO_FINGER = -16;//没有手指
    public static final int LIVESCAN_SEARCH_THRESHOLD = -17;//搜索相似度低于阀值
    public static final int LIVESCAN_CLEAR_ERROR = -18;//删除错误
    public static final int LIVESCAN_ERROR_LENGTH = -19;//入参长度不足
    public static final int LIVESCAN_NOTADD = -20;//添加指纹特征值失败
```


#### 接口调用流程

- ##### 打开并初始化指纹模块

![bigfingerusb.png](https://i.loli.net/2019/05/08/5cd24e0add367.png)


- ##### 采集指纹图像

![bigfingerimage.png](https://i.loli.net/2019/05/08/5cd24e0aa5a02.png)





- ##### 采集指纹

![bigfingerluru.png](https://i.loli.net/2019/05/08/5cd24e0ae5f66.png)

- ##### 1:1比对

![bigfinger11.png](https://i.loli.net/2019/05/08/5cd24e0ac2ecb.png)



- ##### 1:N比对

![bigfinger1n.png](https://i.loli.net/2019/05/08/5cd24e0a9c803.png)


#### 接口调用案例

参考Demo源码,NewFingerGAABigActivity.java



#### 二次开发问题汇总


- 指纹类的应用在横竖屏切换时,如果重复初始化模块导致出现的一系列问题？

    ```
    在清单文件里相应的Activity标签下添加一下内容:
    android:configChanges="orientation|keyboardHidden|screenSize"


    ```