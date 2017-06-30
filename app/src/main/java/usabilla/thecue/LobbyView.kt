package usabilla.thecue

import android.os.Bundle
import android.support.v4.app.Fragment
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


class LobbyView : Fragment(), View.OnClickListener {

    private val SAVED_DATA = "saved list data"

    private var players = ArrayList<QueuingPerson>()
    private var mDatabase = FirebaseDatabase.getInstance()

    private var iAmInTheList = false

    companion object {
        val ARG_OFFICE_RES = "office argument"

        fun getInstance(office: String): LobbyView {
            val fragment = LobbyView()
            val args = Bundle()
            args.putString(LobbyView.ARG_OFFICE_RES, office)
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
        list_players.adapter = PlayerAdapter(players, listener)

        join_queue.setOnClickListener(this)
        requestPlayers()
    }

    private fun requestPlayers() {
        (activity as BaseActivity).showProgressDialog()
        mDatabase.getReference("queues")
                .child(arguments.getString(ARG_OFFICE_RES))
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
                        (activity as BaseActivity).hideProgressDialog()
                        (list_players.adapter as PlayerAdapter).updatePlayers(players)
                        buttonVisual()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        (activity as BaseActivity).hideProgressDialog()
                        context.toast("Failed to read value")
                    }
                })
    }

    override fun onClick(p0: View?) {
        when (iAmInTheList) {
            true -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    mDatabase.getReference("queues").child(arguments.getString(ARG_OFFICE_RES)).child(user.providerData[0].uid).removeValue()
                }
            }
            false -> {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val newUser = QueuingPerson(user.providerData[0].displayName!!, user.providerData[0].uid)
                    val map = HashMap<String, Any>()
                    map.put(user.providerData[0].uid, newUser)
                    mDatabase.getReference("queues").child(arguments.getString(ARG_OFFICE_RES)).updateChildren(map)
                }
            }
        }
    }

    private fun buttonVisual() {
        when (iAmInTheList) {
            true -> {
                join_queue.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                join_queue.text = getString(R.string.leave)
            }
            else -> {
                join_queue.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                join_queue.text = getString(R.string.join)
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
