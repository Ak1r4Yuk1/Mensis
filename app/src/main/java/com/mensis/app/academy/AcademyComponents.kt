package com.mensis.app.academy

import android.webkit.WebView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(markdown: String, textColor: Int, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setTextColor(textColor)
                textSize = 16f
                setLineSpacing(0f, 1.25f)
            }
        },
        update = { tv ->
            tv.setTextColor(textColor)
            Markwon.create(tv.context).setMarkdown(tv, markdown)
        }
    )
}

/**
 * Embedded YouTube player. YouTube now requires the embedded IFrame player to identify the
 * client via the `origin`/Referer (otherwise it returns error 152). We set the IFrame
 * `origin` to the app package and give the underlying WebView a desktop-like user agent.
 */
@Composable
fun YouTubePlayer(videoId: String, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val origin = "https://com.mensis.app"
    AndroidView(
        modifier = modifier,
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(this)
                (getChildAt(0) as? WebView)?.settings?.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36"
                }
                val options = IFramePlayerOptions.Builder().controls(1).origin(origin).build()
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                }, options)
            }
        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(view)
            view.release()
        }
    )
}
