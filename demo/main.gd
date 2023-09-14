extends Node

var tap_singleton
func _ready():
	if Engine.has_singleton("GodotTaptap"):
		print("tap plugin init success")
		tap_singleton = Engine.get_singleton("GodotTaptap")
		tap_singleton.tapInit("your client id", "your client token")
		tap_singleton.connect("_on_sign_in_success", _on_tap_signin)

func _on_button_pressed():
	if tap_singleton:
		tap_singleton.sinIn()


func _on_tap_signin(result):
	print("tap login ok:", result)
