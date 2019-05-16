# BarCode Function


* [1.Pruduct Function SDK Instruction](#111)
* [2.Secondary Development Instruction](#112)
  * [2.1 Android Studio Programme Configuration Description](#113)
  * [2.2 AndroidManifest Configuration description](#114)
  * [2.3 Interface Instruction](#115)
  * [2.4 Interface call process](#116)
  * [2.5 Interface Call Case](#117)
* [3.Summary of Secondary Development Problems](#118)

<a name="111"></a>
### 1. Instructions for Bar Code scanning SDK

   1.1 Support 1D and 2D Bar code scanner;

   1.2 To develop Bar code scanner function, it is necessary to install the **Scan Settings** of the corresponding model device!! Please first to check whether the device has installed the Scan Settings, if not, please go to the below download address to download and install it in the device;

   1.3 Which device models Bar code scanning SDK is compatible with, you can check: [Instructions for compatible device model of Bar code scanning SDK](https://github.com/CoreWise/CoreWiseDemo#user-content-zh)

   1.4 [Download address of Bar code scanning SDK](https://github.com/CoreWise/CoreWiseDemo#user-content-zh)

   1.5 **Specially illustrate:**

       The Zebra SE4710 barcode scanning dock used in the U1 model and HoneyWell N6603 barcode scanning dock used in the U8 model are both ** image barcode scanning modules **, which means that the hardware interface is the camera interface,
       In other words, at present in the secondary development does not support the opening of both the camera and ** image barcode scanning module **,
       We can adopt time-sharing multiplexing method for secondary development, that is, **call API to close the scanning dock before opening the camera, delay for a period of time, and then open the camera;
       exit camera, delay, and then open the scanning dock **! The process is as follows:

   ```

   - call API to close the scanning dock before opening the camera
   - delay for a period of time, advice to delay 500ms
   - open camera
   - exit camera
   - delay for a period of time, advice to delay 500ms
   - open the scanning dock
   ```

<a name="112"></a>
### 2.  Second Development Instruction

Important Note: To develop barcode function, it is necessary to install corresponding machine ** scanner settings **!! Please check whether the machine has installed the scanner settings, if not, please download via this address .



[U1 1D **scanner settings ** Download](https://coding.net/u/CoreWise/p/SDK/git/raw/master/U1-ue966ScannerSetting-release20190415.apk)

[U1 2D **scanner settings** download](https://coding.net/u/CoreWise/p/SDK/git/raw/master/U1-3680ScannerSetting-release20190415.apk)

U1 4710 Scanner settings are preset in the system

[U3 **scanner settings** download](https://coding.net/u/CoreWise/p/SDK/git/raw/master/u3-ScannerSetting-release20190428.apk)

[U8 **scanner settings** download](https://coding.net/u/CoreWise/p/SDK/git/raw/master/6603ScannerSettingRelease20190423.apk)


<a name="113"></a>
#### 2.1  Android Studio Programme Configuration Description:

- 1.Add development kit aar to libs 

- 2.Configuration module ‘s build.gradle, References are as follows


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
         dirs 'libs'   // aar catalog
     }
 }

 dependencies {
     ...
    //Barcode SDK
    //Barcode SDK
    compile(name: 'barcode_sdk_20190428', ext: 'aar')

 }
```

<a name="114"></a>
#### 2.2 AndroidManifest.xml Configuration description

```
<!--Barcode permission-->
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.WRITE_SETTINGS" />

```





<a name="115"></a>
#### 2.3  Interface Insturction

**Barcode Class: SoftDecodingAPI**


| API Interface | Interface Insturction |
| :----- | :---- |
|SoftDecodingAPI| Barcode Class
|openBarCodeReceiver|*Logon broadcasts, which need to appear in pairs, recommended to be added to onResume
|closeBarCodeReceiver|*Cancel broadcasting, recommend adding in onPause
|setGlobalSwicth|*Switch、Close the scanner 
|scan|* Starting scanning 
|~~closeScan~~|*Close Scan
|ContinuousScanning| Start continuous scanning 
|CloseScanning| close continuous scanning
|getTime| Obtaining continuous scanning space 
|setTime|Setting continuous scanning space 
|getSettings|Getting Scanner System Setting
|setSettings|Setting parameters of scanner
|isScannerServiceRunning|Check whether the scanner service is running

Specific Instructions

- public SoftDecodingAPI(Context context, SoftDecodingAPI.IBarCodeData inter)
    ```
    Barcode Constructor 1
    context:context
    inter:Callback of monitoring results
    ```
- public SoftDecodingAPI(Context context)
    ```
    Callback of monitoring results2
    context:context
    ```

- public void  setOnBarCodeDataListener(SoftDecodingAPI.IBarCodeData inter)
    ```
    Set up the callback of the listening result and use it in conjunction with constructor 2
    ```

- public void setGlobalSwicth(boolean status);
    ```
    Remote open and close  scanner setup service
    status:true ，false
    ```

- public void scan()
    ```
    Scaning,single scanning
    ```

- ~~public void closeScan()~~
    ```
    Close scanning ,discard
    ```

- public void openBarCodeReceiver()
    ```
    Logon broadcasts, which need to appear in pairs, recommended to be added to onResume
    ```

- public void closeBarCodeReceiver()
    ```
   Cancel broadcasting, recommend adding in onPause
    ```

- public boolean isScannerServiceRunning(Context context)
    ```
    Check whether the scanner service is running
    ```


- public void getSettings()
    ```
     Getting  Scanner System Settings
    ```

- public void setSettings(int PowerOnOff, int OutputMode, int TerminalChar, String Prefix, String Suffix, int Volume, int PlayoneMode)
    ```
     Setting sanner parameters 
    ```


<a name="116"></a>
#### 2.4 Interface call process




![Barcode.png](https://i.loli.net/2019/05/08/5cd24de928418.png)



<a name="117"></a>
#### 2.5  Interface Call Case

```java

import com.cw.barcodesdk.SoftDecodingAPI;


public class ScannerActivity extends Activity implements SoftDecodingAPI.IBarCodeData {


    SoftDecodingAPI api;

    @Nullable
    @Override
    public View onCreate(Bundle savedInstanceState) {
        api = new SoftDecodingAPI(getActivity(), this);
        //api=new SoftDecodingAPI(getActivity());
        //api.setOnBarCodeDataListener(this);

    }


    @Override
    public void sendScan() { 
        // Generally used to calculate scanning sum
    }

    @Override
    public void onBarCodeData(final String data) {
        // Get the scanner value, data is the scanned value

    @Override
    public void getSettings(int PowerOnOff, int OutputMode, int TerminalChar, String Prefix, String Suffix, int Volume, int PlayoneMode) {
        // Get the scanner current settings 
    }

    @Override
    public void setSettingsSuccess() {
        //Setting up Scanner ,Setting Successful Callback
    }


    @Override
    public void onResume() {
        super.onResume();
        api.openBarCodeReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        api.closeBarCodeReceiver();
    }


    // Click events
    public void onViewClicked(View view) {
        api.scan();
    }
}
```

<a name="118"></a>
### Summary of Secondary Development Problems