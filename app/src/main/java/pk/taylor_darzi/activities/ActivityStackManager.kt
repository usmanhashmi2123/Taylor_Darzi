package pk.taylor_darzi.activities

import android.app.Activity
import android.content.Intent
import pk.taylor_darzi.utils.LoadingProgressDialog

class ActivityStackManager {
    fun startLoginActivity(mContext: Activity) {
        val intent = Intent(mContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        LoadingProgressDialog.destroy(mContext)
        mContext.startActivity(intent)
    }

    companion object {
        var INSTANCE: ActivityStackManager? = null
        val instance: ActivityStackManager?
            get() {
                if (INSTANCE == null)
                    INSTANCE = ActivityStackManager()
                return INSTANCE
            }
    }
}