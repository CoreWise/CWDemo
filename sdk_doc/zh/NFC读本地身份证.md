## NFC读本地身份证配置方法


#### 1.配置AndroidManifest.xml

```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    <uses-permission android:name="android.permission.NFC" />

    <uses-feature
        android:name="android.hardware.nfc"
        android:required="true" />

```


#### 2.配置build

2.1 在Project视图下，在moudle/libs/下添加aar开发包

2.2 配置moudle build文件
```

android {
    defaultConfig {
        ....
        //必须1
        targetSdkVersion 22
    }
}



//必须2
repositories {
    flatDir {
        dirs 'libs'   // aar目录
    }
}

dependencies {
    ....
    ////必须3
    implementation(name: 'nfc-local-idcard-sdk-20190305', ext: 'aar')

}


```


#### 3. app二次开发

建议activity继承NFCReadLocalIDCardActivity基类，实现以下抽象方法即可,如下图

```

 public class DemoActivity extends NFCReadLocalIDCardActivity {

    @Override
    protected void onReadIDCardStart() {
        //NFC识别到卡，一般用于读卡总数统计次数


    }

    @Override
    protected void onReadIDCardSuccess(PeopleBean peopleBean, long l) {
        //读卡成功，peopleBean:身份证信息bean，l:读卡时间
    }

    @Override
    protected void onReadIDCardFailure(String s) {
        //读卡失败，s:错误信息
    }

    @Override
    protected void onReadIDCardUID(byte[] bytes) {
        //身份证卡号（UID），字节数据
        Log.d(TAG, DataUtils.toHexString(uid));

    }
}

```

