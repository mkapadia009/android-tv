package com.app.itaptv.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.itaptv.R;
import com.app.itaptv.holder.EpisodeHolder;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.SeasonData;
import com.app.itaptv.structure.SeriesData;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import java.util.ArrayList;

public class EpisodesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    View view;
    SeriesData seriesData;
    AppCompatSpinner spSeason;
    ImageView ivDownloadSeason;
    RecyclerView rvEpisode;
    KRecyclerViewAdapter adapterEpisode;

    String seasonId;
    String episodeId;
    int selectedSeasonPosition;
    boolean isCreateViewCalled;
    ArrayList<FeedContentData> arrayListEpisodeData = new ArrayList<>();

    public static String KEY_SERIES_DATA = "SeriesData";
    public static String KEY_SEASON_ID = "seasonId";
    public static String KEY_EPISODE_ID = "episodeId";

    public EpisodesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.layout_episode_list, container, false);

        if (getArguments() != null) {
            seriesData = getArguments().getParcelable(KEY_SERIES_DATA);
            seasonId = String.valueOf(getArguments().getInt(KEY_SEASON_ID));
            episodeId = String.valueOf(getArguments().getInt(KEY_EPISODE_ID));
            isCreateViewCalled = true;
        }

        init();
        return view;
    }

    public void init() {
        Log.d("attach", "attach");
        spSeason = view.findViewById(R.id.acsseason);
        ivDownloadSeason = view.findViewById(R.id.ivDownloadSeason);
        rvEpisode = view.findViewById(R.id.rvEpisode);

        ArrayAdapter<SeasonData> adapter = new ArrayAdapter<SeasonData>(getActivity(),
                R.layout.spinner_textview, seriesData.arrayListSeasonData);
        spSeason.setAdapter(adapter);
        spSeason.setOnItemSelectedListener(this);

        for (int i = 0; i < seriesData.arrayListSeasonData.size(); i++) {
            SeasonData seasonData = seriesData.arrayListSeasonData.get(i);
            if (seasonData.termId.equals(seasonId)) {
                selectedSeasonPosition = i;
                break;
            }
        }
        spSeason.setSelection(selectedSeasonPosition);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int position = spSeason.getSelectedItemPosition();
        setEpisodeList(seriesData.arrayListSeasonData.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        setEpisodeList(seriesData.arrayListSeasonData.get(0));
    }

    private void setEpisodeList(SeasonData seasonData) {
        arrayListEpisodeData.clear();
        arrayListEpisodeData.addAll(seasonData.arrayListFeedContentData);


        if (adapterEpisode == null || isCreateViewCalled) {
            rvEpisode.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            rvEpisode.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));
            adapterEpisode = new KRecyclerViewAdapter(getContext(), arrayListEpisodeData, new KRecyclerViewHolderCallBack() {
                @Override
                public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_episode, viewGroup, false);
                    return new EpisodeHolder(layoutView,null);
                }
            }, new KRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                    if (o instanceof FeedContentData) {
                        playEpisode(i);
                    }
                }
            });
            rvEpisode.setAdapter(adapterEpisode);
            isCreateViewCalled = false;

        } else {
            adapterEpisode.notifyDataSetChanged();
        }
    }

    public void playEpisode(int position) {
        /*HomeActivity activity = (HomeActivity) getActivity();
        activity.playEpisode(arrayListEpisodeData, position, seriesData.postId);*/

    }
}
