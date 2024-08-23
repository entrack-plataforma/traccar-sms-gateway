@file:Suppress("DEPRECATION")

package org.traccar.gateway

import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.simplemobiletools.commons.extensions.updateTextColors
import com.simplemobiletools.commons.extensions.viewBinding
import com.simplemobiletools.commons.helpers.NavigationIcon
import com.simplemobiletools.smsmessenger.R
import com.simplemobiletools.smsmessenger.activities.SimpleActivity
import com.simplemobiletools.smsmessenger.databinding.ActivityGatewayBinding

class GatewayActivity : SimpleActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val binding by viewBinding(ActivityGatewayBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        updateMaterialActivityViews(binding.gatewayCoordinator, binding.gatewayHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.gatewayNestedScrollview, binding.gatewayToolbar)
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.gatewayToolbar, NavigationIcon.Arrow)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.gatewayCloudKey.text = task.result
            }
        }


        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        binding.gatewayCloudKeyHolder.setOnClickListener {
            clipboard?.text = binding.gatewayCloudKey.text
            Toast.makeText(this, R.string.gateway_copied_toast, Toast.LENGTH_SHORT).show()
        }

        updateTextColors(binding.gatewayNestedScrollview)
    }
}
