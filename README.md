## Godot Taptap SDK

### 适用于Godot4.1.1及以上

目前已接入登陆接口，后续将会增加广告接口和其它常用接口, 欢迎大家一起来维护

```gdscript
if Engine.has_singleton("GodotTaptap"):
		print("tap plugin init success")
		tap_singleton = Engine.get_singleton("GodotTaptap")
		tap_singleton.tapInit("your client id", "your client token")
		tap_singleton.connect("_on_sign_in_success", _on_tap_signin)

## 登陆
tap_singleton.sinIn()
```