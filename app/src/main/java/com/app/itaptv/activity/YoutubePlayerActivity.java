package com.app.itaptv.activity;

//public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.PlaylistEventListener {
//
//    private static final int RECOVERY_REQUEST = 1;
//    private YouTubePlayerView youTubeView;
//
//    public static String KEY_YOUTUBE_START_INDEX = "youTubeStartIndex";
//
//    private List<String> playList = new ArrayList<>();
//    private int currentIndex = 0;
//
//    private @Nullable
//    YouTubePlayer mYouTubePlayer;
//
//    private boolean isFullScreen = false;
//
//    //title layout
//    TextView tvTitle, tvSubtitle, tvLikeMusic, tvAddToWatchlist, tvCommentMusic, tvDownload;
//    FeedContentData feedContentPlayerData;
//    RelativeLayout rlTopTitleLayout;
//    //for questionLayout
//    ArrayList<QuestionData> arrayListQuestionData = new ArrayList<>();
//    RelativeLayout questionHolder;
//    LinearLayout llQuestion;
//    RelativeLayout rlRightAnswer;
//    RelativeLayout rlWrongAnswer;
//    RelativeLayout rlShopNow;
//    TextView tvWonCoins;
//    TextView tvWonCoin;
//    LinearLayout llLoader;
//    int totalCoinsOfQuestion = 0;
//    String questionTotalCoins;
//    Thread thread;
//    int totalNoOfQuestions;
//    TextView txtQuestion;
//    TextView tvQuestionPoints;
//    ArrayList<Button> ArrayOfButton;
//    Button btOption1;
//    Button btOption2;
//    Button btOption3;
//    Button btOption4;
//    int questionNo = 0;
//    int questionIndex = 0;
//
//    private static final String TAG = "YoutubePlayerActivity";
//
//    /* TODO: - Important things to do to avoid conflict between YouTube Player and Exo Player
//    1. Broadcast event when starting stream of YouTube content.
//    - Any ongoing ExoPlayer / YouTube player will be stopped and released.
//
//    2. Listen to ExoPlayer stream start event.
//    - Any ongoing YouTube player will be stopped and released.
//     */
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_youtube);
//        generatePlayList();
//        getExtras();
//        init();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setData();
//            rlTopTitleLayout.setVisibility(View.VISIBLE);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setNoQuestionLayout();
//            rlTopTitleLayout.setVisibility(View.GONE);
//        }
//    }
//
//    private void generatePlayList() {
//        if (YouTubeDataSource.getInstance().feedData != null &&
//                !YouTubeDataSource.getInstance().feedData.arrayListFeedContent.isEmpty() &&
//                YouTubeDataSource.getInstance().feedData.contentType
//                        .equalsIgnoreCase(FeedContentData.CONTENT_TYPE_YOUTUBE)) {
//            for (FeedContentData feedContentData : YouTubeDataSource.getInstance().feedData.arrayListFeedContent) {
//                // Check if type is 3. 3 stands for YouTube.
//                if (feedContentData.ptype == 3 &&
//                        feedContentData.url != null &&
//                        !feedContentData.url.isEmpty()) {
//                    playList.add(feedContentData.url);
//                }
//            }
//        }
//        if (!playList.isEmpty()) {
//            initialiseYouTubePlayer();
//        } else {
//            AlertUtils.showToast("No items were added to the play list.", 1, this);
//        }
//    }
//
//    private void getExtras() {
//        if (getIntent() != null) {
//            if (getIntent().hasExtra(KEY_YOUTUBE_START_INDEX)) {
//                currentIndex = getIntent().getIntExtra(KEY_YOUTUBE_START_INDEX, 0);
//                setFeedContentPlayerData();
//            }
//        }
//    }
//
//    private void setFeedContentPlayerData() {
//        if (YouTubeDataSource.getInstance().feedData != null) {
//            for (FeedContentData feedContentData : YouTubeDataSource.getInstance().feedData.arrayListFeedContent) {
//                // Get the correct object as per the current playing it from the playlist.
//                String url = playList.get(currentIndex);
//                if (feedContentData.url.equals(url)) {
//                    feedContentPlayerData = feedContentData;
//                }
//            }
//        }
//    }
//
//    private void initialiseYouTubePlayer() {
//        youTubeView = findViewById(R.id.youtube_view);
//        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
//    }
//
//    private void init() {
//        // for title layout
//        rlTopTitleLayout = findViewById(R.id.rlTopTitleLayout);
//        tvTitle = findViewById(R.id.tvStoreName);
//        tvSubtitle = findViewById(R.id.tvSubtitle);
//        tvLikeMusic = findViewById(R.id.tvLikeMusic);
//        tvCommentMusic = findViewById(R.id.tvCommentMusic);
//        tvDownload = findViewById(R.id.tvDownload);
//        tvAddToWatchlist = findViewById(R.id.tvAddtoWatchlist);
//        tvAddToWatchlist.setOnClickListener(view -> addToWatchList());
//        tvCommentMusic.setOnClickListener(view -> {
//            Intent intent = new Intent(YoutubePlayerActivity.this, CommentListActivity.class);
//            intent.putExtra("postId", feedContentPlayerData.postId);
//            intent.putExtra("postTitle", feedContentPlayerData.postTitle);
//            startActivityForResult(intent, CommentListActivity.REQUEST_CODE);
//        });
//        // for questionLayout
//        questionHolder = findViewById(R.id.rlQuestions);
//        rlRightAnswer = findViewById(R.id.rlRightAnswer);
//        rlWrongAnswer = findViewById(R.id.rlWrongAnswer);
//        rlShopNow = findViewById(R.id.rlShopnow);
//        llQuestion = findViewById(R.id.llQuestion);
//        tvWonCoins = findViewById(R.id.tvWonCoins);
//        tvWonCoin = findViewById(R.id.tvWonCoin);
//        llLoader = findViewById(R.id.llLoader);
//        btOption1 = findViewById(R.id.btOption1);
//        btOption2 = findViewById(R.id.btOption2);
//        btOption3 = findViewById(R.id.btOption3);
//        btOption4 = findViewById(R.id.btOption4);
//        ArrayOfButton = new ArrayList<>();
//        ArrayOfButton.add(btOption1);
//        ArrayOfButton.add(btOption2);
//        ArrayOfButton.add(btOption3);
//        ArrayOfButton.add(btOption4);
//        setData();
//    }
//
//    private void setData() {
//        if (feedContentPlayerData != null) {
//            tvTitle.setText(feedContentPlayerData.postTitle);
//            tvAddToWatchlist.setTag(feedContentPlayerData.postId);
//            tvLikeMusic.setTag(feedContentPlayerData.postId);
//            tvDownload.setText(String.format(getResources().getString(R.string.label_button_download_name), ""));
//            boolean isLiked = LocalStorage.isLiked(Integer.parseInt(feedContentPlayerData.postId));
//            if (isLiked) {
//                LikeData likeData = LocalStorage.getLikes(Integer.parseInt(feedContentPlayerData.postId));
//                tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), likeData.getCount()));
//                setLikeMusicImage(likeData.isLike());
//            } else {
//                tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), feedContentPlayerData.likesCount));
//                setLikeMusicImage(feedContentPlayerData.likeByMe);
//            }
//            boolean isCommented = LocalStorage.isCommented(Integer.parseInt(feedContentPlayerData.postId));
//            if (isCommented) {
//                LikeData likeData = LocalStorage.getComments(Integer.parseInt(feedContentPlayerData.postId));
//                tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), likeData.getCount()));
//
//            } else {
//                tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), String.valueOf(feedContentPlayerData.commentCount)));
//            }
//            tvAddToWatchlist.setTag(feedContentPlayerData.postId);
//            boolean isAddedToWatchList = LocalStorage.isAddedToWishList(Integer.parseInt(feedContentPlayerData.postId), getApplicationContext());
//
//            if (isAddedToWatchList) {
//                setWishImage(isAddedToWatchList);
//            } else {
//                setWishImage(feedContentPlayerData.iswatchlisted);
//            }
//
//            //---------------------- Set Questions -----------------------
//            setNoQuestionLayout();
//            getSongQuestionAPI(feedContentPlayerData.postId);
//        }
//    }
//
//
//    //Youtube callbacks
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//        mYouTubePlayer = youTubePlayer;
//        if (!wasRestored && !playList.isEmpty()) {
//            mYouTubePlayer.setPlaylistEventListener(this);
//            mYouTubePlayer.setFullscreen(false);
//            mYouTubePlayer.setOnFullscreenListener(b -> isFullScreen = b);
//            mYouTubePlayer.loadVideos(playList, currentIndex, 0);
//            mYouTubePlayer.play();
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
//        } else {
//            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
//            AlertUtils.showToast(error, 1, this);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RECOVERY_REQUEST) {
//            // Retry initialization if user performed a recovery action
//            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
//        }
//
//        if (requestCode == CommentListActivity.REQUEST_CODE) {
//            if (data != null) {
//                tvCommentMusic.setText(String.format(getResources().getString(R.string.label_button_comment_no), data.getStringExtra("comment_count")));
//                LocalStorage.setCommented(Integer.parseInt(data.getStringExtra("song_id")), data.getStringExtra("comment_count"), true);
//            }
//        }
//    }
//
//    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
//        return youTubeView;
//    }
//
//    @Override
//    public void onLoading() {
//    }
//
//    @Override
//    public void onLoaded(String s) {
//    }
//
//    @Override
//    public void onAdStarted() {
//        // May require in future to check if user watched the ad.
//    }
//
//    @Override
//    public void onVideoStarted() {
//    }
//
//    @Override
//    public void onVideoEnded() {
//    }
//
//    @Override
//    public void onError(YouTubePlayer.ErrorReason errorReason) {
//    }
//
//    @Override
//    public void onPrevious() {
//        currentIndex--;
//        setFeedContentPlayerData();
//        setData();
//    }
//
//    @Override
//    public void onNext() {
//        currentIndex++;
//        setFeedContentPlayerData();
//        setData();
//    }
//
//    @Override
//    public void onPlaylistEnded() {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mYouTubePlayer != null && isFullScreen) {
//            isFullScreen = false;
//            mYouTubePlayer.setFullscreen(false);
//            return;
//        }
//        if (mYouTubePlayer != null && mYouTubePlayer.isPlaying()) {
//            mYouTubePlayer.pause();
//            return;
//        }
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        YouTubeDataSource.getInstance().feedData = null;
//    }
//
//    // Title layout methods
//    public void setLikeMusic(View view) {
//        try {
//            Map<String, String> params = new HashMap<>();
//            params.put("post_id", view.getTag().toString());
//            APIRequest apiRequest = new APIRequest(Url.LIKES, Request.Method.POST, params, null, this);
//            apiRequest.showLoader = false;
//            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleLikeResponse(response, error, view.getTag().toString()));
//        } catch (Exception ignored) {
//        }
//    }
//
//    public void setCommentMusic(View view) {
//        Intent intent = new Intent(YoutubePlayerActivity.this, CommentListActivity.class);
//        intent.putExtra("postId", feedContentPlayerData.postId);
//        intent.putExtra("postTitle", feedContentPlayerData.postTitle);
//        startActivityForResult(intent, CommentListActivity.REQUEST_CODE);
//    }
//
//    public void shareData(View view) {
//        String feedImage = feedContentPlayerData.imgUrl.equals("") ? feedContentPlayerData.playlistImageUrl.equals("") ? "" : feedContentPlayerData.playlistImageUrl : feedContentPlayerData.imgUrl;
//        if (!feedImage.equals("")) {
//            Picasso.get().load(feedImage).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.putExtra(Intent.EXTRA_TITLE, "iTap");
//                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, view.getContext()));
//                    i.putExtra(Intent.EXTRA_TEXT, "iTap \n" + "https://drive.google.com/open?id=1GHbJyc86i0rskN4w9nM6Wwc_zqFm8LvQ");
//                    i.setType("*/*");
//                    startActivity(Intent.createChooser(i, "Share Image"));
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                }
//            });
//        }
//    }
//
//    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
//        Uri bmpUri = null;
//        try {
//            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
//            FileOutputStream out = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.close();
//            //bmpUri = Uri.fromFile(file);
//            bmpUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bmpUri;
//    }
//
//    private void handleLikeResponse(String response, Exception error, String s) {
//        try {
//            if (error != null) {
//                //  TODO: Handle error state (Empty State)
//            } else {
//                JSONObject jsonObject = new JSONObject(response);
//                String type = jsonObject.getString("type");
//                if (type.equals("OK")) {
//                    JSONObject msg = jsonObject.getJSONObject("msg");
//                    tvLikeMusic.setText(String.format(getResources().getString(R.string.label_button_like_no), msg.getString("no_likes")));
//                    setLikeMusicImage(msg.getBoolean("liked"));
//                    LocalStorage.setLiked(Integer.parseInt(s), msg.getString("no_likes"), msg.getBoolean("liked"));
//                }
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//    /**
//     * Set Music Like unlike Image
//     *
//     * @param likeByMe :Ture or False
//     */
//    private void setLikeMusicImage(boolean likeByMe) {
//        if (likeByMe) {
//            tvLikeMusic.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_likes_fill_assent, 0, 0);
//        } else {
//            tvLikeMusic.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_likes_fill, 0, 0);
//        }
//    }
//
//    private void addToWatchList() {
//        String ID = tvAddToWatchlist.getTag().toString();
//        Log.e("avinash", "addToWatchList postId = " + feedContentPlayerData.postId);
//        if (!feedContentPlayerData.postId.isEmpty()) {
//            try {
//                Map<String, String> params = new HashMap<>();
//                Log.e("avinash", params.toString());
//                APIRequest apiRequest = new APIRequest(Url.ADD_REMOVE_WATCHLIST + ID, Request.Method.GET, params, null, this);
//                apiRequest.showLoader = false;
//                APIManager.request(apiRequest, new APIResponse() {
//                    @Override
//                    public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
//                        JSONObject jsonObjectResponse = null;
//                        try {
//                            jsonObjectResponse = new JSONObject(response);
//                            Log.e("avinash", String.valueOf(jsonObjectResponse));
//                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
//                            if (type.equalsIgnoreCase("error")) {
//                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
//                                Toast.makeText(YoutubePlayerActivity.this, message, Toast.LENGTH_LONG).show();
//                            } else if (type.equalsIgnoreCase("ok")) {
//                                JSONObject msg = jsonObjectResponse.getJSONObject("msg");
//                                setWishImage(msg.getBoolean("is_added"));
//                                if (msg.getBoolean("is_added")) {
//                                    Toast.makeText(YoutubePlayerActivity.this, "Added To Watchlist.", Toast.LENGTH_LONG).show();
//                                } else {
//                                    Toast.makeText(YoutubePlayerActivity.this, "Removed From Watchlist.", Toast.LENGTH_LONG).show();
//                                }
//                                LocalStorage.setWishedItem(Integer.parseInt(ID), msg.getBoolean("is_added"), getApplicationContext());
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                Utility.showError("Failed to comment.", YoutubePlayerActivity.this);
//            }
//        }
//    }
//
//    private void setWishImage(boolean isWished) {
//        if (isWished) {
//            tvAddToWatchlist.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.blue_tick, 0, 0);
//        } else {
//            tvAddToWatchlist.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add_black, 0, 0);
//        }
//    }
//
//    // QuestionLayout
//    public void shopNow(View view) throws JSONException {
//        if (view instanceof ImageView) {
//            finish();
//            HomeFragment.tlHomeTabs.getTabAt(2).select();
//        }
//    }
//
//    private void setNoQuestionLayout() {
//        questionHolder.setVisibility(View.GONE);
//        rlWrongAnswer.setVisibility(View.GONE);
//        rlRightAnswer.setVisibility(View.GONE);
//        rlShopNow.setVisibility(View.GONE);
//    }
//
//    private void setRightAnswerLayout(long coins) {
//        tvWonCoins.setText(String.format(getString(R.string.text_coin), String.valueOf(coins)));
//        totalCoinsOfQuestion = totalCoinsOfQuestion + (int) coins;
//        llQuestion.setVisibility(View.GONE);
//        rlWrongAnswer.setVisibility(View.GONE);
//        rlRightAnswer.setVisibility(View.VISIBLE);
//        rlShopNow.setVisibility(View.GONE);
//    }
//
//    private void setRightAnswerWonLayout(String questiontotalcoins) {
//        questiontotalcoins = questiontotalcoins.equalsIgnoreCase("") || questiontotalcoins.equalsIgnoreCase(null) || questiontotalcoins.equalsIgnoreCase("null") ? "0" : questiontotalcoins;
//        tvWonCoin.setText(String.format(getString(R.string.text_coin), questiontotalcoins));
//        questionHolder.setVisibility(View.VISIBLE);
//        llQuestion.setVisibility(View.GONE);
//        rlWrongAnswer.setVisibility(View.GONE);
//        rlRightAnswer.setVisibility(View.GONE);
//        rlShopNow.setVisibility(View.VISIBLE);
//    }
//
//    private void setWrongAnswerLayout() {
//        llQuestion.setVisibility(View.GONE);
//        rlWrongAnswer.setVisibility(View.VISIBLE);
//        rlRightAnswer.setVisibility(View.GONE);
//        rlShopNow.setVisibility(View.GONE);
//    }
//
//    private void setQuestionLayout() {
//        questionHolder.setVisibility(View.VISIBLE);
//        llQuestion.setVisibility(View.VISIBLE);
//        rlWrongAnswer.setVisibility(View.GONE);
//        rlRightAnswer.setVisibility(View.GONE);
//        rlShopNow.setVisibility(View.GONE);
//        llLoader.setVisibility(View.GONE);
//    }
//
//    private void getSongQuestionAPI(String ID) {
//        try {
//            Map<String, String> params = new HashMap<>();
//            APIRequest apiRequest = new APIRequest(Url.GET_QUESTIONS + ID, Request.Method.GET, params, null, this);
//            apiRequest.showLoader = false;
//            APIManager.request(apiRequest, new APIResponse() {
//                @Override
//                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
//                    handleSongQuestionResponse(response, error);
//                }
//            });
//        } catch (Exception ignored) {
//        }
//    }
//
//    private void handleSongQuestionResponse(String response, Exception error) {
//        try {
//            if (error != null) {
//                setNoQuestionLayout();
//            } else {
//                JSONObject jsonObject = new JSONObject(response);
//                String type = jsonObject.getString("type");
//                if (type.equals("OK")) {
//                    JSONObject msg = jsonObject.getJSONObject("msg");
//                    JSONArray questions = msg.getJSONArray("questions");
//                    questionTotalCoins = msg.getString("total_coins");
//                    if (questions.length() == 0 && questionTotalCoins.equalsIgnoreCase("0")) {
//                        setNoQuestionLayout();
//
//                    } else if (questions.length() == 0 && !questionTotalCoins.equalsIgnoreCase("0")) {
//                        setRightAnswerWonLayout(questionTotalCoins);
//                    } else {
//                        for (int i = 0; i < questions.length(); i++) {
//                            JSONObject object = questions.getJSONObject(i);
//                            QuestionData questionData = new QuestionData(object);
//                            arrayListQuestionData.add(questionData);
//                        }
//                        setEmbeddedGameQuestion();
//                    }
//                }
//            }
//        } catch (Exception ignored) {
//            Log.e(TAG, ignored.toString());
//        }
//    }
//
//    private void setEmbeddedGameQuestion() {
//        totalNoOfQuestions = arrayListQuestionData.size();
//        if (arrayListQuestionData.size() > 0) {
//            QuestionData questionData = arrayListQuestionData.get(questionIndex);
//            if (txtQuestion == null) txtQuestion = findViewById(R.id.txtQuestion);
//            if (tvQuestionPoints == null)
//                tvQuestionPoints = findViewById(R.id.tvQuestionPoints);
//            txtQuestion.setText(questionData.postTitle);
//            tvQuestionPoints.setText(String.format(getString(R.string.text_coin), questionData.points));
//
//            new Thread(() -> {
//                try {
//                    for (int p = 0; p < questionData.choices.size(); p++) {
//                        QuestionChoiceData questionChoiceData = questionData.choices.get(p);
//                        ArrayOfButton.get(p).setText(questionChoiceData.text);
//                        ArrayOfButton.get(p).setTag(questionChoiceData.correct);
//                        ArrayOfButton.get(p).setId(questionData.id);
//                        ArrayOfButton.get(p).setContentDescription(questionData.points);
//                        LinearLayout linearLayout = (LinearLayout) ArrayOfButton.get(p).getParent();
//                        linearLayout.setBackground(getDrawable(R.drawable.bg_lightgray));
//                        ViewGroup view = (ViewGroup) ArrayOfButton.get(p).getParent();
//                        view.setId(Integer.parseInt(feedContentPlayerData.postId));
//                        view.setTag(feedContentPlayerData.postTitle);
//                    }
//                    new Handler(Looper.getMainLooper()).post(() -> setQuestionLayout());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        } else {
//            setNoQuestionLayout();
//        }
//    }
//
//    public void checkOption(View view) throws JSONException {
//        if (view instanceof Button) {
//            Button button = (Button) view;
//            questionNo++;
//            LinearLayout linearLayout = (LinearLayout) button.getParent();
//            Utility.setShadeBackground(linearLayout);
//            boolean isCorrectAns = button.getTag().equals("") ? false : (boolean) button.getTag();
//            ViewGroup viewGroup = (ViewGroup) button.getParent();
//            JSONObject answer = new JSONObject().put("answer", button.getText().toString());
//            String context_id = String.valueOf(viewGroup.getId());
//
//            Analyticals.CallplayedQuestionApi(YoutubePlayerActivity.this, Analyticals.AUDIO_SONG_QUESTION_ACTIVITY_TYPE, String.valueOf(button.getId()), Analyticals.CONTEXT_AUDIO_SONG, context_id, answer, (success, activity_id, error) -> {
//                if (success) {
//                    addWalletCoins(view);
//                } else {
//                    llLoader.setVisibility(View.GONE);
//                    setWrongAnswerLayout();
//                }
//            });
//        }
//    }
//
//    public void tryAnotherQuestion(View view) {
//        if (thread != null && thread.isAlive()) thread.interrupt();
//        if (totalNoOfQuestions == questionNo) {
//            setRightAnswerWonLayout(String.valueOf(totalCoinsOfQuestion));
//        } else {
//            startNextQuestionUIThread();
//        }
//    }
//
//    private void startNextQuestionUIThread() {
//        thread = new Thread() {
//            @Override
//            public void run() {
//                // After sleep finished blocking, create a Runnable to run on the UI Thread.
//                runOnUiThread(() -> {
//                    llLoader.setVisibility(View.VISIBLE);
//                    questionIndex++;
//                    if (totalNoOfQuestions > questionIndex) {
//                        setEmbeddedGameQuestion();
//                    } else {
//                        llLoader.setVisibility(View.GONE);
//                    }
//                });
//            }
//        };
//        // Don't forget to start the thread.
//        thread.start();
//    }
//
//    //------------------------- App42 APIs START -------------------------------------------------------
//    public void addWalletCoins(View view) {
//        if (view instanceof Button) {
//            Button button = (Button) view;
//            boolean isCorrectAns = (boolean) button.getTag();
//            int questionId = button.getId();
//
//            if (isCorrectAns) {
//                llLoader.setVisibility(View.VISIBLE);
//                ViewGroup viewGroup = (ViewGroup) button.getParent();
//                String contentDescription = button.getContentDescription().toString();
//                String contentId = String.valueOf(viewGroup.getId());
//                String description = viewGroup.getTag().toString();
//                long coins = contentDescription.equals("") || contentDescription.equals("null") ? 0 : Long.parseLong(contentDescription);
//
//                Wallet.earnCoins(this, questionId, description, Wallet.FLAG_QUESTION, coins, new WalletCallback() {
//                    @Override
//                    public boolean onResult(boolean success, @Nullable String error, long coins, JSONArray historyData, int historyCount) {
//                        llLoader.setVisibility(View.GONE);
//                        handleApp42Response(success, error, questionId, coins);
//                        return success;
//                    }
//                });
//            } else {
//                llLoader.setVisibility(View.GONE);
//                setWrongAnswerLayout();
//            }
//        }
//    }
//
//    /**
//     * Handle earn coin response
//     *
//     * @param success    True if coins added successfully else false
//     * @param error      Error message in case of error
//     * @param questionId Question or content id
//     * @param coins      Number of coins earned
//     */
//    private void handleApp42Response(boolean success, @Nullable String error, int questionId, long coins) {
//        if (success) {
//            setRightAnswerLayout(coins);
//        } else {
//            String errorMessage = error == null ? Wallet.GENERIC_ERROR_MESSAGE : error;
//            AlertUtils.showAlert("Error", errorMessage, null, YoutubePlayerActivity.this, null);
//        }
//    }
//
//}
