# BYD大指纹

### 1. BYD大指纹开发包说明

   1.1 支持BYD大指纹模块;

   1.2 BYD大指纹功能占用了机器唯一的USB口

   1.3 BYD大指纹开发包兼容机器请查看: [BYD大指纹开发包兼容机器说明](https://coding.net/u/CoreWise/p/SDK/git)

   1.4 [BYD大指纹开发包下载地址](https://coding.net/u/CoreWise/p/SDK/git)

### 2. 二次开发说明

   由于本机器的高通CPU只支持一个USB口,所以在使用BYD大指纹模块时，需要先调用USB管理类将USB切换到指纹模组,此时USB正常情况下不能用来充电、数据线通信等操作。
   在这样的情况下，USB数据线调试不能使用，建议网络adb调试;

   网络adb调试推荐Android Studio安装Android Wifi ADB插件;
#### 2.1 Android Studio工程配置说明

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
    //BYD大指纹开发包(新固件开发包)
    //BYD Big FingerPrint SDK
    compile(name: 'finger_byd_big_sdk_20190429', ext: 'aar')

 }

```

#### 2.2 AndroidManifest.xml配置说明


```xml

<uses-feature android:name="android.hardware.usb.host"
android:required="true" />

<uses-permission
android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

#### 2.3  接口说明


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

**BYD大指纹类**

常量

```
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


    public static int LIVESCAN_IMAGE_SCORE_THRESHOLD = 25;
    public static int LIVESCAN_IMAGE_WIDTH = 256;
    public static int LIVESCAN_IMAGE_HEIGHT = 360;
    public static int LIVESCAN_IMAGE_HEADER = 1078;
    public static final byte LIVESCAN_MSG_PERMISSION = 17;
    public static final byte LIVESCAN_MSG_IN = 18;
    public static final byte LIVESCAN_MSG_OUT = 19;
    public static final int LIVESCAN_MSG_KEY = 8213;

    public static String LIVESCAN_S_EINVAL = "Invalid Parameter";
    public static String LIVESCAN_S_MEMORY = "Memory allocation failed";
    public static String LIVESCAN_S_FUN = "Function unrealized";
    public static String LIVESCAN_S_DEVICE = "The device does not exist";
    public static String LIVESCAN_S_INIT = "Device has not been initialized";
    public static String LIVESCAN_S_UNKOWN = "Unkown";
    public static String LIVESCAN_S_ELSE = "Other errors";
    public static String LIVESCAN_S_SUCCESS = "Success";

```


| BYD FingerPrint API接口 | 接口说明 |
| :----- | :---- |
| ID_Fpr | 大指纹API类 |
|LIVESCAN_Init|初始化设备|
|LIVESCAN_Close|关闭设备|
|LIVESCAN_GetFPRawData(byte[]pRawData);|获取指纹图像|
|LIVESCAN_FPRawDataToBmp(byte[] pRawData) |图像转BMP|
|LIVESCAN_GetFPBmpData(byte[]pBmpData) |获取BMP指纹图像|
|LIVESCAN_GetSdkVersion()|SDK版本
|LIVESCAN_GetDevVersion();|设备版本
|LIVESCAN_GetDevSN(byte[]bySN)|设备序列号
|LIVESCAN_GetErrorInfo(int nErrorNo)|获取错误信息
|LIVESCAN_FeatureExtract(byte[] pFeatureData)|特征提取
|LIVESCAN_GetMatchThreshold()|获取匹配分数阀值，注：需在初始化调用
|LIVESCAN_FeatureMatch(byte[] pFeatureData2,byte[] pFeatureData2,float[]pfSimilarity) |指纹匹配(1:1)
|LIVESCAN_GetQualityScore(byte[] pFingerImgBuf,byte[] pnScore)|获取图像质量
|LIVESCAN_FeatureSearch(byte[] jpFeatureData, byte[] jpFeatureDatas, int nFeatureData,int[] index,float[] pfSimilarity); |搜索指纹(1:N)|
|LIVESCAN_Encrypt(byte[]pISVData)|加密

具体说明:

- public int LIVESCAN_Init();

- public int LIVESCAN_Close ();

- public int LIVESCAN_GetFPRawData(byte[]pRawData);
    ```
    byte[]pRawData：内存为 LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
    返回值参考常量
    ```

- public  Bitmap LIVESCAN_FPRawDataToBmp(byte[] pRawData);
    ```
    byte[] pRawData：内存为 LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
    返回值 Bitmap
    ```
- public int LIVESCAN_GetFPBmpData(byte[]pBmpData);
    ```
    byte[]pBmpData：内存为 LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT+ LIVESCAN_IMAGE_HEADER
    ```
- public String LIVESCAN_GetSdkVersion();

- public String LIVESCAN_GetDevVersion();

- public int LIVESCAN_GetDevSN(byte[]bySN); byte[]bySN 32 字节
    ```
    返回值参考常量
    ```

- public String LIVESCAN_GetErrorInfo(int nErrorNo);
    ```
    int nErrorNo：错误号
    返回值错误信息
    ```
- public int LIVESCAN_FeatureExtract(byte[] pFeatureData);
    ```
    byte[] pFeatureData 特征大小 LIVESCAN_FEATURE_SIZE 返回值参考常量

    ```
- public float LIVESCAN_GetMatchThreshold();
    ```
    返回值为匹配分数阈值
    注：需在初始化调用
    ```
- LIVESCAN_FeatureMatch(byte[] pFeatureData2,byte[] pFeatureData2,float[]pfSimilarity);
    ```
    byte[] pFeatureData1：特征 1，特征大小 LIVESCAN_FEATURE_SIZE
    byte[] pFeatureData2：特征 2，特征大小 LIVESCAN_FEATURE_SIZE
    float[] pfSimilarity：返回的相似度
    返回值参考常量
    ```
- public int LIVESCAN_GetQualityScore(byte[] pFingerImgBuf,byte[] pnScore)

    ```
    byte[] pFingerImgBuf：内存为LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
    byte[] pnScore：图像质量，建议值 LIVESCAN_IMAGE_SCORE_THRESHOLD
    返回值参考常量
    ```

- public int LIVESCAN_FeatureSearch(byte[] jpFeatureData, byte[] jpFeatureDatas, int nFeatureData,int[] index,float[] pfSimilarity);
    ```
    byte[] jpFeatureData 特征大小 LIVESCAN_FEATURE_SIZE

    byte[] jpFeatureDatas 模板数据 j 按照 LIVESCAN_FEATURE_SIZEE 的长度累计 长度为 nFeatureData *LIVESCAN_FEATURE_SIZE

    int[] nFeatureData 模板个数

    int[] index 返回相似度最高的索引 float[] pfSimilarity 返回相似度

- public int LIVESCAN_Encrypt(byte[]pISVData);
    ```
    pISVData 大小 16bytes
    采用厂商密钥对 pISVData 进行加密返回，已确认是否为指定厂商设备 返回值参考常量
    ```


**错误码**

调用该方法,返回信息即为错误信息
- mLiveScan.LIVESCAN_GetErrorInfo(iRet)

```
    public static String LIVESCAN_S_EINVAL = "Invalid Parameter";
    public static String LIVESCAN_S_MEMORY = "Memory allocation failed";
    public static String LIVESCAN_S_FUN = "Function unrealized";
    public static String LIVESCAN_S_DEVICE = "The device does not exist";
    public static String LIVESCAN_S_INIT = "Device has not been initialized";
    public static String LIVESCAN_S_UNKOWN = "Unkown";
    public static String LIVESCAN_S_ELSE = "Other errors";
    public static String LIVESCAN_S_SUCCESS = "Success";
```


#### 2.4 接口调用流程

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


#### 2.5 接口调用案例

参考Demo源码,FingerBYDBigActivity.java