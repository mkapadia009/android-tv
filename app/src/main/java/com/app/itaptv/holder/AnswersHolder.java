package com.app.itaptv.holder;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.OnAnswerSelectListener;
import com.app.itaptv.structure.AnswersData;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import java.util.ArrayList;

public class AnswersHolder extends KRecyclerViewHolder implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbAnswersName;
    OnAnswerSelectListener onAnswerSelectListener;
    private String answer;
    private View view;
    private ArrayList<String> checkedList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public AnswersHolder(View itemView) {
        super(itemView);
        view = itemView;
        cbAnswersName = view.findViewById(R.id.cbAnswers);
        cbAnswersName.setOnCheckedChangeListener(this);

        // set default font to deselected checkboxes (By default all checkboxes will be deselected)
        cbAnswersName.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_light));

    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof AnswersData) {
            onAnswerSelectListener = (OnAnswerSelectListener) context;
            AnswersData answersData = (AnswersData) itemObject;
            answer = answersData.name;
            cbAnswersName.setText(answersData.name);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            buttonView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
            buttonView.setTextColor(view.getContext().getResources().getColor(R.color.colorAccent));
            onAnswerSelectListener.addSelectedValue(answer);
        } else {
            buttonView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_light));
            buttonView.setTextColor(view.getContext().getResources().getColor(R.color.colorWhite));
            onAnswerSelectListener.removeDeselectedValue(answer);
        }
    }
}
