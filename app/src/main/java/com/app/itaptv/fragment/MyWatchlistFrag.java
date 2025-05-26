package com.app.itaptv.fragment;

/*
public class MyWatchlistFrag extends Fragment {

    private HomeActivity activity;
    WatchListAdaptor watchListAdaptor = null;
    public static String CONTENT_DATA = "contentData";
    public static int REQUEST_CODE = 2;
    public static String KEY_ACTION_TYPE = "actionType";
    public static String KEY_CONTENT_DATA = "contentData";
    public static final String VIEW_PURCHASES = "viewPurchases";
    public static final String PLAY_NOW = "playNow";
    private View view;

    ArrayList<FeedContentData> watchList = new ArrayList<FeedContentData>();

    private RecyclerView rvWatchList;
    static private int currentPageIndex = 1;
    private boolean isNetworkBussy = false;
    private boolean noMorePages = false;
    private int lastVisibleIndex = 0;
    LinearLayoutManager layoutManager;

    ProgressBar progressBar;
    EmptyStateManager emptyState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_watch_list, container, false);
        init();
        return view;
    }

    public static MyWatchlistFrag newInstance() {
        return new MyWatchlistFrag();
    }

    private void init() {
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setCustomizedTitle(0);
        ((HomeActivity) getActivity()).setToolbarTitle("My Watchlist");
        rvWatchList = view.findViewById(R.id.rvMyWatchList);
        layoutManager = new LinearLayoutManager(requireContext());
        setUpEmptyState();
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        rvWatchList.setVisibility(View.INVISIBLE);

        rvWatchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleIndex - 2 == watchList.size() - 3 && !isNetworkBussy) {
                    isNetworkBussy = true;
                    if (!noMorePages) {
                        loadWatchList();
                    }
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        loadWatchList();
    }

    private void loadWatchList() {
        Log.e("avinash", "loadWatchList postId = ");
        try {
            Map<String, String> params = new HashMap<>();
            String url = Url.GET_WATCHLIST + "&page_no=" + String.valueOf(currentPageIndex);
            Log.e("avinash", "loadWatchList URL = " + url);
            APIRequest apiRequest = new APIRequest(url, Request.Method.GET, null, null, requireContext());
            apiRequest.showLoader = false;
            isNetworkBussy = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    isNetworkBussy = false;
                    JSONObject jsonObjectResponse = null;
                    progressBar.setVisibility(View.GONE);
                    rvWatchList.setVisibility(View.VISIBLE);
                    try {
                        if (error != null) {
                            // showError(error.getMessage());
                            updateEmptyState(error.getMessage());
                        } else {
                            if (response != null) {
                                jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    Utility.showError(message, requireContext());
                                } else if (type.equalsIgnoreCase("ok")) {

                                    JSONArray itemList = jsonObjectResponse.getJSONObject("msg").getJSONArray("contents");
                                    if (itemList.length() > 0) {
                                        for (int i = 0; i < itemList.length(); i++) {
                                            FeedContentData feedContentData = new FeedContentData(itemList.getJSONObject(i), -1);
                                            watchList.add(feedContentData);
                                            Log.e("avinash", "RESPONSE " + i + " = " + String.valueOf(itemList.get(i)));
                                        }
                                        Log.e("avinash", "loadWatchList Response count = " + String.valueOf(watchList.size()));
                                        rvWatchList.setLayoutManager(layoutManager);
                                        if (watchListAdaptor == null) {
                                            watchListAdaptor = new WatchListAdaptor(watchList, requireContext());
                                            rvWatchList.setAdapter(watchListAdaptor);
                                        } else {
                                            watchListAdaptor.updateWatchListAdaptor(watchList);
                                            watchListAdaptor.notifyDataSetChanged();
                                            rvWatchList.scrollToPosition(lastVisibleIndex + 1);
                                        }
                                        if (jsonObjectResponse.getJSONObject("msg").getInt("next_page") != 0) {
                                            currentPageIndex = currentPageIndex + 1;
                                        } else {
                                            noMorePages = true;
                                        }
                                        updateEmptyState(null);
                                    } else {
                                        updateEmptyState(null);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        updateEmptyState(error.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            isNetworkBussy = false;
            Utility.showError("Failed to comment.", requireContext());
        }
    }

    */
/**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     *//*

    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
    }

    */
/**
     * Initialization of Empty State
     *//*

    private void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInFragment(view, activity, new EmptyStateManager.ActionBtnCallBack() {
            @Override
            public void onBtnClick(String action) {
                if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                    getLiveListingData(0);
                }
            }
        });
    }

    */
/**
     * Update of Empty State
     *//*

    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListLiveContent.isEmpty()) {
                emptyState.setImgAndMsg("No Content Available", null);
                rvLiveRecycler.setVisibility(View.INVISIBLE);
            } else {
                rvLiveRecycler.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            rvLiveRecycler.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(getContext())) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }
}
*/
