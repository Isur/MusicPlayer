package com.example.isur.musicplayer

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mtechviral.mplaylib.MusicFinder
import kotlinx.android.synthetic.main.list_item_song.view.*

class MusicAdapter(private val context: Context, private val dataSource: List<MusicFinder.Song>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_song, parent, false)
            holder = ViewHolder()
            holder.titleTextView = view.Title
            holder.artistTextView = view.Author
            holder.durationTextView = view.Duration
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }
        val titleTextView = holder.titleTextView
        val artistTextView = holder.artistTextView
        val durationTextView = holder.durationTextView

        val song = getItem(position) as MusicFinder.Song
        titleTextView.text = song.title
        artistTextView.text = song.artist
        val duration = song.duration

        var songDuration: String
        if (duration > 60000) {
            songDuration = (duration / 60000).toInt().toString() + ":"
            val sec = ((duration % 60000) / 1000).toInt()
            songDuration += if (sec < 10) {
                "0$sec"
            } else {
                sec
            }
        } else {
            val sec = (duration / 1000).toInt()
            songDuration = if (sec < 10) {
                "0$sec"
            } else {
                sec.toString()
            }
        }

        durationTextView.text = songDuration

        view.setOnClickListener {
            goToPlayer(song, position)
        }

        return view
    }

    private fun goToPlayer(song: MusicFinder.Song, position: Int) {
        val intent = Intent(context, Player::class.java)
        val allSongsUri = mutableListOf<String>()
        val allTitles = mutableListOf<String>()
        val allAuthors = mutableListOf<String>()
        dataSource.forEach {
            allSongsUri.add(it.uri.toString())
            allTitles.add(it.title.toString())
            allAuthors.add(it.artist.toString())
        }

        intent.putExtra("source", song.uri.toString())
        intent.putExtra("title", song.title.toString())
        intent.putExtra("duration", song.duration.toString())
        intent.putExtra("album", song.album.toString())
        intent.putExtra("artist", song.artist.toString())
        intent.putExtra("albumArt", song.albumArt.toString())
        intent.putExtra("position", position)
        intent.putExtra("allSongsUri", allSongsUri.toTypedArray())
        intent.putExtra("allAuthors", allAuthors.toTypedArray())
        intent.putExtra("allTitles", allTitles.toTypedArray())
        startActivity(context, intent, null)
    }

    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var artistTextView: TextView
        lateinit var durationTextView: TextView
    }

}