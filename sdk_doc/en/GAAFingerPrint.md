# GAA Fingerprint

### 1.  GAA Fingerprint Development Kit Instruction.

   1.1  Support GAA Fingerprint Module;

   1.2 GAA Fingerprint function occupies the only USB port of the machine

   1.3 BYD Big fingerprint development kit compatible machine, please check :[GAA fingerprint development kit compatible machine description](https://github.com/CoreWise/CWDemo#user-content-en)

   1.4 [GAA fingerprint development kit download website](https://github.com/CoreWise/CWDemo#user-content-en)

### 2. Secondary Development Instruction

Because the Qualcomm CPU of this machine only supports one USB port, when using GAA fingerprint module, it is necessary to call USB management class to switch USB to fingerprint module first. At this time, USB can not be used for charging, data line communication and other operations under normal circumstances.

In this case, USB data line debugging can not be used, it is recommended that network ADB debugging;

Network ADB debugging recommend Android Studio install Android Wifi ADB plug in;

#### 2.1 Android Studio Programme Configuration Description:

- 1.Add development kit aar to libs

- 2.Configuration module ‘s build.gradle, References are as follows:

    ```
    ...
     android {
         ...
         defaultConfig {
             ...
             targetSdkVersion 22 // ID card function must be reduced by 22, other things don't matter.
             ...
         }
         ...
     }
     //2.must 2
     repositories {
         flatDir {
             dirs 'libs'   // aar Catalog     }
     }

     dependencies {
         ...
        //BYD Big Fingerprint Development Kit .(New firmware development kit)
        //BYD Big FingerPrint SDK
        compile(name: 'fp_gaa_sdk_20190429', ext: 'aar')

     }

    ```

#### 2.2 AndroidManifest.xml Configuration description
    ```xml

    <uses-feature android:name="android.hardware.usb.host"
    android:required="true" />

    <uses-permission
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

    ```

#### 2.3  Port Instruction:

 **USB Management Category**

| USBFinger Manager API Port | Port Instruction  |
| :----- | :---- |
| USBFingerManager | USB Management Category,Singleton Mode |
| USBFingerManager.getInstance(this) | Obtain the USB management class case ,Singleton Mode |
| USBFingerManager.getInstance(this).setDelayMs(ms) | Setting Delay of Cutting to Fingerprint Module，Default 1000ms |
|USBFingerManager.getInstance(this).openUSB(OnUSBFingerListener()) | Cut USB into fingerprint module |
| USBFingerManager.getInstance(this).closeUSB(); | Cut USB into normal mode |

OnUSBFingerListener Callback interface description:

- onOpenUSBFingerSuccess(String device):

    Switching USB to fingerprint module successfully,then returns the name of the current fingerprint module

- onOpenUSBFingerFailure(String error):

    Switching USB to fingerprint module failed,then returns wrong code



---

**GAA Fingerprint Class**

Constant
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


| BYD FingerPrint API Port | Port Instruction |
| :----- | :---- |
| ID_Fpr | Big Fingerprint API Class |
|LIVESCAN_Init|Initialization Device|
|LIVESCAN_Close|Close Equipment|
|LIVESCAN_GetFPRawData(byte[]pRawData);| Obtain fingerprint image|
|LIVESCAN_FPRawDataToBmp(byte[] pRawData) |Image to BMP|
|LIVESCAN_GetFPBmpData(byte[]pBmpData) |Obtain BMP fingerprint image|
|LIVESCAN_GetSdkVersion()|SDK Version
|LIVESCAN_GetDevVersion();|Equipment Version
|LIVESCAN_GetDevSN(byte[]bySN)|Equipment serial number
|LIVESCAN_GetErrorInfo(int nErrorNo)| Obtain Wrong information
|LIVESCAN_FeatureExtract(byte[] pFeatureData)|Feature Collect
|LIVESCAN_GetMatchThreshold()| Obtain the matching fraction threshold,Note: Initialization calls are required
|LIVESCAN_FeatureMatch(byte[] pFeatureData2,byte[] pFeatureData2,float[]pfSimilarity) |Fingerprint Match(1:1)
|LIVESCAN_GetQualityScore(byte[] pFingerImgBuf,byte[] pnScore)| Obtain Image quality
|LIVESCAN_FeatureSearch(byte[] jpFeatureData, byte[] jpFeatureDatas, int nFeatureData,int[] index,float[] pfSimilarity); |Search fingerprint(1:N)|
|LIVESCAN_Encrypt(byte[]pISVData)|Encryption

Specific Instruction:

- public int LIVESCAN_Init();

- public int LIVESCAN_Close ();

- public int LIVESCAN_GetFPRawData(byte[]pRawData);
    ```
    byte[]pRawData：Memory is  LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
Return value reference constant
```

- public  Bitmap LIVESCAN_FPRawDataToBmp(byte[] pRawData);
    ```
    byte[] pRawData：Memory  LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
    Return value Bitmap
    ```
- public int LIVESCAN_GetFPBmpData(byte[]pBmpData);
    ```
    byte[]pBmpData：Memory is LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT+ LIVESCAN_IMAGE_HEADER
    ```
- public String LIVESCAN_GetSdkVersion();

- public String LIVESCAN_GetDevVersion();

- public int LIVESCAN_GetDevSN(byte[]bySN); byte[]bySN 32 byte
    ```
Return value reference constant
    ```

- public String LIVESCAN_GetErrorInfo(int nErrorNo);
    ```
    int nErrorNo：Error Number
Return error information

    ```
- public int LIVESCAN_FeatureExtract(byte[] pFeatureData);
    ```
    byte[] pFeatureData Feature size LIVESCAN_FEATURE_SIZE Return value reference constant
    ```
- public float LIVESCAN_GetMatchThreshold();
    ```
     Obtain the matching fraction threshold,
Note: Initialization calls are required
    ```
- LIVESCAN_FeatureMatch(byte[] pFeatureData2,byte[] pFeatureData2,float[]pfSimilarity);
    ```
    byte[] pFeatureData1：Feature  1，Feature size LIVESCAN_FEATURE_SIZE
    byte[] pFeatureData2：Feature  2，Feature  LIVESCAN_FEATURE_SIZE
float[] pfSimilarity：Return similarity
 Return value reference constant
 ```
- public int LIVESCAN_GetQualityScore(byte[] pFingerImgBuf,byte[] pnScore)

    ```
    byte[] pFingerImgBuf：Memory LIVESCAN_IMAGE_WIDTH* LIVESCAN_IMAGE_HEIGHT
    byte[] pnScore：Image Quality，recommended value LIVESCAN_IMAGE_SCORE_THRESHOLD
    Return value reference constant
    ```

- public int LIVESCAN_FeatureSearch(byte[] jpFeatureData, byte[] jpFeatureDatas, int nFeatureData,int[] index,float[] pfSimilarity);
    ```
    byte[] jpFeatureData Feature Size LIVESCAN_FEATURE_SIZE

    byte[] jpFeatureDatas Module Data j according  LIVESCAN_FEATURE_SIZEE length cumulative ,length is  nFeatureData *LIVESCAN_FEATURE_SIZE

    int[] nFeatureData Template number

    int[] index Returns the index with the highest similarity  float[] pf Similarity Returns the  similarity
- public int LIVESCAN_Encrypt(byte[]pISVData);
    ```
    pISVData size 16bytes
   Use Manufacturer keys to encrypted return of pISVData,has been confirmed as a designated manufacturer's equipment,Return value reference constant
    ```


**Error Code**

Call this method,return information is error information
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


#### 2.4  Interface invocation process
- ##### Open and initialize fingerprint module
![bigfingerusb.png](https://i.loli.net/2019/05/08/5cd24e0add367.png)


- ##### collection fingerprint image

![bigfingerimage.png](https://i.loli.net/2019/05/08/5cd24e0aa5a02.png)





- #####  collection fingerprint

![bigfingerluru.png](https://i.loli.net/2019/05/08/5cd24e0ae5f66.png)

- ##### 1:1 comparison
![bigfinger11.png](https://i.loli.net/2019/05/08/5cd24e0ac2ecb.png)



- ##### 1:N comparison
![bigfinger1n.png](https://i.loli.net/2019/05/08/5cd24e0a9c803.png)


#### 2.5  Interface call case

Reference Demo source code FingerBYDBigActivity.java

