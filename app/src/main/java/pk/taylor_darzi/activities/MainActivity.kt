package pk.taylor_darzi.activities

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import kotlinx.android.synthetic.main.activity_login.*
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.R
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.LoadingProgressDialog
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils

class MainActivity : BaseActivity() {
    var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this
        Utils.setCurrentActivity (mActivity as MainActivity)

        linearLayout= findViewById(R.id.language_group)
        findViewById<RadioGroup>(R.id.languages).setOnCheckedChangeListener(radioChangeListener)
    }

    override fun onResume()
    {
        super.onResume()
        if(Preferences.instance!!.userEmailId.isBlank()) linearLayout!!.visibility = View.VISIBLE
        else  signIn()

    }
    fun signIn()
    {

            progress_circular.visibility = View.VISIBLE
            Config.auth!!.signInWithEmailAndPassword(
                Preferences.instance!!.userEmailId,
                Preferences.instance!!.userPass
            )
                .addOnCompleteListener(this) { task ->
                    progress_circular.visibility = View.GONE
                    if (task.isSuccessful) {
                        Config.currentUser = Config.auth!!.currentUser
                        Preferences.instance!!.saveStringPrefValue(Preferences.instance!!.PREF_USER_ID, Config.currentUser?.uid)
                        ActivityStackManager.instance!!.goToDashBoard(mActivity!!)
                        finish()
                    } else {
                        if( Config.auth?.currentUser!= null)
                        {
                            Config.currentUser = Config.auth!!.currentUser
                            Preferences.instance!!.saveStringPrefValue(Preferences.instance!!.PREF_USER_ID, Config.currentUser?.uid)
                            ActivityStackManager.instance!!.goToDashBoard(mActivity!!)
                            finish()

                        }
                        else linearLayout!!.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener(this){ task2 ->
                    linearLayout!!.visibility = View.VISIBLE
                }
        /*else
        {

                auth.signInWithCredential(credential!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success")

                            val user = task.result?.user
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            // Update UI
                        }
                    }

        }*/

    }
    var radioChangeListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->

            Preferences.instance!!.saveStringPrefValue(
                Preferences.instance!!.PREF_LANG,
                findViewById<AppCompatRadioButton>(checkedId).tag.toString())
            ActivityStackManager.instance!!.startLoginActivity(mActivity!!)

    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }
}