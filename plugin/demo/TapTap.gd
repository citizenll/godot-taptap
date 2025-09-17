class_name TapTap
extends Node

## Emitted upon successful login.
signal login_success(account_info: Dictionary)

## Emitted upon failed login.
signal login_fail(error_message: String)

## Emitted when the user cancels the login.
signal login_cancel

const _plugin_name = "GodotTapSDK"
var _plugin_singleton = null

func _init():
	if Engine.has_singleton(_plugin_name):
		_plugin_singleton = Engine.get_singleton(_plugin_name)
		_plugin_singleton.connect("login_success", Callable(self, "_on_login_success"))
		_plugin_singleton.connect("login_fail", Callable(self, "_on_login_fail"))
		_plugin_singleton.connect("login_cancel", Callable(self, "_on_login_cancel"))
	else:
		printerr("TapTap plugin not found. Please ensure the plugin is enabled.")

## Initializes the TapTap SDK.
## @param client_id Your TapTap client ID.
## @param client_token Your TapTap client token.
func init(client_id: String, client_token: String):
	if _plugin_singleton:
		_plugin_singleton.init(client_id, client_token)

## Starts the TapTap login process.
## @param permissions An array of permissions to request. Defaults to ["public_profile"].
func login(permissions: Array = ["public_profile"]):
	if _plugin_singleton:
		_plugin_singleton.login(permissions)

## Logs the user out.
func logout():
	if _plugin_singleton:
		_plugin_singleton.logout()

## Gets the currently logged-in user's account information.
## @return A Dictionary with account info, or null if not logged in.
func get_current_account() -> Dictionary:
	if _plugin_singleton:
		return _plugin_singleton.getCurrentAccount()
	return null

func _on_login_success(account_info: Dictionary):
	emit_signal("login_success", account_info)

func _on_login_fail(error_message: String):
	emit_signal("login_fail", error_message)

func _on_login_cancel():
	emit_signal("login_cancel")