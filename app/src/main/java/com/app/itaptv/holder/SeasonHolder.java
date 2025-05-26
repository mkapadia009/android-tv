package com.app.itaptv.holder;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.activity.SeasonsTvFragment;
import com.app.itaptv.fragment.EpisodePlaylistTabFragment;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.utils.Log;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class SeasonHolder extends KRecyclerViewHolder {

    public interface KeyCallback {
        void keyCallback();
    }

    TextView tvSeasonName;

    public SeasonHolder(View itemView, KeyCallback keyCallback) {
        super(itemView);

        tvSeasonName = itemView.findViewById(R.id.tab1);
        tvSeasonName.setFocusable(true);
        tvSeasonName.setPadding(10, 5, 10, 5);
        tvSeasonName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    tvSeasonName.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.bg_accent));
                } else {
                    for (int i = 0; i < SeasonsTvFragment.Companion.getViewsList().size(); i++) {
                        if (SeasonsTvFragment.Companion.getLastSelectedSeason() == i) {
                            SeasonsTvFragment.Companion.getViewsList().get(i).setBackground(itemView.getContext().getResources().getDrawable(R.drawable.bg_button_grey));
                        } else {
                            SeasonsTvFragment.Companion.getViewsList().get(i).setBackground(null);
                        }
                    }
                }
            }
        });

        tvSeasonName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    int selectedPosition = 0;
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            selectedPosition = SeasonsTvFragment.Companion.getLastSelectedSeasonAdapter() - 1;
                            Log.i("selected", "selectedPosition " + selectedPosition);
                            if (selectedPosition >= 0) {
                                SeasonsTvFragment.Companion.setLastSelectedSeasonAdapter(selectedPosition);
                            } else {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            selectedPosition = SeasonsTvFragment.Companion.getLastSelectedSeasonAdapter() + 1;
                            Log.i("selected", "" + selectedPosition);
                            if (selectedPosition < SeasonsTvFragment.Companion.getViewsList().size()) {
                                SeasonsTvFragment.Companion.setLastSelectedSeasonAdapter(selectedPosition);
                            } else {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            keyCallback.keyCallback();
                            EpisodePlaylistTabFragment.restoreFocus();
                            break;
                    }
                }
                return false;
            }
        });
        SeasonsTvFragment.Companion.getViewsList().add(tvSeasonName);
        if (SeasonsTvFragment.Companion.getViewsList().size() == 1) {
            tvSeasonName.requestFocus();
            tvSeasonName.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.bg_accent));
        }
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof SeasonData) {
            SeasonData seasonData = (SeasonData) itemObject;
            tvSeasonName.setText(seasonData.name);
        }
    }
}
