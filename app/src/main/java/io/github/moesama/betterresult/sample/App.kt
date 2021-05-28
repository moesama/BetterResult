package io.github.moesama.betterresult.sample

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import io.github.moesama.betterresult.createContract

class App : Application() {
    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        appUsageContracts = createContract {
            intent { action = Settings.ACTION_USAGE_ACCESS_SETTINGS }
            sync { context, _ -> if (context.checkAppUsagePermission()) true else null }
            parse { _, _ -> checkAppUsagePermission() }
        }
    }

    companion object {
        lateinit var appUsageContracts: ActivityResultContract<Nothing, Boolean>
            private set
    }
}