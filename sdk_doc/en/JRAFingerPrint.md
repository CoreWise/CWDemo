### 1. 开发包说明

### 2. 二次开发说明

#### 2.1 Android Studio工程配置说明
#### 2.2 AndroidManifest.xml配置说明

#### 2.3  接口说明



#### 2.4 接口调用流程


#### 2.5 接口调用案例


# JRA指纹

## 1. JRA fingerprint Development Kit Instruction.

  1.1 Support JRA  Fingerprint Module.

  1.2 JRA Fingerprint function occupies the only USB port of the machine


  1.3 JRA fingerprint development kit compatible machine, please check :[JRA fingerprint development kit compatible machine description](https://github.com/CoreWise/CWDemo#user-content-en)

  1.4 [JRA fingerprint development kit download website](https://github.com/CoreWise/CWDemo#user-content-en)



## 2. Secondary Development Instruction

Because the Qualcomm CPU of this machine only supports one USB port, when using JRA fingerprint module, it is necessary to call USB management class to switch USB to fingerprint module first. At this time, USB can not be used for charging, data line communication and other operations under normal circumstances.


 In this case, USB data line debugging can not be used, it is recommended that network ADB debugging;

### 2.1 Android Studio Programme Configuration Description:

- 1. Add development kit aar to libs

- 2. Configuration module ‘s build.gradle, References are as follows:


```
...
 android {
     ...
     defaultConfig {
         ...
         targetSdkVersion 22 //ID card function must be reduced by 22, other things don't matter.
         ...
     }
     ...
 }
 //2.must 2
 repositories {
     flatDir {
         dirs 'libs'   // aar catalog     }
 }

 dependencies {
     ...
    //JRA Fingerprint Development Kit .(New firmware development kit)
    //JRA FingerPrint SDK
    compile(name: 'fp_gaa_sdk_20190429', ext: 'aar')
    //SerialPort SDK,need usbManager
    compile(name: 'serialport_sdk_20190702', ext: 'aar')

 }

```

### 2.2 AndroidManifest.xml Configuration description
```xml

  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

### 2.3 Interface Instruction:

**USB Management Class**

| USBFingerManager APIInterface | Interface Instruction |
| :----- | :---- |
| USBFingerManager | USB Management Class,Singleton Module |
| USBFingerManager.getInstance(this) | Obtain the USB management class case ,Singleton Mode |
| USBFingerManager.getInstance(this).setDelayMs(ms) | Setting Delay of Cutting to Fingerprint Module,Default 1000ms |
| USBFingerManager.getInstance(this).openUSB(OnUSBFingerListener()) |  Cut USB into fingerprint module |
| USBFingerManager.getInstance(this).closeUSB(); | Cut USB into normal mode |

OnUSBFingerListenerCallback interface description::

- onOpenUSBFingerSuccess(String device):

 Switching USB to fingerprint module successfully,then returns the name of the current fingerprint module


- onOpenUSBFingerFailure(String error):

   Switching USB to fingerprint module failed,then returns wrong code


---

**BYD Small Fingerprint Class**

Constant

常量

```
    private static final int PS_OK = 0;
    private static final int CHAR_BUFF_A = 1;
    private static final int CHAR_BUFF_B = 2;
    private static final int DEV_ADDR = -1;

```




| JRA FingerPrint API接口 | 接口说明 |
| :----- | :---- |
| JRA_API | Constructor |
| openJRA | Open device |
| closeJRA | close device |
| PSGetImage | JRA obtains fingerprint image data |
| PSUpImage | Upload image data to app |
| PSGenChar | Generate feature data in the specified buffer area |
| PSRegModule | Merging generated feature values |
| PSStoreChar | Store feature values to flash |
| PSSearch | Search fingerprint |
| PSDownCharToJRA | Download feature values to JRA Flash |
| WriteBmp | Generate fingerprint image |
| PSEmpty|clear all|


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

- public int SyClear()

Clear the database ,successful return 0


**Error Code**

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



### 2.4 Interface call process

- #### Open and initialize fingerprint module

![usb.png](https://i.loli.net/2019/07/04/5d1d9c1c0145f92487.png)


- #### Collection fingerprint
![jra_cature_erroll.jpg](https://i.loli.net/2019/07/04/5d1d9bf14929d24967.jpg)


- #### Search fingerprint
![jrasearch.png](https://i.loli.net/2019/07/04/5d1d9d7d0892b66361.png)




#### 2.5 Interface call case

Reference Demo source code,[JRAActivity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/fingerprint/jra/JRAActivity.java)
