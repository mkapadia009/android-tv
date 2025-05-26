package com.app.itaptv.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.app.itaptv.R
import com.app.itaptv.utils.LocalStorage
import com.app.itaptv.utils.Utility
import com.app.itaptv.utils.Wallet
import kotlinx.android.synthetic.main.activity_coins.coins_progressbar
import kotlinx.android.synthetic.main.activity_coins.ivCoinWins
import kotlinx.android.synthetic.main.activity_coins.konfettiView
import kotlinx.android.synthetic.main.activity_coins.llCoinActivity
import kotlinx.android.synthetic.main.activity_coins.pbCoinActivity
import kotlinx.android.synthetic.main.activity_coins.tvCoinBalance
import kotlinx.android.synthetic.main.activity_coins.tvCoinWins
import kotlinx.android.synthetic.main.activity_coins.tvReferral
import kotlinx.android.synthetic.main.activity_referral_coins.*
import nl.dionsegijn.konfetti.KonfettiView

internal class ReferralCoinsActivity : BaseActivity() {

    var walletBalance = 0L
    var successVal = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral_coins)
        earnCoin()
        coins_progressbar.visibility = View.VISIBLE
        ivCoinWins.visibility = View.GONE
        tvCoinWins.visibility = View.GONE
        tvWalletBalance.visibility = View.GONE
        setTheme(R.style.Theme_Leanback)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun earnCoin() {
        var flag = Wallet.FLAG_REFER
        var coins = 50

        Wallet.earnCoins(
            this,
            Integer.parseInt(LocalStorage.getUserId()),
            "iTap",
            Wallet.FLAG_REFER,
            LocalStorage.getReferralPoints().toLong()
        ) { success, error, coins, diamonds, creditedCoins, historyData, historyCount ->
            handleWinCoins(success, coins)
            false
        }
    }

    fun handleWinCoins(success: Boolean, coins: Long) {
        if (success) {
            successVal = success
            coins_progressbar.visibility = View.GONE
            llCoinActivity.visibility = View.VISIBLE
            pbCoinActivity.visibility = View.GONE
            if (LocalStorage.isWinner()) {
                ivCoinWins.visibility = View.GONE
                tvCoinWins.visibility = View.GONE
                tvHeader.text = getString(R.string.lucky_draw_entered)
                tvReferral.text =
                    getString(R.string.best_of_luck_message)
            } else {
                ivCoinWins.visibility = View.VISIBLE
                tvCoinWins.visibility = View.VISIBLE
                tvWalletBalance.visibility = View.VISIBLE
                tvHeader.text = getString(R.string.label_referral_coins)
                tvCoinWins.text = LocalStorage.getReferralPoints().toString()
                tvCoinBalance.text = String.format(getString(R.string.coins), coins)
                walletBalance = coins
                tvReferral.text =
                    getString(R.string.you_have_received) + LocalStorage.getReferralPoints() + getString(
                        R.string.icoins_from_referral
                    )
            }
            //streamKonfetti(BaseActivity.TOP)
            setResult()
        }
    }

    fun setResult() {
        val intent = intent
        intent.putExtra(WalletActivity.KEY_WALLET_BALANCE, walletBalance)
        intent.putExtra(WalletActivity.KEY_SUCCESS, successVal)
        intent.putExtra(WalletActivity.KEY_REGISTER, true)
        setResult(RESULT_OK, intent)
    }

    override fun getConfettiView(): KonfettiView? {
        return konfettiView
    }

    fun learnMoreAction(v: View) {
        //startActivity(Intent(this, GameResultActivity::class.java))
        startActivity(Intent(this, LearnMoreActivity::class.java))
        finish()
    }

    fun gotItAction(v: View) {
        finish()
    }

    override fun onBackPressed() {
    }

}
