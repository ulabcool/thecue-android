package usabilla.thecue

import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment() {

    fun showProgressDialog() {
        if (activity == null) {
            return
        }
        (activity as BaseActivity).showProgressDialog()
    }

    fun hideProgressDialog() {
        if (activity == null) {
            return
        }
        (activity as BaseActivity).hideProgressDialog()
    }

    override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}
