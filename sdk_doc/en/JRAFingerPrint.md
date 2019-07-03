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

```
    private static final int PS_OK = 0;
    private static final int CHAR_BUFF_A = 1;
    private static final int CHAR_BUFF_B = 2;
    private static final int DEV_ADDR = -1;

```

| JRA FingerPrint API Interface | Interface Instruction |
| :----- | :---- |
| SyOTG_Key | Constructed Function |
| SyOpen | Open Equipment |
| SyClose | Close Equipment|
| SyGetInfo | Obtain Equipment Information |
| SyUpChar | Upload Feature |
| SyDownChar | Download Features |
| SyGetImage | Obtain Image |
| SyUpImage | Upload Images |
| SyEnroll | Fingerprint Entry |
| SySearch | Fingerprint Comparison |
| SyDeletChar | Fingerprint Delete |
| SyClear | Clear the database |

Specific Instruction:

-  public SyOTG_Key(UsbManager mManager, UsbDevice mDev)

    Constructed Function

    ```
    Parameter：
mManager：UsbManagerObjet
    mDev	：USB bus detected the fingerprint module equipment.
    ```

- public int SyOpen()

Open equipment, successful return to 0

- public void SyClose()

   Close equipment

- public int SyGetInfo(byte [] DeviceInfo)

    Obtain equipment information
    ```
    Parameter：
DeviceInfo： 32Byte Equipment Information
Return value:successful return to 0

    ```

- public int SyUpChar(int pageId,byte[] tempdata)

Upload Feature

    ```
    Parameter：
    pageId： Download fingerprint features to database ID number.
    Tempdata：512ByteStoring fingerprint feature block.
Return value:successful return to 0
    ```

- public int SyDownChar(int pageId,byte[] tempdata)

    Download feature
    ```
    Parameter：
    	pageId：Download fingerprint feature database ID number
    	Tempdata：512Byte Fingerprint feature.
    Return value:successful return to 0
  ```

- public int SyGetImage()

    Obtain Image,successful return to 0
- public int SyUpImage(byte [] pImageData)

    Upload Image
    ```
    Parameter：
pImageData： Obtain Image data.

Return value:successful return 0
    ```

- public int SyEnroll(int cnt,int fingerId)

    Fingerprint Enter
    ```
    Parameter：
    	Cnt：Enter Times
    	fingerId：ID number to be stored in the database。
    Return value: successful return 0    ```

- public int SySearch(int[] fingerId )

    Fingerprint Comparison
    ```
    Parameter ：
fingerId：Search to  matching fingerprint template number, int type 4Byte.

Return value:successful return to 0
    ```

- public int SyDeletChar(int fingerId)

    Fingerprint delete
    ```
    Parameter：
    	fingerId: The number of fingerprint template to delete.
Return value:successful return to 0

    ```

- public int SyClear()

Clear the database ,successful return 0


**Error Code**

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

### 2.4 Interface call process

- #### Open and initialize fingerprint module

![smallfinger.png](https://i.loli.net/2019/05/08/5cd24de9568f8.png)


- #### Collection fingerprint
![smallfingerluru.png](https://i.loli.net/2019/05/08/5cd24de95695e.png)


- #### Search fingerprint
![smallfingersousuo.png](https://i.loli.net/2019/05/08/5cd24de93aaa4.png)


#### 2.5 Interface call case

Reference Demo source code,[FpJRAActivity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/fingerprint/jra/FpJRAActivity.java)

