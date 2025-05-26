package com.app.itaptv.tvControllers


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.leanback.widget.BaseCardView
import com.app.itaptv.R
import com.app.itaptv.structure.FeedCombinedData
import com.app.itaptv.structure.FeedContentData
import com.app.itaptv.utils.Constant
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * This Presenter will display a card consisting of an image on the left side of the card followed
 * by text on the right side. The image and text have equal width. The text will work like a info
 * box, thus it will be hidden if the parent row is inactive. This behavior is unique to this card
 * and requires a special focus handler.
 */
class CardPresenter(context: Context, feedCombinedData: FeedCombinedData) :
    AbstractCardPresenter<BaseCardView>(context) {

    var view: View? = null
    var ivFeedImage: ImageView? = null
    var cvFeedImage: CardView? = null
    var tvViewAll: TextView? = null
    var ivPremiumLogo: ImageView? = null

    //LinearLayout llRewards;
    var title = ""
    var fData: FeedCombinedData? = feedCombinedData

    override fun onCreateView(): BaseCardView {
        val cardView = BaseCardView(context, null, R.style.Widget_Leanback_BaseCardViewStyle)
        cardView.isFocusable = true
        cardView.addView(LayoutInflater.from(context).inflate(R.layout.row_feed_tv, null, false))
        return cardView
    }

    override fun onBindViewHolder(card: Any, cardView: BaseCardView) {
        view = cardView
        ivFeedImage = view!!.findViewById(R.id.ivFeedImage)
        cvFeedImage = view!!.findViewById(R.id.cvFeedImage)
        tvViewAll = view!!.findViewById(R.id.tvViewAll)
        ivPremiumLogo = view!!.findViewById(R.id.iv_premium_logo)
        ivPremiumLogo!!.visibility = View.GONE
        tvViewAll!!.visibility = View.GONE

        view!!.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (view.hasFocus()) {
                view.foreground = view.context.getDrawable(R.drawable.highlight_white_border)
                /* if (fData!!.tileShape == "lv_rectangle") {
                     setIvLayoutParams(context, 540, 400)
                 }*/
            } else {
                view.foreground = null
                /* if (fData!!.tileShape == "lv_rectangle") {
                     setIvLayoutParams(context, 180, 400)
                 }*/
            }
        }

        tvViewAll!!.setOnFocusChangeListener { view, b ->
            if (view.hasFocus()) {
                tvViewAll!!.setTextColor(context.resources.getColor(R.color.colorAccent))
            } else {
                tvViewAll!!.setTextColor(context.resources.getColor(R.color.white))
            }
        }

        setData(cardView.context, card)
    }

    fun setData(context: Context, itemObject: Any?) {
        if (itemObject is FeedContentData) {
            val feedContentData = itemObject
            if (feedContentData.feedContentType.equals("viewall")) {
                tvViewAll!!.visibility = View.VISIBLE
            }
            when (fData!!.tileShape) {
                "v_rectangle" -> {
                    Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivFeedImage!!)
                    setIvLayoutParams(context, 150, 200)
                    feedContentData.tileShape = "v_rectangle"
                }
                "h_rectangle" -> {
                    Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivFeedImage!!)
                    setIvLayoutParams(context, 235, 170)
                    feedContentData.tileShape = "h_rectangle"
                }
                "lv_rectangle" -> {
                    Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivFeedImage!!)
                    setIvLayoutParams(context, 150, 200)
                    feedContentData.tileShape = "lv_rectangle"

                }
                else -> {
                    Glide.with(context)
                        .load(feedContentData.imgUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivFeedImage!!)
                    setIvLayoutParams(context, 150, 200)
                    feedContentData.tileShape = "v_rectangle"
                }
            }

            if (feedContentData.postExcerpt.equals("paid", ignoreCase = true)) {
                ivPremiumLogo!!.visibility = View.VISIBLE
            } else {
                ivPremiumLogo!!.visibility = View.GONE
            }
        }
    }

    private fun setIvLayoutParams(context: Context, dpWidth: Int, dpHeight: Int) {
        val scale = context.resources.displayMetrics.density
        val dpWidthInPx = (dpWidth * scale).toInt()
        val dpHeightInPx = (dpHeight * scale).toInt()
        if (fData!!.feedType.equals(Constant.TAB_BUY, ignoreCase = true)) {
            val layoutParams = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
            ivFeedImage!!.layoutParams = layoutParams
        } else {
            val layoutParams1 = RelativeLayout.LayoutParams(dpWidthInPx, dpHeightInPx)
            ivFeedImage!!.layoutParams = layoutParams1
        }
    }
}
