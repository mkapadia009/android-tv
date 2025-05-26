package com.app.itaptv.tvControllers

import android.content.Context
import android.os.SystemClock
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.itaptv.R
import com.app.itaptv.interfaces.KeyEventListener
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.Constant
import com.app.itaptv.utils.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ViewAllFeedTvAdapter(
    context1: Context,
    feedContentDataList: ArrayList<FeedContentData>,
    receivedTileShape: String,
    receivedFeedType: String,
    spancount: Int,
    keyEventListener: KeyEventListener
) : RecyclerView.Adapter<ViewAllFeedTvAdapter.ViewAllFeedHolder>() {

    var context = context1
    var arrayListFeedContent: ArrayList<FeedContentData> = feedContentDataList
    var tileShape: String? = receivedTileShape
    var feedType: String? = receivedFeedType
    var mLastClickTime: Long = 0
    var lastSelectedIndex: Int = 0
    var spanCount: Int = spancount
    var keyListener: KeyEventListener = keyEventListener
    var view: View? = null
    var firstView: View? = null
    var canAccess = true

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewAllFeedHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view: View = inflater.inflate(R.layout.row_all_feed_tv, viewGroup, false)
        return ViewAllFeedHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayListFeedContent.size
    }

    fun getFocusedView(): View? {
        return firstView;
    }

    fun getFocusedIndex(): Int? {
        return lastSelectedIndex;
    }

    override fun onBindViewHolder(holder: ViewAllFeedHolder, i: Int) {
        var feedContentData: FeedContentData = arrayListFeedContent[i]
        var count: Int = arrayListFeedContent.size
        holder.itemView.isFocusable = true
        feedContentData.view = holder.itemView

        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                view = v
                feedContentData.isFocused = true
            } else {
                feedContentData.isFocused = false
            }
        }

        if (feedContentData.isFocused) {
            holder.itemView.requestFocus()
            firstView = holder.itemView
        }

        if (tileShape != null) {
            when (tileShape) {
                "default", "v_rectangle" -> {
                    Glide.with(context)
                        .load(feedContentData!!.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.ivFeedImage)

                    val scale = context.resources.displayMetrics.density
                    val dpWidthInPx = (150 * scale).toInt()
                    val dpHeightInPx = (200 * scale).toInt()
                    val layoutParams = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
                    layoutParams.setMargins(5, 5, 5, 5)
                    holder.ivFeedImage.layoutParams = layoutParams
                }

                "h_rectangle" -> {
                    Glide.with(context)
                        .load(feedContentData!!.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.ivFeedImage)

                    val scale = context.resources.displayMetrics.density
                    val dpWidthInPx = (235 * scale).toInt()
                    val dpHeightInPx = (170 * scale).toInt()
                    val layoutParams = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
                    layoutParams.setMargins(5, 5, 5, 5)
                    holder.ivFeedImage.layoutParams = layoutParams
                }

                else -> {
                    Glide.with(context)
                        .load(feedContentData!!.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.ivFeedImage)

                    val scale = context.resources.displayMetrics.density
                    val dpWidthInPx = (150 * scale).toInt()
                    val dpHeightInPx = (200 * scale).toInt()
                    val layoutParams = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
                    layoutParams.setMargins(5, 5, 5, 5)
                    holder.ivFeedImage.layoutParams = layoutParams
                }
            }
        } else {
            Glide.with(context)
                .load(feedContentData!!.imgUrl)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.ivFeedImage)

            val scale = context.resources.displayMetrics.density
            val dpWidthInPx = (150 * scale).toInt()
            val dpHeightInPx = (200 * scale).toInt()
            val layoutParams = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
            layoutParams.setMargins(5, 5, 5, 5)
            holder.ivFeedImage.layoutParams = layoutParams
        }

        holder.iv_premium_logo.visibility =
            if (feedContentData.postExcerpt == Constant.CONTENT_PAID) View.VISIBLE else View.GONE

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
                                selectedPosition = lastSelectedIndex + spanCount
                                Log.i("selected", "" + selectedPosition)
                                if (selectedPosition < count) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    return@OnKeyListener true
                                }
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {
                                selectedPosition = lastSelectedIndex - spanCount
                                Log.i("selected", "selectedPosition " + selectedPosition)
                                if (selectedPosition >= 0) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    return@OnKeyListener true
                                }
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                selectedPosition = lastSelectedIndex - 1
                                val itemPosition = lastSelectedIndex % spanCount
                                Log.i("selected", "selectedPosition " + selectedPosition)
                                if (selectedPosition >= 0 && itemPosition != 0) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, true)
                                }
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                selectedPosition = lastSelectedIndex + 1
                                val itemPosition = selectedPosition % spanCount
                                Log.i("selected", "selectedPosition " + itemPosition)
                                if (selectedPosition < count && itemPosition != 0) {
                                    lastSelectedIndex = selectedPosition
                                    keyListener.onKeyEvent(lastSelectedIndex, null, view, false)
                                } else {
                                    return@OnKeyListener true
                                }
                            }
                            /* KeyEvent.KEYCODE_DPAD_CENTER -> {
                            keyListener.onKeyEvent(lastSelectedIndex, feedContentData, view,false)
                        }*/
                        }
                    }
                }
            }
            false
        })

        holder.itemView.setOnClickListener {
            keyListener.onKeyEvent(lastSelectedIndex, feedContentData, feedContentData.view, false)
        }
    }

    class ViewAllFeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFeedImage: ImageView = itemView.findViewById(R.id.ivFeedImage)
        val iv_premium_logo: ImageView = itemView.findViewById(R.id.iv_premium_logo)

    }

}