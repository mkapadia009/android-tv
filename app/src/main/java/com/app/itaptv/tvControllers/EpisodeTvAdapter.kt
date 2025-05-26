package com.app.itaptv.tvControllers

import android.content.Context
import android.os.SystemClock
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.itaptv.R
import com.app.itaptv.fragment.EpisodePlaylistTabFragment
import com.app.itaptv.interfaces.KeyEventListener
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class EpisodeTvAdapter(
    context1: Context,
    feedContentData: ArrayList<FeedContentData>,
    keyEventListener: KeyEventListener
) : RecyclerView.Adapter<EpisodeTvAdapter.EpisodeTvHolder>() {

    var context = context1
    var feedContentDataList: ArrayList<FeedContentData> = feedContentData
    var mLastClickTime: Long = 0
    var lastSelectedIndex: Int = 0
    var keyListener: KeyEventListener = keyEventListener
    var view: View? = null
    var canAccess = true

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): EpisodeTvAdapter.EpisodeTvHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view: View = inflater.inflate(R.layout.row_episode, viewGroup, false)
        return EpisodeTvAdapter.EpisodeTvHolder(view)
    }

    override fun getItemCount(): Int {
        return feedContentDataList.size
    }

    override fun onBindViewHolder(holder: EpisodeTvAdapter.EpisodeTvHolder, i: Int) {
        var feedContentData: FeedContentData = feedContentDataList[i]
        var count: Int = feedContentDataList.size
        holder.itemView.isFocusable = true
        view = holder.itemView

        holder.txtEpisodeTitle.text = feedContentData.postTitle
        val sd = LocalStorage.getUserSubscriptionDetails()
        if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equals(Constant.YES, ignoreCase = true)) {
                holder.ivPlay.visibility = View.VISIBLE
            } else {
                if (feedContentData.can_i_use) {
                    holder.ivPlay.visibility = View.VISIBLE
                } else {
                    holder.ivPlay.visibility = View.INVISIBLE
                }
            }
        }
        Glide.with(context)
            .load(feedContentData.imgUrl)
            .thumbnail(0.1f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(200, 200)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.ivImage)

        if (feedContentData.isSelected) {
            holder.txtEpisodeTitle.setTextColor(context.resources.getColor(R.color.colorAccent))
        }

        holder.itemView.setPadding(10, 5, 10, 5)
        holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (view.hasFocus()) {
                feedContentData.isFocused = true
                view.background =
                    view.context.getDrawable(R.drawable.highlight_text_field)
            } else {
                feedContentData.isFocused = false
                view.background = null
            }
        }

        if (feedContentData.isFocused) {
            holder.itemView.requestFocus()
        }

        holder.itemView.setOnKeyListener(View.OnKeyListener { v, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (canAccess) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 200) {
                        return@OnKeyListener true
                    } else {
                        mLastClickTime = SystemClock.elapsedRealtime()
                        var selectedPosition = 0
                        when (i) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                selectedPosition = lastSelectedIndex + 1
                                Log.i("selected", "" + selectedPosition)
                                if (selectedPosition < count) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    return@OnKeyListener true
                                }
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {
                                selectedPosition = lastSelectedIndex - 1
                                Log.i("selected", "selectedPosition " + selectedPosition)
                                if (selectedPosition >= 0) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    if (EpisodePlaylistTabFragment.buyNow != null && EpisodePlaylistTabFragment.buyNow.visibility == View.VISIBLE) {
                                        EpisodePlaylistTabFragment.restoreFocus()
                                    } else {
                                        return@OnKeyListener true
                                    }
                                }
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                keyListener.onKeyEvent(lastSelectedIndex, null, view, true)
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                return@OnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                keyListener.onKeyEvent(
                                    lastSelectedIndex,
                                    feedContentData,
                                    view,
                                    false
                                )
                            }
                        }
                    }
                }
            }
            false
        })
    }


    class EpisodeTvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage = itemView.findViewById<ImageView?>(R.id.ivImage)
        val ivEpisodeDownload =
            itemView.findViewById<ImageView?>(R.id.ivEpisodeDownload)
        val txtEpisodeTitle = itemView.findViewById<TextView?>(R.id.txtEpisodeTitle)
        val txtEpisodeSubtitle = itemView.findViewById<TextView?>(R.id.txtEpisodeSubtitle)
        val ivPlay = itemView.findViewById<ImageView?>(R.id.ivPlay)
    }
}