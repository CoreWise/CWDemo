# HX超高频

## 1、开发包说明



## 2、API参考

### 2.1 接口概述

主要方法，实现超高频功能必须使用

| 接口名称             | 描述                                            |
| -------------------- | ----------------------------------------------- |
| UHFHXAPI             | *初始化api                                      |
| openHXUHFSerialPort  | *打开串口                                       |
| closeHXUHFSerialPort | *关闭串口                                       |
| open                 | *开启超高频模块                                 |
| close                | *关闭超高频模块                                 |
| startAutoRead2C      | *盘点标签，读到标签就停，并且返回需要读到的数据 |
| readTypeCTagData     | *读取C类标签数据                                |
| writeTypeCTagData    | *写入C类标签数据                                |
| arguments            | *读取、写入标签入参（必须）                     |
| setRegion            | *设置当前区域（读取、写入必须）                 |

---

备用方法，根据需求使用

| 接口名称                   | 描述                                                         |
| -------------------------- | ------------------------------------------------------------ |
| startAutoRead2A            | 在清单轮次期间启动自动标签读取操作，标签ID通过通知包发送回用户 |
| startAutoRead2             | 启动自动标签读取操作，标签ID通过通知包发送回用户             |
| stopAutoRead2              | 停止自动read2操作                                            |
| readEPC                    | 读取EPC区域                                                  |
| readTID                    | 读取TID区域                                                  |
| stopAutoRead               | 停止自动标签读取操作                                         |
| getReaderInformation       | 从阅读器获取基本信息                                         |
| getRegion                  | 获取当前区域                                                 |
| setSystemReset             | 设置系统级复位                                               |
| getTypeCAISelectParameters | 获取18000-6C空中接口协议命令'选择'参数                       |
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

## 2.2

### 2.2.1 UHFHXAPI

初始化API

```java
api = new UHFHXAPI();
```

### 2.2.2 openHXUHFSerialPort

打开超高频串口模块，建议在onResume中实现

```java
boolean isOpen = api.openHXUHFSerialPort();//true 串口打开成功，false 串口打开失败
```

### 2.2.3 closeHXUHFSerialPort

关闭超高频串口模块，建议在onPause中实现，与 openHXUHFSerialPort 对应

### 2.2.4 open

开启超高频模块

## 3、开发问题汇总

