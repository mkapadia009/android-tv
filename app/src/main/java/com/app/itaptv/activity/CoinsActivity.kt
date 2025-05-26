package com.app.itaptv.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.app.itaptv.R
import com.app.itaptv.utils.*
import kotlinx.android.synthetic.main.activity_coins.*
import nl.dionsegijn.konfetti.KonfettiView

class CoinsActivity : BaseActivity() {
    var walletBalance = 0L
    var successVal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_coins)

            val registrationCoins = LocalStorage.getRegisterBonus()
            earnCoin(registrationCoins)
            coins_progressbar.visibility = View.VISIBLE
            ivCoinWins.visibility = View.INVISIBLE
            tvCoinWins.visibility = View.INVISIBLE

            //showGoogleKeyHash();
            setTheme(R.style.Theme_Leanback)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Utility.customEventsTracking(Constant.Registration, "")
            FirebaseAnalyticsLogs.logEvent().registrationRate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun earnCoin(registrationCoins: Long) {
        //var flag = Wallet.FLAG_REGISTER
        //var coins = 100

        Wallet.earnCoins(
            this,
            Integer.parseInt(LocalStorage.getUserId()),
            "iTap",
            Wallet.FLAG_REGISTER,
            registrationCoins
        )
        { success, error, coins, creditedCoins, diamonds, historyData, historyCount ->
            handleWinCoins(success, coins)
            false
        }

        /*Wallet.earnCoins(0, "iTap", Wallet.FLAG_REGISTER, 100) { success, error, coins, historyData, historyCount ->
            //val llCoinActivity: LinearLayout = findViewById(R.id.llCoinActivity) as LinearLayout
            //val tvCoinWins: TextView = findViewById(R.id.tvCoinWins) as TextView
            //val tvCoinBalance:TextView=findViewById(R.id.tvCoinBalance) as TextView
            if (success) {
                successVal = success
                llCoinActivity.visibility = View.VISIBLE;
                pbCoinActivity.visibility = View.GONE
                tvCoinWins.text = coins.toString()
                tvCoinBalance.text = coins.toString() + " coins"
                walletBalance = coins
                streamKonfetti(BaseActivity.TOP)
                setResult()
                return@earnCoins
            }
        }*/
    }

    fun handleWinCoins(success: Boolean, coins: Long) {
        try {
            if (success) {
                successVal = success
                pbCoinActivity.visibility = View.INVISIBLE
                coins_progressbar.visibility = View.INVISIBLE
                llCoinActivity.visibility = View.VISIBLE
                ivCoinWins.visibility = View.VISIBLE
                tvCoinWins.visibility = View.VISIBLE
                tvCoinWins.text = coins.toString()
                tvCoinBalance.text = String.format(getString(R.string.coins), coins)
                walletBalance = coins
                //streamKonfetti(BaseActivity.TOP)
                setResult()
            }
        } catch (e: Exception) {

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