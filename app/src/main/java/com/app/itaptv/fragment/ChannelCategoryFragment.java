package com.app.itaptv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.adapter.IntegratedAdapter;
import com.app.itaptv.custom_interface.OnPlayerListener;
import com.app.itaptv.fireworks.IntegratedViewModel;
import com.app.itaptv.holder.FeedHolder;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.FeedData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChannelCategoryFragment extends Fragment {
    View view;
    LinearLayout llChannel, llNsvChannel;
    NestedScrollView nsvChannel;

    private static final String ClassName = "classname";
    private static final String PageName = "pagename";
    private static final String ID = "id";
    private static final String Title = "title";
    private String mClassName;
    private String mPageName;
    private int mID;
    private String mTitle;
    private String mUrl;

    private static final String CategoryClass = "category";
    private static final String ChannelClass = "channel";

    private static final String TYPE_SUBCATEGORY = "subcategory";
    private static final String TYPE_ORIGINALS = "originals";

    ArrayList<FeedData> arrayListChannelOrCatData = new ArrayList<>();
    ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
    EmptyStateManager emptyState;

    OnPlayerListener onPlayerListener;
    HomeActivity activity;

    public ChannelCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragmentName :Dynamic pass fragment
     * @param pageName     :Dynamic pass page fragment like listen,read,channel,play
     * @param id           :Dynamic pass category term id
     * @param title        :Dynamic pass title of subcategory
     * @return
     */
    public static ChannelCategoryFragment newInstance(String fragmentName, String pageName, int id, String title) {
        ChannelCategoryFragment fragment = new ChannelCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ClassName, fragmentName);
        args.putString(PageName, pageName);
        args.putInt(ID, id);
        args.putString(Title, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClassName = getArguments().getString(ClassName);
            mPageName = getArguments().getString(PageName);
            mID = getArguments().getInt(ID);
            mTitle = getArguments().getString(Title);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_channel, container, false);
            init();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mClassName.equalsIgnoreCase("category")) {

        } else {
            if ((HomeActivity) getActivity() != null) {
                ((HomeActivity) getActivity()).showToolbarIcon();
                ((HomeActivity) getActivity()).showToolbarTitle(false);
            }
        }
    }

    /**
     * Initialize data
     */
    private void init() {
        llChannel = view.findViewById(R.id.llChannel);
        nsvChannel = view.findViewById(R.id.nsvChannel);
        //llNsvChannel = view.findViewById(R.id.ll_nsv_channel);
        //llNsvChannel.setVisibility(View.INVISIBLE);
        onPlayerListener = (OnPlayerListener) getActivity();
        if (mClassName.equalsIgnoreCase("category")) {
            ((HomeActivity) getActivity()).setToolbar(true);
            ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
            ((HomeActivity) getActivity()).showToolbarTitle(true);
            ((HomeActivity) getActivity()).setToolbarTitle(mTitle);
            ((HomeActivity) getActivity()).setCustomizedTitle(0);
        } else {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);

        }
        setHasOptionsMenu(false);
        setUpEmptyState();
        initUrl();
        getChannelOrCategoryAPI();
    }

    private void initUrl() {
        if (mClassName.equalsIgnoreCase("channel")) {
            mUrl = Url.GET_FEEDS + mPageName;
        } else if (mClassName.equalsIgnoreCase("category")) {
            mUrl = Url.GET_CATEGORY + mPageName + "&ID=" + mID;
        }
    }

    /**
     * Returns Channel data
     */
    private void getChannelOrCategoryAPI() {
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<>();
            APIRequest apiRequest = new APIRequest(mUrl, Request.Method.GET, params, null, getActivity());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleFeedResponse(response, error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles feed response
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleFeedResponse(@Nullable String response, @Nullable Exception error) {
        arrayListChannelOrCatData.clear();
        try {
            if (error != null) {
                // Utility.showError(error.getMessage(),getActivity());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);

                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        //Utility.showError(message,getActivity());
                        updateEmptyState(jsonObjectResponse.getString("msg"));
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                        for (int i = 0; i < jsonArrayMsg.length(); i++) {
                            FeedData feedData = new FeedData(jsonArrayMsg.getJSONObject(i), i);
                            arrayListChannelOrCatData.add(feedData);
                        }
                        updateUIData(arrayListChannelOrCatData);
                        updateEmptyState(null);
                    }
                }
            }
        } catch (JSONException e) {
            Utility.showError("hasgd", getActivity());
        }
    }

    /*private void setPlaybackData(FeedContentData feedContentData, int position) {
        if (arrayListChannelOrCatData.size() > 0) {
            if (getActivity() != null && getActivity() instanceof HomeActivity) {
                activity = (HomeActivity) getActivity();
                if (!feedContentData.taxonomy.equals(FeedContentData.CONTENT_TYPE_CATEGORY) && (feedContentData.postType.equals(FeedContentData.POST_TYPE_POST))) {
                    activity.playItems(arrayListFeedData.get(feedContentData.feedPosition).arrayListFeedContent, position);
                } else if (feedContentData.postType.equals(FeedContentData.POST_TYPE_STREAM)) {
                    playChannelAPI(feedContentData.postId, feedContentData.imgUrl, 1, position);
                } else {
                    ChannelCategoryFragment channelcategoryFragment = ChannelCategoryFragment.newInstance("category", tabType, feedContentData.termId, feedContentData.name);
                    openFragment(channelcategoryFragment);
                }
            }
        }
    }*/

    /**
     * It calls the particular method to add a view dynamically depending on the view type
     *
     * @param arrayListChannelData array list of data segments to be displayed on page
     */
    void updateUIData(ArrayList<FeedData> arrayListChannelData) {
        llChannel.removeAllViews();
        //llNsvChannel.setVisibility(View.VISIBLE);
        for (FeedData feedData : arrayListChannelData) {
            switch (feedData.viewType) {
                case FeedData.VIEW_TYPE_H_LIST:
                    String mtitle = getTitle(mClassName, feedData);
                    int mid = getId(mClassName, feedData);
                    setHorizontalRecyclerView(mtitle, feedData.arrayListFeedContent, mid, feedData);
                    break;
                case FeedData.VIEW_TYPE_AD:
                    setAdLayout(feedData.feedContentObjectData, feedData.id);
                    break;
            }
        }
    }

    /**
     * Getting id for Bucket
     *
     * @param mClassName:Page     Name like channel,category
     * @param feedData:Particular Bucket data
     * @return
     */
    private int getId(String mClassName, FeedData feedData) {
        int mId = 0;
        switch (mClassName) {
            case ChannelClass:
                mId = feedData.id;
                break;
            case CategoryClass:
                //mId = mID;
                switch (feedData.type) {
                    case TYPE_SUBCATEGORY:
                        mId = feedData.termId;
                        break;
                    case TYPE_ORIGINALS:
                        mId = feedData.id;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return mId;
    }

    /**
     * Getting Title for Bucket
     *
     * @param mClassName:Page     Name like channel,category
     * @param feedData:Perticular Bucket data
     * @return
     */
    private String getTitle(String mClassName, FeedData feedData) {
        String title = "";
        switch (mClassName) {
            case ChannelClass:
                title = feedData.title;
                break;
            case CategoryClass:
                switch (feedData.type) {
                    case TYPE_SUBCATEGORY:
                        title = feedData.name;
                        break;
                    case TYPE_ORIGINALS:
                        title = feedData.postTitle;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }

        return title;
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getChannelOrCategoryAPI();
                }
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListChannelOrCatData.isEmpty()) {
                //emptyState.setImgAndMsg("No Channels Found.", R.drawable.channels);
                nsvChannel.setVisibility(View.INVISIBLE);
            } else {
                nsvChannel.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            nsvChannel.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    /**
     * This method adds the horizontal recycler view dynamically to the parent layout of the page
     *
     * @param title                Title of the feed
     * @param arrayListFeedContent Content of the feed
     * @param id
     */
    private void setHorizontalRecyclerView(final String title, final ArrayList<FeedContentData> arrayListFeedContent, final int id, FeedData feedData) {
        try {

            View viewContent = View.inflate(getActivity(), R.layout.row_horizontal_list, null);
            llChannel.addView(viewContent);

            LinearLayout llHzRow = viewContent.findViewById(R.id.llHzRow);
            TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
            RecyclerView rvContent = viewContent.findViewById(R.id.rvContent);
            TextView tvViewAll = viewContent.findViewById(R.id.tvViewAll);
            //llNsvChannel = viewContent.findViewById(R.id.ll_nsv_channel);

            RelativeLayout rlParentTitle = viewContent.findViewById(R.id.rlParentTitle);
            /*if (arrayListFeedContent.size() == 0) {
                llHzRow.setVisibility(View.GONE);
                return;
            }*/

            llHzRow.setVisibility(View.VISIBLE);
            tvTitle.setText(title);

            if (feedData.feedType.equalsIgnoreCase("fireworks")) {
                tvViewAll.setVisibility(View.GONE);
                IntegratedAdapter integratedAdapter = new IntegratedAdapter(getActivity().getSupportFragmentManager(), false);
                IntegratedViewModel integratedViewModel;
                integratedViewModel = ViewModelProviders.of(this).get(IntegratedViewModel.class);
                integratedViewModel.getContent().observe(this, demoContents -> {
                    integratedAdapter.getContentFeed().clear();
                    integratedAdapter.getContentFeed().addAll(demoContents);
                    rvContent.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, 1, SpacingItemDecoration.LEFT));
                    rvContent.setAdapter(integratedAdapter);
                    integratedAdapter.notifyDataSetChanged();
                });
            } else {
                if (arrayListFeedContent.size() == 0) {
                    llHzRow.setVisibility(View.GONE);
                    return;
                }
                rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rvContent.setNestedScrollingEnabled(false);
                rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING, SpacingItemDecoration.LEFT));

                KRecyclerViewAdapter adapter = new KRecyclerViewAdapter(getActivity(), arrayListFeedContent, new KRecyclerViewHolderCallBack() {
                    @Override
                    public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_feed, viewGroup, false);
                        return new FeedHolder(layoutView, feedData);
                    }
                }, new KRecyclerItemClickListener() {
                    @Override
                    public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

                        setPlaybackData((FeedContentData) o, i);

                    }
                });

                rvContent.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            rlParentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllFeedsFragment viewAllFeedsFragment;
                    if (mClassName.equalsIgnoreCase("category")) {
                        viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(id, title, mPageName, Constant.TYPE_CATEGORIES, feedData.tileShape, feedData.feedType,"");
                    } else {
                        viewAllFeedsFragment = ViewAllFeedsFragment.newInstance(id, title, mPageName, Constant.TYPE_FEEDS, feedData.tileShape, feedData.feedType,"");

                    }
                    openFragment(viewAllFeedsFragment);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlaybackData(FeedContentData feedContentData, int position) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            activity = (HomeActivity) getActivity();

            switch (mClassName) {
                case ChannelClass:
                    //playChannelAPI(feedContentData, 1);
                    activity.showChannelExpandedView(feedContentData, 1, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), Analyticals.CONTEXT_CHANNEL);
                    break;
                case CategoryClass:
                    /*if (arrayListChannelOrCatData.size() > 0) {
                        activity.playItems(arrayListChannelOrCatData.get(feedContentData.feedPosition).arrayListFeedContent, position, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), arrayListChannelOrCatData.get(feedContentData.feedPosition).tabType);
                    }*/

                    if (arrayListChannelOrCatData.size() > 0) {
                        if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_ORIGINALS)) {
                            activity.showSeriesExpandedView(feedContentData.postId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                        } else if (feedContentData.postType.equalsIgnoreCase(FeedContentData.POST_TYPE_PLAYLIST)) {
                            activity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                        } else {
                            ArrayList<FeedContentData> arrayList = new ArrayList<>();
                            arrayList.add(feedContentData);
                            //activity.playItems(arrayListChannelOrCatData.get(feedContentData.feedPosition).arrayListFeedContent, position, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), arrayListChannelOrCatData.get(feedContentData.feedPosition).tabType,"");
                            activity.showAudioSongExpandedView(arrayList, 0, String.valueOf(arrayListChannelOrCatData.get(feedContentData.feedPosition).id), Analyticals.CONTEXT_FEED, String.valueOf(feedContentData.iswatchlisted));

                        }
                    }
                    break;
            }
        }
    }


    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /**
     * This method adds the specific layout of the Ad depending on the ad type dynamically to the parent layout of the page
     *
     * @param feedContentData Content of the Ad
     * @param id
     */
    private void setAdLayout(FeedContentData feedContentData, int id) {

        switch (feedContentData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                setAdLayoutAudio(feedContentData);
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setAdLayoutImage(feedContentData);
                break;
        }
    }


    /**
     * Sets layout for the Audio Ad to play the audio file
     *
     * @param feedContentData Ad content data object
     */
    private void setAdLayoutAudio(FeedContentData feedContentData) {
        View viewContent = View.inflate(getContext(), R.layout.row_audio, null);
        llChannel.addView(viewContent);

        TextView tvTitle = viewContent.findViewById(R.id.tvStoreName);
        tvTitle.setText(feedContentData.postTitle);

        RelativeLayout rlAudio = viewContent.findViewById(R.id.rlAudio);
        rlAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showToast(getString(R.string.play_audio), Toast.LENGTH_SHORT, getContext());
            }
        });
    }

    /**
     * Sets layout for the Ad to display image
     *
     * @param feedContentData Ad content data object
     */
    private void setAdLayoutImage(FeedContentData feedContentData) {

        String imageUrl = feedContentData.imgUrl;

        View viewContent = View.inflate(getContext(), R.layout.row_image, null);
        llChannel.addView(viewContent);

        ImageView ivImage = viewContent.findViewById(R.id.ivImage);
        Glide.with(getContext())
                .load(imageUrl)
                .apply(new RequestOptions().error(R.drawable.no_image_avail).placeholder(R.drawable.no_image_avail).dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(ivImage);
    }

    /**
     * Sets layout for the Ad of the type question 71hYFr
     */
    private void setAdLayoutQuestion() {
        View viewContent = View.inflate(getContext(), R.layout.row_question, null);
        llChannel.addView(viewContent);

        Button btOption4 = viewContent.findViewById(R.id.btOption4);
        btOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertUtils.showToast(getString(R.string.option_selected), Toast.LENGTH_SHORT, getContext());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIManager.cancelAllRequests(APIManager.PAGE_NAVIGATION);
    }
}
