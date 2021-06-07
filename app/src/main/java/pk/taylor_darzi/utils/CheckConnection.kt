package pk.taylor_darzi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class CheckConnection(private val context: Context) {

    val isConnectedToInternet: Boolean
        @SuppressLint("MissingPermission")
        get() {
            try {
                var isConected = false
                val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork = connectivity.activeNetwork
                    if (activeNetwork != null) {
                        val nc = connectivity.getNetworkCapabilities(activeNetwork)
                        if (nc != null) isConected = nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    }
                //if (!isConected)
                    //Utility.appToast(context, context.getString(R.string.check_interent));

                return isConected
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

    companion object {
        private const val TAG = "CheckConnection"
        private var checkConnection: CheckConnection? = null
        fun getInstance(context: Context): CheckConnection? {
            if (checkConnection == null) checkConnection = CheckConnection(context)
            return checkConnection
        }
    }
}