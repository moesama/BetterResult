package io.github.moesama.betterresult.sample

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build

internal fun Context.checkAppUsagePermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return false
        val currentTime = System.currentTimeMillis()
        // try to get app usage state in last 1 min
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 60 * 1000, currentTime
        )
        if (stats.size == 0) {
            return false
        }
    }
    return true
}