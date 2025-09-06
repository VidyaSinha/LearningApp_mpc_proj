package com.example.learningapp

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val onDeleteClick: (ScanHistoryItem) -> Unit,
    private val onFavoriteClick: (ScanHistoryItem) -> Unit
) : ListAdapter<ScanHistoryItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.historyImageView)
        private val summaryView: TextView = itemView.findViewById(R.id.historySummaryTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(item: ScanHistoryItem) {
            summaryView.text = item.summary
            val bitmap = BitmapFactory.decodeFile(item.imagePath)
            imageView.setImageBitmap(bitmap)

            // Set favorite button state
            if (item.isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_star_filled)
            } else {
                favoriteButton.setImageResource(R.drawable.ic_star_outline)
            }

            deleteButton.setOnClickListener { onDeleteClick(item) }
            favoriteButton.setOnClickListener { onFavoriteClick(item) }
        }
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<ScanHistoryItem>() {
    override fun areItemsTheSame(oldItem: ScanHistoryItem, newItem: ScanHistoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ScanHistoryItem, newItem: ScanHistoryItem): Boolean {
        return oldItem == newItem
    }
}