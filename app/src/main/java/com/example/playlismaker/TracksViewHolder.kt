package com.example.playlismaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {
    private val trackCover: ImageView = trackView.findViewById(R.id.track_cover)
    private val trackName: TextView = trackView.findViewById(R.id.track_name)
    private val trackArtist: TextView = trackView.findViewById(R.id.track_artist)
    private val trackDuration: TextView = trackView.findViewById(R.id.track_duration)

    fun bind(model: Track) {
        Glide.with(trackCover)
            .load(model.artWorkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(
                    dpToPx(
                        trackCover.context.resources.getFloat(R.dimen.track_cover_corner_radius),
                        trackCover.context
                    )
                )
            )
            .into(trackCover)

        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackDuration.text = model.trackTime
    }


    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}