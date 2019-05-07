### 1. SY指纹开发包说明
 1.1 支持SY指纹模块;

 1.2 SY指纹开发包兼容机器请查看: [开发包兼容机器说明](https://coding.net/u/CoreWise/p/SDK/git)

 1.3 [SY指纹开发包下载地址](https://coding.net/u/CoreWise/p/SDK/git)

 1.4 SY指纹需要依赖串口开发包，占用/dev/ttyHSL0,波特率:460800


芯片内设有一个72K 字节的图像缓冲区与二个512 bytes(256 字)大小的特征文件缓冲区，名字分别称为：ImageBuffer，CharBuffer1，CharBuffer2。用户可以通过指令读写任意一个缓冲区。CharBuffer1 或CharBuffer2 既可以用于存放普通特征文件也可以用于存放模板特征文件。通过UART 口上传或下载图像时为了加快速度，只用到像素字节的高四位，即将两个像素合成一个字节传送。通过USB 口则是整8 位像素。指纹库容量根据挂接的FLASH 容量不同而改变，系统会自动判别。指纹模板按照序号存放，序号定义为：0—N-1（N 指指纹库容量）。用户只能根据序号访问指纹库内容。

### 2. 二次开发说明

#### 2.1 Android Studio工程配置说明

- 1.添加开发包aar到项目libs目录下

- 2.配置Moudle的build.gradle,参考如下:


```
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

    //SY指纹开发包,需要依赖串口开发包
    //SY FingerPrint SDK
    compile(name: 'finger_sy_sdk_20190429', ext: 'aar')

 }
```

#### 2.2 AndroidManifest.xml配置说明

```
<!--SY指纹权限-->


```

#### 2.3  接口说明

**条码类: FingerprintAPI**


| API接口 | 接口说明 |
| :----- | :---- |
|PSGetImage()|录入指纹图像
|PSGenChar(int bufferId)|
|PSRegModel()|合并特征生成模板
|PSStoreChar(int bufferId, int pageId)|
|PSLoadChar(int bufferId, int pageId)|
|PSSearch(int bufferId, int startPageId,int pageNum)|
|PSMatch()|精确比对
|PSEnroll()|采集一次指纹注册模板
|PSIdentify()|自动采集指纹
|PSDeleteChar(short pageIDStart, short delNum)|删除模板
|PSEmpty()|删除flash 数据库中所有指纹模板
|PSUpChar(int bufferId)|将特征缓冲区中的特征文件上传给上位机
|PSDownChar(int bufferId, byte[] model)|上位机下载特征文件到模块的特征缓冲区(默认的缓冲区为CharBuffer2)
|PS_ReadIndexTable(byte[] data, int pageId)|读取录入模版的索引表
|getValidId()|Get a can store fingerprint model ID(0~1009)
|PSUpImage()|将图像缓冲区的数据上传给上位机
|PSCalibration()|校准
|PSDownImage(byte[] image)|将图像数据下传到下位机


具体说明:

- int PSGetImage();

    ```
    /**
    * 录入指纹图像 功能说明： 探测手指，探测到后录入指纹图像存于ImageBuffer。 返回确认码表示：录入成功、无手指等。
    *
    * @return 返回值为确认码
    * 确认码=00H表示录入成功
    * 确认码=01H表示收包有错
    * 确认码=02H表示传感器上无手指
    * 确认码=03H表示录入不成功
    * 确认码=ffH 表示无响应
    */
    ```

- int PSGenChar(int bufferId);

    ```
    /**
    * 功能说明： 将ImageBuffer 中的原始图像生成指纹特征文件存于CharBuffer1 或CharBuffer2 输入参数：
    * BufferID(特征缓冲区号)
    *
    * @param bufferId（CharBuffer1:1h,CharBuffer2:2h）
    * @return 返回值为确认码
    * 确认码=00H表示生成特征成功
    * 确认码=01H表示收包有错
    * 确认码=06H表示指纹图像太乱而生不成特征
    * 确认码=07H表示指纹图像正常，但特征点太少而生不成特征
    * 确认码=15H表示图像缓冲区内没有有效原始图而生不成图像
    * 确认码=ffH表示无响应
    */
    ```

- int PSRegModel();

    ```
    /**
         * 合并特征生成模板，将CharBuffer1和CharBuffer2中的特征文件合并生成模板，
         * 结果存于CharBuffer1和CharBuffer2中。
         *
         * @return 返回值为确认码
    * 确认码=00H表示合并成功
    * 确认码=01H表示收包有错
    * 确认码=0aH表示合并失败（两枚指纹不属于同一手指）
         * 确认码=ffH 表示无响应
         */
    ```

- int PSStoreChar(int bufferId, int pageId);

    ```
        /**
         * 将CharBuffer中的模板储存到指定的pageId号的flash数据库位置 bufferId:只能为1h或2h
         * pageId：范围为0~1009 输入参数：BufferID(缓冲区号)，PageID（指纹库位置号）
         *
         * @return 返回值为确认码
    * 确认码=00H表示储存成功
    * 确认码=01H表示收包有错
    * 确认码=0bH表示PageID超出指纹库范围
    * 确认码=18H表示写FLASH出错
    * 确认码=ffH 表示无响应
         */
    ```

- int PSLoadChar(int bufferId, int pageId);

    ```

    /**
         * 将flash 数据库中指定pageId号的指纹模板读入到模板缓冲区CharBuffer1或CharBuffer2
         * bufferId:只能为1h或2h pageId：范围为0~1023 输入参数： BufferID(缓冲区号)，PageID(指纹库模板号)
         *
         * @param index
         *            pageId号
         * @return 返回值为确认码
    * 确认码=00H表示读出成功
    * 确认码=01H表示收包有错
    * 确认码=0cH表示读出有错或模板有错
    * 确认码=0BH表示PageID超出指纹库范围
    * 确认码=ffH 表示无响应
         */
    ```

- Result PSSearch(int bufferId, int startPageId,int pageNum);

    ```

    /**
         * 以CharBuffer1 或CharBuffer2 中的特征文件搜索整个或部分指纹库。若搜索到，则返回页码。 输入参数： BufferID，
         * StartPage(起始页)，PageNum（页数） 返回参数： 确认字，页码（相配指纹模板）
         *
         * @param bufferId
         *            缓冲区1h，2h
         * @param startPageId
         *            起始页
         * @param pageNum
         *            页数
         * @return 确认码=00H 表示搜索到；
    * 确认码=01H 表示收包有错；
         * 确认码=09H 表示没搜索到；此时页码与得分为0
         * 确认码=ffH 表示无响应
         */

    ```

- boolean PSMatch();

    ```

    /**
         * 精确比对CharBuffer1与CharBuffer2中的特征文件 注意点:下位机返回的数据里面还有一个得分，当得分大于等于50时，指纹匹配
         *
         * @return true：指纹匹配成功 false：比对失败
         */

    ```

- Result PSEnroll();

    ```


    /**
         * 采集一次指纹注册模板，在指纹库中搜索空位并存储，返回存储pageId 返回参数： 确认字，页码（相配指纹模板）
         * @return 确认码=00H 表示注册成功； 确认码=01H 表示收包有错； 确认码=1eH 表示注册失败。 确认码=ffH 表示无响应
         */

    ```


- Result PSIdentify();
    ```


    /**
         * 自动采集指纹，在指纹库中搜索目标模板并返回搜索结果。 如果目标模板同当前采集的指纹比对得分大于最高阀值，
         * 并且目标模板为不完整特征则以采集的特征更新目标模板的空白区域。 返回参数： 确认码，页码（相配指纹模板）
     * @return 确认码=00H 表示搜索到； 确认码=01H 表示收包有错； 确认码=09H 表示没搜索到；此时页码与得分为0 确认码=ffH
         *         表示无响应
         */
    ```


- int PSDeleteChar(short pageIDStart, short delNum);
    ```


    /**
         * 删除模板 删除flash 数据库中指定ID 号开始的N 个指纹模板 输入参数：PageID(指纹库模板号)，N 删除的模板个数。
         *
         * @param pageIDStart
         * @param delNum
         * @return 确认码=00H 表示删除模板成功； 确认码=01H 表示收包有错； 确认码=10H 表示删除模板失败； 确认码=ffH 表示无响应
         */
    ```


- int PSEmpty();
    ```


    /**
         * 功能说明： 删除flash 数据库中所有指纹模板
         *
         * @return 确认码=00H 表示清空成功； 确认码=01H 表示收包有错； 确认码=11H 表示清空失败； 确认码=ffH 表示无响应
         */
    ```


- byte[] PSUpChar(int bufferId);
    ```


    /**
         * 将特征缓冲区中的特征文件上传给上位机
         *
         * @return byte[]：长度为512字节成功 否则失败 null:上传特征文件失败
         */
    ```


- int PSDownChar(int bufferId, byte[] model);
    ```


    /**
         * 上位机下载特征文件到模块的特征缓冲区(默认的缓冲区为CharBuffer2)
         *
         * @param model
         *            :指纹的特征文件
         * @return 返回值为确认码 确认码=00H 表示可以接收后续数据包； 确认码=01H 表示收包有错； 确认码=0eH 表示不能接收后续数据包；
         *         确认码=ffH 表示无响应
         */
    ```


- int PS_ReadIndexTable(byte[] data, int pageId);
    ```


    /**
         * 功能说明：读取录入模版的索引表。 输入参数： 索引表页码, 页码0,1,2,3 分别对应模版从0-256，256-512，
         * 512-768，768-1024 的索引，每1 位代表一个模版，1 表示对应存储区域 的模版已经录入，0 表示没录入。
         * */
    ```


- int getValidId();
    ```


    /**
         * Function Description: Get a can store fingerprint model ID(0~1009)
         *
         * @return If it returns -1, which means that the fingerprint database is full.
         */
    ```


- byte[] PSUpImage();
    ```


    /**
         * 将图像缓冲区的数据上传给上位机
         * @return 返回值为bmp格式的指纹图像，如果为null上传失败
         */
    ```


- int PSCalibration();
    ```


    /**
    * 校准
    */
    ```


- int PSDownImage(byte[] image);
    ```

    /**
    * 将图像数据下传到下位机
    */

    ```

```java

//以下参数或返回值出现Result结构如下表示；

public class Result {
		public int code; /*** 确认码*/
		public int pageId; /*** 页码*/
		public int matchScore; /*** 得分*/
}


```


#### 2.4 接口调用流程

- 指纹注册功能流程图

![TIM图片20190507162823.png](https://i.loli.net/2019/05/07/5cd141ba84b95.png)

- 指纹验证功能流程图

![TIM图片20190507162952.png](https://i.loli.net/2019/05/07/5cd14233c6225.png)



#### 2.5 接口调用案例

参考Demo源码,FingerprintActivity.java