package com.app.itaptv.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import com.app.itaptv.R
import com.app.itaptv.activity.WalletActivity.KEY_WALLET_BALANCE
import com.app.itaptv.activity.WalletActivity.REQUEST_CODE
import com.app.itaptv.structure.GameData
import com.app.itaptv.utils.*
import com.shephertz.app42.gaming.multiplayer.client.WarpClient
import kotlinx.android.synthetic.main.activity_game_result.*
import org.json.JSONException
import org.json.JSONObject

class GameResultActivity : BaseActivity() {

    companion object {
        const val GAME_DATA = "gameData"
        const val GAME_ID = "gameId"
        const val GAME_TYPE = "gameType"
        const val GAME_STATUS = "gameStatus"
        const val YOUR_SCORE = "yourScore"
        const val OPPONENT_SCORE = "opponentScore"
        const val TOTAL_GAME_COINS = "totalGameCoins"
        const val GAME_TYPE_PRACTICE = "gamePractice"
        const val GAME_WIN = 1
        const val GAME_LOST = 0
    }

    internal var gameData: GameData? = null
    var gameId = 0
    var gameType = 0
    var gameStatus = 0
    var yourScore = ""
    var opponentScore = ""
    var totalGameCoins = 0L
    var walletBalance = 0L
    var isPracticeGame = false
    private var warpClient: WarpClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)
        setTheme(R.style.Theme_Leanback)
        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val bundle = intent.extras?.getBundle("Bundle")
        gameData = bundle?.get(GAME_DATA) as GameData
        gameId = gameData!!.id.toInt()
        gameStatus = bundle.getInt(GAME_STATUS)
        gameType = bundle.getInt(GAME_TYPE)
        totalGameCoins = bundle.getString(TOTAL_GAME_COINS)?.toLong() ?: 0
        isPracticeGame = bundle.getBoolean(GAME_TYPE_PRACTICE)


        if (gameType == GameStartActivity.GAME_TYPE_MULTI_USER) {
            yourScore = bundle.getString(YOUR_SCORE).toString()
            opponentScore = bundle.getString(OPPONENT_SCORE).toString()
            warpClient = WarpClient.getInstance()
        }

        buttonFocusListener(btReplay)
        buttonFocusListener(btNextGame)
        buttonFocusListener(btLeaderboard)
        buttonFocusListener(btShare)
        buttonFocusListener(btRedeemCoins)
        buttonFocusListener(btExit)
        btReplay.requestFocus()

        setImageSize()
        setQuizTypeLayout()
    }

    private fun setImageSize() {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val imageHeight = (displaymetrics.heightPixels / 3.5).toInt()
        val layoutMargin = (imageHeight / 1.1).toInt()
        val layoutHeight = (imageHeight / 1.5).toInt()
        val layoutWidth = (displaymetrics.widthPixels / 1.5).toInt()
        ivWinLoseImage.layoutParams.height = imageHeight
        llBalance.layoutParams.height = layoutHeight
        llBalance.layoutParams.width = layoutWidth
        tblLayout.layoutParams.width = layoutWidth


        val params = llParent.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(0, layoutMargin, 0, 0)
        llParent.layoutParams = params
    }

    fun btnAction(v: View) {
        when {
            v.id == R.id.btExit -> finish()
            v.id == R.id.btReplay -> finish()
            v.id == R.id.btRedeemCoins -> {
                startActivityForResult(
                    Intent(this, RedeemCoinsActivity::class.java)
                        .putExtra(KEY_WALLET_BALANCE, walletBalance),
                    REQUEST_CODE
                )

                finish()

            }

            v.id == R.id.btLeaderboard -> {
                startActivity(Intent(this, LeaderBoardActivity::class.java))
                finish()
            }

            v.id == R.id.btShare -> {
                if (AnalyticsTracker.isGameOngoing) {
                    AnalyticsTracker.pauseTimer(AnalyticsTracker.GAME)
                }
                val appLink = Constant.APP_SHARE_LINK
                val i = Intent(Intent.ACTION_SEND)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra(Intent.EXTRA_TITLE, getString(R.string.label_itap))
                i.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.i_won) + tvWonCoins.text + getString(R.string.iCoins_by_playing) + gameData?.postTitle + getString(
                        R.string.on_iTap
                    ) + "\n\n" +
                            getString(R.string.download_iTap_now_and_play) + gameData?.postTitle + ".\n\n" + appLink
                )
                i.type = "text/plain"
                startActivity(Intent.createChooser(i, getString(R.string.share_game_result)))
            }

            v.id == R.id.btNextGame -> {
                /*val bundle = Bundle()
                bundle.putSerializable(GameStartActivity.GAME_DATA, gameData)
                startActivity(Intent(this, GameStartActivity::class.java)
                        .putExtra("Bundle", bundle))*/
                finish()
            }
        }
    }

    private fun setQuizTypeLayout() {
        when (gameData?.quizType) {


            GameData.QUIZE_TYPE_LIVE -> {
                //rlScore.visibility = View.GONE
                //btNextGame.visibility = View.GONE
            }


            GameData.QUIZE_TYPE_TURN_BASED -> {
                setTurnBasedGameLayout()
            }

        }

        setGameStatusLayout()
    }

    private fun setTurnBasedGameLayout() {
        if (gameType == GameStartActivity.GAME_TYPE_SINGLE_USER) {
            //rlScore.visibility = View.GONE
            callAddActivityAPI()
            tvPlayer1Points.visibility = View.GONE
            tvPlayer2Points.visibility = View.GONE
            tvPlayer1.visibility = View.GONE
            tvPlayer2.visibility = View.GONE

        } else {
            //rlScore.visibility = View.VISIBLE
            tvPlayer1Points.text = yourScore
            tvPlayer2Points.text = opponentScore
        }
        // btNextGame.visibility = View.VISIBLE

        if (warpClient != null)
            warpClient!!.disconnect()
    }

    private fun setGameStatusLayout() {
        when (gameStatus) {
            GAME_LOST -> {
                setLostLayout()
            }

            GAME_WIN -> {
                setWinLayout()
            }

            2 -> {
                //setOutOfQuestionsLayout()

            }
        }
    }

    /********************** WIN METHODS ***************************/
    private fun setWinLayout() {

        ivWinLoseImage.setImageResource(R.drawable.you_won)
        when (gameType) {
            GameStartActivity.GAME_TYPE_SINGLE_USER -> {
                tvWonCoins.text = totalGameCoins.toString()
            }

            GameStartActivity.GAME_TYPE_MULTI_USER -> {
                tvWonCoins.text = (gameData!!.bonusPoints.toLong()).toString()
            }
        }
        //earnedCoinsLbl.text = gameData!!.bonusPoints
        addWalletCoins()
    }

    /********************** LOST METHODS ***************************/

    private fun setLostLayout() {
        //llMiddleLayout.visibility = View.GONE
        //statusLbl.text = getString(R.string.you_lost)
        //setLostImageSize()
        ivWinLoseImage.setImageResource(R.drawable.you_lose)
        //btRedeemCoins.visibility = View.GONE
        //btShare.visibility = View.GONE
        //btLeaderboard.visibility = View.GONE
        getWalletBalance()

    }

    /********************** OUT OF QUESTIONS ***************************/

    /*private fun setOutOfQuestionsLayout() {
        llMiddleLayout.visibility = View.GONE
        //btRedeemCoins.visibility = View.GONE
        //shareBtn.visibility = View.GONE
        rlScore.visibility = View.GONE
        statusLbl.visibility = View.GONE
        outOfQuestionLbl.visibility = View.VISIBLE
        ivWinLoseImage.setImageResource(R.drawable.loser)
    }*/

    private fun addWalletCoins() {
        var quizType = gameData!!.quizType;
        var gameFlag = ""

        if (isPracticeGame) {
            gameFlag = Wallet.FLAG_LIVE_PRACTICE_GAME
        } else {
            when (quizType) {
                GameData.QUIZE_TYPE_TURN_BASED -> {
                    gameFlag = Wallet.FLAG_TRIVIA_GAME
                }

                GameData.QUIZE_TYPE_LIVE -> {
                    gameFlag = Wallet.FLAG_LIVE_GAME
                }
            }

        }

        if (gameType == GameStartActivity.GAME_TYPE_MULTI_USER) {
            totalGameCoins = totalGameCoins
            //+ gameData!!.bonusPoints.toLong()
        }

        //totalGameCoins = totalGameCoins + gameData!!.bonusPoints.toLong()
        Wallet.earnCoins(
            this,
            gameId,
            gameData!!.postTitle,
            gameFlag,
            totalGameCoins
        ) { success, error, coins, diamonds, creditedCoins, historyData, historyCount ->
            handleApp42Response(success, error, gameId, totalGameCoins)
        }
    }

    private fun handleApp42Response(
        success: Boolean,
        error: String?,
        gameId: Int,
        totalCoinsWon: Long
    ): Boolean {
        pbWalletBalance.visibility = View.GONE
        if (success) {
            llWalletBalance.visibility = View.VISIBLE
            if (gameId == 0) {
                tvBalance.text =
                    String.format(getString(R.string.text_total_earned_coins), totalCoinsWon)
                walletBalance = totalCoinsWon
            } else {
                getWalletBalance()
            }

        } else {
            val errorMessage =
                if (error == null) getString(R.string.GENERIC_API_ERROR_MESSAGE) else error
            if (!this@GameResultActivity.isFinishing) {
                AlertUtils.showAlert(
                    getString(R.string.label_error),
                    errorMessage,
                    null,
                    this@GameResultActivity,
                    null
                )
            }
        }
        return true
    }

    private fun getWalletBalance() {
        Wallet.getWalletBalance(
            this,
            { success, error, coins, diamonds, creditedCoins, historyData, historyCount ->
                handleApp42Response(success, error, 0, coins)
            })
    }

    private fun callAddActivityAPI() {
        val `object` = JSONObject()
        try {
            val activity_id =
                LocalStorage.getValue(LocalStorage.KEY_ACTIVITY_ID, "", String::class.java)
            `object`.put("completed", "completed")
            `object`.put("activity_id", activity_id)
            Analyticals.CallPlayedActivity(
                Analyticals.GAME_ACTIVITY_TYPE,
                gameData!!.id,
                "",
                "",
                this,
                `object`.toString()
            ) { success, acitivity_id, error ->

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onResume() {
        super.onResume()
        AnalyticsTracker.resumeTimer(AnalyticsTracker.GAME)
    }

    private fun buttonFocusListener(view: View) {
        if (Utility.isTelevision()) {
            view.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (view.hasFocus()) {
                    view.background = view.context.getDrawable(R.drawable.highlight_text_field)
                } else {
                    view.background = view.context.getDrawable(R.drawable.bg_tab_gray_radius_2)
                }
            }
        }
    }

}