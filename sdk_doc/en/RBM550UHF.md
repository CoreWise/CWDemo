# RBM550

## 1、SDK Instruction

 1.1 Support B class,C Class etc. Normal label ;
 
 1.2 RBM550 SDK Compatible Machines Please Check: [SDK Compatible Machines Instruction (https://coding.net/u/CoreWise/p/SDK/git)

 1.3 [HX SDK download website](https://coding.net/u/CoreWise/p/SDK/git)
 
 1.4 RBM550 needs to rely on serial port development kit
 
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
    compile(name: 'serialport_sdk_*', ext: 'aar')
    
    //RBM550 SDK ,need serial port SDK
    //RBM550 SDK,need Seria lPort SDK
    compile(name: 'rbm550uhf_sdk_*', ext: 'aar')
 }
```

### 2.2  Interface Instruction

**RBM550: UHFHXAPI**
The main method, to realize UHF function must be used

| Interface Name | Description                    |
| -------------------- | ----------------------------------------------- |
| openRBUHFSerialPort  | * open                                       |
| closeRBUHFSerialPort | * close                                       |
| reset                 | * reset                                 |
| setBaudRate                | * set uart baudrate                               |
| getVersion      | * get firmware version |
| setPower      | * set output power |
| inventory           | * inventory                                   |
| readArguments           | * Synthetic label read instruction                                  |
| readTypeC           | * read TypeC                                 |

---

Standby method, used according to requirement

| Interface Name                | Description                                                    |
| -------------------------- | ------------------------------------------------------------ |
| setCMDSystem             | cmd System             |
| getPower     | * get output power                                |
| setFrequencyRegion    | * set frequency region                             |
| getFrequencyRegion            | * get frequency region                     |

**Specific Instructions:**

-     /**
       * Open m550
       *
       * @param device
       * @return
       */
      public boolean openRBUHFSerialPort(int device)

- /**
       * Close
       */
      public void closeRBUHFSerialPort(int device)

-  /**
       * reset
       */
      public int reset()

-  /**
       * set baud rate
       *
       * @param BaudRate  0x03	38400  bps  RBUFHConfig.BaudRate_38400
       *                  0x04  115200 bps  RBUFHConfig.BaudRate_115200
       * @return
       */
      public int setBaudRate(byte BaudRate)

- /**
       * version
       *
       * @return
       */
      public String getVersion()

- /**
       * Power
       *
       * @param rfPower m550 Ranges 18 -26(0x12 – 0x1a)
       * @return
       */
      public int setPower(byte rfPower)

- /**
       * cmd_get_output_power
       *
       * @return
       */
      public CmdResponse getPower()

-  /**
       * cmd_set_frequency_region
       * 2 ways
       * region = 0x01 FCC | 0x02 ETSI | 0x03 CHN
       * region  + StartFreq  + EndFreq
       *
       * region = 0x04 User-defined spectrum
       * region  + FreqSpace  + FreqQuantity  + StartFreq
       * @param freq
       * @return
       */
      public int setFrequencyRegion(byte[] freq)

-  /**
       * cmd_get_frequency_region
       * @return
       */
      public CmdResponse getFrequencyRegion()

-    /**
        * inventory
        */
       public void inventory()

-   /**
        * read TypeC
        *
        * @param writeArguments
        */
       public void readTypeC(byte[] writeArguments)

-    /**
      * Synthetic label read instruction
      *
      * @param pwd password
      * @param mb  Label storage area
      * @param sa  Data first address
      * @param dl  Word length written
      * @return
      */
     public byte[] readArguments(String pwd, byte mb, int sa, int dl)

**Instructions for Monitor callback interface:**

```
- 1、
    /**
        * Read and write callback
        */
       public interface onReadWriteRespondListener {
           /**
            * Tag response successful
            *
            * @param address
            * @param cmd      Command code
            * @param tagCount The total number of tags successfully operated
            * @param data     Valid data of the operated label
            * @param epc      epc
            * @param readData Read data
            */
           void onRespondSuccess(String address, String cmd, String tagCount, String data, String epc, String readData);

           /**
            * Tag response failed
            *
            * @param errorCode 1 Data format is wrong  || 0 The packet header command code is incorrect
            * @param respond   raw data
            */
           void onRespondFailure(int errorCode, String respond);

           /**
            * Reader operation failed
            *
            * @param address
            * @param cmd
            * @param errorCode
            */
           void onReaderFailure(String address, String cmd, String errorCode);

           void TimeOut();
       }

- 2、
/**
     * Inventory callback
     */
    public interface onInventoryRespondListener {

        /**
         * Label reply
         *
         * @param address
         * @param cmd
         * @param freqAnt The high 6 bits of this byte are the frequency parameters of the read tag, and the low 2 bits are the antenna number
         * @param PC      Tag PC, fixed two bytes
         * @param EPC     The EPC number of the label can be changed in length
         * @param RSSI    Real-time RSSI of tags
         */
        void onRespondSuccess(String address, String cmd, String freqAnt, String PC, String EPC, String RSSI);

        /**
         * Tag response failed
         *
         * @param errorCode
         * @param respond   raw data
         */
        void onRespondFailure(int errorCode, String respond);

        /**
         * Reader operation successful
         *
         * @param address
         * @param cmd
         * @param antID     The antenna number used in this inventory
         * @param readRate  This round commands the tag recognition rate.
         * @param totalRead The total number of records of label responses
         */
        void onReaderSuccess(String address, String cmd, String antID, String readRate, String totalRead);

        /**
         * Reader operation failed
         *
         * @param address
         * @param cmd
         * @param errorCode
         */
        void onReaderFailure(String address, String cmd, String errorCode);

        void TimeOut();
    }

- 3、CmdResponse

        //Cmd command sent
        public byte sendCmd;
        //Return data length
        public int len = -1;
        //Return temp
        public byte[] temp;
        //Return data
        public String respondData;
        //Packet header
        public String responseHead;
        //Effective length
        public String responseLength;
        //Reader address
        public String responseAddress;
        //Cmd command code returned
        public String responseCmd;
        //Error message
        public byte responseErrorCode = 0x00;
```

### Interface invocation case

Please follow the steps to implement the method.

```

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxuhf_activity);

        api = new RBUFHAPI();;//Step:1

        //UHF controllable switch can be turned on by default according to demand
        view.onClick( .. api.openRBUHFSerialPort(cw.getDeviceModel());//Step:2)

        //Click on the inventory operation, you can switch to other operations as required.
        view.onClick( .. api.inventory();//Step:3)
    }

    @Override
    protected void onPause() {
        api.closeRBUHFSerialPort(cw.getDeviceModel());//Step:4
        super.onPause();
    }

```

## 3、Summary of development issues

1、Q：Why can't the demo tag read?

A：It is necessary to open the UHF module, search the label, select the label, and read the label to show the ID description of the label to achieve success.

Select the area to read, access password (default 0000,000), offset address, ** data length ** and click Read. *** Labels are written in the same way***

2、Q：Why does the demo write tag always show the wrong input length?

A：Write length needs to select data length first, for example: length 2, you need to input 0001 0011, ** 1 length = 4 16 digits **, currently demo only supports 16 digits input.
