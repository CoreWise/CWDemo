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

    //相关工具开发包
    //Utils SDK，必须依赖
    compile(name: 'serialport_sdk_20190702', ext: 'aar')

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

- onOpenUSBFingerSuccess(String device, UsbManager mUsbManager, UsbDevice mDevice):

    切换USB到指纹模组成功，返回当前指纹模组的名称
    device:返回当前指纹模组型号
    mUsbManager:返回UsbManager管理实例
    mDevice:返回UsbDevice实例

- onOpenUSBFingerFailure(String error):

    切换USB到指纹模组失败，返回错误码


---


**JRA小指纹类**

常量

```
    private static final int PS_OK = 0;
    private static final int CHAR_BUFF_A = 1;
    private static final int CHAR_BUFF_B = 2;
    private static final int DEV_ADDR = -1;

```




| JRA FingerPrint API接口 | 接口说明 |
| :----- | :---- |
| JRA_API | 构造函数 |
| openJRA | 打开设备 |
| closeJRA | 关闭设备 |
| PSGetImage | JRA获取指纹图片数据 |
| PSUpImage | 上传图片数据到app |
| PSGenChar | 在指定缓存区生成特征数据 |
| PSRegModule | 合并生成特征值 |
| PSStoreChar | 存储特征值到flash |
| PSSearch | 搜索指纹 |
| PSDownCharToJRA | 将特征值下载到JRA Flash |
| WriteBmp | 生成指纹图片 |
| PSEmpty|清空指纹库|

具体说明:

- public JRA_API(UsbManager mManager, UsbDevice mDev)

  ```
  构造函数

  ```

- public int openJRA()

    ```
    /**
     * 打开模组
     *
     * @return
     *          {@link PS_DEVICE_NOT_FOUND:has not found jra device}
     *          {@link PS_EXCEPTION:exception}
     *          {@link PS_OK:ok}
     */


    ```

- public int closeJRA()
    ```
    /**
     * 关闭模组
     *
     * @return
     *       {@link PS_EXCEPTION:exception}
     *       {@link PS_OK:ok}
     */

    ```
- public int PSGetImage()
    ```
    /**
     * JRA模组采集指纹图像存在JRA模组缓存区内（容易丢失或者被覆盖,需要配合PSUpImage将数据上传到app）
     *
     * @return
     *       {@link PS_OK:ok}
     */
    ```
- public int PSUpImage(byte[] pImageData)
    ```
   /**
     * JRA模组将存在JRA缓存区内图片数据(byte[])上传到app
     *
     * @param pImageData
     * @return
     *       {@link PS_OK:ok}
     */
    ```
- public int PSGenChar(int bufferID)
    ```
    /**
     * 将ImageBuffer 中的原始图像生成指纹特征文件存于CharBuffer1 或CharBuffer2 输入参数：
     * BufferID(特征缓冲区号)
     * <p>
     * 根据原始图像生成指纹特征存于特征文件缓冲区
     *
     * @param bufferID {@link CHAR_BUFFER_A} ,{@link CHAR_BUFFER_B}
     * @return
     *        {@link PS_OK:ok}
     */
    ```
- public int PSRegModule()
    ```
    /**
     * 合并特征生成模板，将CharBuffer1和CharBuffer2中的特征文件合并生成模板，
     * 将特征文件合并生成模板存于特征文件缓冲区
     *
     * @param fpRaw 返回特征值
     * @return
     *        {@link PS_OK:ok}
     */
    ```
- public int PSStoreChar(int[] pageID, byte[] fpRaw)
    ```
    /**
     *
     * 将CharBuffer中的模板储存到指定的pageId号的flash数据库位置
     * pageId：返回id范围为0~1000 ： BufferID(缓冲区号)，PageID（指纹库位置号0~999）
     *
     * @param pageID,返回注册指纹的id
     * @param fpRaw,返回注册指纹的特征值，512bit
     * @return
     *        {@link PS_OK:ok}
     */
    ```
- public int PSSearch(int bufferID, int[] id)
    ```

    ```
- public int PSDownCharToJRA(byte[] fpRaw, int[] id)
    ```
        /**
     * 将指纹数据下载到JRA模组里
     *
     * @param fpRaw,特征值，512byte
     * @param id 返回到该特征值的id
     * @return
     *        {@link PS_OK:ok}
     */
    ```
- public int PSEmpty()
    ```
     /**
     * 删除flash 数据库中所有指纹模板
     *
     * @return
     *        {@link PS_OK:ok}
     */
    ```

- public int getUserIndex()
    ```
    /**
     * 获取当前JRA指纹模组里指纹数量
     *
     * @return 返回指纹数量
     *        {@link PS_OK:ok}
     */

    ```
- public int[] getUserId()
    ```
   /**
     * 获取指纹ID数组
     *
     * @return
     */
    ```
- public int getUserMaxId()
    ```
    /**
     * 获取指纹最大ID
     *
     * @return
     */
    ```


**错误码**

```
    public static final int PS_DEVICE_NOT_FOUND = -1;
    public static final int PS_EXCEPTION = -2;
    public static final int PS_NO_FINGER_IN_JRA = -3;
    public static final int PS_OK = 0x00;
    public static final int PS_NO_FINGER = 0x02;
    public final static int PS_DEVICE_SUCCESS = 0x00000000;
    public final static int PS_DEVICE_FAILED = 0x20000001;
    public static final int PS_MAX_FINGER = 1000;
    public static final int PS_RAW_NOT_512 = -101;

```


### 接口调用流程

- #### 打开并初始化指纹模块

![usb.png](https://i.loli.net/2019/07/04/5d1d9c1c0145f92487.png)


- #### 采集指纹

![jra_cature_erroll.jpg](https://i.loli.net/2019/07/04/5d1d9bf14929d24967.jpg)


- #### 搜索指纹

![jrasearch.png](https://i.loli.net/2019/07/04/5d1d9d7d0892b66361.png)


#### 接口调用案例

参考Demo源码,[JRAActivity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/fingerprint/jra/JRAActivity.java)


#### 二次开发问题汇总
