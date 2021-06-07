package pk.taylor_darzi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.R
import pk.taylor_darzi.utils.LoadingProgressDialog

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mActivity = this

    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }
}