package com.movieappjc.app

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun CountdownTimerUI() {
    val oShowDialog by CountdownTimer.oShowDialog
    val onTimerFinish by CountdownTimer.onTimerFinish

    if(oShowDialog) {
        Log.d("CountdownTimer", "UI Show dialog")
        SessionTimeoutDialog(onExtendSession = {
            Log.d("CountdownTimer", "onExtendSession")
            CountdownTimer.oShowDialog.value = false
            CountdownTimer.onTimerFinish.value = false
            CountdownTimer.cancel()
            CountdownTimer.start()
        }, onLogout = {
            Log.d("CountdownTimer", "onLogout")
            CountdownTimer.cancel()
            CountdownTimer.oShowDialog.value = false
            CountdownTimer.onTimerFinish.value = true
        })
    }

    LaunchedEffect(onTimerFinish) {
        if (onTimerFinish) {
            Log.d("CountdownTimer", "UI Het session")
        }
    }
}

@Composable
fun SessionTimeoutDialog(
    onExtendSession: () -> Unit,
    onLogout: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Session Timeout") },
        text = { Text("Your session is about to expire. Would you like to extend your session?") },
        confirmButton = {
            Button(onClick = onExtendSession) {
                Text("Extend")
            }
        },
        dismissButton = {
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    )
}