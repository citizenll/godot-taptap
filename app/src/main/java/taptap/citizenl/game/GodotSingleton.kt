package taptap.citizenl.game

import android.widget.Toast
import com.taptap.sdk.AccessToken
import com.taptap.sdk.AccountGlobalError
import com.taptap.sdk.Log
import com.taptap.sdk.Profile
import com.taptap.sdk.TapLoginHelper

import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import taptap.citizenl.game.BuildConfig.*

class GodotSingleton(godot: Godot) : GodotPlugin(godot) {

    private var tapInit = false

    companion object {
        val SIGNAL_SIGN_IN_SUCCESSFUL = SignalInfo("_on_sign_in_success", String::class.java)
    }

    override fun onGodotSetupCompleted() {
        Log.DEBUG_LOG("Tap插件加载完成 $LIBRARY_NAME")
    }

    override fun getPluginName(): String {
        return LIBRARY_NAME
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SIGNAL_SIGN_IN_SUCCESSFUL
        )
    }

    @UsedByGodot
    fun tapInit(clientId: String, clientToken: String) {
        TapLoginHelper.init(activity, clientId)
        TapLoginHelper.registerLoginCallback(loginCallback)
        tapInit = true
    }

    @UsedByGodot
    fun sinIn() {
        if (!tapInit) {
            runOnUiThread { Toast.makeText(activity, "未初始化SDK", Toast.LENGTH_SHORT).show() }
            return
        }
        TapLoginHelper.startTapLogin(activity, TapLoginHelper.SCOPE_PUBLIC_PROFILE)
    }

    private var loginCallback: TapLoginHelper.TapLoginResultCallback =
        object : TapLoginHelper.TapLoginResultCallback {
            override fun onLoginSuccess(token: AccessToken) {
                val profile: Profile = TapLoginHelper.getCurrentProfile()
                emitSignal(SIGNAL_SIGN_IN_SUCCESSFUL.name, profile.toJsonString())
                Log.DEBUG_LOG("TapTap authorization succeed :" + profile.toJsonString())
            }

            override fun onLoginCancel() {
                emitSignal(SIGNAL_SIGN_IN_SUCCESSFUL.name, "")
                Log.DEBUG_LOG("TapTap authorization cancelled")
            }

            override fun onLoginError(globalError: AccountGlobalError) {
                emitSignal(SIGNAL_SIGN_IN_SUCCESSFUL.name, "")
                Log.DEBUG_LOG("TapTap authorization failed. cause: " + globalError.message)
            }
        }
}