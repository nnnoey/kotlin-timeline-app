package com.example.noey.twitterapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listTweets = ArrayList<Ticket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //dummy data
        listTweets.add(Ticket("0", "him","url", "add"))
        listTweets.add(Ticket("0", "him","url", "uid"))
        listTweets.add(Ticket("1", "her","url", "uid"))

        var adapter = MyTweetAdpater(this, listTweets)
        listviewTweets.adapter = adapter
    }

    inner class  MyTweetAdpater: BaseAdapter {
        var listNotesAdpater=ArrayList<Ticket>()
        var context: Context?=null
        constructor(context:Context, listNotesAdpater:ArrayList<Ticket>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var mytweet = listNotesAdpater[p0]

            if(mytweet.tweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)
                return myView

            } else if(mytweet.tweetPersonUID.equals("loading")){
                var myView=layoutInflater.inflate(R.layout.loading_ticket,null)
                return myView

            } else{
                var myView=layoutInflater.inflate(R.layout.tweets_ticket,null)
                return myView
            }
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listNotesAdpater.size

        }
    }
}



