package pk.taylor_darzi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.EmailAuthProvider
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.ActivityStackManager
import pk.taylor_darzi.databinding.MyAccountFragmentBinding
import pk.taylor_darzi.utils.CheckConnection
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils


class MyAccountFragnment : ParentFragnment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: MyAccountFragmentBinding
            private var resumed = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= MyAccountFragmentBinding.inflate(layoutInflater, container, false)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.lang_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //language_name.text
            binding.languageName.adapter = adapter
            if (language!!.contains("Eng"))
                binding.languageName.setSelection(0, false)
            else
                binding.languageName.setSelection(1, false)
            binding.languageName.onItemSelectedListener = this

        }
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.keyboard_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //language_name.text
            binding.keyboardName.adapter = adapter
            if (Preferences.instance!!.isCustomKeyboard)
                binding.keyboardName.setSelection(1, false)
            else
                binding.keyboardName.setSelection(0, false)
            binding.keyboardName.onItemSelectedListener = this

        }
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.messaging_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //language_name.text
            binding.messagingName.adapter = adapter
            if (Preferences.instance!!.isSmsApp)
                binding.messagingName.setSelection(1, false)
            else
                binding.messagingName.setSelection(0, false)
            binding.messagingName.onItemSelectedListener = this

        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        binding.nameVal.text = Config.currentUser?.displayName
        if(binding.phoneVal.text.isNullOrBlank())
        {
            docRef?.get()?.addOnSuccessListener (requireActivity() ){documentSnapshot ->
                if(documentSnapshot.contains("phoneNumber"))
                    binding.phoneVal.text = documentSnapshot.get("phoneNumber").toString()
            }
        }
        binding.emailVal.text = Config.currentUser?.email
        //binding.deleteAccount.setOnClickListener(clickListener)
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if(parent?.id == R.id.language_name)
        {
            if (position ==1 && language!!.contains("En")) {
                Utils.changeLanguage("ur", "PK")
                Preferences.instance!!.saveStringPrefValue(
                        Preferences.instance!!.PREF_LANG,
                        requireActivity().resources.getString(R.string.urdu)
                )
                Utils.curentActivity?.finish()
                ActivityStackManager.instance!!.goToDashBoard(Utils.curentActivity!!)
            } else  if(position ==0 && !language!!.contains("En")){
                Utils.changeLanguage("en", "US")
                Preferences.instance!!.saveStringPrefValue(
                        Preferences.instance!!.PREF_LANG,
                        requireActivity().resources.getString(R.string.english)
                )
                Utils.curentActivity?.finish()
                ActivityStackManager.instance!!.goToDashBoard(Utils.curentActivity!!)
            }
        }
        else if(parent?.id == R.id.keyboard_name)
        {
            if (position ==1)  Preferences.instance!!.saveBooleanPrefValue(Preferences.instance!!.PREF_KEYBORAD,true)
            else  if(position ==0) Preferences.instance!!.saveBooleanPrefValue(Preferences.instance!!.PREF_KEYBORAD,false)

        }
        else if(parent?.id == R.id.messaging_name)
        {
            if (position ==1) Preferences.instance!!.saveBooleanPrefValue(Preferences.instance!!.PREF_APP,true)
            else  if(position ==0) Preferences.instance!!.saveBooleanPrefValue(Preferences.instance!!.PREF_APP,false)

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
//    private val clickListener = View.OnClickListener { view ->
//        when (view.id) {
//            R.id.delete_account -> {
//                if(CheckConnection.isConnectedToInternet) {
//                    val credential = EmailAuthProvider
//                        .getCredential(Preferences.instance!!.userEmailId, Preferences.instance!!.userPass)
//                    Config.currentUser?.reauthenticate(credential)?.addOnCompleteListener(requireActivity()) { task ->
//                        if(task.isSuccessful)
//                        {
//                            docRef = Config.getFirebaseFirestore.collection(Config.User).document(Preferences.instance!!.userAuthId)
//                            docRef?.delete()
//                            Config.currentUser?.delete()
//                            requireActivity().finish()
//                            ActivityStackManager.instance!!.startLoginActivity(Utils.curentActivity!!)
//                        }
//                        else  Config.appToast(
//                            task.exception?.message)
//
//                    }?.addOnFailureListener(requireActivity()) { task2 ->
//                        Config.appToast(
//                            task2.message)
//                    }
//
//                }
//                else Config.appToast( getString(R.string.check_interent))
//
//            }
//        }
//
//    }
    override fun onPause() {
        super.onPause()
        resumed = false
    }


}