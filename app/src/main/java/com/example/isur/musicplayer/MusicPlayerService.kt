package com.example.isur.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class MusicPlayerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        val a = intent?.getStringArrayExtra("SongList")
        currentTrackIndex = intent!!.getIntExtra("Index", 0)
        a?.forEach {
            uri = uri.plus(Uri.parse(it))
        }
        return myBinder
    }

    private val myBinder = LocalBinder()
    private lateinit var uri: Array<Uri>
    private var context: Context = applicationContext
    private var randomTrack = false
    private var repeatList = false
    private var currentTrackIndex: Int = 0
    private lateinit var mediaPlayer: MediaPlayer
    private var isEnd = false
    private var playing = false

    fun SetResource(uri: Array<Uri>) {
        this.uri = uri
    }

    fun SetContext(contex: Context) {
        this.context = context
    }

    fun SelectTrack(index: Int) {
        currentTrackIndex = index
    }

    fun NextTrack() {
        selectNextTrack(true)
        changeSong()
        mediaPlayer.start()
    }

    fun Run() {
        mediaPlayer = MediaPlayer.create(context, uri[currentTrackIndex])
        setMediaPlayerCompletionListener()
        mediaPlayer.start()

    }

    fun PreviousTrack() {
        selectNextTrack(false)
        changeSong()
        mediaPlayer.start()
    }

    fun Play() {
        if(isEnd) {
            mediaPlayer = MediaPlayer.create(context, uri[0])
            isEnd = false
        }
        mediaPlayer.start()
        playing = true
    }

    fun Pause() {
        playing = false
        mediaPlayer.pause()
    }

    fun RandomOnOff() {
        randomTrack = !randomTrack
    }

    fun RepeatOnOff() {
        repeatList = !repeatList
    }

    fun PlayPause() {
        if(!playing) {
            Pause()
        } else {
            Play()
        }
    }

    fun GetPosition(): Int {
//        return mediaPlayer.currentPosition
        return 1
    }

    fun GetDuration(): Int {
//        return mediaPlayer.duration
        return 10
    }

    fun GetTitile(): String {
        return "Title"
    }

    fun GetAuthor(): String {
//        return "Author"
        return (Math.random()*1000).toString()
    }

    fun SeekTo(msec: Int) {
        mediaPlayer.seekTo(msec)
    }

    fun IsPlaying() : Boolean {
        return playing
    }

    fun IsRandom(): Boolean {
        return randomTrack
    }

    fun IsRepeat(): Boolean {
        return repeatList
    }

    private fun selectNextTrack(next: Boolean) {
        if(randomTrack) {
            currentTrackIndex = (0 until uri.size-1).shuffled().first()
        } else {
            if(next) {
                currentTrackIndex++
                isFinish()
            } else {
                currentTrackIndex = maxOf(--currentTrackIndex, 0)
            }
        }
    }

    private fun isFinish() {
        if (currentTrackIndex == uri.size && repeatList) {
            currentTrackIndex = 0
        } else if(currentTrackIndex == uri.size) {
            isEnd = true
            playing = false
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun changeSong() {
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(context, uri[currentTrackIndex])
        setMediaPlayerCompletionListener()
    }

    private fun setMediaPlayerCompletionListener() {
        mediaPlayer.setOnCompletionListener {
            NextTrack()
        }
    }

    inner class LocalBinder: Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }
}