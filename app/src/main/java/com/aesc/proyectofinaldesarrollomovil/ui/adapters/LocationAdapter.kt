package com.aesc.proyectofinaldesarrollomovil.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.extension.loadByURL
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.Locations
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LocationAdapter(
    val options: FirestoreRecyclerOptions<Locations>,
    val listener: IPostAdapter
) :
    FirestoreRecyclerAdapter<Locations, LocationAdapter.PostViewHolder>(
        options
    ) {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val share: TextView = itemView.findViewById(R.id.btnShareData)
        val delete: TextView = itemView.findViewById(R.id.btnDeleteData)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location_history, parent, false)
        )

        viewHolder.share.setOnClickListener {
            listener.onShareClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).data?.get("latitude")
                    ?.toString()!!,
                snapshots.getSnapshot(viewHolder.adapterPosition).data!!["longitude"].toString()
            )
        }
        viewHolder.delete.setOnClickListener {
            listener.onDeleteClicked(
                snapshots.getSnapshot(viewHolder.adapterPosition).id
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Locations) {

        val texto =
            "Location name: ${model.userText}\nLatitud: ${model.latitude}\nLongitud: ${model.longitude}"
        holder.postText.text = texto
        holder.userText.text = model.createdBy.displayName
        holder.userImage.loadByURL(model.createdBy.imageUrl)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        listener.onItemsSize(1)

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isMine = model.createdBy.uid.contains(currentUserId)
        if (isMine) holder.delete.visibility = VISIBLE else holder.delete.visibility = GONE
    }

    override fun getItemCount(): Int {
        val count = options.snapshots.size
        listener.onItemsSize(count)
        return count

    }
}

interface IPostAdapter {
    fun onShareClicked(latitude: String, longitude: String)
    fun onDeleteClicked(id: String)
    fun onItemsSize(size: Int)
}