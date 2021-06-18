package pk.taylor_darzi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.customer_item.view.name_user
import kotlinx.android.synthetic.main.customer_item.view.phone_user
import kotlinx.android.synthetic.main.order_item.view.*
import pk.taylor_darzi.R
import pk.taylor_darzi.dataModels.Customer

class OrdersRecyclerViewAdapter(val onClick: (Customer) -> Unit, val onDelivered: (Customer) -> Unit) : RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder?>() {
    private var dataList: ArrayList<Customer>? = ArrayList<Customer>()
    private var dataListFiltered: ArrayList<Customer>? = null

    fun setData(arrayList: ArrayList<Customer>?) {
        dataList = arrayList
        dataListFiltered = arrayList
    }

    fun getItem(position: Int): Customer? {
        return if (dataListFiltered != null && position < dataListFiltered!!.size) dataListFiltered!![position] else null
    }

    val listLoaded: List<Customer>? get() = dataListFiltered

    class ViewHolder(itemView: View, val onClick: (Customer) -> Unit, val onDelivered: (Customer) -> Unit) : RecyclerView.ViewHolder(
        itemView
    ){

        fun bindItems(user: Customer, pos :Int) {
            itemView.name_user.text = user.name
            itemView.phone_user.text = user.phone
            itemView.wasooli_Val.text = user.order?.amountRcvd
            itemView.rema_Val.text = user.order?.amountRemaining
            itemView.khata_val.text = user.no.toString()
            itemView.deliver.setOnClickListener { user.let { onDelivered(it)} }
            itemView.setOnClickListener { user.let { onClick(it)} }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return ViewHolder(v, onClick, onDelivered)
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