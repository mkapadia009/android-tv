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
import com.app.itaptv.interfaces.KeyEventListener
import com.app.itaptv.structure.MoreMenuData
import com.app.itaptv.utils.Log

class MoreTvAdapter(
    context1: Context,
    moreMenuData: ArrayList<MoreMenuData>,
    keyEventListener: KeyEventListener
) : RecyclerView.Adapter<MoreTvAdapter.MoreTvHolder>() {

    var context = context1
    var moreMenuDataList: ArrayList<MoreMenuData> = moreMenuData
    var mLastClickTime: Long = 0
    var lastSelectedIndex: Int = 0
    var keyListener: KeyEventListener = keyEventListener
    var view: View? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MoreTvHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view: View = inflater.inflate(R.layout.row_more_menu, viewGroup, false)
        return MoreTvHolder(view)
    }

    override fun getItemCount(): Int {
        return moreMenuDataList.size
    }

    override fun onBindViewHolder(holder: MoreTvHolder, i: Int) {
        var moreMenuData: MoreMenuData = moreMenuDataList[i]
        var count: Int = moreMenuDataList.size
        holder.itemView.isFocusable = true
        view = holder.itemView

        holder.tvMoreMenuName.text = moreMenuData.MenuName
        holder.ivMoreMenu.setImageResource(moreMenuData.img)
        holder.itemView.setPadding(10, 15, 10, 15)
        holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (view.hasFocus()) {
                moreMenuData.isFocused = true
                view.background =
                    view.context.getDrawable(R.drawable.highlight_white_border)
            } else {
                moreMenuData.isFocused = false
               view.background = null
            }
        }

        if (moreMenuData.isFocused) {
            holder.itemView.requestFocus()
        }

        holder.itemView.setOnKeyListener(View.OnKeyListener { v, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
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
                                return@OnKeyListener true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            keyListener.onKeyEvent(lastSelectedIndex, null, view, true)
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            return@OnKeyListener true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            keyListener.onKeyEvent(lastSelectedIndex, moreMenuData, view, false)
                        }
                    }
                }
            }
            false
        })
    }

    class MoreTvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMoreMenuName: TextView = itemView.findViewById(R.id.tvMoreMenu)
        val ivMoreMenu: ImageView = itemView.findViewById(R.id.ivMoreMenu)
    }
}