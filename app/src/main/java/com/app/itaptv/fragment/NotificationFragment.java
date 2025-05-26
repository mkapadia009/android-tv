package com.app.itaptv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BuyDetailActivity;
import com.app.itaptv.activity.CouponDetailsActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LeaderBoardActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.RedeemCoinsActivity;
import com.app.itaptv.holder.NotificationViewHolder;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.NotificationData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.SeparatorDecoration;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.itaptv.activity.WalletActivity.KEY_WALLET_BALANCE;
import static com.app.itaptv.activity.WalletActivity.REQUEST_CODE;

public class NotificationFragment extends Fragment {

    ProgressBar progressBar;
    EmptyStateManager emptyState;
    RecyclerView rvNotification;
    KRecyclerViewAdapter adapter;

    // Pagination
    private static final int GRID_SPAN = 1;
    private boolean loading = false;
    private int page = 0;
    private Handler handler;
    private Paginate paginate;
    protected long networkDelay = 2000;
    protected boolean customLoadingListItem = false;
    int nextPageNo = 1;

    String notificationUrl = "";
    ArrayList<NotificationData> arrayNotificationList = new ArrayList<>();

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        init();
        return view;
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    /**
     * Initialization of ALL
     */
    private void init() {
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.label_notification));
        ((HomeActivity) getActivity()).setCustomizedTitle(0);

        progressBar = view.findViewById(R.id.progressBar);
        rvNotification = view.findViewById(R.id.rvNotification);

        progressBar.setVisibility(View.VISIBLE);
        rvNotification.setVisibility(View.INVISIBLE);
        setUpEmptyState();
        setUpRecyclerView();

    }

    /**
     * Set Up the RecyclerView
     */
    private void setUpRecyclerView() {
        rvNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        SeparatorDecoration decoration = new SeparatorDecoration(getContext(), getResources().getColor(R.color.tab_grey), 0.5f);
        rvNotification.addItemDecoration(decoration);
        adapter = new KRecyclerViewAdapter(getContext(), arrayNotificationList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_notification, viewGroup, false);
                return new NotificationViewHolder(view);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o != null) {
                    readNotification((NotificationData) o, i);

                    setNotificationPage((NotificationData) o, i);
                }
            }
        });
        rvNotification.setAdapter(adapter);
        initializePagination();
    }

    private void setNotificationPage(NotificationData data, int i) {
        Intent intent = null;
        switch (data.page) {
            case Constant.LUCKY_WINNER:
                intent = new Intent(getContext(), LuckyWinnerActivity.class);
                intent.putExtra(LuckyWinnerActivity.KEY_GAME_ID, data.gameId);
                startActivity(intent);
                break;
            case Constant.PLAYLIST:
                if (data.feedContentData == null) {
                    ((HomeActivity) getActivity()).showPlaylistExpandedView(data.ID, "false");
                } else {
                    if (data.feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                        ((HomeActivity) getActivity()).showPlaylistExpandedView(data.ID, "false");
                    } else {
                        Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                        intent1.putExtra(BuyDetailActivity.CONTENT_DATA, data.feedContentData);
                        getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                    }
                }
                break;
            case Constant.GAME:
                if (data.gameData != null) {
                    GameData gameData = data.gameData;
                    switch (gameData.quizType) {

                        case GameData.QUIZE_TYPE_LIVE:
                            String invalidDateMessage = GameDateValidation.getInvalidDateMsg(getActivity(), gameData.start, gameData.end);
                            if (invalidDateMessage.equals("")) {
                                if (gameData.playedGame) {
                                    AlertUtils.showToast(getString(R.string.msg_game_played), Toast.LENGTH_SHORT, getActivity());
                                    return;
                                }
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                                startActivityForResult(new Intent(getActivity(), GameStartActivity.class)
                                        .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            } else {
                                if (gameData.winnersDeclared) {
                                    startActivity(
                                            new Intent(getContext(), LuckyWinnerActivity.class)
                                                    .putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id)
                                    );
                                    return;
                                }
                                AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, getActivity());
                            }
                            break;

                        case GameData.QUIZE_TYPE_TURN_BASED:
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                            startActivityForResult(new Intent(getContext(), GameStartActivity.class)
                                    .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            break;
                    }

                }

                // tlHomeTabs.getTabAt(1).select();
                break;
            case Constant.EPISODE:
                if (data.feedContentData == null) {
                    ((HomeActivity) getActivity()).showSeriesExpandedView(data.SeriesId, data.SeasonId, data.ID, "");
                } else {
                    if (data.feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                        ((HomeActivity) getActivity()).showSeriesExpandedView(data.SeriesId, data.SeasonId, data.ID, "");
                    } else {
                        Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                        intent1.putExtra(BuyDetailActivity.CONTENT_DATA, data.feedContentData);
                        getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                    }
                }
                break;
            case Constant.GAME_LIST:
                ((HomeActivity) getActivity()).openFragment(HomePlayTabFragment.newInstance());
                break;
            case Constant.COUPONS:
                String walletBalance = "0";
                if (HomeActivity.toolbarWalletBalance != null) {
                    walletBalance = HomeActivity.toolbarWalletBalance.getText().toString();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("CoupanData", data.couponData);
                bundle.putString("Balance", walletBalance);
                startActivityForResult(
                        new Intent(getActivity(), CouponDetailsActivity.class).putExtra("CoupanDetail", bundle),
                        REQUEST_CODE
                );
                break;
            case Constant.ORIGINALS:
                if (data.feedContentData == null) {
                    ((HomeActivity) getActivity()).showSeriesExpandedView(data.ID, data.feedContentData.seasonId, data.feedContentData.episodeId, "");
                } else {
                    if (data.feedContentData.postExcerpt.equalsIgnoreCase("free")) {
                        ((HomeActivity) getActivity()).showSeriesExpandedView(data.ID, data.feedContentData.seasonId, data.feedContentData.episodeId, "");
                    } else {
                        Intent intent1 = new Intent(getActivity(), BuyDetailActivity.class);
                        intent1.putExtra(BuyDetailActivity.CONTENT_DATA, data.feedContentData);
                        getActivity().startActivityForResult(intent1, BuyDetailActivity.REQUEST_CODE);
                    }
                }
                break;
            case Constant.COUPON_LISTING:
                startActivityForResult(
                        new Intent(getActivity(), RedeemCoinsActivity.class)
                                .putExtra(KEY_WALLET_BALANCE, "0"),
                        REQUEST_CODE
                );
                break;
            case Constant.CHANNEL:
                ((HomeActivity) getActivity()).showChannelExpandedView(data.feedContentData, 1, data.ID, Analyticals.CONTEXT_CHANNEL);
                break;
            case Constant.LEADERBOARD:
                startActivity(new Intent(getActivity(), LeaderBoardActivity.class));
                break;
            default:
                break;

        }
    }


    /************************ PAGINATION METHODS -- START **********************/

    public void initializePagination() {
        arrayNotificationList.clear();
        adapter.notifyDataSetChanged();
        notificationUrl = Url.GET_NOTIFICATION + "&page_no=";
        customLoadingListItem = false;

        if (paginate != null) {
            paginate.unbind();
        }
        isLoading = false;
        isLastPage = false;
        currentPage = 0;

        loading = false;
        page = 0;
        handler = new Handler();
        handler.removeCallbacks(fakeCallback);
        paginate = Paginate.with(rvNotification, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(customLoadingListItem ? new CustomLoadingListItemCreator() : null)
                .setLoadingListItemSpanSizeLookup(() -> GRID_SPAN)
                .build();
    }

    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            //loading = true;
            if (!loading) {
                handler.postDelayed(fakeCallback, networkDelay);
            }

        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            return isLoading/*loading*/; // Return boolean weather data is already loading or not
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            return isLastPage;
            //return nextPageNo == 0; // If all pages are loaded return true
        }
    };


    private Runnable fakeCallback = new Runnable() {
        @Override
        public void run() {

            if (page > 0) {
                notificationUrl = notificationUrl;

            }
            if (nextPageNo != 0) {
                getNotificationAPI(nextPageNo);
            }

        }
    };


    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvLoading.setText(String.format(getString(R.string.total_items_loaded), adapter.getItemCount()));

            // This is how you can make full span if you are using StaggeredGridLayoutManager
            if (rvNotification.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) vh.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvLoading;

        public VH(View itemView) {
            super(itemView);
            tvLoading = itemView.findViewById(R.id.tv_loading_text);
        }
    }

    /************************ PAGINATION METHODS -- END **********************/

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, (AppCompatActivity) getActivity(), new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getNotificationAPI(nextPageNo);
                }
            }
        });
    }

    boolean isLoading, isLastPage;
    int currentPage;

    /**
     * Call Notification API
     *
     * @param nextPageNo
     */
    private void getNotificationAPI(int nextPageNo) {
        if (isLoading || isLastPage) return;
        isLoading = true;
        currentPage++;
        Map<String, String> params = new HashMap<>();
        Log.d("notification", notificationUrl);
        APIRequest apiRequest = new APIRequest(notificationUrl + currentPage, Request.Method.GET, params, null, getContext());
        apiRequest.showLoader = false;
        APIManager.request(apiRequest, new APIResponse() {
            @Override
            public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                isLoading = false;
                loading = false;
                progressBar.setVisibility(View.GONE);
                rvNotification.setVisibility(View.VISIBLE);
                handleNotificationResponce(response, error, statusCode);
            }
        });
    }

    private void handleNotificationResponce(String response, Exception error, int statusCode) {
        try {

            if (error != null) {
                // showError(error.getMessage());
                updateEmptyState(error.getMessage());
                isLastPage = true;
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                        isLastPage = true;
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonArrayMsg = jsonObjectResponse.getJSONObject("msg");
                        JSONArray jsonArrayContents = jsonArrayMsg.getJSONArray("contents");
                        int nextPage = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : currentPage;
                        isLastPage = nextPage == currentPage;
                        nextPageNo = jsonArrayMsg.has("next_page") ? jsonArrayMsg.getInt("next_page") : 0;
                        for (int i = 0; i < jsonArrayContents.length(); i++) {
                            NotificationData notificationData = new NotificationData(jsonArrayContents.getJSONObject(i));
                            arrayNotificationList.add(notificationData);
                        }
                        if (jsonArrayContents.length() < 10) {
                            isLastPage = true;
                        }
                        adapter.notifyDataSetChanged();
                        loading = false;
                        page++;
                    }
                    updateEmptyState(null);
                }
            }

        } catch (JSONException e) {
            updateEmptyState(e.getMessage());
        }


    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayNotificationList.isEmpty()) {
                emptyState.setImgAndMsg("No Data Found.", null);
                rvNotification.setVisibility(View.INVISIBLE);
            } else {
                rvNotification.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvNotification.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }

    private void readNotification(NotificationData data, int pos) {
        if (data != null) {
            APIRequest apiRequest = new APIRequest(Url.READ_NOTIFICATION + "&message_id=" + data.meassageId, Request.Method.GET, null, null, getActivity());
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    if (arrayNotificationList.size() > pos) {
                        arrayNotificationList.remove(pos);
                    }
                    adapter.notifyDataSetChanged();
                    if (arrayNotificationList.size() == 0) {
                        updateEmptyState(null);
                    }
                }
            });
        }
    }
}
