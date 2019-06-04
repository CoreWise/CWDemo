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
| setTypeCAISelectParameters | Setting 18000-6C Air Interface Protocol Command'Select'Parameters
                     |
| getTypeCAIQueryParameters  | Obtain 18000-6C Air Interface Protocol Command'Query'Parameters
                   |
| setTypeCAIQueryParameters  | Setting 18000-6C Air Interface Protocol Command'Query'Parameters
                    |
| getCurrentRFChannel        | Get the radio frequency channel, this command is only valid for non-FH mode
                        |
| setCurrentRFChannel        | Set up radio frequency channel, this command is only valid for non-FHSS mode                  |
| getFHAndLBTParameters      | Obtain FH and LBT Control                                            |
| setFHAndLBTParameters      | Setting FH and LBT Parameters                                            |
| getTxPowerLevel            | Obtain current Tx power level                                          |
| setTxPowerLevel            | Setting current Tx power level                                          |
| RF_CW_SignalControl        | Turn on/off continuous wave (CW) signal, this command package is only valid for idle mode
         |
| readTypeCUII               | Read EPC block (PC + EPC)                                      |
| getFrequencyHoppingTable   | Obtaion current Frequency hopping table                                             |
| setFrequencyHoppingTable   | Setting current Frequency hopping table                                             |
| getModulationMode          | Gets the current modulation mode. Modulation mode is a combination of Rx modulation type and BLF
         |
| setModulationMode          | Gets the current modulation mode. Modulation mode is a combination of Rx modulation type and BLF     
    |
| getAntiCollisionMode       | Get Acquisition of Anti-Conflict Algorithms                                              |
| setAntiCollisionMode       | Setting Anti-Conflict Algorithms                                        |
| blockWriteTypeCTagData     | Blockwrite Type C Label Data                          |
| blockEraseTypeCTagData     | Block Erase Class C Label Data                                         |
| blockPermalockTypeCTag     | BlockPermalock Class C Label                                 |
| killTypeCTag               | Delete a Label                                         |
| lockTypeCTag               | Lock the repository indicated in the label                                     |
| getTemperature             | Take the current temperature                                               |
| getRSSI                    | Get RSSI level                                            |
| scanRSSI                   | Scanning RSSI levels for all channels                                     |
| updateRegistry             | Setting up Registry Update Function                                        |
| eraseRegistry              | Setting Registry Erase Function                                          |
| getRegistryItem            | Get registry entries                                              |


**Specific Instructions:**

- openHXUHFSerialPort

  Open the UHF Serial Port Module and recommend to implement it in onResume
  
- closeHXUHFSerialPort

 Close the UHF Serial Port module and recommend to implement it in onPause, corresponding to openHXUHFSerial Port

- open

  Turn on UHF Module

- close

  Turn off UHF module, corresponding to open

- startAutoRead2A(AutoRead autoRead)

  Start the inventory, start the automatic tag reading operation during the list rounds, and the tag ID is sent back to the user through the notification package.
  
  * @param autoRead Receiving Data Callback Monitor

- startAutoRead2C(int times, int code, String pwd,
  int sa, int dl, SearchAndRead Interface)
  
  Inventory tags, stop reading tags, and return the data you need to read
  
  * @param times How many seconds no data stops
  * @param code  READING AREA 0: Read EPC, 1: Read TID
  * @param pwd   Label Access Password
  * @param sa    Offset length
  * @param dl    Length to read 
  * @param Interface Receiving Data Callback Monitor

- readTypeCTagData(byte[] arguments)

 Read labels, pass-on advice using arguments assignment
  
  * @param arguments Directives sent
  * @return Readable data

- writeTypeCTagData(byte[] arguments)

  Write labels, pass-on advice using arguments assignment
  
  * @param arguments Directives sent
  
- arguments(String pwd, short epcLength, String epc, byte mb,int sa, int dl)

  Directives sent
  
  * @param pwd Label Access Password
  * @param epc Length  Tag ID length
  * @param epc Tag ID
  * @param mb  Read and write areas 0:EPC, 1:TID, 2:User
  * @param sa  Offset length
  * @param dl  Length to read
  
- setRegion(int argument)

  Set the current area.
  
   * - Korea (0x11)<br>
   * - US (0x21)<br>
   * - US2 (0x22)<br>
   * - Europe (0x31)<br>
   * - Japan (0x41)<br>
   * - China1 (0x51)<br>
   * - China2 (0x52)<br>

**Instructions for Monitor callback interface:**

```
//Start counting
 api.startAutoRead2A(new UHFHXAPI.AutoRead() {

                @Override
                public void timeout() {
                    //overtime
                }


                @Override
                public void start() {
                    //Start counting
                }


                @Override
                public void processing(byte[] data) {
                    //get data
                    //Lable id
                    String epc = DataUtils.toHexString(data).substring(4);
                }

                @Override
                public void end() {
                    //End, restart threads as required
                }

            });

```

### 2.3 Interface invocation process

![HXUHF.png](https://i.loli.net/2019/05/08/5cd24de943c73.png)


### 2.4 Interface invocation case

Please follow the steps to implement the method.

```
    protected ExecutorService pool;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxuhf_activity);
        
        api = new UHFHXAPI();//Step: 1
        
        //UHF controllable switch can be turned on by default according to demand
        view.onClick( .. api.open();//Step:3)
        
        //Click on the inventory operation, you can switch to other operations as required.
        view.onClick( .. pool.execute(task);//Step:4)
    }

    @Override
    protected void onResume() {
        super.onResume();
        pool = Executors.newSingleThreadExecutor();//Step:2
        api.openHXUHFSerialPort();
    }

    @Override
    protected void onPause() {
        api.close();//Step:5
        api.closeHXUHFSerialPort();//Step:6
        pool.shutdown();
        pool = null;
        super.onPause();
    }
    
    //Cyclic Inventory Operation
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

## 3、Summary of development issues

1、Q：Why can't the demo tag read?

A：It is necessary to open the UHF module, search the label, select the label, and read the label to show the ID description of the label to achieve success.

Select the area to read, access password (default 0000,000), offset address, ** data length ** and click Read. *** Labels are written in the same way***

2、Q：Why does the demo write tag always show the wrong input length?

A：Write length needs to select data length first, for example: length 2, you need to input 0001 0011, ** 1 length = 4 16 digits **, currently demo only supports 16 digits input.
