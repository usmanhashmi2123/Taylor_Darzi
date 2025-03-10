package pk.taylor_darzi.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.taylor_darzi.dataModels.Customer
import pk.taylor_darzi.databinding.CustomerItemBinding
import pk.taylor_darzi.utils.Utils

class CustomersRecyclerViewAdapter(private val onClick: (Customer) -> Unit) : RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder?>() {
    private var dataList: ArrayList<Customer>? = ArrayList<Customer>()
    private var dataListFiltered: ArrayList<Customer>? = null
    private var checkEnabled = false
    fun setData(arrayList: ArrayList<Customer>?) {
        dataList = arrayList
        dataListFiltered = arrayList
    }

    fun updateData(arrayList: ArrayList<Customer>?) {
        dataListFiltered = arrayList
    }

    fun getItem(position: Int): Customer? {
        return if (dataListFiltered != null && position < dataListFiltered!!.size) dataListFiltered!![position] else null
    }

    val listLoaded: List<Customer>? get() = dataListFiltered

    class ViewHolder(private val itemBinding: CustomerItemBinding, val onClick: (Customer) -> Unit) : RecyclerView.ViewHolder(itemBinding.root){
        private var currentCustomer: Customer? = null

        fun bindItems(user: Customer) {
            itemBinding.nameUser.text = user.name
            itemBinding.phoneUser.text = user.phone
            itemBinding.khataVal.text = user.no.toString()
           user.imageUri?.let { it->
               Glide.with(Utils.mContext!!)
                   .load(Uri.parse(it))
                   .into(itemBinding.customerPic)
           }

            itemBinding.root.setOnClickListener { user.let { onClick(it)} }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding  = CustomerItemBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return ViewHolder(itemBinding , onClick)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(itemCount > 0)
            holder.bindItems(dataListFiltered!!.get(position))
    }

    override fun getItemCount(): Int {
        if (dataListFiltered == null) return 0 else return dataListFiltered!!.size
    }
    fun filter(querry: String)
    {
        val filteredList: ArrayList<Customer> = ArrayList<Customer>()
        if(dataList!= null)
        {
            if(querry.isBlank()) filteredList.addAll(dataList!!)
            else
            {
                for (customer in dataList!!)
                {
                    var phone = customer.phone.replace("+92","0")
                    if(customer.name!!.lowercase().contains(querry.lowercase())
                        || phone.lowercase().contains(querry.lowercase())
                        || customer.phone.lowercase().contains(querry.lowercase()))
                        filteredList.add(customer)

                }
            }

        }
        dataListFiltered = filteredList
        notifyDataSetChanged()
    }

    fun clearDataAdapter() {
        if(dataList!= null) dataList!!.clear()
        if(dataListFiltered!= null) dataListFiltered!!.clear()

    }


}