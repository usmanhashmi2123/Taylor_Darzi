package pk.taylor_darzi.activities

import android.os.Bundle
import android.view.View
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.R
import pk.taylor_darzi.dataModels.CustomersList
import pk.taylor_darzi.dataModels.User
import pk.taylor_darzi.databinding.ActivityLoginBinding
import pk.taylor_darzi.utils.*
import java.util.regex.Pattern


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var resendToken: PhoneAuthProvider.ForceResendingToken?=null
    private var storedVerificationId: String?=null
    var userPhone =""
    val phonePat = "^[+][0-9]{7,13}\$"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mActivity = this
        Utils.setCurrentActivity(mActivity as LoginActivity)
        Config.auth!!.useAppLanguage()
        binding.createAacBtn.setOnClickListener(clickListener)
        binding.loginBtn.setOnClickListener(clickListener)
        binding.register.setOnClickListener(clickListener)
        binding.loginText.setOnClickListener(clickListener)
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if(Config.currentUser == null)
            Config.currentUser = Config.auth!!.currentUser

    }
    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }
    val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.register -> {
                binding.signupGroup.visibility = View.VISIBLE
                binding.siginGroup.visibility = View.GONE
            }
            R.id.login_text -> {
                binding.siginGroup.visibility = View.VISIBLE
                binding.signupGroup.visibility = View.GONE
            }
            R.id.create_aac_btn -> {
                if (validate(true)) signUp()
            }
            R.id.login_btn -> {
                if (validate(false)) signIn(null)
            }
            R.id.forgotten_Pass_text -> {
                if (validateEmail(binding.useremailPhone.text.toString()))
                    Config.auth!!.sendPasswordResetEmail(binding.useremailPhone.text.toString().trim())
                        .addOnCompleteListener(
                            this
                        ) { task ->

                        }
            }
        }
    }
    fun validateEmail(email:String): Boolean {
        var validate = true
        binding.userName.error = null

        if (email.isBlank() || !EMAIL_ADDRESS.matcher(email.trim()).matches())
        {
            validate = false
            binding.useremailPhone.error = getString(R.string.inavlid_data)
        }
        return  validate
    }

    fun validate(signup: Boolean): Boolean {
        var validate = true
        binding.userName.error = null
        binding.useremailPhone.error = null
        binding.userPhone.error = null
        binding.password.error = null
        binding.password2.error = null

        if(!validateEmail(binding.useremailPhone.text.toString()) && !Pattern.compile(phonePat).matcher(binding.useremailPhone.text.toString().trim()).matches())
        {
            validate = false
            binding.useremailPhone.error = getString(R.string.inavlid_data)
        }

        if(signup)
        {
            if(!Pattern.compile(phonePat).matcher(binding.userPhone.text.toString().trim()).matches())
            {
                validate = false
                userPhone= ""
                binding.userPhone.error = getString(R.string.inavlid_data)
            }
            else
                userPhone = binding.userPhone.text.toString().trim()
            if(binding.userName.text.toString().isBlank())
            {
                validate = false
                binding.userName.error = getString(R.string.inavlid_data)
            }

            if(binding.password.text.toString().isBlank())
            {
                validate = false
                binding.password.error = getString(R.string.inavlid_data)
            }
            else if(binding.password2.text.toString().isBlank() || !binding.password.text.toString().equals(
                    binding.password2.text.toString(),
                    false
                ))
            {
                validate = false
                binding.password2.error = getString(R.string.inavlid_data)
            }
        }
        else if(binding.password.text.toString().isBlank())
        {
            validate = false
            binding.password.error = getString(R.string.inavlid_data)
        }
        return validate
    }
    fun signUp()
    {
        if(CheckConnection.getInstance(mActivity!!)!!.isConnectedToInternet)
        {
            binding.progressCircular.visibility = View.VISIBLE
            //if(EMAIL_ADDRESS.matcher(useremail_phone.text.toString().trim()).matches())

            Config.auth?.createUserWithEmailAndPassword(
                binding.useremailPhone.text.toString().trim(),
                binding.password2.text.toString()
            )
                    ?.addOnCompleteListener(this){ task ->
                        binding.progressCircular.visibility = View.GONE
                        if (task.isSuccessful) {

                            Config.currentUser = Firebase.auth.currentUser
                            Config.getFirebaseFirestore
                            if (Config.currentUser != null) {
                                Preferences.instance!!.saveStringPrefValue(
                                    Preferences.instance!!.PREF_USER_EMAIL,
                                    binding.useremailPhone.text.toString().trim()
                                )
                                Preferences.instance!!.saveStringPrefValue(
                                    Preferences.instance!!.PREF_USER_PASS,
                                    binding.password2.text.toString()
                                )
                                Preferences.instance!!.saveStringPrefValue(
                                    Preferences.instance!!.PREF_USER_ID,
                                    Config.currentUser?.uid
                                )

                                val profileUpdates = userProfileChangeRequest {
                                    displayName = binding.userName.text?.toString()
                                    //photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                                }

                                Config.currentUser!!.updateProfile(profileUpdates)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Config.appToast(mActivity, "User profile updated.")
                                                Config.currentUser = Firebase.auth.currentUser

                                                val docIdRef :DocumentReference = Config.firebaseDb!!.collection(
                                                    Config.User
                                                ).document(Config.currentUser!!.uid)
                                                docIdRef.get()
                                                        .addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
                                                            if (task.isSuccessful) {
                                                                val document = task.result

                                                                    if (document!= null && !document.exists()) {
                                                                        docIdRef.set(CustomersList(phoneNumber = userPhone))
                                                                            .addOnFailureListener(this) { task3 ->
                                                                                Config.appToast(
                                                                                    mActivity,
                                                                                    task3.message
                                                                                )
                                                                    }
                                                                } else
                                                                    docIdRef.set(CustomersList(phoneNumber = userPhone))
                                                                        .addOnFailureListener(
                                                                            this
                                                                        ) { task3 ->
                                                                            Config.appToast(
                                                                                mActivity,
                                                                                task3.message
                                                                            )
                                                                        }
                                                            } else docIdRef.set(CustomersList(phoneNumber = userPhone))
                                                                .addOnFailureListener(
                                                                    this
                                                                ) { task3 ->
                                                                    Config.appToast(
                                                                        mActivity,
                                                                        task3.message
                                                                    )
                                                                }

                                                        })
                                                ActivityStackManager.instance!!.goToDashBoard(
                                                    mActivity!!
                                                )
                                            }
                                        }
                            }
                            binding.signupGroup.visibility = View.GONE
                        } else {
                            Config.appToast(mActivity, task.exception!!.message)
                        }
                    }
                    ?.addOnFailureListener(this){ task2 ->  Config.appToast(
                        mActivity,
                        task2.message
                    )}
        }
        else  Config.appToast(mActivity, mActivity?.getString(R.string.check_interent))


       /* else
        {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(useremail_phone.text.toString().trim())       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }*/

    }
    fun signIn(credential: PhoneAuthCredential?)
    {
        if(CheckConnection.getInstance(mActivity!!)!!.isConnectedToInternet) {
            binding.progressCircular.visibility = View.VISIBLE
            //if(EMAIL_ADDRESS.matcher(useremail_phone.text.toString().trim()).matches())

            Config.auth?.signInWithEmailAndPassword(
                binding.useremailPhone.text.toString().trim(),
                binding.password.text.toString()
            )
                ?.addOnCompleteListener(this) { task ->
                    binding.progressCircular.visibility = View.GONE
                    if (task.isSuccessful) {
                        Config.currentUser = Config.auth!!.currentUser

                        Config.getFirebaseFirestore
                        Preferences.instance!!.saveStringPrefValue(
                            Preferences.instance!!.PREF_USER_EMAIL,
                            binding.useremailPhone.text.toString().trim()
                        )
                        Preferences.instance!!.saveStringPrefValue(
                            Preferences.instance!!.PREF_USER_PASS,
                            binding.password.text.toString()
                        )
                        Preferences.instance!!.saveStringPrefValue(
                            Preferences.instance!!.PREF_USER_ID,
                            Config.currentUser?.uid
                        )
                        Config.currentUser = Firebase.auth.currentUser
                        val user = User(
                            Config.currentUser!!.email!!,
                            Config.currentUser!!.displayName!!,
                            Config.currentUser!!.uid
                        )
                        val docIdRef: DocumentReference =
                            Config.firebaseDb!!.collection(Config.User).document(user.uid)
                        docIdRef.get()
                            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document != null) {
                                        if (!document.exists()) docIdRef.set(CustomersList())
                                            .addOnFailureListener(this) { task3 ->
                                                Config.appToast(
                                                    mActivity,
                                                    task3.message
                                                )
                                            }
                                    } else
                                        docIdRef.set(CustomersList())
                                            .addOnFailureListener(this) { task3 ->
                                                Config.appToast(
                                                    mActivity,
                                                    task3.message
                                                )
                                            }
                                } else docIdRef.set(CustomersList())
                                    .addOnFailureListener(this) { task3 ->
                                        Config.appToast(
                                            mActivity,
                                            task3.message
                                        )
                                    }
                                ActivityStackManager.instance!!.goToDashBoard(mActivity!!)
                                finish()
                            })
                    } else {
                        Config.appToast(mActivity, task.exception?.message)
                    }
                }
                ?.addOnFailureListener(this) { task2 ->
                    Config.appToast(mActivity, task2.message)

                }

        }
        else  Config.appToast(mActivity, mActivity?.getString(R.string.check_interent))
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

    val callbacks =  object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            //Log.d(TAG, "onVerificationCompleted:$credential")
            signIn(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            //Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            //Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
        override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
            val credential = PhoneAuthProvider.getCredential(verificationId, "code")
        }
    }
}