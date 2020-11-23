package edu.utap.mapreduce

import android.text.SpannableStringBuilder

class GameLogger {
    private var builder = SpannableStringBuilder()

    // TODO: Consider adding some styles to the log?
    fun log(msg: CharSequence) {
        builder.append(msg)
        builder.append("\n")
    }

    fun show(): SpannableStringBuilder {
        return builder
    }
}
