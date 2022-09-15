package com.nvsp.covidtester

import android.app.Activity
import android.content.Intent
import com.nvsp.covidtester.activity.MainActivity


object ResetApp {
    fun triggerRestart(context: Activity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

            (context ).finish()

        Runtime.getRuntime().exit(0)
    }
}