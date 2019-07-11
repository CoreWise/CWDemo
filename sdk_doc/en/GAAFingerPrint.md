# GAA Fingerprint

### 1.  GAA Fingerprint Development Kit Instruction.

   1.1  Support GAA Fingerprint Module;

   1.2 GAA Fingerprint function occupies the only USB port of the machine

   1.3 GAA fingerprint development kit compatible machine, please check :[GAA fingerprint development kit compatible machine description](https://github.com/CoreWise/CWDemo#user-content-en)

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
       public static final int LIVESCAN_SUCCESS = 1;//Successful operation
       public static final int LIVESCAN_NOTINIT = -12;//Not initialized
       public static final int LIVESCAN_AUTH_FAILED = -13;//Authentication module verification failed
       public static final int LIVESCAN_AUTH_ENCRYPT_FAILED = -14;//Encryption algorithm failed
       public static final int LIVESCAN_UPIMAGE_FAILED = -15;//Uploading image failed
       public static final int LIVESCAN_NO_FINGER = -16;//No finger
       public static final int LIVESCAN_SEARCH_THRESHOLD = -17;//Search similarity is below threshold
       public static final int LIVESCAN_CLEAR_ERROR = -18;//Delete error
       public static final int LIVESCAN_ERROR_LENGTH = -19;//Insufficient length of entry
       public static final int LIVESCAN_NOTADD = -20;//Failed to add fingerprint feature value

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


| BYD FingerPrint API Port | Port Instruction |
| :----- | :---- |
| GAA_API| Constructor |
|openGAA|Open module|
|closeGAA|Close module|
|PSGetImage|GAA collects fingerprint images and saves them locally|
|PSUpImage |Upload GAA save local fingerprint image to view layer|
|PSGenChar |Extract feature values and save feature values|
|PSSearch|Search fingerprint|
|PSEmpty|Clear fingerprint library|
|DataToBmp|Fingerprint picture byte to bitmap|

Specific Instruction:

- public GAA_API(Context context)
    ```
    Constructor
    ```

- public int openGAA()
    ```
    Open module
    Return successfully:LIVESCAN_SUCCESS
    ```

- public int closeGAA()
    ```
    Close module
    ```

- public int PSGetImage()
    ```
    GAA module starts collecting fingerprint images And save it locally
    ```
- public int PSUpImage(byte[] pImageData)
    ```
    The GAA module will save the local fingerprint image to the app.

    byte[] pImageData  Incoming size needs to be greater than 256 * 360, return fingerprint image
    ```
- public int PSGenChar(byte[] mFeature,int[] id)
    ```
    Extract the feature values of the locally saved fingerprint image, save the feature values, and add the fingerprint library.

    byte[] mFeature  The incoming size needs to be greater than 512, returning the generated eigenvalue
    int[] id  Returns the id corresponding to the feature value
    ```

- public int PSGenChar(byte[] mFeature)
    ```
    Extract the feature value of the locally saved fingerprint image without saving the feature value

    byte[] mFeature  The incoming size needs to be greater than 512, returning the feature value of the saved fingerprint image.
    ```

- public int PSSearch(byte[] mFeature, int[] mId)
    ```
    Search fingerprint

    byte[] mFeature  Fingerprint feature value that needs to be searched
    int[] mId  Returned fingerprint index
    Return successfully:LIVESCAN_SUCCESS
    ```

- public int PSEmpty()
    ```
    Clear fingerprint library
    ```
- public Bitmap DataToBmp(byte[] fpRaw)
    ```
   Fingerprint picture byte to bitmap

   byte[] fpRaw  back Fingerprint picture
    ```


**Error Code**

Call this method,return information is error information
- mLiveScan.LIVESCAN_GetErrorInfo(iRet)

```
    public static final int LIVESCAN_NOTINIT = -12;//Not initialized
    public static final int LIVESCAN_AUTH_FAILED = -13;//Authentication module verification failed
    public static final int LIVESCAN_AUTH_ENCRYPT_FAILED = -14;//Encryption algorithm failed
    public static final int LIVESCAN_UPIMAGE_FAILED = -15;//Uploading image failed
    public static final int LIVESCAN_NO_FINGER = -16;//No finger
    public static final int LIVESCAN_SEARCH_THRESHOLD = -17;//Search similarity is below threshold
    public static final int LIVESCAN_CLEAR_ERROR = -18;//Delete error
    public static final int LIVESCAN_ERROR_LENGTH = -19;//Insufficient length of entry
    public static final int LIVESCAN_NOTADD = -20;//Failed to add fingerprint feature value
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

Reference Demo source code [NewFpGAAActivity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/fingerprint/gaa/NewFpGAAActivity.java)

