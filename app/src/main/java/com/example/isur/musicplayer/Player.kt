package com.example.isur.musicplayer

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_player.*

class Player : AppCompatActivity() {

    //Buttons
    private var mPreviousTrackImageView: ImageView? = null
    private var mNextTrackImageView: ImageView? = null
    private var mRandomTrackImageView: ImageView? = null
    private var mRepeatListImageView: ImageView? = null
    private var mPlayPauseImageView: ImageView? = null
    //Track Image
    private var mImageTrackImageView: ImageView? = null
    //SeekBar
    private var mDurationSeekBar: SeekBar? = null
    //TextView
    private var mCurrentDurationTextView: TextView? = null
    private var mEndDurationTextView: TextView? = null

    private var mMediaPlayer: MediaPlayer? = null

    private var mRandomTrack: Boolean = false
    private var mRepeatList: Boolean = false
    private var mPlay: Boolean = true







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        loadComponents()
        setupComponents()


    }

    private fun loadComponents() {
        mPreviousTrackImageView = findViewById<ImageView>(R.id.PreviousTrackImageButton)
        mNextTrackImageView = findViewById<ImageView>(R.id.NextTrackImageButton)
        mRandomTrackImageView = findViewById<ImageView>(R.id.RandomTrackImageButton)
        mRepeatListImageView = findViewById<ImageView>(R.id.RepeatListImageButton)
        mPlayPauseImageView = findViewById<ImageView>(R.id.PlayPauseImageButton)

        mImageTrackImageView = findViewById<ImageView>(R.id.TrackImage)

        mDurationSeekBar = findViewById<SeekBar>(R.id.DurationSeekBar)

        mCurrentDurationTextView = findViewById<TextView>(R.id.DurationCurrentTextView)
        mEndDurationTextView = findViewById<TextView>(R.id.DurationEndTextView)


        mMediaPlayer = MediaPlayer.create(applicationContext, 1)
    }


    private fun setupComponents() {
        mPreviousTrackImageView!!.setOnClickListener {
            //TODO add PreviousTrackImageButton onClick
        }
        mNextTrackImageView!!.setOnClickListener {
            //TODO add NextTrackImageButton onClick
        }
        mPlayPauseImageView!!.setOnClickListener {
            mPlay = !mPlay
            if(mPlay)
                it.setBackgroundResource(R.drawable.ic_play)
            else
                it.setBackgroundResource(R.drawable.ic_pause)
        }
        mRandomTrackImageView!!.setOnClickListener {
            mRandomTrack = !mRandomTrack
            if(mRandomTrack)
                it.setBackgroundResource(R.drawable.ic_shuffle_on)
            else
                it.setBackgroundResource(R.drawable.ic_shuffle_off)
        }
        mRepeatListImageView!!.setOnClickListener {
            mRepeatList = !mRepeatList
            if(mRepeatList)
                it.setBackgroundResource(R.drawable.ic_repeat_on)
            else
                it.setBackgroundResource(R.drawable.ic_repeat_off)
        }
    }
}
