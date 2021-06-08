package pk.taylor_darzi.activities

import android.os.Bundle
import android.util.Patterns.PHONE
import android.view.View
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.R
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.LoadingProgressDialog

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    var email :String ?=null
    var phone :String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mActivity = this
        auth = Firebase.auth
        create_aac_btn.setOnClickListener(clickListener)
        login_btn.setOnClickListener(clickListener)

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        Config.currentUser = auth.currentUser

    }
    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }
    val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.create_aac_btn -> {
                if (signup_group.visibility == View.VISIBLE) {
                    if(validate(true))
                    {

                    }

                }
                signup_group.visibility = View.VISIBLE
            }
            R.id.login_btn -> {
                if (signup_group.visibility == View.GONE)
                {
                    if(validate(false))
                    {

                    }
                }
                else
                    signup_group.visibility = View.GONE
            }
        }
    }
    fun validate(signup :Boolean ): Boolean {
        var validate = true
        userName.error = null
        useremail_phone.error = null
        password.error = null
        password_2.error = null

        if(useremail_phone.text.toString().isBlank() || (!EMAIL_ADDRESS.matcher(useremail_phone.text.toString()).matches() && !PHONE.matcher(useremail_phone.text.toString()).matches()))
        {
            validate = false
            useremail_phone.error = getString(R.string.inavlid_data)
        }

        if(signup)
        {
            if(userName.text.toString().isBlank())
            {
                validate = false
                userName.error = getString(R.string.inavlid_data)
            }

            if(password.text.toString().isBlank())
            {
                validate = false
                password.error = getString(R.string.inavlid_data)
            }
            else if(password_2.text.toString().isBlank() || !password.text.toString().equals(password_2.text.toString(), false))
            {
                validate = false
                password_2.error = getString(R.string.inavlid_data)
            }
        }
        else if(password.text.toString().isBlank())
        {
            validate = false
            password.error = getString(R.string.inavlid_data)
        }
        return validate
    }
    fun signUp()
    {
        if(EMAIL_ADDRESS.matcher(useremail_phone.text.toString()).matches())
        {
            auth.createUserWithEmailAndPassword(useremail_phone.text.toString(),password_2.text.toString())
                .addOnCompleteListener(this){ task ->
                    if (task.isSuccessful) {

                        Config.currentUser = Firebase.auth.currentUser
                        if (Config.currentUser != null) {
                            val profileUpdates = userProfileChangeRequest {
                                displayName = userName.text.toString()
                                //photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                            }
                            Config.currentUser!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Config.appToastShort(mActivity,"User profile updated.")
                                    }
                                }
                        }
                        signup_group.visibility = View.GONE
                    } else {
                        Config.appToastShort(mActivity, task.exception!!.message)
                    }
                }
                .addOnFailureListener(this){task2 ->  Config.appToastShort(mActivity, task2.message)}
        }
        else
        {
        }

    }
    fun signIn()
    {
        if(EMAIL_ADDRESS.matcher(useremail_phone.text.toString()).matches())
        {
            auth.signInWithEmailAndPassword(useremail_phone.text.toString(),password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                    } else {

                    }
                }
                .addOnFailureListener(this){task2 ->  Config.appToastShort(mActivity, task2.message)}
        }
        else
        {

        }
    }
}