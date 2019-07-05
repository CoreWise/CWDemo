# JRA小指纹


* [1.JRA小指纹开发包说明](#JRA小指纹开发包说明)
* [2.二次开发说明](#二次开发说明)
  * [2.1 AndroidStudio工程配置说明](#AndroidStudio工程配置说明)
  * [2.2 AndroidManifest.xml配置说明](#AndroidManifest配置说明)
  * [2.3 接口说明](#接口说明)
  * [2.4 接口调用流程](#接口调用流程)
  * [2.5 接口调用案例](#接口调用案例)
* [3.二次开发问题汇总](#二次开发问题汇总)

## JRA小指纹开发包说明

  1.1 支持JRA小指纹模块,算法为ISO19794-2的特征值协议
  
  1.2 JRA小指纹功能占用了机器唯一的USB口
  
  1.3 JRA小指纹开发包兼容机器请查看: [JRA小指纹开发包兼容机器说明](https://github.com/CoreWise/CWDemo#user-content-zh)
  
  1.4 [JRA小指纹开发包下载地址](https://github.com/CoreWise/CWDemo#user-content-zh)

## 二次开发说明

  由于本机器的高通CPU只支持一个USB口,所以在使用JRA小指纹模块时，需要先调用USB管理类将USB切换到指纹模组,此时USB正常情况下不能用来充电、数据线通信等操作。
  在这样的情况下，USB数据线调试不能使用，建议网络adb调试;

  网络adb调试推荐Android Studio安装Android Wifi ADB插件;

### AndroidStudio工程配置说明

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
    //JRA小指纹开发包(新固件开发包)
    //JRA FingerPrint SDK
    compile(name: 'fp_jra_sdk_20190429', ext: 'aar')

 }

```

### AndroidManifest配置说明

```xml

  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

### 接口说明

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

**JRA小指纹类**
以下接口废弃，不再做升级处理


| JRA FingerPrint API接口 | 接口说明 |
| :----- | :---- |
| OTG_KEY | 构造函数 |
| UsbOpen | 打开设备 |
| CloseCard | 关闭设备 |
| SyGetInfo | 获取设备信息 |
| SyUpChar | 上传特征 |
| SyDownChar | 下载特征 |
| SyGetImage | 获取图像 |
| SyUpImage | 上传图像 |
| SyEnroll | 指纹录入 |
| SySearch | 指纹对比 |
| SyDeletChar | 指纹删除 |
| SyClear | 清除数据库 |


具体说明:

-  public SyOTG_Key(UsbManager mManager, UsbDevice mDev)

    构造函数

    ```
    参数：
    mManager：UsbManager对象
    mDev	：USB总线探测到的指纹模组设备。
    ```
    
- public int SyOpen()

    打开设备,成功返回0
   
- public void SyClose()

    关闭设备
    
- public int SyGetInfo(byte [] DeviceInfo)

    获取设备信息

    ```
    参数：
    DeviceInfo： 32Byte设备信息。
    返回值：成功返回0
    ```
    
- public int SyUpChar(int pageId,byte[] tempdata)
    
    上传特征
    
    ```
    参数：
    pageId：要上传的特征的数据库ID号。
    Tempdata：512Byte存储指纹特征数组。
    返回值：成功返回0
    ```
    
- public int SyDownChar(int pageId,byte[] tempdata)
    
    下载特征
    
    ```
    参数：
    	pageId：下载指纹特征到的数据库ID号。
    	Tempdata：512Byte指纹特征。
    返回值：成功返回0
    ```
    
- public int SyGetImage()

    获取图像,成功返回0
    
- public int SyUpImage(byte [] pImageData)

    上传图像

    ```
    参数：
    pImageData：获取到的图像数据。
    返回值：成功返回0
    ```
    
- public int SyEnroll(int cnt,int fingerId)

    指纹录入
    
    ```
    参数：
    	Cnt：录入次数。
    	fingerId：要存储到数据库的ID号。
    返回值：成功返回0
    ```
    
- public int SySearch(int[] fingerId )

    指纹对比
    
    ```
    参数：
    fingerId：搜索到匹配指纹模版号码,int 型4Byte。
    返回值：成功返回0
    ```
    
- public int SyDeletChar(int fingerId)

    指纹删除
    
    ```
    参数：
    	fingerId:要删除的指纹模版号码。
    返回值：成功返回0
    ```
    
- public int SyClear()

    清除数据库,成功返回0
    

**错误码**

```
#define PS_OK                			0x00
#define PS_COMM_ERR          			0x01
#define PS_NO_FINGER         			0x02
#define PS_GET_IMG_ERR     			    0x03
#define PS_FP_TOO_DRY        			0x04
#define PS_FP_TOO_WET        			0x05
#define PS_FP_DISORDER      			0x06
#define PS_LITTLE_FEATURE  			    0x07
#define PS_NOT_MATCH        	  		0x08
#define PS_NOT_SEARCHED      			0x09
#define PS_MERGE_ERR         			0x0a
#define PS_ADDRESS_OVER      			0x0b
#define PS_READ_ERR          			0x0c
#define PS_UP_TEMP_ERR       			0x0d
#define PS_RECV_ERR          			0x0e
#define PS_UP_IMG_ERR        			0x0f
#define PS_DEL_TEMP_ERR      			0x10
#define PS_CLEAR_TEMP_ERR    			0x11
#define PS_SLEEP_ERR         			0x12
#define PS_INVALID_PASSWORD  			0x13
#define PS_RESET_ERR         			0x14
#define PS_INVALID_IMAGE     			0x15
#define PS_HANGOVER_UNREMOVE 		    0x17
```

### 接口调用流程

- #### 打开并初始化指纹模块

![smallfinger.png](https://i.loli.net/2019/05/08/5cd24de9568f8.png)


- #### 采集指纹

![smallfingerluru.png](https://i.loli.net/2019/05/08/5cd24de95695e.png)


- #### 搜索指纹

![smallfingersousuo.png](https://i.loli.net/2019/05/08/5cd24de93aaa4.png)


#### 接口调用案例

参考Demo源码,FpJRAActivity.java


#### 二次开发问题汇总


- 指纹类的应用在横竖屏切换时,如果重复初始化模块导致出现的一系列问题？

    ```
    在清单文件里相应的Activity标签下添加一下内容:
    android:configChanges="orientation|keyboardHidden|screenSize"


    ```
