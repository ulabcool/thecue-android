package usabilla.thecue

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }


}
