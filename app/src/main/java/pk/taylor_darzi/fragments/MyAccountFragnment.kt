package pk.taylor_darzi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.my_account_fragment.*
import kotlinx.android.synthetic.main.my_account_fragment.view.*
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.ActivityStackManager
import pk.taylor_darzi.utils.CheckConnection
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils


class MyAccountFragnment : ParentFragnment(), AdapterView.OnItemSelectedListener {
    private var resumed = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.my_account_fragment, container, false)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lang_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //language_name.text
            view.language_name.adapter = adapter
            if (language!!.contains("Eng"))
                view.language_name.setSelection(0, false)
            else
                view.language_name.setSelection(1, false)
            view.language_name.onItemSelectedListener = this

        }
        return view
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        name_val.text = Config.currentUser?.displayName
        if(phone_val.text.isNullOrBlank())
        {
            docRef?.get()?.addOnSuccessListener (requireActivity() ){documentSnapshot ->
                if(documentSnapshot.contains("phoneNumber"))
                    phone_val.text = documentSnapshot.get("phoneNumber").toString()
            }
        }
        email_val.text = Config.currentUser?.email
        delete_account.setOnClickListener(clickListener)
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position ==1 && language!!.contains("Eng")) {
            Utils.changeLanguage("ur", "PK")
            Preferences.instance!!.saveStringPrefValue(
                Preferences.instance!!.PREF_LANG,
                requireActivity().resources.getString(R.string.urdu)
            )
            requireActivity().finish()
            ActivityStackManager.instance!!.goToDashBoard(Utils.curentActivity!!)
        } else  if(position ==0 && !language!!.contains("Eng")){
            Utils.changeLanguage("en", "US")
            Preferences.instance!!.saveStringPrefValue(
                Preferences.instance!!.PREF_LANG,
                requireActivity().resources.getString(R.string.english)
            )
            requireActivity().finish()
            ActivityStackManager.instance!!.goToDashBoard(Utils.curentActivity!!)
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.delete_account -> {
                if(CheckConnection.getInstance(requireActivity())!!.isConnectedToInternet) {
                    val credential = EmailAuthProvider
                        .getCredential(Preferences.instance!!.userEmailId, Preferences.instance!!.userPass)
                    Config.currentUser?.reauthenticate(credential)?.addOnCompleteListener(requireActivity()) { task ->
                        if(task.isSuccessful)
                        {
                            docRef = Config.getFirebaseFirestore.collection(Config.User).document(Preferences.instance!!.userAuthId)
                            docRef?.delete()
                            Config.currentUser?.delete()
                            requireActivity().finish()
                            ActivityStackManager.instance!!.startLoginActivity(Utils.curentActivity!!)
                        }
                        else  Config.appToast(
                            requireActivity(),
                            task.exception?.message)

                    }?.addOnFailureListener(requireActivity()) { task2 ->
                        Config.appToast(
                            requireActivity(),
                            task2.message)
                    }

                }
                else Config.appToast(requireActivity(), getString(R.string.check_interent))

            }
        }

    }
    override fun onPause() {
        super.onPause()
        resumed = false
    }


}