package com.app.itaptv.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.QuestionsLiveNowData;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class QuestionsHolder extends KRecyclerViewHolder {

    View view;
    TextView tvUsername;
    TextView tvQuestion;
    TextView tvTimeStamp;
    private String sourceDate = "yyyy-MM-dd HH:mm:ss";
    private String destinationDate = "hh:mm a";

    public QuestionsHolder(View itemView) {
        super(itemView);
        view = itemView;
        tvUsername = view.findViewById(R.id.text_user);
        tvQuestion = view.findViewById(R.id.text_question);
        tvTimeStamp = view.findViewById(R.id.text_timestamp);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof QuestionsLiveNowData) {
            QuestionsLiveNowData questionsLiveNowData = (QuestionsLiveNowData) itemObject;
            tvUsername.setText(questionsLiveNowData.senderName);
            tvQuestion.setText(questionsLiveNowData.senderQuestion);
            tvTimeStamp.setText(Utility.formatDate(sourceDate, destinationDate, questionsLiveNowData.senderTime)
                    .replace("AM", "am")
                    .replace("PM", "pm"));
        }
    }
}
