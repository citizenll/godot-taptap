# 开发指南
权限说明
该模块依赖如下权限：

权限	使用目的	权限申请时机
网络权限	用于访问网络数据	用户首次使用该功能时会申请权限
该模块将在应用中添加如下配置：

<uses-permission android:name="android.permission.INTERNET"/>

# 准备集成 SDK
参考 准备工作 创建应用、开启应用配置；
参考 快速开始 配置包名与签名证书；

项目根目录的 build.gradle 添加仓库地址：
repositories {
  google()
  mavenCentral()
}

app module 的 build.gradle 添加对应依赖：
dependencies {
  implementation 'com.taptap.sdk:tap-core:4.8.1'
  implementation 'com.taptap.sdk:tap-login:4.8.1'
}

提示
如果 targetSdkVersion < 29，还需要添加如下配置：

manifest 节点添加 xmlns:tools="http://schemas.android.com/tools"
application 节点添加 tools:remove="android:requestLegacyExternalStorage"
SDK 初始化
信息
游戏 适用地区 在开启应用配置时选定。

TapTap 开发者中心适用地区为中国大陆。

出海游戏请前往 TapTap.io 开发者中心 开启游戏服务，适用地区为其他国家或地区。

TapTap 登录模块依赖于 TapTapSDK 初始化，具体参考 TapSDK 集成

TapTapSdkOptions 详细参数见 入门指南#快速开始
```java
import com.taptap.sdk.core.TapTapRegion;
import com.taptap.sdk.core.TapTapSdk;
import com.taptap.sdk.core.TapTapSdkOptions;

/* 必选配置 */
// 开发者中心对应 Client ID
String clientId = "";
// 开发者中心对应 Client Token
String clientToken = "";
// 是否开启 log，建议 Debug 开启，Release 关闭，默认关闭 log
boolean enableLog = BuildConfig.DEBUG;

TapTapSdkOptions tapSdkOptions = new TapTapSdkOptions(
        clientId, // 游戏 Client ID
        clientToken, // 游戏 Client Token
        TapTapRegion.CN // 游戏可玩区域: [TapTapRegion.CN]=国内 [TapTapRegion.GLOBAL]=海外
);
tapSdkOptions.setEnableLog(enableLog);
// 初始化 TapSDK
TapTapSdk.init(context, tapSdkOptions);
```
# 登录
提示
当用户启动游戏时，可以先检查用户登录状态，如果玩家已经登录，则不显示登录按钮，让玩家直接进入游戏。

```java
import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;
import androidx.annotation.NonNull;

// 定义授权范围
String[] scopes = new String[]{Scopes.SCOPE_PUBLIC_PROFILE};

TapTapLogin.loginWithScopes(activity, scopes, new TapTapCallback<TapTapAccount>() {
    @Override
    public void onSuccess(TapTapAccount tapTapAccount) {
        // 登录成功
    }

    @Override
    public void onFail(@NonNull TapTapException exception) {
        // 登录失败
    }

    @Override
    public void onCancel() {
        // 登录取消
    }
});
```

不同的授权范围
TapTap 授权登录接口支持选择不同的授权范围且支持任意组合，但必须包含 public_profile 和 basic_info 中的一个，不然无法获得 openId 和 unionId。TapTap 授权登录接口可以传递 String 类型的数组作为参数，开发者可以根据自己业务需求添加不同的权限为数组的元素。

权限	说明
public_profile	获得 openId、unionId、用户昵称、用户头像
user_friends	获得访问 TapTap 好友相关数据的权限
basic_info	获得 openId 和 unionId
提示
若游戏发起 TapTap 授权登录时只请求 basic_info 的权限，则用户可享受无感登录的特性，即用户不需要手动确认授权即可自动授权完成登录。

获取用户信息
TapTap 用户登录成功之后，开发者可以通过如下方式获取到 TapTap 授权结果的详细信息：
```java
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;
import com.taptap.sdk.login.AccessToken;

TapTapAccount taptapAccount = TapTapLogin.getCurrentTapAccount();
AccessToken accessToken = taptapAccount.getAccessToken();
String openId = taptapAccount.getOpenId();
```
其中 taptapAccount 可能会包含如下信息：

参数	说明
accessToken	玩家在 TapTap 平台的用户 token 信息
name	玩家在 TapTap 平台的昵称
avatar	玩家在 TapTap 平台的头像 url
openId	通过用户信息和游戏信息生成的用户唯一标识，每个玩家在每个游戏中的 openId 都是不一样的
unionId	通过用户信息加上厂商信息生成的用户唯一标识，一个玩家在同一个厂商的所有游戏中 unionId 都是一样的，不同厂商下 unionId 不同
openId 和 unionId 使用标准的 Base64（带 Padding）编码，包含的字符有 A-Za-z0-9+/=。openId 和 unionId 长度最大值为 50 个字符。

信息
由于 unionId 与游戏所属厂商有强关联性，因此 unionId 适用于如下场景：厂商使用测试服进行付费删档等测试，正式服需要对于之前参与测试的老玩家进行返利等操作。因为同一个玩家在同一个厂商下的所有游戏中的 unionId 不变。 一个游戏在厂商转移后同一个用户的 unionId 会发生改变，如果游戏使用了 unionId，TDS 技术支持会在转移前通过工单和游戏开发者确认相关数据的处理方案，保证迁移前后用户数据不错乱。

检查登录状态和用户信息
登录状态和用户信息存在本地缓存中，重新登录将会重置，登出将会清除。


```java
import com.taptap.sdk.login.AccessToken;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();

if (currentTapAccount != null) {
    // 已登录
    AccessToken accessToken = currentTapAccount.getAccessToken();
} else {
    // 未登录
}```

登出 

```java
import com.taptap.sdk.login.TapTapLogin;

TapTapLogin.logout();
```