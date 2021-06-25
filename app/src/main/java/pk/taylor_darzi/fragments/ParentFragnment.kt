package pk.taylor_darzi.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentReference
import pk.taylor_darzi.dataModels.Customer
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences

abstract class ParentFragnment : Fragment() {
    protected var language: String? = null
    protected var customers = 0
    protected var docRef : DocumentReference? =null
    protected var customersList: ArrayList<Customer>? = ArrayList<Customer>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        language = Preferences.instance!!.language
        docRef = Config.getFirebaseFirestore.collection(Config.User).document(Preferences.instance!!.userAuthId)
    }

}