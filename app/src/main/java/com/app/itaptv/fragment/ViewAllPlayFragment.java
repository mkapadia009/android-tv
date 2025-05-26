package com.app.itaptv.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.holder.FeedGameViewAllHolder;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SpacingItemDecoration;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewAllPlayFragment extends Fragment {
    View view;
    RelativeLayout llprogressbar;
    ProgressBar progressBar;
    EmptyStateManager emptyState;
    RecyclerView rvContent;
    KRecyclerViewAdapter adapter;
    private String mtitle;
    private String mID;
    private String tabScreen;

    private static final String Title = "title";
    private static final String ID = "id";
    private static final String TAB_SCREEN = "tab_screen";

    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;

    String Game_list_next_page_url;
    int Next_Page_N0 = 1;

    //Game Activity Result Code
    public static final int RequestCodeOfGame = 103;

    ArrayList<GameData> arrayListFeedGameData = new ArrayList<>();
    public boolean fromCombinedTab = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id    :Dyanamic pass fragment
     * @param title :Dyanamic pass page fragment like listen,read,channel,play
     * @return A new instance of fragment ViewAllPlayFragment.
     */
    public static ViewAllPlayFragment newInstance(String id, String title, String tabScreen) {
        ViewAllPlayFragment fragment = new ViewAllPlayFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(Title, title);
        args.putString(TAB_SCREEN, tabScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mtitle = getArguments().getString(Title);
            mID = getArguments().getString(ID);
            tabScreen= getArguments().getString(TAB_SCREEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_all_play, container, false);
            init();
        }
        return view;
    }

    private void init() {
        setHasOptionsMenu(false);
        setUpEmptyState();

        rvContent = view.findViewById(R.id.rvContent);
        llprogressbar = view.findViewById(R.id.llprogressbar);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        rvContent.setVisibility(View.INVISIBLE);

        adapter = new KRecyclerViewAdapter(getContext(), arrayListFeedGameData, (viewGroup, i) -> {
            View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_view_all_play, viewGroup, false);
            return new FeedGameViewAllHolder(layoutView);
        }, (kRecyclerViewHolder, o, i) -> {

            if (o instanceof GameData) {
                GameData gameData = (GameData) o;
                switch (gameData.quizType) {

                    case GameData.QUIZE_TYPE_LIVE:
                        String invalidDateMessage = GameDateValidation.getInvalidDateMsg(getActivity(), gameData.start, gameData.end);
                        if (invalidDateMessage.equals("")) {
                            if (gameData.playedGame) {
                                AlertUtils.showToast(getString(R.string.msg_game_played), Toast.LENGTH_SHORT, getContext());
                                return;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                            startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                        } else {
                            if (gameData.winnersDeclared) {
                                startActivity(new Intent(getActivity(), LuckyWinnerActivity.class).putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id));
                                return;
                            }
                            AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getContext());
                        }
                        break;

                    case GameData.QUIZE_TYPE_TURN_BASED: {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                        startActivityForResult(new Intent(getContext(), GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                    }
                    break;
                    case GameData.QUIZE_TYPE_HUNTER_GAMES: {
                        /*Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl);
                        startActivityForResult(intent,WebViewActivity.GAME_REDIRECT_REQUEST_CODE);*/

                        // initializing object for custom chrome tabs.
                        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();
                        String token = LocalStorage.getToken();
                        String userId = LocalStorage.getUserId();
                        String url = null;
                        try {
                            url = gameData.webviewUrl + "&authToken=" + token + "&userid=" + Utility.encryptData(userId, Constant.getSecretKeyDateTime(), Constant.getIvParameterDateTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Utility.openCustomTab(getActivity(), customIntent.build(), Uri.parse(url));
                    }
                    break;
                }
            }

        });
        rvContent.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvContent.setLayoutManager(manager);
        rvContent.addItemDecoration(new SpacingItemDecoration(Constant.RV_HV_SPACING));

        initializePagination();
    }

    public void initializePagination() {
        Log.e("test", "initializePagination");
        arrayListFeedGameData.clear();
        adapter.notifyDataSetChanged();
        if (fromCombinedTab) {
            Game_list_next_page_url = Url.GET_FEEDS + "listen&ID=" + mID + "&rectangle_image=true" + "&page_no=";
        } else {
            Game_list_next_page_url = Url.GET_GAME_VIEW_ALL_LISTING + mID + "&page_no=";
        }

        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvContent, callbacks).setLoadingTriggerThreshold(0).addLoadingListItem(true).setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null).setLoadingListItemSpanSizeLookup(() -> GRID_SPAN).build();

    }

    //Pagination Code
    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            loading = true;
            handler.postDelayed(fakeCallback, networkDelay);
        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            return loading; // Return boolean weather data is already loading or not
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            return Next_Page_N0 == 0; // If all pages are loaded return true
        }
    };

    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {
            //  page++;
            // loading = false;
            if (page > 0) {
                Game_list_next_page_url = Game_list_next_page_url;

            }
            Log.e("test", "fakeCallback");
            if (Next_Page_N0 != 0) {

                getViewAllGame(Next_Page_N0);

            }

        }
    };

    private void getViewAllGame(int pageno) {
        try {
            Map<String, String> params = new HashMap<>();
            String mUrl;
            mUrl = Game_list_next_page_url + pageno + "&tab_screen=" + tabScreen;

            APIRequest apiRequest = new APIRequest(mUrl, Request.Method.GET, params, null, getContext());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    llprogressbar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rvContent.setVisibility(View.VISIBLE);
                    handleGameResponse(response, error);
                }
            });
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    private void handleGameResponse(String response, Exception error) {
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

                        updateEmptyState(jsonObjectResponse.getString("msg"));
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObjectMsg = jsonObjectResponse.getJSONObject("msg");
                        Next_Page_N0 = jsonObjectMsg.getInt("next_page");
                        JSONArray jsonArrayContent = jsonObjectMsg.getJSONArray("contents");
                        ArrayList<GameData> arrayListFeedGameDataPageWise = new ArrayList<>();
                        for (int i = 0; i < jsonArrayContent.length(); i++) {
                            GameData gameData = new GameData(jsonArrayContent.getJSONObject(i), mID);
                            arrayListFeedGameDataPageWise.add(gameData);
                        }
                        arrayListFeedGameData.addAll(adapter.getItemCount(), arrayListFeedGameDataPageWise);
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                        updateEmptyState(null);
                    }
                }
            }
        } catch (JSONException e) {
            loading = false;
            paginate.unbind();

        }
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListFeedGameData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_data_found), null);
                rvContent.setVisibility(View.INVISIBLE);
            } else {
                rvContent.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvContent.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }

    }

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new ViewAllFeedsFragment.VH(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvContent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        VH(View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tv_loading_text);
        }
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                action.equals(EmptyStateManager.ACTION_RETRY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeOfGame) {
            if (data != null && data.getBooleanExtra("GameStatus", false)) initializePagination();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) Objects.requireNonNull(getActivity())).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setToolbarTitle(mtitle);
        ((HomeActivity) getActivity()).setCustomizedTitle(0);
    }
}
