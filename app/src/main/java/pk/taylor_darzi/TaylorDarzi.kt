package pk.taylor_darzi

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class TaylorDarzi : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        mContext = this
    }

    companion object {
        private var INSTANCE: TaylorDarzi? = null
        var mContext: Context? = null
        val instance: TaylorDarzi?
            get() {
                if (INSTANCE == null)
                    INSTANCE = mContext as TaylorDarzi?
                return INSTANCE
            }
    }
}