# HX UHF

## 1、SDK Instruction

 1.1 Support B class,C Class etc. Normal label ;
 
 1.2 HX UHF SDK Compatible Machines Please Check: [SDK Compatible Machines Instruction (https://coding.net/u/CoreWise/p/SDK/git)

 1.3 [HX SDK download website](https://coding.net/u/CoreWise/p/SDK/git)
 
 1.4 HX UHF needs to rely on serial port development kit
 
## 2、 Secondary Development Instruction  

### 2.1 Android Studio Programme Configuration Description:

- 1. Add development kit aar to libs 

- 2. Configuration module ‘s build.gradle, References are as follows:

```
...
 //2.must 2
 repositories {
     flatDir {
         dirs 'libs'   // aar catalog     }
 }

 dependencies {
     ...
    //Port SDK
    //SerialPort SDK
    compile(name: 'serialport_sdk_20190429', ext: 'aar')
    
//HX UHF SDK ,need serial port SDK
    //HX UHF SDK,need Seria lPort SDK
    compile(name: 'hxuhf_sdk_20190429', ext: 'aar')
 }
```

### 2.2  Interface Instruction

**HX UHF: UHFHXAPI**
The main method, to realize UHF function must be used

| Interface Name | Description                    |
| -------------------- | ----------------------------------------------- |
| UHFHXAPI             | *Initialization api                                      |
| openHXUHFSerialPort  | *Open Port                                  |
| closeHXUHFSerialPort | *Close Port                                     |
| open                 | *Turn on UHF Module                                 |
| close                | *Turn off UHF Module                                |
| startAutoRead2A      | *Start the automatic tag reading operation during the list rounds, and the tag ID is sent back to the user through the notification package.|
| startAutoRead2C      | *Inventory tags, Will stop,when reading tags, and return the need red data |
| readTypeCTagData     | *Reading Class C Label Data                            |
| writeTypeCTagData    | *Write Class C Label Data                               
| arguments            | *Reading and writing tags into parameters (must)                     |
| setRegion            | *Setting the current area                                |

---
Standby method, used according to requirement

| Interface Name                | Description                                                    |
| -------------------------- | ------------------------------------------------------------ |
| startAutoRead2             | Start automatic tag reading operation, and the tag ID is sent back to the user through the notification package.           |
| stopAutoRead2              | Stop automatic read2 operation                                          |
| readEPC                    | Read EPC region                                               |
| readTID                    | Read TID region                                                |
| stopAutoRead               | Stop automatic tag reading operation                                        |
| getReaderInformation       | Get Basic Information from Reader                                     |
| getRegion                  | Get the current region                                              |
| setSystemReset             | Setting system level reset                                       |
| getTypeCAISelectParameters | Get 18000-6C Air Interface Protocol Command'Select'parameter
                      |
| setTypeCAISelectParameters | 设置18000-6C空中接口协议命令'选择'参数                       |
| getTypeCAIQueryParameters  | 获取18000-6C空中接口协议命令'查询'参数                       |
| setTypeCAIQueryParameters  | 设置18000-6C空中接口协议命令'查询'参数                       |
| getCurrentRFChannel        | 获取射频频道，该命令仅对非FH模式有效                         |
| setCurrentRFChannel        | 设置射频通道， 该命令仅对非FHSS模式有效                      |
| getFHAndLBTParameters      | 获取FH和LBT控制                                              |
| setFHAndLBTParameters      | 设置FH和LBT参数                                              |
| getTxPowerLevel            | 获取当前Tx功率水平                                           |
| setTxPowerLevel            | 设置当前Tx功率电平                                           |
| RF_CW_SignalControl        | 打开/关闭连续波（CW）信号， 该命令包仅对空闲模式有效         |
| readTypeCUII               | 读取EPC块（PC + EPC）                                        |
| getFrequencyHoppingTable   | 获取当前跳频表                                               |
| setFrequencyHoppingTable   | 设置当前跳频表                                               |
| getModulationMode          | 获取当前调制模式。 调制模式是Rx调制类型和BLF的组合           |
| setModulationMode          | 获取当前调制模式。 调制模式是Rx调制类型和BLF的组合           |
| getAntiCollisionMode       | 获取防冲突算法                                               |
| setAntiCollisionMode       | 设置防冲突算法                                               |
| blockWriteTypeCTagData     | Blockwrite类型C标签数据                                      |
| blockEraseTypeCTagData     | 块擦除C类标签数据                                            |
| blockPermalockTypeCTag     | BlockPermalock C型标签                                       |
| killTypeCTag               | 删除标签                                                     |
| lockTypeCTag               | 锁定标签中指示的存储库                                       |
| getTemperature             | 获取当前温度                                                 |
| getRSSI                    | 获取RSSI级别                                                 |
| scanRSSI                   | 扫描所有通道的RSSI级别                                       |
| updateRegistry             | 设置注册表更新功能                                           |
| eraseRegistry              | 设置注册表擦除功能                                           |
| getRegistryItem            | 获取注册表项                                                 |


**具体说明:**

- openHXUHFSerialPort

  打开超高频串口模块，建议在onResume中实现
  
- closeHXUHFSerialPort

  关闭超高频串口模块，建议在onPause中实现，与 openHXUHFSerialPort 对应

- open

  开启超高频模块

- close

  关闭超高频模块,与open对应

- startAutoRead2A(AutoRead autoRead)

  开始盘点，在清单轮次期间启动自动标签读取操作，标签ID通过通知包发送回用户
  
  * @param autoRead 收到数据回调监听

- startAutoRead2C(int times, int code, String pwd,
  int sa, int dl, SearchAndRead Interface)
  
  盘点标签，读到标签就停，并且返回需要读到的数据
  
  * @param times 多少秒无数据停止
  * @param code  需要读的区域 0:读取EPC,1:读取TID
  * @param pwd   标签访问密码
  * @param sa    偏移长度
  * @param dl    要读的长度
  * @param Interface 收到数据回调监听

- readTypeCTagData(byte[] arguments)

  读取标签,传参建议使用方法 arguments 赋值
  
  * @param arguments 发送的指令
  * @return 读到的数据

- writeTypeCTagData(byte[] arguments)

  写入标签，传参建议使用方法 arguments 赋值
  
  * @param arguments 发送的指令
  
- arguments(String pwd, short epcLength, String epc, byte mb,int sa, int dl)

  发送的指令  
  
  * @param pwd 标签访问密码
  * @param epcLength  标签id长度
  * @param epc 标签id
  * @param mb  需要读、写的区域 0:EPC,1:TID,2:User
  * @param sa  偏移长度
  * @param dl  要读的长度
  
- setRegion(int argument)

  设置当前区域。
  
   * - Korea (0x11)<br>
   * - US (0x21)<br>
   * - US2 (0x22)<br>
   * - Europe (0x31)<br>
   * - Japan (0x41)<br>
   * - China1 (0x51)<br>
   * - China2 (0x52)<br>

**监听回调接口说明:**

```
//开始盘点
 api.startAutoRead2A(new UHFHXAPI.AutoRead() {

                @Override
                public void timeout() {
                    //超时
                }


                @Override
                public void start() {
                    //开始盘点
                }


                @Override
                public void processing(byte[] data) {
                    //获取数据
                    //标签id
                    String epc = DataUtils.toHexString(data).substring(4);
                }

                @Override
                public void end() {
                    //结束,根据需求重开线程
                }

            });

```

### 2.3 接口调用流程

![HXUHF.png](https://i.loli.net/2019/05/08/5cd24de943c73.png)


### 2.4 接口调用案例

请按步骤实现方法。

```
    protected ExecutorService pool;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxuhf_activity);
        
        api = new UHFHXAPI();//步骤:1
        
        //超高频可控开关，可按需求默认打开
        view.onClick( .. api.open();//步骤:3)
        
        //点击盘点操作,可按需求换成其他操作
        view.onClick( .. pool.execute(task);//步骤:4)
    }

    @Override
    protected void onResume() {
        super.onResume();
        pool = Executors.newSingleThreadExecutor();//步骤:2
        api.openHXUHFSerialPort();
    }

    @Override
    protected void onPause() {
        api.close();//步骤:5
        api.closeHXUHFSerialPort();//步骤:6
        pool.shutdown();
        pool = null;
        super.onPause();
    }
    
    //循环盘点操作
     private Runnable task = new Runnable() {
    
            @Override
            public void run() {
    
                api.startAutoRead2A(new UHFHXAPI.AutoRead() {
    
                    @Override
                    public void timeout() {
                        Log.i("zzdstartAutoRead", "timeout");
                    }
    
    
                    @Override
                    public void start() {
                        //load = soundPool.load(getApplicationContext(), R.raw.ok, 1);
                        Log.i("zzdstartAutoRead", "start");
                        startTime = System.currentTimeMillis();
                    }
    
    
                    @Override
                    public void processing(byte[] data) {
                        String epc = DataUtils.toHexString(data).substring(4);
                        long l = System.currentTimeMillis() - startTime;
                        readTime.put(epc, l);
                        hMsg.obtainMessage(MSG_SHOW_EPC_INFO, epc).sendToTarget();
                        Log.i("zzdstartAutoRead", "data=" + epc + "    time=" + l);
                    }
    
                    @Override
                    public void end() {
                        Log.i("zzdstartAutoRead", "end");
                        Log.i("zzdstartAutoRead", "isStop=" + isStop);
                        Log.e("zzdstartAutoRead", "===================================================================================");
                        if (!isStop) {
                            pool.execute(task);
                        } else {
                            hMsg.sendEmptyMessage(INVENTORY_OVER);
                        }
                    }
    
                });
            }
        };

```

## 3、开发问题汇总

1、问：demo标签读取为什么读不了？

答：需要先开启超高频模块，搜索到标签，**再选择标签**，标签读取显示出标签id说明获取成功。
选择需要读取区域、访问密码（默认 0000 0000），偏移地址，**数据长度**,点击读取。***标签写入同理***

2、问：demo写入标签为什么一直显示输入长度不对？

答：写入长度需要先选择数据长度，例如：长度为2，就需要输入 0001 0011，**1长度 = 4个16位数字**，当前demo只支持16位输入
