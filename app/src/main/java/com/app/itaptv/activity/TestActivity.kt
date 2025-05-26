package com.app.itaptv.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
import com.app.itaptv.R
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setInitialState()
        Handler().postDelayed({
            revealPlayer()
        }, 500)
    }

    /**
     * Set the initial state if the UI where all the components are set invisible.
     */
    private fun setInitialState() {
        playerProgressBar.progress = 0

        val visibility = View.INVISIBLE
        val alpha = 0f

        playerLayout.visibility = visibility
        playerLayout.alpha = alpha

        questionLbl.visibility = visibility
        questionLbl.alpha = alpha

        opt1Btn.visibility = visibility
        opt1Btn.alpha = alpha

        opt2Btn.visibility = visibility
        opt2Btn.alpha = alpha

        opt3Btn.visibility = visibility
        opt3Btn.alpha = alpha

        opt4Btn.visibility = visibility
        opt4Btn.alpha = alpha
    }

    /**
     * Reveals player view with animation.
     */
    private fun revealPlayer() {
        val progressAnimator = ObjectAnimator.ofInt(playerProgressBar, "progress", 100)
        progressAnimator.duration = 15000
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()
        revealView(playerLayout)
        revealQuestion()

        executeAfter(15000) { revealOptions() }
    }

    /**
     * Reveals the question with animation.
     */
    private fun revealQuestion() {
        revealView(questionLbl)
    }

    /**
     * Reveals options with animation.
     */
    private fun revealOptions() {
        executeAfter(0) { revealView(opt1Btn) }
        executeAfter(300) { revealView(opt2Btn) }
        executeAfter(600) { revealView(opt3Btn) }
        executeAfter(900) { revealView(opt4Btn) }
    }

    fun optionClicked(view: View) {
        setInitialState()
        executeAfter(200) { revealPlayer() }
    }

    private fun pseudoCode() {
        // image > 1 sec > question > 3 sec options
        // audio > 0 sec > question > 3 sec options
        // question > 3 sec > options

        // all alpha = 0f
        // has attachment
        //     picture
        //         audio gone
        //         picture visible
        //     audio
        //         audio visible
        //         picture gone
        // picture gone
        // audio gone
        // reveal question
    }

    private fun reset() {
        setInitialState()
    }

}
