package com.app.itaptv.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import com.android.volley.Request
import com.app.itaptv.API.APIManager
import com.app.itaptv.API.APIRequest
import com.app.itaptv.API.Url
import com.app.itaptv.R
import com.app.itaptv.custom_interface.OnAnswerSelectListener
import com.app.itaptv.font_awesome.FontAwesome
import com.app.itaptv.holder.AnswersHolder
import com.app.itaptv.structure.AnswersData
import com.app.itaptv.utils.AlertUtils
import com.app.itaptv.utils.EmptyStateManager
import com.app.itaptv.utils.Utility
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_questionnaire.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class QuestionnaireActivity : AppCompatActivity(), OnAnswerSelectListener {

    var adapter: KRecyclerViewAdapter? = null
    var arrayListAnswers = ArrayList<AnswersData>()
    var arrayListAnswersName = ArrayList<String>()
    var emptyState: EmptyStateManager? = null
    var questionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)

        getQuestionAnswers()
        setupRecycler()

    }

    fun answersOnClick(v: View) {
        when (v.id) {
            R.id.buttonAnswersSubmit -> {
                sendSurveyAnswers()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun setupRecycler() {
        val rvQuestionnaire = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvQuestionnaire)
        rvQuestionnaire.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)

        adapter = KRecyclerViewAdapter(this, arrayListAnswers, KRecyclerViewHolderCallBack { viewGroup, i ->
            val layoutView = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_answers, viewGroup, false)
            AnswersHolder(layoutView)
        }, KRecyclerItemClickListener { _, _, _ -> })

        rvQuestionnaire.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    private fun getQuestionAnswers() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        arrayListAnswers.clear()
        val params: Map<String, String> = HashMap()
        try {
            val apiRequest = APIRequest(Url.GET_SURVEY_QUESTIONS, Request.Method.POST, params, null, this@QuestionnaireActivity)
            apiRequest.showLoader = false
            APIManager.request(apiRequest) { response, error, _, _ ->
                progressBar.visibility = View.GONE
                handleQuestionsResponse(response, error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleQuestionsResponse(@Nullable response: String?, @Nullable error: Exception?) {
        try {
            if (error != null) {
                updateEmptyState(error.message)
            } else {
                if (response != null) {
                    //Log.e("response", response)
                    val jsonObjectResponse = JSONObject(response)
                    val type = if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
                    if (type.equals("error", ignoreCase = true)) {
                        updateEmptyState(jsonObjectResponse.getString("msg"))
                    } else if (type.equals("ok", ignoreCase = true)) {
                        val jsonMsg: JSONObject = jsonObjectResponse.getJSONObject("msg")
                        questionId = jsonMsg.getInt("question_id")
                        tv_question_title.text = jsonMsg.getString("question")
                        val jsonArrayMsg: JSONArray = jsonMsg.getJSONArray("answer")
                        for (i in 0 until jsonArrayMsg.length()) {
                            val answersData = AnswersData(jsonArrayMsg.getJSONObject(i))
                            arrayListAnswers.add(answersData)
                        }
                        adapter!!.notifyDataSetChanged()
                        updateEmptyState(null)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendSurveyAnswers() {
        if (arrayListAnswersName.size == 0) {
            showError(getString(R.string.select_an_answer))
            return
        }
        val params: MutableMap<String, String> = HashMap()
        for (i in arrayListAnswersName.indices) {
            params["answer[$i]"] = arrayListAnswersName[i]
        }
        params["question_id"] = questionId.toString()
        try {
            val apiRequest = APIRequest(Url.SET_ANSWERS, Request.Method.POST, params, null, this@QuestionnaireActivity)
            apiRequest.showLoader = true
            APIManager.request(apiRequest) { response, error, _, _ -> handleSurveyAnswersResponse(response, error) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleSurveyAnswersResponse(@Nullable response: String, @Nullable error: Exception?) {
        try {
            val jsonObjectResponse = JSONObject(response)
            val type = if (jsonObjectResponse.has("type")) jsonObjectResponse.getString("type") else ""
            if (type.equals("error", ignoreCase = true)) {
                finishFlow()
            } else if (type.equals("ok", ignoreCase = true)) {
                val jsonMsg: String = jsonObjectResponse.getString("msg")
                AlertUtils.showAlert(getString(R.string.thanks_for_your_response), jsonMsg, getString(R.string.label_continue), this) { finishFlow() }
            } else {
                showError(getString(R.string.something_went_wrong))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showError(errorMessage: String?) {
        var error = errorMessage
        if (error == null) error = APIManager.GENERIC_API_ERROR_MESSAGE else {
            AlertUtils.showAlert(getString(R.string.label_error), error, null, this, null)
        }
    }

    private fun finishFlow() {
        val intent = Intent()
        intent.putExtra("USER_REFER", 0)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Update of Empty State
     */
    private fun updateEmptyState(error: String?) {
        if (error == null) {
            if (arrayListAnswers.isEmpty()) {
                emptyState?.setImgAndMsg(getString(R.string.no_questions_found), FontAwesome.FA_Exclamation)
                llHeaderLabel.visibility = View.INVISIBLE
            } else {
                llHeaderLabel.visibility = View.VISIBLE
                emptyState?.hide()
            }
        } else {
            llHeaderLabel.visibility = View.INVISIBLE
            if (Utility.isConnectingToInternet(this)) {
                emptyState?.message = APIManager.GENERIC_API_ERROR_MESSAGE
            } else {
                emptyState?.showNoConnectionState()
            }
        }
    }

    override fun removeDeselectedValue(name: String) {
        for (answerName in ArrayList<String>(arrayListAnswersName)) {
            if (answerName == name) {
                arrayListAnswersName.remove(answerName)
            }
        }
    }

    override fun addSelectedValue(name: String) {
        arrayListAnswersName.add(name)
    }
}
