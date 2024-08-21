package com.example.playlismaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {
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
                    context.resources.getFloat(R.dimen.track_cover_corner_radius).dpToPx(
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

        trackDuration.text = if (!model.trackTime.isNullOrEmpty()) {
            model.trackTime
        } else if (model.trackTimeMillis != null) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        } else {
            trackDuration.visibility = View.GONE
            ""
        }
    }

    /**
     * Добавляет возможность конвертации в dp
     *
     * @param context контекст для обращения к ресурсам
     */
    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    }
}