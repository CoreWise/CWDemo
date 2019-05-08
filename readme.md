

[中文说明](#user-content-zh) | [English Doc](#user-content-en)

---

Demo APK下载

---

<h3 id="user-content-zh">中文说明</h3>


该项目源码为Demo源码! 中英文文档源码在源代码根目录的sdk_doc目录下,开发包在源代码CoreWiseDemo/app/libs/目录下


功能包括:
- 条码
- 身份证(RFID)
- 本地身份证(NFC)
- BYD大指纹
- BYD小指纹
- SY指纹
- HX超高频
- R2000超高频
- 北斗
- M1 RFID
- M1 NFC
- 15693 NFC


<a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/条码.md" target="_blank">查看</a>

#### SDK说明


| 功能            | sdk目前支持的机型                                          | sdk名字                                                      |                           开发文档                           |
| :-------------- | :--------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------: |
| 条码            | u1,u3,u8,CFON640,A370                                      | [barcode_sdk_*.aar](<https://coding.net/u/CoreWise/p/SDK/git/raw/master/barcode_sdk_20190429.aar>) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/条码.md" target="_blank">查看</a> |
| 身份证          | u3,u8,CFON640,A370,新A370                                  | [idcard_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/idcard_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/身份证.md" target="_blank">查看</a> |
| SY指纹          | CFON640,A370                                               | [finger_sy_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_sy_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/SY指纹.md" target="_blank">查看</a> |
| BYD大指纹       | u3,u8                                                      | [finger_byd_big_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_byd_big_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/BYD大指纹.md" target="_blank">查看</a> |
| BYD小指纹       | u3,u8                                                      | [finger_byd_small_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_byd_small_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/BYD小指纹.md" target="_blank">查看</a> |
| FBI指纹         | u3,u8                                                      | 暂无                                                         | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/FBI指纹.md" target="_blank">查看</a> |
| HX超高频        | u3,CFON640,A370                                            | [hxuhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/hxuhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/HX超高频.md" target="_blank">查看</a> |
| R2000超高频     | u8                                                         | [r2000uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/r2000uhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/R2000超高频.md" target="_blank">查看</a> |
| 串口SDK         | 需要串口的功能需要添加该sdk,如HX超高频,身份证,北斗,M1 RFID | [serialport_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/serialport_sdk_20190429.aar) |                                    None                          |
| 北斗SDK         | u8                                                         | [beidou_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/beidou_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/北斗.md" target="_blank">查看</a> |
| M1 RFID SDK     | u3,CFON640                                                 | [m1rfid_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/m1rfid_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/M1RFID.md" target="_blank">查看</a> |
| M1 NFC          | 该功能调用Android标准M1接口，无SDK，有调用标准接口Demo     | 无                                                           |                                                              |
| 15693 NFC       | 该功能调用Android标准15693接口，无SDK，有调用标准接口Demo  | 无                                                           |                                                              |
| NFC读本地身份证 | A370                                                       | [nfc-local-idcard-sdk-*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/nfc-local-idcard-sdk-20190401.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/NFC读本地身份证.md" target="_blank">查看</a> |
| NFC读网络身份证 | 带NFC机器或者CR30S设备                                     | [net-idcard-sdk-*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/net-idcard-sdk-20190401.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/zh/NFC读网络身份证.md" target="_blank">查看</a> |


----

----

<h3 id="user-content-en">English Doc</h3>


The source code of the project is Demo source! The Chinese and English document source code is in the sdk_doc directory of the source code root directory, and the development package is in the source code CoreWiseDemo/app/libs/ directory.
Features include :

- BarCode
- IDCard(RFID)
- Local IDCard(NFC)
- BYD Big FingerPrint
- BYD Small FingerPrint
- SY FingerPrint
- HX UHF
- R2000 UHF
- BeiDou
- M1 RFID
- M1 NFC
- 15693 NFC



#### SDK Description



| Functions             | Models currently supported by sdk                            | SDK Name                                                     |                           SDK Doc                            |
| :-------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------: |
| BarCode               | u1,u3,u8,CFON640,A370                                        | [barcode_sdk_*.aar](<https://coding.net/u/CoreWise/p/SDK/git/raw/master/barcode_sdk_20190429.aar>) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/BarCode.md" target="_blank">View</a> |
| IDCard                | u3,u8,CFON640,A370,New A370                                  | [idcard_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/idcard_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/IDCard.md" target="_blank">View</a> |
| SY FingerPrint        | CFON640,A370                                                 | [finger_sy_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_sy_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/SYFingerPrint.md" target="_blank">View</a> |
| BYD Big FingerPrint   | u3,u8                                                        | [finger_byd_big_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_byd_big_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/BYDBigFingerPrint.md" target="_blank">View</a> |
| BYD Small FingerPrint | u3,u8                                                        | [finger_byd_small_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/finger_byd_small_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/BYDSmallFingerPrint.md" target="_blank">View</a> |
| FBI FingerPrint       | u3,u8                                                        | No                                                           |                                                              |
| HX UHF                | u3,CFON640,A370                                              | [hxuhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/hxuhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/HXUHF.md" target="_blank">View</a> |
| R2000 UHF             | u8                                                           | [r2000uhf_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/r2000uhf_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/R2000UHF.md" target="_blank">View</a> |
| SerialPort SDK        | Need to add the sdk function, such as HX UHF, ID card, Beidou, M1 RFID | [serialport_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/serialport_sdk_20190429.aar) |                                                              |
| BeiDou SDK            | u8                                                           | [beidou_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/beidou_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/BeiDou.md" target="_blank">View</a> |
| M1 RFID SDK           | u3,CFON640                                                   | [m1rfid_sdk_*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/m1rfid_sdk_20190429.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/M1RFID.md" target="_blank">View</a> |
| M1 NFC                | This function calls the Android standard M1 interface, no SDK, there is a call standard interface Demo | No                                                           |                                                              |
| 15693 NFC             | This function calls the Android standard 15693 interface, no SDK, there is a call standard interface Demo | No                                                           |                                                              |
| NFC Read Local IDCard | A370                                                         | [nfc-local-idcard-sdk-*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/nfc-local-idcard-sdk-20190401.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/NFCReadLocalIDCard.md" target="_blank">View</a> |
| NFC Read Net IDCard   | With NFC machine or CR30S device                             | [net-idcard-sdk-*.aar](https://coding.net/u/CoreWise/p/SDK/git/raw/master/net-idcard-sdk-20190401.aar) | <a href="https://github.com/CoreWise/CoreWiseDemo/blob/master/sdk_doc/en/NFCReadNetIDCard.md" target="_blank">View</a> |