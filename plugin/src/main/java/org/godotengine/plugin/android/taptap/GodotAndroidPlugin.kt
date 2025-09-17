// TODO: Update to match your plugin's package name.
package org.godotengine.plugin.android.taptap

import android.app.Activity
import android.util.Log
import com.taptap.sdk.core.TapTapRegion
import com.taptap.sdk.core.TapTapSdk
import com.taptap.sdk.core.TapTapSdkOptions
import com.taptap.sdk.kit.internal.callback.TapTapCallback
import com.taptap.sdk.kit.internal.exception.TapTapException
import com.taptap.sdk.login.TapTapLogin
import com.taptap.sdk.login.TapTapAccount
import com.taptap.sdk.login.AccessToken
import com.taptap.sdk.login.Scopes
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import org.godotengine.godot.Dictionary
import org.godotengine.godot.plugin.SignalInfo

class GodotAndroidPlugin(godot: Godot) : GodotPlugin(godot) {

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    override fun getPluginSignals(): Set<SignalInfo> {
        return setOf(
            SignalInfo("login_success", Dictionary::class.java),
            SignalInfo("login_fail", String::class.java),
            SignalInfo("login_cancel")
        )
    }

    @UsedByGodot
    fun init(clientId: String, clientToken: String) {
        val activity: Activity = activity ?: return
        val tapSdkOptions = TapTapSdkOptions(
            clientId,
            clientToken,
            TapTapRegion.CN
        )
        TapTapSdk.init(activity.application, tapSdkOptions)
    }

    @UsedByGodot
    fun login(permissions: Array<String>) {
        val activity: Activity = activity ?: return
        val scopes = permissions.ifEmpty { arrayOf(Scopes.SCOPE_PUBLIC_PROFILE) }

        TapTapLogin.loginWithScopes(activity, scopes, object : TapTapCallback<TapTapAccount> {
            override fun onSuccess(tapTapAccount: TapTapAccount) {
                val accountInfo = Dictionary().apply {
                    put("accessToken", tapTapAccount.accessToken.toString())
                    put("name", tapTapAccount.name)
                    put("avatar", tapTapAccount.avatar)
                    put("openId", tapTapAccount.openId)
                    put("unionId", tapTapAccount.unionId)
                }
                emitSignal("login_success", accountInfo)
            }

            override fun onFail(exception: TapTapException) {
                emitSignal("login_fail", exception.message ?: "Unknown error")
            }

            override fun onCancel() {
                emitSignal("login_cancel")
            }
        })
    }

    @UsedByGodot
    fun getCurrentAccount(): Dictionary? {
        val taptapAccount = TapTapLogin.getCurrentTapAccount() ?: return null
        return Dictionary().apply {
            put("accessToken", taptapAccount.accessToken.toString())
            put("name", taptapAccount.name)
            put("avatar", taptapAccount.avatar)
            put("openId", taptapAccount.openId)
            put("unionId", taptapAccount.unionId)
        }
    }

    @UsedByGodot
    fun logout() {
        TapTapLogin.logout()
    }
}
