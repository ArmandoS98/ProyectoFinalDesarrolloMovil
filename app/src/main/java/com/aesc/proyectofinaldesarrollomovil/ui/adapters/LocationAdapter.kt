package com.aesc.proyectofinaldesarrollomovil.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.extension.loadByURL
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.LocationDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.Locations
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationAdapter(options: FirestoreRecyclerOptions<Locations>, val locationDao: LocationDao) :
    FirestoreRecyclerAdapter<Locations, LocationAdapter.PostViewHolder>(
        options
    ) {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_location_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Locations) {

        val texto = "Location name: ${model.userText}\nLatitud: ${model.latitude}\nLongitud: ${model.longitude}"
        holder.postText.text = texto
        holder.userText.text = model.createdBy.displayName
        holder.userImage.loadByURL(model.createdBy.imageUrl)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
    }
}