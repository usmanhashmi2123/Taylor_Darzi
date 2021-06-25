package pk.taylor_darzi

import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import java.util.*

abstract class BaseActivity : FragmentActivity() {
    protected var language: String? = null
    protected var mActivity: FragmentActivity? = null
    protected var resumed = false

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
        Config.getFirebaseAuth
        Config.getFirebaseFirestore
    }

    override fun onPause() {
        super.onPause()
        resumed = false
    }

    fun updateBaseContextLocale(context: Context): Context? {
        language = Preferences.instance!!.language
        if (language!!.contains("En"))
            Config.locale = Locale("en", "US")
        else
            Config.locale = Locale("ur", "PK")
        Locale.setDefault(Config.locale)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateResourcesLocale(context, Config.locale!!)
        } else updateResourcesLocaleLegacy(context, Config.locale!!)
    }


    fun updateResourcesLocale(context: Context, locale: Locale): Context? {
        val res = context.resources
        val conf = res.configuration
        conf.setLocale(locale)
        return context.createConfigurationContext(conf)
    }

    @Suppress("DEPRECATION")
    fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context? {
        try {
            val resources = context.resources
            val configuration = resources.configuration
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
        }
        return context
    }

}