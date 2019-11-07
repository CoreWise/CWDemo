# GAB大指纹


* [1.GAB大指纹开发包说明](#GAA大指纹开发包说明)
* [2.二次开发说明](#二次开发说明)
  * [2.1 AndroidStudio工程配置说明](#AndroidStudio工程配置说明)
  * [2.2 AndroidManifest.xml配置说明](#AndroidManifest配置说明)
  * [2.3 接口说明](#接口说明)
  * [2.4 接口调用流程](#接口调用流程)
  * [2.5 接口调用案例](#接口调用案例)
* [3.二次开发问题汇总](#二次开发问题汇总)

### GAB大指纹开发包说明

   1.1 支持GAB大指纹模块;

   1.2 GAB大指纹功能占用了机器唯一的USB口

   1.3 GAB大指纹开发包兼容机器请查看:
   [GAB大指纹开发包兼容机器说明](https://github.com/CoreWise/CWDemo#user-content-zh)

   1.4
   [GAB大指纹开发包下载地址](https://github.com/CoreWise/CWDemo#user-content-zh)

### 二次开发说明

   由于本机器的高通CPU只支持一个USB口,所以在使用GAB大指纹模块时，需要先调用USB管理类将USB切换到指纹模组,此时USB正常情况下不能用来充电、数据线通信等操作。
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
    //GAB大指纹开发包(新固件开发包)
    //GAB FingerPrint SDK
    compile(name: 'fp_gab_sdk_20190429', ext: 'aar')

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
- onOpenUSBFingerSuccess(String device, UsbManager mUsbManager, UsbDevice mDevice):

    切换USB到指纹模组成功，返回当前指纹模组的名称

- onOpenUSBFingerFailure(String error):

    切换USB到指纹模组失败，返回错误码



---

**GAB大指纹类**


| FingerprintScanner API接口 | 接口说明 |
| :----- | :---- |
| getInstance  | 获取终端序列号 |
|open |获取设备型号
|close|获取Android版本号
|prepare|准备采集指纹
|finish|采集指纹结束
|getDriverVersion|获取指纹设备驱动版本
|getSN|获取指纹设备序列号
|getSensorName|获取指纹传感器名称
|capture|采集一帧FingerprintImage类型指纹图像

具体说明:

- public static FingerprintScanner getInstance(Contextcontext);

  功能描述：获取FingerprintScanner类型指纹设备控制实例
  
  参数：context应用程序上下文 
  
  返回值：FingerprintScanner控制实例
  
  
- public int open(); 

    打开指纹设备电源

- public int close(); 

    关闭指纹设备电源

- public int prepare(); 

    准备采集指纹

- public int finish(); 

    采集指纹结束

- public Result capture(); 

    采集一帧FingerprintImage类型指纹图像
    
    包含FingerprintImage类型指纹图像及错误代码
    

| FingerprintImage API接口 | 接口说明 |
| :----- | :---- |
| raw[域]  | 指纹图像原始数据（byte[]） |
|width[域]|指纹图像宽（int）
|height[域] |指纹图像高（int）
|dpi[域] |指纹图像DPI（int）
|convert2Bmp|将指纹图像转化为BMP图像字节序列

具体说明:

- public byte[] convert2Bmp();

    将指纹图像转化为BMP图像字节序列
    
    byte[]类型BMP图像字节序列
    

| Bione API接口 | 接口说明 |
| :----- | :---- |
| initialize  | 初始化Bione算法 |
|exit|结束Bione算法并执行清理
|getVersion |获取当前算法版本信息 
|getFingerprintQuality  |取得指定FingerprintImage指纹图像对象质量 
|extractFeature |从FingerprintImage指纹图像对象提取特征
|makeTemplate|从同一手指的三个指纹特征合成模板数据 
|isFreeID|判断该id在当前指纹库中是否已被注册指纹
|getFreeID |获取一个当前指纹库中未使用的id值
|getFeature|获取当前指纹库中指定id值对应的指纹特征或模板
|getFeature|获取当前指纹库中所有指纹特征或模板
|getEnrolledCount|获取当前指纹库中已注册指纹特征或模板数量
|enroll|注册指纹特征入当前指纹库
|delete|删除当前指纹库中指定id对应的指纹特征
|clear|清空当前指纹特征库
|verify|将当前指纹库中id所对应的指纹特征和目标指纹特征进行比对
|verify|比对两个指纹特征是否匹配
|identify|搜索当前指纹库，查询匹配的指纹特征对应的id并返回结果
|idcardVerify|比对二代身份证内的指纹特征和设备取到的指纹特征是否匹配
|idcardIdentify|比对用户提供的二代身份证指纹特征集合和设备取到的指纹特征是否有匹配
|setSecurityLevel|设置指纹比对的安全等级

具体说明:

- public static int initialize(Context context,String dbPath);

  初始化Bione算法
  
  context当前应用程序上下文环境
  
  dbPath指纹库文件路径返回值：错误代码
    
- public static int  exit(); 

    结束Bione算法并执行清理错误代码

- public static int getFingerprintQuality(FingerprintImage image);

  取得指定FingerprintImage指纹图像对象质量
  
  参数：imageFingerprintImage类型指纹图像对象
  
  返回值：>=0指纹图像质量值，<0错误代码
  
  
- public static Result extractFeature(FingerprintImage image);

  从FingerprintImage指纹图像对象提取特征
  
  参数：imageFingerprintImage类型指纹图像对象
  
  返回值：包含byte[]类型指纹特征数据及错误代码
  
- public Result makeTemplate(byte[] feature1,byte[]
  feature2,byte[] feature3);
  
  从同一手指的三个指纹特征合成模板数据 feature1同一手指第一次采集的指纹特征数据
  
  eature2同一手指第二次采集的指纹特征数据
  
  feature3同一手指第三次采集的指纹特征数据
  
  包含byte[]类型指纹模板数据及错误代码


- public static boolean isFreeID(int id);

    判断该id在当前指纹库中是否已被注册指纹 
    
    参数：id需要判断是否可用于注册的id
    
    返回值：true该id尚未注册指纹，可以进行注册
    
    false该id已经注册过指纹或该id无效，不可注册指纹

- public static int getFreeID();

    功能描述：获取一个当前指纹库中未使用的id值
    
    返回值：>=0可用于注册的id ，<0错误代码


- public static getFeature(int id); 

  功能描述：获取当前指纹库中指定id值对应的指纹特征或模板
  
  参数：id要获取的指纹特征的id值 包含byte[]类型指纹特征或模板数据及错误代码


- public static Result getAllFeatures();

  功能描述：获取当前指纹库中所有指纹特征或模板
  返回值：包含Map<Integer,byte[]>类型指纹特征或模板数据及错误代码（请参看Bione错误代码）的Result
  实例

- public static int getEnrolledCount();

    功能描述：获取当前指纹库中已注册指纹特征或模板数量返 回 值：>= 0 已注册入当前库的指纹特征或模板数量
    < 0 错误代码

- public static int enroll(int id, byte[] feature);

    功能描述：注册指纹特征入当前指纹库
    
    参 数：id 需要注册入当前库的指纹 id 值
    feature 需要注册入库的指纹特征或模板数据
    
    返 回 值：错误代码说 明：必须保证 id 值在当前库中唯一，必要时可使用 isFreeID 方法判断该 id 是否已被注册或使用
    getFreeID 方法申请一个未使用的 id 值进行注册


- public static int delete(int id);

    功能描述：删除当前指纹库中指定 id 对应的指纹特征
    
    参 数：id 需要从当前库中删除的指纹特征或模板 id返 回 值：错误代码


- public static int clear();

    功能描述：清空当前指纹特征库
    
    参 数：无
    
    返 回 值：错误代码

- public static Result verify(int id, byte[] feature);

    功能描述：将当前指纹库中 id 所对应的指纹特征和目标指纹特征进行比对
    
    参 数：id 需要比对的指纹库中的指纹 id ，feature 需要比对的目标指纹特征数据
    
    返 回 值：包含 Boolean 类型比对结果及错误代码


- public static Result verify(byte[] feature1, byte[] feature2);

    功能描述：比对两个指纹特征是否匹配
    
    参 数：feature1 指纹特征 1 ，feature2 指纹特征 2
    
    返 回 值：包含 Boolean 类型比对结果及错误代码


- public static int identify(byte[] feature);

    功能描述：搜索当前指纹库，查询匹配的指纹特征对应的 id 并返回结果
    
    参 数：feature 要在当前库中进行匹配的指纹特征或模板
    
    返 回 值：>= 0 匹配成功，返回匹配 id ，< 0 错误代码


- public static Result idcardVerify(byte[] idcardFeature, byte[] feature);

    功能描述：比对二代身份证内的指纹特征和设备取到的指纹特征是否匹配
    
    参 数：idcardFeature 二代身份证内的指纹特征
    
    feature 设备取到的指纹特征
    
    返 回 值：包含 Boolean 类型比对结果及错误代码的 Result 实例 说 明：无。 <
    0 错误代码


- public static Result idcardIdentify(Map<String, byte[]> idcardFeatureMap, byte[] feature);

    功能描述：比对用户提供的二代身份证指纹特征集合和设备取到的指纹特征是否有匹配
    
    参 数：idcardFeatureMap 要进行 1:N 比对的二代身份证指纹特征集合
    
    feature 设备取到的指纹特征
    
    返 回 值：包含 String 类型比对匹配结果及错误代码

- public static void setSecurityLevel(int level);

    功能描述：设置指纹比对的安全等级
    
    参 数：level 指纹比对的安全等级（HIGH、MEDIUM、LOW）




**错误码**


| FingerprintScanner 错误码定义 | 错误代码 |错误说明| 
| :----- | :---- |:----|
| RESULT_OK | 0 |操作成功
| RESULT_FAIL | -1000 |操作失败
| WRONG_CONNECTION | -1001 |设备连接错误
| DEVICE_BUSY | -1002 |设备正忙
| DEVICE_NOT_OPEN | -1003 |设备未打开
| TIMEOUT  | -1004 |超时
| NO_PERMISSION  | -1005 |未授权或无权限
|WRONG_PARAMETER |-1006|参数错误
|DECODE_ERROR |-1007|解码错误
|INIT_FAIL |-1008|初始化错误
|UNKNOWN_ERROR |-1009|未知错误
|NOT_SUPPORT |-1010|不支持
|NOT_ENOUGH_MEMORY |-1011|内存不足
|DEVICE_NOT_FOUND |-1012|未找到支持的设备
|DEVICE_REOPEN |-1013|设备重复打开
|NO_FINGER|-2005|没有检测到手指





| Bione 错误码定义 | 错误代码 |错误说明| 
| :----- | :---- |:----|
| RESULT_OK | 0 |算法操作成功
|INITIALIZE_ERROR |-2000|算法初始化失败
|INVALID_FEATURE_DATA |-2001|错误的特征值格式
|BAD_IMAGE |-2002|指纹图像质量差
|NOT_MATCH |-2003|不是同一枚指纹
|LOW_POINT|-2004|一对一比对指纹分值低或合成模板时三个指纹不匹配
|NO_FINGER |-2005|输入图像无手指
|NO_RESULT |-2006|一对多比对未返回匹配结果
|OUT_OF_BOUND|-2007|指定ID越界（<0或>=指纹库最大容量）
|DATABASE_FULL |-2008|指纹库已满
|LIBRARY_MISSING |-2010|找不到算法库
|UNINITIALIZE |-2011|算法库未被初始化
|REINITIALIZE |-2012|算法库重复初始化
|REPEATED_ENROLL |-2013|指定ID已被注册指纹
|NOT_ENROLLED|-2014|指定ID未被注册



#### 接口调用案例

参考Demo源码



#### 二次开发问题汇总


- 指纹类的应用在横竖屏切换时,如果重复初始化模块导致出现的一系列问题？

    ```
    在清单文件里相应的Activity标签下添加一下内容:
    android:configChanges="orientation|keyboardHidden|screenSize"


    ```