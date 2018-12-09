package com.example.isur.musicplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.ListView
import com.mtechviral.mplaylib.MusicFinder

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= 23) {
            val perm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (perm != PackageManager.PERMISSION_GRANTED) {
                Log.i("myLog", "permission denied")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            } else {
                display()
            }
        } else {
            display()
        }
    }

    private fun display() {
        val musicFinder = MusicFinder(contentResolver)
        musicFinder.prepare()
        val songList = musicFinder.allSongs
        listView = this.findViewById(R.id.songList)
        val adapter = MusicAdapter(this, songList)
        listView.adapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            101 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    display()
                } else {

                }
                return
            }
            else -> {
            }
        }
    }
}
