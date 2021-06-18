package pk.taylor_darzi.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.customer_data_layout.*
import pk.taylor_darzi.dataModels.Customer
import pk.taylor_darzi.dataModels.CustomersList
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Config {
    var LOGIN_GUID = ""
    var User = "user"
    var Version = "version"
    var Khata = "khata"
    var Customers = "customers"
    var locale: Locale? = null
    var NOTIFICATION_ID = 5000
    var currentUser: FirebaseUser?= null
    var customersList: ArrayList<Customer>? = ArrayList<Customer>()
    const val DEFAULT_CACHE_SIZE_BYTES = (300 * 1024 * 1024).toLong() // 300 MB
    fun appToast(mCurrentActivity: Context?, message: String?) {
        Toast.makeText(mCurrentActivity, message, Toast.LENGTH_SHORT).show()
    }
    fun getFormatedStringDate(date: Date?): String? {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(date)
    }
    fun getDateFromStringDate(dateString: String?): Date? {

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        var date: Date? = null
        try {
            date = sdf.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            e.message
        }
        return date
    }
    var auth: FirebaseAuth?=null
    var firebaseDb: FirebaseFirestore?=null
    // Returns singleton instance
    val getFirebaseAuth: FirebaseAuth
        get() {
            if (auth == null) auth = FirebaseAuth.getInstance()
            return auth as FirebaseAuth
        }

    val getFirebaseFirestore: FirebaseFirestore
        get() {
            if (firebaseDb == null) firebaseDb = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(DEFAULT_CACHE_SIZE_BYTES)
                .build()
            firebaseDb!!.firestoreSettings = settings
            return firebaseDb as FirebaseFirestore
        }

    private fun getData(docRef: DocumentReference)
    {
        try{
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Config.appToast(Utils.curentActivity, e.message)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists() && snapshot.get(Config.Customers) != null) {


                    var list: List<Customer>? = snapshot.toObject(CustomersList::class.java)?.customers
                    customersList?.clear()
                    if(!list.isNullOrEmpty()) {
                        customersList?.addAll(list)

                    }
                } else Config.appToast(Utils.curentActivity, "Current data: null")

            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast(Utils.curentActivity, ex.message)

        }
    }
}