extends Node2D

var taptap: TapTap

func _ready():
	# Add the TapTap wrapper as a child node.
	taptap = TapTap.new()
	add_child(taptap)

	# Connect to the login signals.
	taptap.connect("login_success", Callable(self, "_on_login_success"))
	taptap.connect("login_fail", Callable(self, "_on_login_fail"))
	taptap.connect("login_cancel", Callable(self, "_on_login_cancel"))

	# Initialize the SDK.
	# TODO: Replace with your actual Client ID and Client Token.
	taptap.init("YOUR_CLIENT_ID", "YOUR_CLIENT_TOKEN")

func _on_Button_pressed():
	print("Login button pressed. Calling TapTap login...")
	taptap.login()

func _on_login_success(account_info: Dictionary):
	print("TapTap Login Success!")
	print("Account Info: ", account_info)
	var current_account = taptap.get_current_account()
	print("Current Account from get_current_account(): ", current_account)


func _on_login_fail(error_message: String):
	printerr("TapTap Login Failed: " + error_message)


func _on_login_cancel():
	print("TapTap Login Canceled by user.")
