package pk.taylor_darzi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.customer_item.view.*
import pk.taylor_darzi.R
import pk.taylor_darzi.dataModels.Customer

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

    class ViewHolder(itemView: View, val onClick: (Customer) -> Unit) : RecyclerView.ViewHolder(
        itemView
    ){
        private var currentCustomer: Customer? = null
       /* init {
            itemView.setOnClickListener { currentCustomer?.let { onClick(it) }
            }
        }*/
        fun bindItems(user: Customer, pos :Int) {
            itemView.name_user.text = user.name
            itemView.phone_user.text = user.phone
            itemView.khata_Val.text = user.no.toString()
            itemView.setOnClickListener { user.let { onClick(it)} }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.customer_item, parent, false)
        return ViewHolder(v, onClick)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(itemCount > 0)
            holder.bindItems(dataListFiltered!!.get(position), position)
    }

    override fun getItemCount(): Int {
        if (dataListFiltered == null) return 0 else return dataListFiltered!!.size
    }
    fun filter(querry: String)
    {
        val filteredList: ArrayList<Customer> = ArrayList<Customer>()
        if(dataList!= null)
        {
            if(querry.isNullOrBlank()) filteredList.addAll(dataList!!)
            else
            {
                for (customer in dataList!!)
                {
                    if(customer.name!!.toLowerCase().contains(querry.toLowerCase()) || customer.phone.toLowerCase()
                            .contains(
                            querry.toLowerCase()
                        ))
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