package com.app.itaptv.tvControllers

import android.content.Context
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.app.itaptv.structure.FeedCombinedData

class CardPresenterSelector(private val mContext: Context,private val feedCombinedData: FeedCombinedData) : PresenterSelector() {

    override fun getPresenter(item: Any?): Presenter? {
        return CardPresenter(mContext,feedCombinedData)
    }

    override fun getPresenters(): Array<Presenter> {
        return super.getPresenters()
    }

}







