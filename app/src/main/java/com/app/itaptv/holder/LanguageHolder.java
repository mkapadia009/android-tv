package com.app.itaptv.holder;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.app.itaptv.R;
import com.app.itaptv.custom_interface.OnSelectListener;
import com.app.itaptv.structure.LanguageData;
import com.app.itaptv.utils.LocalStorage;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import java.util.ArrayList;

/**
 * Created by poonam on 27/8/18.
 */

public class LanguageHolder extends KRecyclerViewHolder implements CompoundButton.OnCheckedChangeListener {

    CheckBox cbLanguageName;
    OnSelectListener onSelectListener;
    int languageId;
    private View view;
    ArrayList<Integer> checkedList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LanguageHolder(View itemView) {
        super(itemView);
        view = itemView;
        cbLanguageName = view.findViewById(R.id.cbLanguageName);
        cbLanguageName.setOnCheckedChangeListener(this);

        // set default font to deselected checkboxes (By default all checkboxes will be deselected)
        cbLanguageName.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_light));

    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof LanguageData) {
            onSelectListener = (OnSelectListener) context;
            LanguageData languageData = (LanguageData) itemObject;
            languageId = languageData.termId;
            cbLanguageName.setText(languageData.name);
            checkedList = LocalStorage.getLanguageArrayList("savedLanguages", context);
            if (checkedList != null && !checkedList.isEmpty()) {
                if (checkedList.contains(languageId)) {
                    cbLanguageName.setChecked(true);
                }
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            buttonView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_medium));
            buttonView.setTextColor(view.getContext().getResources().getColor(R.color.colorAccent));
            onSelectListener.addSelectedValue(languageId);
        } else {
            buttonView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.rubik_light));
            buttonView.setTextColor(view.getContext().getResources().getColor(R.color.colorWhite));
            onSelectListener.removeDeselectedValue(languageId);
        }
    }
}
