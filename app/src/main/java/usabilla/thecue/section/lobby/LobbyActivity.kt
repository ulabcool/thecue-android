package usabilla.thecue.section.lobby

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.view_lobby.*
import usabilla.thecue.BaseActivity
import usabilla.thecue.R
import usabilla.thecue.section.signin.AuthActivity


class LobbyActivity : BaseActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_rokin -> {
                pager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_herengracht -> {
                pager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val mPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(position: Int) {
            // Do nothing
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // Do nothing
        }

        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> navigation?.selectedItemId = R.id.navigation_rokin
                1 -> navigation?.selectedItemId = R.id.navigation_herengracht
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_lobby)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        pager.adapter = LobbyAdapter(supportFragmentManager)
        pager.addOnPageChangeListener(mPageChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
