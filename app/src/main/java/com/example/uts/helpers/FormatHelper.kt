package com.example.uts.helpers

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object FormatHelper {

    fun formatPrice(price: Any?): String {
        if (price == null) return "$0.00"
        val doubleVal = when (price) {
            is Number -> price.toDouble()
            is String -> price.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(doubleVal)
    }

    fun formatRating(rating: Any?): String {
        if (rating == null) return "0.0"
        val doubleVal = when (rating) {
            is Number -> rating.toDouble()
            is String -> rating.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        return String.format(Locale.US, "%.1f", doubleVal)
    }

    fun formatDimensions(width: Any?, height: Any?, depth: Any?): String {
        val w = width?.toString() ?: "-"
        val h = height?.toString() ?: "-"
        val d = depth?.toString() ?: "-"
        return "$w × $h × $d cm"
    }

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "-"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}
