package pk.taylor_darzi.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import java.util.*

object Config {
    var LOGIN_GUID = ""
    var locale: Locale? = null
    var NOTIFICATION_ID = 5000
    var currentUser: FirebaseUser?= null

    fun appToastShort(mCurrentActivity: Context?, message: String?) {
        Toast.makeText(mCurrentActivity, message, Toast.LENGTH_SHORT).show()
    }
}