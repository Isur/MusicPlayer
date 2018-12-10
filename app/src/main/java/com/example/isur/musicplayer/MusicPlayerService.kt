package com.example.isur.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayerService : Service() {

    private val myBinder = LocalBinder()
    private var uri: Array<Uri> = arrayOf()
    private var randomTrack = false
    private var repeatList = false
    private var currentTrackIndex: Int = 0
    private var authors: Array<String> = arrayOf()
    private var titles: Array<String> = arrayOf()
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var isEnd = false
    private var currPos: Int = 0
    private var playing = false
    private var init = false


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

//     fun SetResource(uri: Array<Uri>) {
//        this.uri = uri
//    }

    fun selectTrack(index: Int) {
        currentTrackIndex = index
        changeSong()
        if (playing) mediaPlayer.start()
    }

    fun nextTrack() {
        selectNextTrack(true)
        changeSong()
        if (playing) mediaPlayer.start()
    }

    fun run() {
        init = true
        mediaPlayer = MediaPlayer.create(applicationContext, uri[currentTrackIndex])
        setMediaPlayerCompletionListener()
        if (playing) {
            mediaPlayer.start()
        }
        seekTo(currPos)

    }

    fun previousTrack() {
        selectNextTrack(false)
        changeSong()
        if (playing) mediaPlayer.start()
    }

    fun play() {
        if (isEnd) {
            mediaPlayer = MediaPlayer.create(applicationContext, uri[0])
            isEnd = false
        }
        mediaPlayer.start()
        playing = true
    }

    fun pause() {
        playing = false
        mediaPlayer.pause()
    }

    fun randomOnOff() {
        randomTrack = !randomTrack
    }

    fun repeatOnOff() {
        repeatList = !repeatList
    }

    fun playPause() {
        if (playing) {
            pause()
        } else {
            play()
        }
    }

    fun getPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun getTitle(): String {
        return authors[currentTrackIndex]
    }

    fun getAuthor(): String {
        return titles[currentTrackIndex]
    }

    fun getTrack(): Int {
        return currentTrackIndex
    }

    fun seekTo(msec: Int) {
        if(!playing){
            changeSong()
        }
        mediaPlayer.seekTo(msec)
    }

    fun isPlaying(): Boolean {
        return playing
    }

    fun isInit(): Boolean = init

    fun isRandom(): Boolean {
        return randomTrack
    }

    fun isRepeat(): Boolean {
        return repeatList
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
            nextTrack()
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }
}