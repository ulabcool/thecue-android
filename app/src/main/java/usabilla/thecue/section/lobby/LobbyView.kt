package usabilla.thecue.section.lobby

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.view_list.*
import usabilla.thecue.BaseFragment
import usabilla.thecue.R
import usabilla.thecue.model.QueuingPerson
import usabilla.thecue.toast

class LobbyView : BaseFragment(), View.OnClickListener {

    private val SAVED_DATA = "saved list data"
    private val DB_NAME = "queues"

    private val players = ArrayList<QueuingPerson>()
    private val mDatabase = FirebaseDatabase.getInstance()

    private var iAmInTheList = false

    companion object {
        val ARG_OFFICE = "office argument"

        fun getInstance(office: String): LobbyView {
            val fragment = LobbyView()
            val args = Bundle()
            args.putString(ARG_OFFICE, office)
            fragment.arguments = args
            return fragment
        }
    }

    private var listener = { player: QueuingPerson ->
        context.toast("click on player ${player.name}")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.view_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_players.setHasFixedSize(true)
        list_players.layoutManager = LinearLayoutManager(context)
        list_players.adapter = QueueAdapter(players, listener)

        join_queue.setOnClickListener(this)
        requestPlayers()
    }

    private fun requestPlayers() {
        showProgressDialog()
        mDatabase.getReference(DB_NAME)
                .child(arguments.getString(ARG_OFFICE))
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        players.clear()

                        var myId = ""
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            myId = user.providerData[0].uid
                        }

                        iAmInTheList = false
                        dataSnapshot.children.forEach {
                            val name = it.child("name").value as CharSequence
                            val userId = it.child("userId").value as CharSequence

                            if (userId.equals(myId)) {
                                iAmInTheList = true
                            }
                            players.add(QueuingPerson(name, userId))
                        }
                        hideProgressDialog()
                        (list_players.adapter as QueueAdapter).updatePlayers(players)
                        changeUiButton()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        hideProgressDialog()
                    }
                })
    }

    override fun onClick(p0: View?) {
        when (iAmInTheList) {
            true -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    mDatabase.getReference(DB_NAME).child(arguments.getString(ARG_OFFICE)).child(user.providerData[0].uid).removeValue()
                }
            }
            false -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val newUser = QueuingPerson(user.providerData[0].displayName!!, user.providerData[0].uid)
                    val map = HashMap<String, Any>()
                    map.put(user.providerData[0].uid, newUser)
                    mDatabase.getReference(DB_NAME).child(arguments.getString(ARG_OFFICE)).updateChildren(map)
                }
            }
        }
    }

    private fun changeUiButton() {
        when (iAmInTheList) {
            true -> {
                join_queue.setBackgroundColor(ContextCompat.getColor(activity, R.color.delete_button))
                join_queue.text = getString(R.string.button_leave)
            }
            else -> {
                join_queue.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                join_queue.text = getString(R.string.button_join)
            }
        }
    }

//    private fun showPic() {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            for (profile in user.providerData) {
//                val providerId = profile.providerId
//                val uid = profile.uid
//                val name = profile.displayName
//                val email = profile.email
//                val photoUrl = profile.photoUrl
//                Picasso.with(this)
//                        .load(photoUrl)
//                        .into(image)
//            }
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_DATA, list_players.layoutManager.onSaveInstanceState())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        list_players.layoutManager.onRestoreInstanceState(savedInstanceState?.getParcelable(SAVED_DATA))
    }
}
