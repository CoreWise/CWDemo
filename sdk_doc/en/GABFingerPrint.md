# GAB Large fingerprint


* [1.GAB Large fingerprint development kit description](#111)
* [2.Secondary development description](#112)
  * [2.1 AndroidStudio Engineering configuration description](#113)
  * [2.2 AndroidManifest.xml Configuration instructions](#114)
  * [2.3 Interface instructions](#115)
  * [2.4 Interface invocation process](#116)
* [3.Summary of secondary development problems](#117)

<a name="111"></a>
### GAB Large fingerprint development kit description

   1.1 Support GAB fingerprint module;

   1.2 The GAB fingerprint feature occupies the only USB port on the machine

   1.3 GAB large fingerprint development kit compatible machine please check:
   [GAB large fingerprint development kit compatible machine please check](https://github.com/CoreWise/CWDemo#user-content-zh)

   1.4
   [GAB large fingerprint development kit download address](https://github.com/CoreWise/CWDemo#user-content-zh)

<a name="112"></a>
### Secondary development description

   Since the Qualcomm CPU of this machine only supports one USB port, when using the GAB large fingerprint module, it is necessary to first call the USB management class to switch the USB to the fingerprint module. At this time, USB cannot be used for charging, data line communication and other operations under normal circumstances.
   In this case, USB data line debugging can not be used, it is recommended that the network adb debugging;

   Android Studio is recommended to install the Android Wifi adb plug-in for adb debugging;
   
<a name="113"></a>
#### AndroidStudio Engineering configuration description

- 1.Add aar to the project libs directory

- 2.Configure Moudle's build.gradle, as shown below:


```
...
 android {
     ...
     defaultConfig {
         ...
         targetSdkVersion 22 //Identity card function must be reduced by 22
         ...
     }
     ...
 }
 //2.Must 2
 repositories {
     flatDir {
         dirs 'libs'   // aar directory
     }
 }

 dependencies {
     ...
    //GAB fingerprint development kit (new firmware development kit)
    //GAB FingerPrint SDK
    compile(name: 'fp_gab_sdk_20190429', ext: 'aar')

 }

```
<a name="114"></a>
#### AndroidManifest Configuration instructions


```xml

<uses-feature android:name="android.hardware.usb.host"
android:required="true" />

<uses-permission
android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

<a name="115"></a>
#### Interface instructions


**USB management class**

| USBFingerManager API interface | Interface instructions |
| :----- | :---- |
| USBFingerManager | USB management class,singleton pattern |
| USBFingerManager.getInstance(this) | Gets an instance of the USB management class，singleton pattern |
| USBFingerManager.getInstance(this).setDelayMs(ms) | Set the delay after cutting to the fingerprint module, default 1000ms |
| USBFingerManager.getInstance(this).openUSB(OnUSBFingerListener()) | Cut the USB into the fingerprint module |
| USBFingerManager.getInstance(this).closeUSB(); | Cut USB to normal mode |

OnUSBFingerListener Callback interface description:
- onOpenUSBFingerSuccess(String device, UsbManager mUsbManager, UsbDevice mDevice):

    Switch USB to fingerprint module successfully, return the name of the current fingerprint module

- onOpenUSBFingerFailure(String error):

    Failed to switch USB to fingerprint module, return error code



---

**GAB Large fingerprint class**


| FingerprintScanner API Interface | Interface instructions |
| :----- | :---- |
| getInstance  | Gets the terminal serial number |
|open |Get device model
|close|Get the Android version number
|prepare|Ready to capture fingerprint
|finish|End of fingerprint collection
|getDriverVersion|Get the fingerprint device driver version
|getSN|Gets the fingerprint device serial number
|getSensorName|Gets the name of the fingerprint sensor
|capture|Capture one Frame of Fingerprint Image in  FingerprintImage type

Specify:

- public static FingerprintScanner getInstance(Contextcontext);

  Function description: An instance to get FingerprintScanner type fingerprint device control
  
  Parameter: context application context 
  
  Return value: the FingerprintScanner control instance
  
  
- public int open(); 

    Power on fingerprint device

- public int close(); 

    Power off fingerprint device

- public int prepare(); 

    Ready to capture fingerprint

- public int finish(); 

    End of fingerprint collection

- public Result capture(); 

    Capture one Frame of Fingerprint Image in  FingerprintImage type
    
    Contains Fingerprint Image in FingerprintImage type and error code
    

| FingerprintImage API Interface | Interface instructions |
| :----- | :---- |
| raw[domain]  | Fingerprint image raw data（byte[]） |
|width[domain]|Fingerprint image width（int）
|height[domain] |Height of fingerprint image（int）
|dpi[domain] |Fingerprint image DPI（int）
|convert2Bmp|Convert fingerprint image to BMP image byte sequence

Specify:

- public byte[] convert2Bmp();

    Convert fingerprint image to BMP image byte sequence
    
    byte[]Type BMP image byte sequence
    

| Bione API Interface | Interface instructions |
| :----- | :---- |
| initialize  | Initialize Bione algorithm |
|exit|End Bione algorithm and perform cleanup
|getVersion |Gets the current algorithm version information 
|getFingerprintQuality  |Gets quality of the specified Fingerprint Image in FingerprintImage 
|extractFeature |Extract features from the Fingerprint Image in FingerprintImage
|makeTemplate|Synthesize template data from three fingerprint characteristics of the same finger 
|isFreeID|Determines whether the ID has been registered in the current fingerprint database
|getFreeID |Gets an unused id value in the current fingerprint library
|getFeature|Gets the fingerprint feature or template corresponding to the specified id value in the current fingerprint library
|getFeature|Gets all fingerprint characteristics or templates in the current fingerprint library
|getEnrolledCount|Gets the number of registered fingerprint characteristics or templates in the current fingerprint library
|enroll|Register fingerprint characteristics into the current fingerprint database
|delete|Delete the fingerprint feature corresponding to the specified id in the current fingerprint library
|clear|Empty the current fingerprint database
|verify|Compare the fingerprint feature corresponding to id in the current fingerprint database with the target fingerprint feature
|verify|Match two fingerprint features
|identify|Search the current fingerprint library, query the id corresponding to the matching fingerprint characteristics and return the result
|idcardVerify|Compare the fingerprint characteristics in the second generation id card with the fingerprint characteristics taken by the device
|idcardIdentify|Match the fingerprint feature set provided by the user and the fingerprint feature taken by the device
|setSecurityLevel|Set the security level for fingerprint matching

Specify:

- public static int initialize(Context context,String dbPath);

  Initialize Bione algorithm
  
  Context current application context 
  
  DbPath fingerprint library file path return value: error code
    
- public static int  exit(); 

    End the Bione algorithm and execute the clean up error code

- public static int getFingerprintQuality(FingerprintImage image);

  Gets quality of the specified Fingerprint Image in FingerprintImage
  
  Parameter: imageFingerprintImage type fingerprint image object
  
  Return value: >=0 fingerprint image quality value, <0 error code
  
  
- public static Result extractFeature(FingerprintImage image);

  Extract features from the FingerprintImage FingerprintImage object
  
  Parameter: imageFingerprintImage type fingerprint image object
  
  Return value: contains byte[] type fingerprint signature data and error code
  
- public Result makeTemplate(byte[] feature1,byte[]
  feature2,byte[] feature3);
  
  Feature1 fingerprint feature data collected for the first time from three fingerprint feature synthesis templates of the same finger
  
  Feature2 Second fingerprint signature data from the same finger
  
  feature3 The fingerprint characteristic data of the third collection of the same finger
  
  Contains byte[] type fingerprint template data and error code


- public static boolean isFreeID(int id);

    Determines whether the id has been registered in the current fingerprint database 
    
    Parameter: id needs to determine whether the id can be used for registration
    
    If the return value is true, the id has not registered the fingerprint and can be registered
    
    If the return value is false, the id has registered a fingerprint or the id is invalid and a fingerprint cannot be registered

- public static int getFreeID();

    Function description: get an unused id value in the current fingerprint library
    
    Return value: >=0 available for registration id, <0 error code


- public static getFeature(int id); 

  Function description: get the fingerprint feature or template corresponding to the specified id value in the current fingerprint library
  
  Parameter: id the id value of the fingerprint feature to be obtained contains byte[] type fingerprint feature or template data and error code


- public static Result getAllFeatures();

  Function description: get all fingerprint features or templates in the current fingerprint library
  Return value: Result containing Map type fingerprint signature or template data and error code (see Bione error code)
  The instance

- public static int getEnrolledCount();

    Function description: get the number of registered fingerprint features or templates in the current fingerprint database. Return value: >= 0 the number of fingerprint features or templates registered in the current library

< 0 error code

- public static int enroll(int id, byte[] feature);

    Function description: register fingerprint characteristics into the current fingerprint library
    
    Parameter: id needs to register the fingerprint id value in the current library

Feature needs to register the fingerprint feature or template data in the database
    
    Return value: the error code indicates that the id value must be unique in the current library. If necessary, the isFreeID method can be used to determine whether the id has been registered or used

The getFreeID method requests an unused id value for registration


- public static int delete(int id);

    Function description: delete the fingerprint characteristics corresponding to the specified id in the current fingerprint library
    
   Parameter: id the fingerprint feature or template id callback that needs to be removed from the current library: error code


- public static int clear();

    Function description: clear the current fingerprint feature library
    
    And the number: nothing
    
    Return value: error code

- public static Result verify(int id, byte[] feature);

    35/5000  
Function description: compare the fingerprint feature corresponding to id in the current fingerprint database with the target fingerprint feature
    
    Parameters: fingerprint id in the fingerprint database that needs matching for id, and feature needs matching for target fingerprint feature data
    
    Return value: contains Boolean type alignment result and error code


- public static Result verify(byte[] feature1, byte[] feature2);

    Function description: match two fingerprint features
    
    Parameters: feature1 fingerprint feature1, feature2 fingerprint feature2
    
    Return value: contains Boolean type alignment result and error code


- public static int identify(byte[] feature);

    Function description: search the current fingerprint library, query the id corresponding to the matching fingerprint characteristics and return the result
    
    Parameter: the fingerprint feature or template that feature wants to match in the current library
    
    Return value: >= 0 matches successfully, return matching id, < 0 error code


- public static Result idcardVerify(byte[] idcardFeature, byte[] feature);

    Function description: compare the fingerprint characteristics in the second generation id card with the fingerprint characteristics taken by the device
    
    Parameter: fingerprint characteristics of idcardFeature ii idcard
    
    Feature - fingerprint feature taken by the device
    
    Return value: Result instance with Boolean type alignment and error code: none. <

0 error code


- public static Result idcardIdentify(Map<String, byte[]> idcardFeatureMap, byte[] feature);

    Function description: compare whether the fingerprint feature set provided by the user and the fingerprint feature taken by the device match
    
   Parameter: idcardFeatureMap to perform 1:N comparison of the second generation identity card fingerprint feature set
    
    Feature - fingerprint feature taken by the device
    
    Return value: contains String type match result and error code

- public static void setSecurityLevel(int level);

    Function description: set the security level of fingerprint comparison
    
    Parameters: the security level of level fingerprint comparison (HIGH, MEDIUM, LOW)




**Error code**


| FingerprintScanner Error code definition | error code |error explanation| 
| :----- | :---- |:----|
| RESULT_OK | 0 |Operation is successful
| RESULT_FAIL | -1000 |The operation failure
| WRONG_CONNECTION | -1001 |Device connection error
| DEVICE_BUSY | -1002 |The device is busy
| DEVICE_NOT_OPEN | -1003 |Device not open
| TIMEOUT  | -1004 |timeout
| NO_PERMISSION  | -1005 |Unauthorized
|WRONG_PARAMETER |-1006|Parameter error
|DECODE_ERROR |-1007|decoding error
|INIT_FAIL |-1008|Initialization error
|UNKNOWN_ERROR |-1009|An unknown error
|NOT_SUPPORT |-1010|not support
|NOT_ENOUGH_MEMORY |-1011|Out of memory
|DEVICE_NOT_FOUND |-1012|No supported device was found
|DEVICE_REOPEN |-1013|Device repeat open
|NO_FINGER|-2005|No finger detected





| Bione Error code definition | error code |error explanation| 
| :----- | :---- |:----|
| RESULT_OK | 0 |Algorithm operation successful
|INITIALIZE_ERROR |-2000|Algorithm initialization failed
|INVALID_FEATURE_DATA |-2001|Incorrect eigenvalue format
|BAD_IMAGE |-2002|Fingerprint image quality is poor
|NOT_MATCH |-2003|It's not the same fingerprint
|LOW_POINT|-2004|Three fingerprints do not match when the fingerprint score is low by one-to-one comparison or when the template is synthesized
|NO_FINGER |-2005|Input image fingerless
|NO_RESULT |-2006|A one-to-many ratio does not return a match
|OUT_OF_BOUND|-2007|Specify ID out of bounds (<0 or >= maximum fingerprint library capacity)
|DATABASE_FULL |-2008|The fingerprint library is full
|LIBRARY_MISSING |-2010|not find my library
|UNINITIALIZE |-2011|The algorithm library is not initialized
|REINITIALIZE |-2012|Algorithm library repeated initialization
|REPEATED_ENROLL |-2013|The specified ID has been registered with the fingerprint
|NOT_ENROLLED|-2014|The specified ID is not registered


<a name="116"></a>
#### Interface invocation case

Refer to Demo source code


<a name="117"></a>
#### Summary of secondary development problems


- Fingerprint class application in the horizontal and vertical screen switch, if repeated initialization module caused a series of problems?

    ```
    Add something under the appropriate Activity TAB in the manifest file:
    android:configChanges="orientation|keyboardHidden|screenSize"


    ```