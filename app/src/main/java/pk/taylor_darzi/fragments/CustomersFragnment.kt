package pk.taylor_darzi.fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.adapters.CustomersRecyclerViewAdapter
import pk.taylor_darzi.dataModels.*
import pk.taylor_darzi.databinding.CustomerFragmentBinding
import pk.taylor_darzi.interfaces.fragmentbackEvents
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Utils
import java.util.*

class CustomersFragnment : ParentFragnment() , fragmentbackEvents {
    private lateinit var binding: CustomerFragmentBinding

    private var resumed = false
    private var customerId = ""
    var selectedCustomer: Customer? =null
    var customersAdapter:CustomersRecyclerViewAdapter?= null

    private var map: Map<String, Customer>? = null

    val date_Cal = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = CustomerFragmentBinding.inflate(layoutInflater,  container, false)
        return binding.root
    }

    private fun adapterOnClick(customer: Customer) {
        selectedCustomer = customer
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        binding.customerDataShow.visibility=View.GONE
        binding.scrollview.visibility = View.VISIBLE
        enableDisable(false)
        showCustomerInfo(selectedCustomer!!, false)
    }

    private fun enableDisable(enable: Boolean)
    {
        binding.customerDataLayout.nameUserVal.isEnabled = enable
        binding.customerDataLayout.phoneUserVal.isEnabled = enable
        binding.customerDataLayout.wasoolVal.isEnabled = enable
        binding.customerDataLayout.remVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.lengthQVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.lengthBVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.shoulderVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.colarVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.chestVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.waistVal.isEnabled = enable
        binding.customerDataLayout.shirtLayoutI.hipVal.isEnabled = enable
        binding.customerDataLayout.trouserLayoutI.lengthSVal.isEnabled = enable
        binding.customerDataLayout.trouserLayoutI.panchaVal.isEnabled = enable
        binding.customerDataLayout.trouserLayoutI.tpocketCheckbox.isEnabled = enable
        binding.customerDataLayout.extrainfoLayoutI.embVal.isEnabled = enable
        binding.customerDataLayout.extrainfoLayoutI.spocketCheckbox.isEnabled = enable
        binding.customerDataLayout.extrainfoLayoutI.fpocketCheckbox.isEnabled = enable
        binding.customerDataLayout.extrainfoLayoutI.notesVal.isEnabled = enable
        binding.customerDataLayout.editPencil.isSelected = enable
        if(enable)
            binding.customerDataLayout.extrainfoLayoutI.save.visibility = View.VISIBLE
        else
            binding.customerDataLayout.extrainfoLayoutI.save.visibility = View.GONE

    }


    private fun showCustomerInfo(customer: Customer, isNew:Boolean)
    {
        binding.customerDataLayout.nameUserVal.setText(customer.name)
        binding.customerDataLayout.phoneUserVal.setText(customer.phone)
        if(isNew)
        {
            binding.customerDataLayout.delete.visibility = View.GONE
            customer.no = customers+1
        }
        else
            binding.customerDataLayout.delete.visibility = View.VISIBLE

        binding.customerDataLayout.noVal.text = customer.no.toString()
        var naapQameez: NaapQameez = customer.naapQameez!!
        binding.customerDataLayout.shirtLayoutI.lengthQVal.setText(naapQameez.shirtLength)
        binding.customerDataLayout.shirtLayoutI.lengthBVal.setText(naapQameez.armLength)
        binding.customerDataLayout.shirtLayoutI.shoulderVal.setText(naapQameez.shoulderLength)
        binding.customerDataLayout.shirtLayoutI.colarVal.setText(naapQameez.colarSize)
        binding.customerDataLayout.shirtLayoutI.chestVal.setText(naapQameez.chest)
        binding.customerDataLayout.shirtLayoutI.waistVal.setText(naapQameez.waist)
        binding.customerDataLayout.shirtLayoutI.hipVal.setText(naapQameez.ghera)
        var naapShalwar: NaapShalwar = customer.naapShalwar!!
        binding.customerDataLayout.trouserLayoutI.lengthSVal.setText(naapShalwar.shalwarLength)
        binding.customerDataLayout.trouserLayoutI.panchaVal.setText(naapShalwar.pancha)
        binding.customerDataLayout.trouserLayoutI.tpocketCheckbox.isChecked =naapShalwar.pocket

        var order: Order = customer.order!!
        binding.customerDataLayout.wasoolVal.setText(order.amountRcvd)
        binding.customerDataLayout.remVal.setText(order.amountRemaining)
        binding.customerDataLayout.extrainfoLayoutI.wapsiVal.text = order.deliveryDate

        var extraInfo: ExtraInfo = customer.extraInfo!!
        binding.customerDataLayout.extrainfoLayoutI.embVal.setText(extraInfo.karhaiNo)
        binding.customerDataLayout.extrainfoLayoutI.fpocketCheckbox.isChecked =extraInfo.pocketFront
        binding.customerDataLayout.extrainfoLayoutI.spocketCheckbox.isChecked = extraInfo.pocketSide
        binding.customerDataLayout.extrainfoLayoutI.notesVal.setText(extraInfo.notes)
        binding.scrollview.fullScroll(ScrollView.FOCUS_UP)
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        binding.customerDataLayout.shirtNaap.setOnClickListener(clickListener)
        binding.customerDataLayout.trouserNaap.setOnClickListener(clickListener)
        binding.customerDataLayout.backButton.setOnClickListener(clickListener)
        binding.customerDataLayout.editPencil.setOnClickListener(clickListener)
        binding.customerDataLayout.delete.setOnClickListener(clickListener)
        binding.addCustomer.setOnClickListener(clickListener)
        binding.customerDataLayout.extrainfoLayoutI.save.setOnClickListener(clickListener)
        binding.customerDataLayout.extrainfoLayoutI.wapsiVal.setOnClickListener(clickListener)
        binding.customerDataLayout.extrainfoLayoutI.wapsiConst.setOnClickListener(clickListener)
        binding.resetSearch.setOnClickListener(clickListener)
        binding.searchIcon.setOnClickListener(clickListener)
        binding.searchValue.addTextChangedListener(textWatcher)
        binding.searchValue.setOnEditorActionListener(OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                loadSearch(v.text.toString())
                Utils.hideKeyboard(requireActivity())
                true
            }
            false
        })
        if (binding.customerDataShow.isVisible)
            getData()

    }

    private fun loadSearch(querry: String) {
        customersAdapter?.filter(querry)
    }

    fun showList()
    {
        if(customersList.isNullOrEmpty()) Config.appToast(requireActivity(), "No data to show")

        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,
            false)
        if(customersAdapter== null)
            customersAdapter = CustomersRecyclerViewAdapter { customer -> adapterOnClick(customer) }

        customersAdapter?.setData(customersList)
        binding.recyclerView.adapter = customersAdapter
        customersAdapter?.notifyDataSetChanged()
    }
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.edit_pencil -> {
                enableDisable(!binding.customerDataLayout.editPencil.isSelected)
            }
            R.id.delete -> {
                if(selectedCustomer!= null)
                {
                    docRef!!.update(Config.Customers, FieldValue.arrayRemove(selectedCustomer)).addOnCompleteListener(
                        requireActivity()
                    ){ task1  ->
                        if (task1.isSuccessful) {
                            Config.appToast(requireActivity(), getString(R.string.deleted))
                            selectedCustomer = null
                            binding.customerDataLayout.backButton.callOnClick()
                        } else Config.appToast(requireActivity(), "failed to remove")
                    }.addOnFailureListener(requireActivity()){ task ->
                        Config.appToast(
                            requireActivity(),
                            task.message
                        )
                    }

                }

            }
            R.id.reset_search -> {
                binding.searchValue.setText("")
                resetSearch(true)
            }
            R.id.search_icon -> {
                if (!binding.searchValue.text.toString().isNullOrBlank()
                ) {
                    loadSearch(binding.searchValue.text.toString())
                    Utils.hideKeyboard(requireActivity())
                } else {
                    binding.searchValue.requestFocus()
                    Utils.showKeyboard(requireActivity(), binding.searchValue)
                }
            }
            R.id.shirt_naap -> {
                if (binding.customerDataLayout.shirtLayoutI.root.isVisible) binding.customerDataLayout.shirtLayoutI.root.visibility = View.GONE
                else binding.customerDataLayout.shirtLayoutI.root.visibility = View.VISIBLE
            }
            R.id.trouser_naap -> {
                if (binding.customerDataLayout.trouserLayoutI.root.isVisible) binding.customerDataLayout.trouserLayoutI.root.visibility = View.GONE
                else binding.customerDataLayout.trouserLayoutI.root.visibility = View.VISIBLE
            }
            R.id.back_button -> {
                binding.customerDataShow.visibility = View.VISIBLE
                binding.scrollview.visibility = View.GONE
            }
            R.id.add_customer -> {
                binding.customerDataShow.visibility = View.GONE
                binding.scrollview.visibility = View.VISIBLE
                binding.customerDataLayout.shirtLayoutI.root.visibility = View.VISIBLE
                binding.customerDataLayout.trouserLayoutI.root.isVisible
                enableDisable(true)
                selectedCustomer = null
                showCustomerInfo(Customer(), true)
            }
            R.id.save -> {
                saveCustomer(addData())
            }
            R.id.wapsi_Val, R.id.wapsi_const -> {
                val datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        date_Cal.set(Calendar.YEAR, year)
                        date_Cal.set(Calendar.MONTH, monthOfYear)
                        date_Cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        binding.customerDataLayout.extrainfoLayoutI.wapsiVal.text = Config.getFormatedStringDate(date_Cal.time)
                    },
                    date_Cal.get(Calendar.YEAR),
                    date_Cal.get(Calendar.MONTH),
                    date_Cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setButton(
                    DialogInterface.BUTTON_NEUTRAL,
                    "Clear"
                ) { dialog: DialogInterface?, which: Int ->
                    datePickerDialog.cancel()
                    binding.customerDataLayout.extrainfoLayoutI.wapsiVal.text = ""
                }
                datePickerDialog.show()
            }
        }
    }
    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(query: Editable) {
            if (query.isNullOrBlank()) resetSearch(true)
            else resetSearch(false)

            loadSearch(query.toString())
        }
    }

    private fun resetSearch(reset: Boolean) {
        if(reset)
        {
            if (!binding.searchValue.text.toString().isNullOrBlank()
            ) {
                binding.searchValue.removeTextChangedListener(textWatcher)
                binding.searchValue.setText("")
                binding.searchValue.addTextChangedListener(textWatcher)
            }

            Utils.hideKeyboard(requireActivity())
            binding.resetSearch.visibility =View.GONE
        }
        else  binding.resetSearch.visibility =View.VISIBLE
    }
    private fun getData()
    {
        try{
            docRef?.addSnapshotListener { snapshot, e ->
                if(Utils.curentActivity is DashBoard)
                {
                    if (e != null) {
                        Config.appToast(requireActivity(), e.message)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists() && snapshot.get(Config.Customers) != null) {
                        selectedCustomer = null
                        binding.customerDataLayout.backButton.callOnClick()
                        var list: List<Customer>? = snapshot.toObject(CustomersList::class.java)?.customers
                        customersList?.clear()
                        if(!list.isNullOrEmpty())
                        {
                            customersList?.addAll(list)
                            customers = customersList!!.size
                        }
                        else customers =-1

                    }
                    else Config.appToast(requireActivity(), "Current data: null")
                    showList()
                }

            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast(requireActivity(), ex.message)

        }

    }
    private fun saveCustomer(new: Boolean)
    {
        if(selectedCustomer!= null)
        {
            if(new)
            {
                docRef!!.update( Config.Customers, FieldValue.arrayUnion(selectedCustomer)).
                addOnCompleteListener(
                    requireActivity()
                ){ task1  ->
                    if (task1.isSuccessful) {
                        Config.appToast(requireActivity(), getString(R.string.added))
                        selectedCustomer = null
                        binding.customerDataLayout.backButton.callOnClick()
                    } else Config.appToast(requireActivity(), "failed to add")
                }.addOnFailureListener(requireActivity()){ task ->
                    Config.appToast(
                        requireActivity(),
                        task.message
                    )
                }

            }
            else
            {
                docRef!!.update(Config.Customers, customersList)
                    .addOnCompleteListener(requireActivity()) { task1 ->
                        if (task1.isSuccessful) {
                            Config.appToast(requireActivity(), getString(R.string.updated))
                            selectedCustomer = null
                            binding.customerDataLayout.backButton.callOnClick()
                        } else Config.appToast(requireActivity(), "failed to update")
                    }.addOnFailureListener(requireActivity()){ task ->
                    Config.appToast(
                        requireActivity(),
                        task.message
                    )
                }
            }


        }

    }

    private fun addData():Boolean
    {
        var new = false
        try{
            if(selectedCustomer == null)
            {
                new = true
                selectedCustomer = Customer(
                    binding.customerDataLayout.nameUserVal.text.toString(),
                    binding.customerDataLayout.phoneUserVal.text.toString(),
                    (customers + 1),
                    Order(),
                    NaapQameez(),
                    NaapShalwar(),
                    ExtraInfo()
                )

            }

            selectedCustomer!!.name = binding.customerDataLayout.nameUserVal.text.toString()
            selectedCustomer!!.phone = binding.customerDataLayout.phoneUserVal.text.toString()
            var naapQameez: NaapQameez = selectedCustomer!!.naapQameez!!
            if(naapQameez!= null)
            {
                naapQameez.shirtLength = binding.customerDataLayout.shirtLayoutI.lengthQVal.text.toString()
                naapQameez.armLength = binding.customerDataLayout.shirtLayoutI.lengthBVal.text.toString()
                naapQameez.shoulderLength = binding.customerDataLayout.shirtLayoutI.shoulderVal.text.toString()
                naapQameez.colarSize = binding.customerDataLayout.shirtLayoutI.colarVal.text.toString()
                naapQameez.chest = binding.customerDataLayout.shirtLayoutI.chestVal.text.toString()
                naapQameez.waist = binding.customerDataLayout.shirtLayoutI.waistVal.text.toString()
                naapQameez.ghera = binding.customerDataLayout.shirtLayoutI.hipVal.text.toString()
            }
            var naapShalwar: NaapShalwar = selectedCustomer!!.naapShalwar!!
            if(naapShalwar!= null)
            {
                naapShalwar.shalwarLength = binding.customerDataLayout.trouserLayoutI.lengthSVal.text.toString()
                naapShalwar.pancha = binding.customerDataLayout.trouserLayoutI.panchaVal.text.toString()
                naapShalwar.pocket = binding.customerDataLayout.trouserLayoutI.tpocketCheckbox.isChecked

            }
            var order: Order = selectedCustomer!!.order!!
            if(order!= null)
            {
                order.amountRcvd = binding.customerDataLayout.wasoolVal.text.toString()
                order.amountRemaining = binding.customerDataLayout.remVal.text.toString()
                order.deliveryDate = binding.customerDataLayout.extrainfoLayoutI.wapsiVal.text.toString()

            }
            var extraInfo: ExtraInfo = selectedCustomer!!.extraInfo!!
            if(extraInfo!= null)
            {
                extraInfo.karhaiNo = binding.customerDataLayout.extrainfoLayoutI.embVal.text.toString()
                extraInfo.pocketFront = binding.customerDataLayout.extrainfoLayoutI.fpocketCheckbox.isChecked
                extraInfo.pocketSide = binding.customerDataLayout.extrainfoLayoutI.spocketCheckbox.isChecked
                extraInfo.notes = binding.customerDataLayout.extrainfoLayoutI.notesVal.text.toString()
            }
        }
        catch (ex: Exception)
        {
            new = true
            ex.printStackTrace()
            Config.appToast(requireActivity(), ex.message)

        }
        if(!new)
        {
            for ((index, customer) in customersList!!.withIndex()) {
                var id = customer.name+"_"+customer.phone+"_"+ customer.no
               if(customerId.equals(id))
               {
                   customersList!!.set(index, selectedCustomer!!)
                   break
               }
            }
        }

        return new
    }
    override fun onPause() {
        super.onPause()
        resumed = false
    }

    override fun doBack(): Boolean {
        if(!binding.customerDataShow.isVisible)
        {
            binding.customerDataLayout.backButton.callOnClick()
            return false
        }
        else return true
    }
}