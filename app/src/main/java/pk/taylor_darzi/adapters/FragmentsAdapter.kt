package pk.taylor_darzi.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private var arrayList : ArrayList<Fragment> = ArrayList()
    override fun getItemCount(): Int {
       return 4
    }

    override fun createFragment(position: Int): Fragment {
        return arrayList.get(position)

    }
    fun setFragments(fragmenstList : ArrayList<Fragment>) {
        arrayList = fragmenstList

    }
}