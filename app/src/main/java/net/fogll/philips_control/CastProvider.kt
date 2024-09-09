package net.fogll.philips_control

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.SessionProvider

class CastOptionsProvider : OptionsProvider {
    @SuppressLint("VisibleForTests")
    override fun getCastOptions(context: Context): CastOptions {
        return CastOptions.Builder()
            .setReceiverApplicationId(context.getString(R.string.app_name)) // Your receiver app ID
            .setSupportedNamespaces(listOf("urn:x-cast:com.example.namespace")) // Your namespaces
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }
}