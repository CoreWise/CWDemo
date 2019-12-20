

[中文说明](#user-content-zh) | [English Doc](#user-content-en)

---


[Demo APK下载](https://github.com/CoreWise/CoreWiseDemo/releases)

![Demo演示](https://i.loli.net/2019/05/08/5cd243747a2c8.gif)


---

<h3 id="user-content-zh">中文说明</h3>


该项目源码为Demo源码! 中英文文档源码在源代码根目录的sdk_doc目录下，开发包在源代码CoreWiseDemo/app/libs/目录下


功能包括:
- 条码
- 身份证(RFID)
- 本地身份证(NFC)
- GAA指纹
- GAB指纹
- JRA指纹
- PhyChips超高频
- R2000超高频
- 北斗短报文
- M1 RFID
- 125K RFID SDK
- M1 NFC
- 15693 NFC



#### SDK说明

**温馨提示1**:  本开发文档均采用MarkDown文件格式，AndroidStudio预览可安装markdown插件

**温馨提示2**:  如果下方aar下载失效，可以点击下方相关历史开发包下载地址去下载最新开发包

| 功能            | sdk目前支持的机型                                          | sdk名字                                                      |                           开发文档                           |
| :-------------- | :--------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------: |
| 条码            | u1,u3,u8,CFON640,A370                                      | [barcode_sdk_*.aar](<https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/barcode_sdk_20190429.aar>) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/条码.md" target="_blank">查看</a> |
| 身份证          | u3,u8,CFON640,A370,新A370                                  | [idcard_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/idcard_sdk_20191218.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/身份证.md" target="_blank">查看</a> |
| GAA指纹       | u3,u8                                                      | [fp_gaa_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_gaa_sdk_20190722.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/GAA指纹.md" target="_blank">查看</a> |
| GAB指纹       | u3,u8,新A370                                                     | [fp_gab_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_gab_sdk_20190701.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/GAB指纹.md" target="_blank">查看</a> |
| JRA小指纹       | u3,u8                                                      | [fp_jra_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_jra_sdk_20190920.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/JRA指纹.md" target="_blank">查看</a> |
| FBB FingerPrint       | u3,u8,新370                                          |请跳转 [FBB FingerPrint Demo](https://github.com/CoreWise/FBBFingerDemo) |  |
| PhyChips超高频      | u3,u8,CFON640,A370                                            | [phychips_uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/phychips_uhf_sdk_20191114.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/phychips超高频.md" target="_blank">查看</a> |
| R2000超高频     | u8                                                         | [r2000uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/r2000uhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/R2000超高频.md" target="_blank">查看</a> |
| 串口SDK         | 需要串口的功能需要添加该sdk,如HX超高频,身份证,北斗,M1 RFID | [serialport_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/serialport_sdk_20190712.aar) |                                    None                          |
| 北斗短报文SDK         | u8                                                         | [beidou_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/beidou_sdk_20190614.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/北斗.md" target="_blank">查看</a> |
| M1 RFID SDK     | u3,CFON640                                                 | [m1rfid_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/m1rfid_sdk_20190911.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/M1RFID.md" target="_blank">查看</a> |
| 125K RFID SDK     | u8                                              | [m1rfid_sdk_*.aar(125K开包集成在M1RFID开发包，2019.09.11)](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/m1rfid_sdk_20190911.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/125KRFID.md" target="_blank">查看</a> |
| M1 NFC          | 该功能调用Android标准M1接口，无SDK，有调用标准接口Demo     | 无                 |              None            |
| ISO15693 NFC       | 该功能调用Android标准15693接口，无SDK，有调用标准接口Demo  | 无              |              None            |
|网络身份证|带NFC机器|请跳转[网络身份证](https://github.com/CoreWise/NetReadIDCard)|None|

----

[**相关APK,历史开发包下载地址**](https://git.dev.tencent.com/CoreWise/SDK.git)


----

<h3 id="user-content-en">English Doc</h3>


The source code of the project is Demo source! The Chinese and English document source code is in the sdk_doc directory of the source code root directory, and the development package is in the source code CoreWiseDemo/app/libs/ directory.
Features include :

- BarCode
- IDCard(RFID)
- Local IDCard(NFC)
- GAA FingerPrint
- GAB FingerPrint
- JRA FingerPrint
- HX UHF
- R2000 UHF
- BeiDou
- M1 RFID
- 125K RFID SDK
- M1 NFC
- 15693 NFC



#### SDK Description


**Tips 1:** This development document uses the MarkDown file format, and the AndroidStudio preview can be installed with the markdown plugin.

**Tips 2**:  If the download below aar is invalid, you can click the relevant historical development package download address below to download the latest development package.

| Functions             | Models currently supported by sdk                            | SDK Name                                                     |                           SDK Doc                            |
| :-------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------: |
| BarCode               | u1,u3,u8,CFON640,A370                                        | [barcode_sdk_*.aar](<https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/barcode_sdk_20190429.aar>) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/BarCode.md" target="_blank">View</a> |
| IDCard                | u3,u8,CFON640,A370,New A370                                  | [idcard_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/idcard_sdk_20190617.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/IDCard.md" target="_blank">View</a> |
| GAA FingerPrint   | u3,u8                                                        | [fp_gaa_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_gaa_sdk_20190722.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/GAAFingerPrint.md" target="_blank">View</a> |
| GAB FingerPrint    | u3,u8,new A370                                                     | [fp_gab_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_gab_sdk_20190701.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/GABFingerPrint.md" target="_blank">View</a> |
| JRA FingerPrint | u3,u8                                                        | [fp_jra_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/fp_jra_sdk_20190731.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/JRAFingerPrint.md" target="_blank">View</a> |
| FBB FingerPrint       | u3,u8,New A370                                                     | See this Project [FBB FingerPrint Demo](https://github.com/CoreWise/FBBFingerDemo) |  |
| PhyChips UHF                | u3,u8,CFON640,A370                                              | [phychips_uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/phychips_uhf_sdk_20191114.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/HXUHF.md" target="_blank">View</a> |
| R2000 UHF             | u8                                                           | [r2000uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/r2000uhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/R2000UHF.md" target="_blank">View</a> |
| SerialPort SDK        | Need to add the sdk function, such as HX UHF, ID card, Beidou, M1 RFID | [serialport_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/serialport_sdk_20190712.aar) |            None                                                  |
| BeiDou SDK            | u8                                                           | [beidou_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/beidou_sdk_20190614.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/BeiDou.md" target="_blank">View</a> |
| M1 RFID SDK           | u3,CFON640                                                   | [m1rfid_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/m1rfid_sdk_20190911.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/en/M1RFID.md" target="_blank">View</a> |
| 125K RFID SDK     | u8                                              | [m1rfid_sdk_*.aar( M1RFID SDK contains 125KRFID SDK ，2019.09.11)](https://coding.net/u/CoreWise/p/SDK/git/raw/master/aar/m1rfid_sdk_20190911.aar) | <a href="https://github.com/CoreWise/CWDemo/blob/master/sdk_doc/cn/125KRFID.md" target="_blank">查看</a> |
| M1 NFC                | This function calls the Android standard M1 interface, no SDK, there is a call standard interface Demo | No                                                           |                  None                                            |
| ISO15693 NFC             | This function calls the Android standard 15693 interface, no SDK, there is a call standard interface Demo | No                                                           |                  None                                            |




---

[**Related APK, history development package download address**](https://git.dev.tencent.com/CoreWise/SDK.git)
