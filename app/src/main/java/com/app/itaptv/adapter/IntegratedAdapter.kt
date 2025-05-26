package com.app.itaptv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.app.itaptv.R
import com.app.itaptv.databinding.VideofeedItemBinding
import com.app.itaptv.structure.DemoContent

class IntegratedAdapter(private val supportFragmentManager: FragmentManager,
                        private var home: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentFeed = ArrayList<DemoContent>()

        override fun onCreateViewHolder(parent: ViewGroup, layout: Int): RecyclerView.ViewHolder {
            /*return when(layout) {
                *//*R.layout.content_item -> {
                ContentItemViewHolder(ContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }*//*

            R.layout.videofeed_item -> {*/
            val binding = VideofeedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.parentLayout.id = View.generateViewId()

            val fragment = supportFragmentManager.findFragmentById(if (home) R.id.integrated_videofeed_home else R.id.integrated_videofeed_channels)
            if (fragment != null) {
                val ft = supportFragmentManager.beginTransaction()
                ft.remove(fragment)
                ft.commitNow()
            }
            val fragmentView = LayoutInflater.from(parent.context).inflate(if (home) R.layout.fragment_videofeed_item_home else R.layout.fragment_videofeed_item_channels,
                    binding.parentLayout, true)

            return VideofeedItemViewHolder(binding)

            /*}

            else -> {
                ContentItemViewHolder(ContentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }

        }*/
        }

        override fun getItemCount(): Int = contentFeed.size

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            when (p0) {
                /*is ContentItemViewHolder -> {
                    p0.binding.content = ((contentFeed[p1]) as Content).text
                    p0.binding.imageUrl = ((contentFeed[p1]) as Content).url
                }*/
                is VideofeedItemViewHolder -> {

                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return contentFeed[position].layout
        }

        //internal class ContentItemViewHolder(val binding: ContentItemBinding) : RecyclerView.ViewHolder(binding.root)
        internal class VideofeedItemViewHolder(binding: VideofeedItemBinding) : RecyclerView.ViewHolder(binding.parentLayout)
    }