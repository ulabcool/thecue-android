package usabilla.thecue

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_player.view.*

class PlayerAdapter(private var mItems: ArrayList<QueuingPerson>, private var listener: (QueuingPerson) -> Unit) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    class ViewHolder(noteView: View) : RecyclerView.ViewHolder(noteView) {
        fun bind(item: QueuingPerson, listener: (QueuingPerson) -> Unit) = with(itemView) {
            player_name.text = item.name
            itemView.setOnClickListener { listener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_player, parent, false)
        return PlayerAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerAdapter.ViewHolder?, position: Int) {
        holder?.bind(mItems[position], listener)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    infix fun updatePlayers(items: ArrayList<QueuingPerson>) {
        mItems = items
        notifyDataSetChanged()
    }
}