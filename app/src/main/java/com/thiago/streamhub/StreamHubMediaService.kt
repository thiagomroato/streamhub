package com.thiago.streamhub

import android.content.Intent
import android.os.Bundle
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class StreamHubMediaService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "StreamHubMedia").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    isActive = true
                    setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING, 0L, 1f).build())
                }
                override fun onPause() {
                    setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PAUSED, 0L, 1f).build())
                }
                override fun onStop() {
                    isActive = false
                    setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_STOPPED, 0L, 0f).build())
                }
            })
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_NONE, 0L, 0f).build())
        }
        sessionToken = mediaSession.sessionToken
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot("streamhub_root", null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        val items = mutableListOf(
            mediaItem("streamhub_music", "StreamHub Música", "Sua central multimídia"),
            mediaItem("streamhub_favorites", "Favoritos", "Conteúdos salvos")
        )
        result.sendResult(items)
    }

    private fun mediaItem(id: String, title: String, subtitle: String): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(id)
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription("Reprodução de áudio compatível com Android Auto")
            .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }
}
