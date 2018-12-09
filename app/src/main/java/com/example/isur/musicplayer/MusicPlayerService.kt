package com.example.isur.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayerService : Service() {

    private val myBinder = LocalBinder()
    private var uri: Array<Uri> = arrayOf()
    //private var context: Context = applicationContext
    private var randomTrack = false
    private var repeatList = false
    private var currentTrackIndex: Int = 0
    private var authors: Array<String> = arrayOf()
    private var titles: Array<String> = arrayOf()
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var isEnd = false
    private var currPos: Int = 0
    var playing = false
    var init = false



    override fun onBind(intent: Intent?): IBinder? {

        Log.i("test", "onBind")
        val a = intent?.getStringArrayExtra("SongList")
        val b = intent?.getStringArrayExtra("AuthorList")
        val c = intent?.getStringArrayExtra("TitlesList")
        currentTrackIndex = intent!!.getIntExtra("Index", 0)
        currPos = intent.getIntExtra("pos", 0)
        playing = intent.getBooleanExtra("playing", true)
        a?.forEach {
            uri = uri.plus(Uri.parse(it))
        }
        b?.forEach {
            authors = authors.plus(it)
        }
        c?.forEach {
            titles = titles.plus(it)
        }
        startService(intent)
        Log.i("test", "current song: $currentTrackIndex")
        return myBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.i("test", "REBIND?")
    }

    fun SetResource(uri: Array<Uri>) {
        this.uri = uri
    }

    fun SetContext(contex: Context) {
        //  this.context = context
    }

    fun SelectTrack(index: Int) {
        currentTrackIndex = index
        changeSong()
        if(playing) mediaPlayer.start()
    }

    fun NextTrack() {
        selectNextTrack(true)
        changeSong()
        if (playing) mediaPlayer.start()
    }

    fun Run() {
        init = true
        mediaPlayer = MediaPlayer.create(applicationContext, uri[currentTrackIndex])
        setMediaPlayerCompletionListener()
        if (playing) {
            mediaPlayer.start()
        }
        SeekTo(currPos)

    }

    fun PreviousTrack() {
        selectNextTrack(false)
        changeSong()
        if (playing) mediaPlayer.start()
    }

    fun Play() {
        if (isEnd) {
            mediaPlayer = MediaPlayer.create(applicationContext, uri[0])
            isEnd = false
        }
        mediaPlayer.start()
        playing = true
    }

    fun Stop() {
        playing = false
        mediaPlayer.stop()
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
        if (playing) {
            Pause()
        } else {
            Play()
        }
    }

    fun GetPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun GetDuration(): Int {
        return mediaPlayer.duration
    }

    fun GetTitile(): String {
        return authors[currentTrackIndex]
    }

    fun GetAuthor(): String {
        return titles[currentTrackIndex]
    }

    fun GetTrack(): Int {
        return currentTrackIndex
    }

    fun SeekTo(msec: Int) {
        mediaPlayer.seekTo(msec)
    }

    fun IsPlaying(): Boolean {
        return playing
    }

    fun IsRandom(): Boolean {
        return randomTrack
    }

    fun IsRepeat(): Boolean {
        return repeatList
    }

    override fun toString(): String {
        return "${titles[currentTrackIndex]} - ${mediaPlayer.duration} - ${mediaPlayer.currentPosition}"

    }

    private fun selectNextTrack(next: Boolean) {
        if (randomTrack) {
            currentTrackIndex = (0 until uri.size - 1).shuffled().first()
        } else {
            if (next) {
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
        } else if (currentTrackIndex == uri.size) {
            isEnd = true
            playing = false
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun changeSong() {
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(applicationContext, uri[currentTrackIndex])
        setMediaPlayerCompletionListener()
    }

    private fun setMediaPlayerCompletionListener() {
        mediaPlayer.setOnCompletionListener {
            NextTrack()
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }
}