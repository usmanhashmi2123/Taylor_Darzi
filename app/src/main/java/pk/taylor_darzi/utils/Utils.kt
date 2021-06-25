package pk.taylor_darzi.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import java.util.*


object Utils {
    const val REQUEST_PERMISSIONS_REQUEST_CODE = 35
    var mContext: Context? = null
    var curentActivity: FragmentActivity? = null
    fun setCurrentActivity(activity: FragmentActivity) {
        curentActivity = activity
        mContext = activity
    }
    fun changeLanguage(language_code: String?, country: String?) {
        val res: Resources =
            mContext!!.applicationContext.resources
        val conf = res.configuration
        val displayMetrics = res.displayMetrics
        conf.setLocale(Locale(language_code, country))
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) mContext!!.applicationContext
            .createConfigurationContext(conf) else res.updateConfiguration(conf, displayMetrics)
    }
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun showKeyboard(activity: Activity, view: EditText?) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    fun getAmount(amount: String?):Float
    {
        var remAmount = 0f
        if(!amount.isNullOrBlank())
        {
            remAmount = amount.toFloat()
        }
        return remAmount
    }


}