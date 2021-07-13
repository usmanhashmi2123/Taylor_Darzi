package pk.taylor_darzi.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.*

object CheckConnection {
    var future: Future<InetAddress?>? =null
    val isConnectedToInternet: Boolean
        get() {
            try {
                var isConected = false
                val connectivity = Utils.curentActivity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork = connectivity.activeNetwork
                    if (activeNetwork != null) {
                        val nc = connectivity.getNetworkCapabilities(activeNetwork)
                        if (nc != null) isConected = nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        if(isConected)
                        {
                            var address: InetAddress? = null
                            future?.cancel(true)
                            try {
                                future= Executors.newSingleThreadExecutor().submit(Callable<InetAddress> {
                                    try {
                                        InetAddress.getByName("google.com")
                                    } catch (e: UnknownHostException) {
                                        null
                                    }
                                })
                                address = future?.get(1500, TimeUnit.MILLISECONDS)

                            } catch (e: InterruptedException) {
                            } catch (e: ExecutionException) {
                            } catch (e: TimeoutException) {
                            }
                            isConected = address!=null && !address.equals("")
                            future?.cancel(true)
                        }
                    }
                     return isConected


            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    fun hasInternetConnected(): Boolean {

            try {
                val connection = URL("https://www.google.com").openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "ConnectionTest")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1000 // configurable
                connection.connect()
                return (connection.responseCode == 200)
            } catch (e: IOException) {
                e.printStackTrace()
            }


        return false
    }

}