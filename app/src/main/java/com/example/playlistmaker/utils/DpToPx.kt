package com.example.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

/**
 * Конвертация в dp
 *
 * @param context контекст для обращения к ресурсам
 */
fun dpToPx(px: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        context.resources.displayMetrics
    ).toInt()
}
