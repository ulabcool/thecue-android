package usabilla.thecue

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class LobbyAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): android.support.v4.app.Fragment {
        when (i) {
            0 -> return LobbyView.getInstance("rokin")
            else -> return LobbyView.getInstance("herengracht")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return (position + 1).toString()
    }
}