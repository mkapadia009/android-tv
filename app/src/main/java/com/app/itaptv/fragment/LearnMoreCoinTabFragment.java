package com.app.itaptv.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.itaptv.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LearnMoreCoinTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LearnMoreCoinTabFragment extends Fragment {

    View view;
    TextView txt_coin_qty;
    public LearnMoreCoinTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LearnMoreCoinTabFragment.
     */

    public static LearnMoreCoinTabFragment newInstance() {
        LearnMoreCoinTabFragment fragment = new LearnMoreCoinTabFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     *  This method used for on tab change change the some values
     * @param position:tab position
     */

    public void callMethod(int position)
    {
        if(position==0)
        {
            txt_coin_qty.setText(getString(R.string.earn_coin_quest));
        }else {
            txt_coin_qty.setText(getString(R.string.spean_coin_quest));
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_learn_more_coin_tab, container, false);
        init();
        return view;
    }

    private void init() {
        txt_coin_qty=view.findViewById(R.id.tv_coin_qty);

    }


}
