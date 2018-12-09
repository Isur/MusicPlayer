package com.example.isur.musicplayer

import android.support.v7.app.AppCompatActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import com.example.isur.musicplayer.R.id.DurationCurrentTextView
import kotlinx.android.synthetic.main.activity_player.*
import android.app.ActivityManager


class Player : AppCompatActivity() {

    private var musicPlayerService: MusicPlayerService? = null
    var isBound = false

    private lateinit var runnable: O
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
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as MusicPlayerService.LocalBinder
            musicPlayerService = binder.getService()
            isBound = true
            if (musicPlayerService?.init == false) {
                musicPlayerService?.Run()
            } else if(position != musicPlayerService?.GetTrack()) {
                musicPlayerService?.SelectTrack(position)
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
        outState?.putInt("pos", musicPlayerService?.GetPosition() as Int)
        outState?.putInt("position", musicPlayerService?.GetTrack() as Int)
        outState?.putBoolean("playing", musicPlayerService?.playing as Boolean)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("test", "ON RESTORE")
        songList = savedInstanceState?.getStringArray("songList") as Array<String>
        authorList = savedInstanceState.getStringArray("authorList") as Array<String>
        titlesList = savedInstanceState.getStringArray("titlesList") as Array<String>
        position = savedInstanceState.getInt("position")
        currPos = savedInstanceState.getInt("pos")
        play = savedInstanceState.getBoolean("playing")

    }

    fun binder() {
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
        position = musicPlayerService?.GetTrack() as Int
        currPos = musicPlayerService?.GetPosition() as Int
        handler.removeCallbacks(runnable)
        isBound = false
        unbindService(connection)
    }

    private fun setupComponents() {
        PreviousTrackImageButton.setOnClickListener {
            musicPlayerService?.PreviousTrack()
        }

        NextTrackImageButton.setOnClickListener {
            musicPlayerService?.NextTrack()
        }

        PlayPauseImageButton.setOnClickListener {
            musicPlayerService?.PlayPause()

            if (!musicPlayerService!!.playing) {
                PlayPauseImageButton.setImageResource(R.drawable.ic_play)
            } else {
                PlayPauseImageButton.setImageResource(R.drawable.ic_pause)
            }
        }

        RandomTrackImageButton.setOnClickListener {
            musicPlayerService?.RandomOnOff()
            randomOn = !randomOn
            if (randomOn) {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_on)
            } else {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_off)
            }
        }

        RepeatListImageButton.setOnClickListener {
            musicPlayerService?.RepeatOnOff()
            repeatOn = !repeatOn
            if (repeatOn) {
                RepeatListImageButton.setImageResource(R.drawable.ic_repeat_on)
            } else {
                RepeatListImageButton.setImageResource(R.drawable.ic_repeat_off)
            }
        }

        DurationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    musicPlayerService?.SeekTo(i)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                musicPlayerService?.Pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                musicPlayerService?.Play()
            }
        })
    }

    private fun initializeView() {
        runnable = O()
        handler.postDelayed(runnable, 100)
        if(position != musicPlayerService?.GetTrack()){
            musicPlayerService?.SelectTrack(position)
        }
    }

    inner class O : Runnable {
        override fun run() {

            if (isBound) {
                DurationSeekBar.max = musicPlayerService!!.GetDuration()
                DurationSeekBar.progress = musicPlayerService!!.GetPosition()
                DurationCurrentTextView.text = minSecTime(musicPlayerService?.GetPosition())
                DurationEndTextView.text = minSecTime(musicPlayerService?.GetDuration())
                AuthorNameTextView.text = musicPlayerService?.GetAuthor()
                TitleNameTextView.text = musicPlayerService?.GetTitile()
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
