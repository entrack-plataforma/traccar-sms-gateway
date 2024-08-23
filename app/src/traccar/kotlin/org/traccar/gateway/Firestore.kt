package org.traccar.gateway

import android.os.Build
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class Firestore {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "Firestore"

    fun log(phone: String, messageId: String?, state: String, message: String? = null) {
        if (message != null) {
            db.document("phones/" + getDeviceUniqueId() + "/messages/" + messageId).set(hashMapOf(
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "datetime" to Date().toString(),
                "phone" to phone
            ))
        } else {
            db.collection("phones/" + getDeviceUniqueId() + "/messages/" + messageId + "/states")
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

    fun saveToken(token: String) {
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
