package com.example.playlistmaker.search.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.dpToPx

class SearchViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {
    private val trackCover: ImageView = trackView.findViewById(R.id.track_cover)
    private val trackName: TextView = trackView.findViewById(R.id.track_name)
    private val trackArtist: TextView = trackView.findViewById(R.id.track_artist)
    private val trackDuration: TextView = trackView.findViewById(R.id.track_duration)

    fun bind(model: Track) {
        val context = trackCover.context

        Glide.with(trackCover)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(

                dpToPx(
                    context.resources.getFloat(R.dimen.track_cover_corner_radius),
                    context
                )
                )
            )
            .into(trackCover)

        trackName.text =
            if (model.trackName.isNullOrEmpty()) {
                context.getString(R.string.empty_track_name)
            } else {
                model.trackName
            }

        trackArtist.text = if (model.trackName.isNullOrEmpty()) {
            context.getString(R.string.empty_artist)
        } else {
            model.artistName
        }

        trackDuration.text = model.getTrackTime()
        trackDuration.isVisible = trackDuration.text.isNullOrEmpty() == false
    }
}