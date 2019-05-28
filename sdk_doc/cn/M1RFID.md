
# RFID读M1卡功能


* [1.RFID读M1卡开发包说明](#RFID读M1卡开发包说明)
* [2.二次开发说明](#二次开发说明)
  * [2.1 AndroidStudio工程配置说明](#AndroidStudio工程配置说明)
  * [2.2 AndroidManifest.xml配置说明](#AndroidManifest配置说明)
  * [2.3 接口说明](#接口说明)
  * [2.4 接口调用流程](#接口调用流程)
  * [2.5 接口调用案例](#接口调用案例)
* [3.二次开发问题汇总](#二次开发问题汇总)

### RFID读M1卡开发包说明

   1.1 支持读M1卡号、对块号读写;

   1.2 RFID读M1卡功能占用了/dev/ttyHSL1串口，波特率115200,需要依赖[串口开发包](https://github.com/CoreWise/CWDemo#user-content-zh);

   1.3 RFID读M1卡开发包兼容机器请查看: [RFID读M1卡开发包兼容机器说明](https://github.com/CoreWise/CWDemo#user-content-zh)

   1.4 [RFID读M1卡开发包下载地址](https://github.com/CoreWise/CWDemo#user-content-zh)


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
    compile(name: 'm1rfid_sdk_20190521', ext: 'aar')

 }

```


#### AndroidManifest配置说明

```

<!--M1 RFID权限-->


```



#### 接口说明


**身份证类: AsyncM1Card**


| API接口 | 接口说明 |
| :----- | :---- |
| AsyncM1Card(getMainLooper()) | 构造函数 |
| openM1RFIDSerialPort() | 打开M1 RFID串口 |
| closeM1RFIDSerialPort() | 关闭M1 RFID串口 |
| readCardNum() | 读M1卡号 |
| read(...) | 读块号 |
| write(...) |写块号|
| updatePwd() |更新控制块密码(只适用于CFON640,U3该功能以及集成在写块里)|
| setOnReadCardNumListener ||
| setOnWriteAtPositionListener ||
| setOnReadAtPositionListener ||






**具体说明:**

- public AsyncM1Card(Looper looper)

  ```

    构造函数
    Looper: getMainLooper()

  ```

- public void openM1RFIDSerialPort(int type)

  ```
  打开RFID M1模块
  type: 传入的机器型号,比如 cw.Device_U8,
        本Demo里的cw.getDeviceModel()为自动判别机器型号,
        建议直接使用型号

  ```


- public void closeM1RFIDSerialPort(int type)

  ```
  关闭RFID M1模块
  type: 传入的机器型号,比如 cw.Device_U8，
        本Demo里的cw.getDeviceModel()为自动判别机器型号,
        建议直接使用型号
  ```


- public void readCardNum()

  ```
  读卡号，结果在setOnReadCardNumListener在监听回调里返回

  ```

- public void setOnReadCardNumListener(OnReadCardNumListener onReadCardNumListener)

  ```
  读卡号结果监听回调

  - public void onReadCardNumSuccess(String num){

    //读卡号成功，num卡号

  }

  - public void onReadCardNumFail(int confirmationCode) {

    //读卡号失败，confirmationCode错误码

  }

  ```


- public void read(int block, int keyType, int num, String keyA, String keyB)

  ```
  读块号，结果在setOnReadAtPositionListener在监听回调里返回
  block:块号
  keytype:密码类型
  num:执行次数，默认是1次
  keyA:密码A
  keyB:密码B

  ```


- public void setOnReadAtPositionListener(OnReadAtPositionListener onReadAtPositionListener)

  ```
  读块号监听结果回调

  - public void onReadAtPositionSuccess(String num){

    //读块号成功，num卡号

  }

  - public void onReadAtPositionFail(int confirmationCode) {

    //读块号失败，confirmationCode错误码

  }

  ```

- public void write(int block, int keyType, int num, String keyA, String keyB, String data)

  ```
  写块号，结果在setOnReadAtPositionListener在监听回调里返回
  block:块号
  keytype:密码类型
  num:执行次数，默认是1次
  keyA:密码A
  keyB:密码B
  data:要写入的数据
  ```



- public void setOnWriteAtPositionListener(OnWriteAtPositionListener onWriteAtPositionListener)

  ```
  写块号监听结果回调

  - public void onWriteAtPositionSuccess(String num){

    //读块号成功，num卡号

  }

  - public void onWriteAtPositionFail(int confirmationCode) {

    //读块号失败，confirmationCode错误码

  }

  ```



#### 接口调用流程

![RFID M1](https://i.loli.net/2019/05/28/5cecdc412761c59119.png)


#### 接口调用案例

参考Demo源码 , [RFIDM1Activity.java](https://github.com/CoreWise/CWDemo/blob/master/app/src/main/java/com/cw/demo/m1/RFIDM1Activity.java)


#### 二次开发问题汇总


