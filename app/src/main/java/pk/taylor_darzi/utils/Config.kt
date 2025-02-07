package pk.taylor_darzi.utils

import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import pk.taylor_darzi.dataModels.Customer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Config {

    val phonePat = "^[+][0-9]{7,13}\$"
    var LOGIN_GUID = ""
    var User = "user"
    var Version = "version"
    var Khata = "khata"
    var Customers = "customers"
    var locale: Locale? = null
    var NOTIFICATION_ID = 5000
    var currentUser: FirebaseUser?= null
    var customersList: ArrayList<Customer>? = ArrayList<Customer>()
    private const val DEFAULT_CACHE_SIZE_BYTES = (300 * 1024 * 1024).toLong() // 300 MB
    fun appToast( message: String?) {
        Toast.makeText(Utils.curentActivity!!, message, Toast.LENGTH_SHORT).show()
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
    fun uploadImage(customerNo: String, customerName: String, fileUri: Uri,onSuccess: (String) -> Unit) {
        getStorageRef
        val filePath = getStorageRef.child("Taylor/$customerName$customerNo.jpg")
        val uploadtask =filePath.putFile(fileUri)

        uploadtask.addOnSuccessListener { taskSnapshot ->
            appToast("Image stored successfully")
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                onSuccess(downloadUrl)
            }
        }.addOnFailureListener {
            appToast(it.message ?: "Upload failed")
        }
    }
    var firebaseDb: FirebaseFirestore?=null
    var fbStorage: StorageReference?= null
    var auth :FirebaseAuth?=null

    // Returns singleton instance
    val getFirebaseAuth: FirebaseAuth
        get() {
            if(auth==null) {
                auth = FirebaseAuth.getInstance()
                currentUser = auth?.currentUser
            }

            return auth as FirebaseAuth
        }
    val getStorageRef: StorageReference
        get() {
            if (fbStorage == null) {
                //getFirebaseAuth
                fbStorage = Firebase.storage.reference
            }
            return fbStorage as StorageReference
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

}