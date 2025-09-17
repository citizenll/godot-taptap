# Godot TapTap 登录插件

本仓库是一个用于集成 TapTap 登录 SDK 的 Godot Android 插件。它为 Godot 4.2+ 构建，处理了所有安卓原生实现细节，并为您的游戏提供了一个简洁的 GDScript 接口。

## 功能特性

*   **TapTap 登录**: 实现了 TapTap 的授权和登录流程。
*   **用户信息**: 在成功登录后获取用户的账户信息。
*   **GDScript 包装器**: 提供了一个简洁的 `TapTap.gd` 包装器，方便您在 Godot 项目中集成。
*   **信号系统**: 使用 Godot 的信号系统来处理登录回调（`login_success`, `login_fail`, `login_cancel`）。

## 如何使用

1.  **下载插件**:
    *   前往本仓库的 [Releases](https://github.com/citizenll/godot-taptap/releases) 页面。
    *   下载最新版本的 `GodotTapSDK-vX.X.zip` 文件。

2.  **安装插件**:
    *   解压下载的 zip 文件。
    *   您会看到一个 `addons` 目录。将 `addons` 目录下的 `GodotTapSDK` 文件夹复制到您自己 Godot 项目的 `addons` 目录中。

3.  **启用插件**:
    *   打开您的 Godot 项目。
    *   前往 `项目` -> `项目设置...` -> `插件` 标签页。
    *   在列表中找到 "GodotTapSDK" 并勾选 "启用" 复选框。

4.  **配置 Android 导出**:
    *   确保您的项目已创建 Android 导出预设。
    *   在导出预设的设置中，找到 `Gradle 构建` 部分，并确保 `使用 Gradle 构建` 已被**启用**。这是插件正常工作的必要条件。

## GDScript API 用法

本插件提供了一个 `TapTap.gd` 脚本，以便于在项目中使用。以下是一个在您游戏中的基本用法示例：

```gdscript
# main_scene.gd
extends Node

var taptap: TapTap

func _ready():
    # 插件启用后，我们可以创建一个 TapTap 节点实例来使用它。
    taptap = TapTap.new()
    add_child(taptap)

    # 连接登录信号以获取回调结果。
    taptap.connect("login_success", Callable(self, "_on_login_success"))
    taptap.connect("login_fail", Callable(self, "_on_login_fail"))
    taptap.connect("login_cancel", Callable(self, "_on_login_cancel"))

    # --- 重要 ---
    # 使用您在 TapTap 开发者中心获取的 Client ID 和 Client Token 初始化 SDK。
    # 这个操作应该在游戏启动时执行一次。
    taptap.init("YOUR_CLIENT_ID", "YOUR_CLIENT_TOKEN")

func _on_login_button_pressed():
    # 当玩家点击您的登录按钮时，调用此函数。
    print("正在尝试使用 TapTap 登录...")
    taptap.login()

func _on_login_success(account_info: Dictionary):
    print("登录成功!")
    print("用户名: ", account_info.get("name"))
    print("头像 URL: ", account_info.get("avatar"))
    print("OpenID: ", account_info.get("openId"))

func _on_login_fail(error_message: String):
    printerr("登录失败: ", error_message)

func _on_login_cancel():
    print("用户取消了登录。")

func get_current_user():
    # 您可以随时检查用户是否已登录。
    var current_account = taptap.get_current_account()
    if current_account:
        print("用户已登录: ", current_account.get("name"))
    else:
        print("当前没有用户登录。")

func logout_user():
    # 调用此函数以登出用户。
    taptap.logout()
    print("用户已登出。")
```

## 从源码构建

如果您需要修改插件或自行构建：

1.  **克隆本仓库**:
    ```sh
    git clone https://github.com/citizenll/godot-taptap.git
    cd godot-taptap
    ```

2.  **构建插件**:
    *   在项目根目录运行以下命令。这会编译 Java/Kotlin 代码并将其打包成一个 `.aar` 文件。
    ```sh
    ./gradlew assemble
    ```

3.  **找到构建产物**:
    *   编译好的插件位于 `plugin/demo/addons/GodotTapSDK`。您可以将这个目录复制到您自己项目的 `addons` 文件夹中。