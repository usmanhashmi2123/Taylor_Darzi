package pk.taylor_darzi.activities

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.R
import pk.taylor_darzi.utils.LoadingProgressDialog
import pk.taylor_darzi.utils.Preferences

class MainActivity : BaseActivity() {
    var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this
        linearLayout= findViewById(R.id.language_group)
        findViewById<RadioGroup>(R.id.languages).setOnCheckedChangeListener(radioChangeListener)
    }

    override fun onResume()
    {
        super.onResume()
        if(!Preferences.instance!!.isLoginON)
        {
            linearLayout!!.visibility = View.VISIBLE
        }

    }
    var radioChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        run {
            Preferences.instance!!.saveStringPrefValue(
                Preferences.instance!!.PREF_LANG,
                findViewById<AppCompatRadioButton>(checkedId).tag.toString()
            )
            ActivityStackManager.instance!!.startLoginActivity(mActivity!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }
}