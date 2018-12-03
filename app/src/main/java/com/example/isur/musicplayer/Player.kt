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

class Player : AppCompatActivity() {

    private var musicPlayerService: MusicPlayerService? = null
    var isBound = false

    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var randomOn = false
    private var repeatOn = false
    private var play = true

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            Log.i("test", "connection")
            val binder = service as MusicPlayerService.LocalBinder
            musicPlayerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        setupComponents()
    }

    override fun onResume() {
        super.onResume()
        val int = Intent(this, MusicPlayerService::class.java)
        int.putExtra("SongList", intent.getStringArrayExtra("allSongsUri"))
        int.putExtra("Index", intent.getIntExtra("position", 0))
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        musicPlayerService?.Run()
        initializeView()
    }


    override fun onPause() {
        super.onPause()
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
            play = !play

            if(play) {
                PlayPauseImageButton.setImageResource(R.drawable.ic_play)
            } else {
                PlayPauseImageButton.setImageResource(R.drawable.ic_pause)
            }
        }

        RandomTrackImageButton.setOnClickListener {
            musicPlayerService?.RandomOnOff()
            randomOn = !randomOn
            if(randomOn) {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_on)
            } else {
                RandomTrackImageButton.setImageResource(R.drawable.ic_shuffle_off)
            }
        }

        RepeatListImageButton.setOnClickListener {
            musicPlayerService?.RepeatOnOff()
            repeatOn = !repeatOn
            if(repeatOn) {
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
        runnable = Runnable {
            Log.i("test", isBound.toString())
            if(isBound) {

//                DurationSeekBar.max = musicPlayerService!!.GetDuration()
//                DurationSeekBar.progress = musicPlayerService!!.GetPosition()
//                DurationCurrentTextView.text = minSecTime(musicPlayerService?.GetPosition())
//                DurationEndTextView.text = minSecTime(musicPlayerService?.GetDuration())
//                AuthorNameTextView.text = musicPlayerService?.GetAuthor()
//                TitleNameTextView.text = musicPlayerService?.GetTitile()
                DurationCurrentTextView.text = musicPlayerService?.GetAuthor()
            }
            handler.postDelayed(runnable, 2000)
        }
        handler.postDelayed(runnable, 2000)
    }

    private fun minSecTime(msec: Int?): String {
        if(msec == null) return minSecTime(0)
        val sec: Int = msec / 1000
        val min: Int = (sec / 60)
        val restMin: Int = sec % 60
        return if(restMin < 10) {
            "$min:0$restMin"
        } else {
            "$min:$restMin"
        }
    }

}
