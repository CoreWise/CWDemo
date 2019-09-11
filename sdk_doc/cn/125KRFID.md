
# RFID读125K功能


* [1.RFID读125K卡开发包说明](#RFID读125K卡开发包说明)
* [2.二次开发说明](#二次开发说明)
  * [2.1 AndroidStudio工程配置说明](#AndroidStudio工程配置说明)
  * [2.2 AndroidManifest.xml配置说明](#AndroidManifest配置说明)
  * [2.3 接口说明](#接口说明)
  * [2.4 接口调用流程](#接口调用流程)
  * [2.5 接口调用案例](#接口调用案例)
* [3.二次开发问题汇总](#二次开发问题汇总)

### RFID读125K卡开发包说明

   1.1 支持读125K;

   1.2 RFID读125K卡功能占用了/dev/ttyHSL2串口，波特率9600,需要依赖[串口开发包](https://github.com/CoreWise/CWDemo#user-content-zh);

   1.3 RFID读125K卡开发包兼容机器请查看: [RFID读M1卡开发包兼容机器说明](https://github.com/CoreWise/CWDemo#user-content-zh)

   1.4 [RFID读125K卡开发包下载地址](https://github.com/CoreWise/CWDemo#user-content-zh)


### 二次开发说明

#### AndroidStudio工程配置说明

- 1.添加开发包aar到项目libs目录下

- 2.配置Moudle的build.gradle,参考如下:


```java

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
    //串口开发包
    //SerialPort SDK
    compile(name: 'serialport_sdk_20190429', ext: 'aar')

    //M1 RFID开发包,需要依赖串口开发包
    //M1 RFID SDK,need SerialPort SDK
    //只有20190911以后的M1开发包支持125K接口
    compile(name: 'm1rfid_sdk_20190911', ext: 'aar')

 }

```


#### AndroidManifest配置说明

```

<!--125K RFID权限-->


```



#### 接口说明


**身份证类: RFIDFor125KAPI**


| API接口 | 接口说明 |
| :----- | :---- |
| RFIDFor125KAPI() | 构造函数 |
| openRFID125K() | 打开125K RFID|
| closeRFID125K() | 关闭125K RFID |
|setReFreshTime(int reFreshTime)|刷新频率,单位ms，默认200ms|
|setOnResultListenner|结果回调|



**具体说明:**

- public RFIDFor125KAPI()

  ```

    构造函数

  ```

- public void openRFID125K()

  ```
  打开RFID 125K模块


  ```


- public void closeRFID125K()

  ```
  关闭RFID 125K模块

  ```
- public void setReFreshTime(int reFreshTime)

  ```
  设置刷新频率，单位ms，默认200ms

  ```  
  
  
- setOnResultListenner(OnResultListenner onResultListenner)

  ```
  结果回调，返回byte[]

  ```




#### 接口调用流程

```java
public class RFIDFor125KActivity extends AppCompatActivity {

    
    RFIDFor125KAPI rfidFor125KAPI;
    


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.实例化
        rfidFor125KAPI = new RFIDFor125KAPI();
        //2.设置监听接口
        rfidFor125KAPI.setOnResultListenner(new RFIDFor125KAPI.OnResultListenner() {
            @Override
            public void onReceive(final byte[] result) {
                Log.i(TAG, "result: " + DataUtils.bytesToHexString(result));
                //
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        //3.打开125K
        rfidFor125KAPI.openRFID125K();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        //4.关闭125K
        rfidFor125KAPI.closeRFID125K();

    }
    
}


```


#### 接口调用案例

参考Demo源码 , [RFIDFor125KActivity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/rfid/RFIDFor125KActivity.java)


#### 二次开发问题汇总


