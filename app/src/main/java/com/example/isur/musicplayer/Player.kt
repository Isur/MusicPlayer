package com.example.isur.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_player.*

class Player : AppCompatActivity() {
    private lateinit var runnable: O
    var isBound = false
    private var musicPlayerService: MusicPlayerService? = null
    private var handler: Handler = Handler()
    private var randomOn = false
    private var repeatOn = false
    private var play = true
    private var songList: Array<String> = arrayOf()
    private var authorList: Array<String> = arrayOf()
    private var titlesList: Array<String> = arrayOf()
    private var position: Int = 0
    private var currPos: Int = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.LocalBinder
            musicPlayerService = binder.getService()
            isBound = true
            if (musicPlayerService?.isInit() == false) {
                musicPlayerService?.run()
            } else if (position != musicPlayerService?.getTrack()) {
                musicPlayerService?.selectTrack(position)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("test", "ON CREATE")
        setContentView(R.layout.activity_player)
        songList = intent.getStringArrayExtra("allSongsUri")
        authorList = intent.getStringArrayExtra("allAuthors")
        titlesList = intent.getStringArrayExtra("allTitles")
        position = intent.getIntExtra("position", 0)
        currPos = intent.getIntExtra("pos", 0)
        setupComponents()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putStringArray("songList", songList)
        outState?.putStringArray("authorList", authorList)
        outState?.putStringArray("titlesList", titlesList)
        outState?.putInt("pos", musicPlayerService?.getPosition() as Int)
        outState?.putInt("position", musicPlayerService?.getTrack() as Int)
        outState?.putBoolean("playing", musicPlayerService?.isPlaying() as Boolean)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        songList = savedInstanceState?.getStringArray("songList") as Array<String>
        authorList = savedInstanceState.getStringArray("authorList") as Array<String>
        titlesList = savedInstanceState.getStringArray("titlesList") as Array<String>
        position = savedInstanceState.getInt("position")
        currPos = savedInstanceState.getInt("pos")
        play = savedInstanceState.getBoolean("playing")
    }

    private fun binder() {
        val int = Intent(this, MusicPlayerService::class.java)
        int.putExtra("SongList", songList)
        int.putExtra("AuthorList", authorList)
        int.putExtra("TitlesList", titlesList)
        int.putExtra("Index", position)
        int.putExtra("pos", currPos)
        int.putExtra("playing", play)
        bindService(int, connection, Context.BIND_AUTO_CREATE)
        initializeView()
    }

    override fun onResume() {
        super.onResume()
        binder()
    }

    override fun onPause() {
        super.onPause()
        position = musicPlayerService?.getTrack() as Int
        currPos = musicPlayerService?.getPosition() as Int
        handler.removeCallbacks(runnable)
        isBound = false
        unbindService(connection)
    }

    private fun setupComponents() {
        PreviousTrackImageButton.setOnClickListener {
            musicPlayerService?.previousTrack()
        }

        NextTrackImageButton.setOnClickListener {
            musicPlayerService?.nextTrack()
        }

        PlayPauseImageButton.setOnClickListener {
            musicPlayerService?.playPause()

            if (!musicPlayerService!!.isPlaying()) {
                PlayPauseImageButton.setImageResource(R.drawable.ic_play)
            } else {
                PlayPauseImageButton.setImageResource(R.drawable.ic_pause)
            }
        }

        RandomTrackImageButton.setOnClickListener {
            musicPlayerService?.randomOnOff()
            randomOn = !randomOn
            if (musicPlayerService!!.isRandom()) {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_on)
            } else {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_off)
            }
        }

        RepeatListImageButton.setOnClickListener {
            musicPlayerService?.repeatOnOff()
            repeatOn = !repeatOn
            if (musicPlayerService!!.isRepeat()) {
                RepeatListImageButton.setImageResource(R.drawable.ic_repeat_on)
            } else {
                RepeatListImageButton.setImageResource(R.drawable.ic_repeat_off)
            }
        }

        DurationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    musicPlayerService?.seekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                musicPlayerService?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                musicPlayerService?.play()
            }
        })
    }

    private fun initializeView() {
        runnable = O()
        handler.postDelayed(runnable, 100)
        if (position != musicPlayerService?.getTrack()) {
            musicPlayerService?.selectTrack(position)
        }
    }

    inner class O : Runnable {
        var done = false
        override fun run() {
            if (isBound) {
                DurationSeekBar.max = musicPlayerService!!.getDuration()
                DurationSeekBar.progress = musicPlayerService!!.getPosition()
                DurationCurrentTextView.text = minSecTime(musicPlayerService?.getPosition())
                DurationEndTextView.text = minSecTime(musicPlayerService?.getDuration())
                AuthorNameTextView.text = musicPlayerService?.getAuthor()
                TitleNameTextView.text = musicPlayerService?.getTitle()
                if (!done) {
                    if (!musicPlayerService!!.isPlaying()) {
                        PlayPauseImageButton.setImageResource(R.drawable.ic_play)
                    } else {
                        PlayPauseImageButton.setImageResource(R.drawable.ic_pause)
                    }
                    if (musicPlayerService!!.isRepeat()) {
                        RepeatListImageButton.setImageResource(R.drawable.ic_repeat_on)
                    } else {
                        RepeatListImageButton.setImageResource(R.drawable.ic_repeat_off)
                    }
                    if (musicPlayerService!!.isRandom()) {
                        RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_on)
                    } else {
                        RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_off)
                    }
                    done = true
                }
                handler.postDelayed(this, 100)
            }
        }
    }

    private fun minSecTime(msec: Int?): String {
        if (msec == null) return minSecTime(0)
        val sec: Int = msec / 1000
        val min: Int = (sec / 60)
        val restMin: Int = sec % 60
        return if (restMin < 10) {
            "$min:0$restMin"
        } else {
            "$min:$restMin"
        }
    }
}
