package usabilla.thecue.section.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.view_auth.*
import usabilla.thecue.BaseActivity
import usabilla.thecue.R
import usabilla.thecue.section.lobby.LobbyActivity
import usabilla.thecue.toast

class AuthActivity : BaseActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_auth)
        button_sign_in.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.currentUser != null) {
            goToList()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                firebaseAuth(result.signInAccount)
            } else {
                hideProgressDialog()
                toast(result.status.statusMessage.toString())
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        toast("Google Play Services error.")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_sign_in -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuth(acct: GoogleSignInAccount?) {
        showProgressDialog()
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        goToList()
                    }
                }
    }

    private fun goToList() {
        startActivity(Intent(this, LobbyActivity::class.java))
        finish()
    }
}
