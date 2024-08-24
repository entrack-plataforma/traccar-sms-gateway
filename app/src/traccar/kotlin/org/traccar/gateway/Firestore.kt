package org.traccar.gateway

import android.os.Build
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

class Firestore {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "Firestore"

    fun updateState(phone: String, messageId: String?, state: String, message: String? = null) {
        log()
        if (message != null) {
            db.document("phones/" + getDeviceUniqueId())
                .set(hashMapOf(
                    "last-online" to System.currentTimeMillis(),
                    "last-message" to message
                ), SetOptions.merge())
                .addOnFailureListener { e ->
                    // Failed to add the log entry
                    Log.e(TAG,"Error seting log entry: ${e.message}")
                }
            db.document("messages/$messageId").set(hashMapOf(
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "datetime" to Date().toString(),
                "phone" to phone,
                "sender" to getDeviceUniqueId()
            ), SetOptions.merge())
        } else {
            db.document("messages/$messageId").set(hashMapOf(
                "last-update" to System.currentTimeMillis(),
                "state" to state
            ), SetOptions.merge())
            .addOnFailureListener { e -> Log.e(TAG,"Error setting messages/$messageId: ${e.message}") }
            db.collection("messages/$messageId/states")
                .add(hashMapOf(
                    "state" to state,
                    "timestamp" to System.currentTimeMillis(),
                    "datetime" to Date().toString()
                ))
        }
    }

    private fun getDeviceUniqueId(): String {
        return listOf(
            Build.ID,              // The device's build ID
            Build.MODEL,           // The model of the device
            Build.MANUFACTURER,    // The manufacturer of the device
            Build.DEVICE,          // The name of the industrial design of the device
            Build.BRAND,           // The consumer-visible brand with which the product/hardware will be associated
            Build.HARDWARE         // The hardware name, from the kernel command line
        ).joinToString(separator = "-")
    }

    fun log() {
        db.document("phones/" + getDeviceUniqueId())
            .set(hashMapOf(
                "last-online" to System.currentTimeMillis(),
            ), SetOptions.merge())
            .addOnFailureListener { e ->
                // Failed to add the log entry
                Log.e(TAG,"Error seting log entry: ${e.message}")
            }
    }

    fun saveToken(token: String) {
        log()
        db.document("phones/" + getDeviceUniqueId())
            .set(hashMapOf(
                "last-install" to Date().toString(),
                "device-model" to Build.MODEL,
                "os-version" to Build.VERSION.RELEASE,
                "manufacturer" to Build.MANUFACTURER,
                "brand" to Build.BRAND,
                "product" to Build.PRODUCT,
                "device" to Build.DEVICE,
                "hardware" to Build.HARDWARE,
                "token" to token,
                "timestamp" to System.currentTimeMillis(),
                "datetime" to Date().toString()
            ))
            .addOnFailureListener { e ->
                // Failed to add the log entry
                Log.e(TAG,"Error seting log entry: ${e.message}")
            }
    }
}
