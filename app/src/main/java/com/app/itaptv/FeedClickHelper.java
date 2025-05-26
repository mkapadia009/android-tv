package com.app.itaptv;

import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_AD;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_BUY;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_CHANNEL;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_CONTENT;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_GAME;
import static com.app.itaptv.structure.FeedCombinedData.FEED_TYPE_PROMOTION;
import static com.app.itaptv.structure.FeedCombinedData.NORMAL;
import static com.app.itaptv.structure.FeedCombinedData.SUBSCRIPTION_TAB;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_AD;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_EPISODE;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_ORIGINALS;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_PLAYLIST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_POST;
import static com.app.itaptv.structure.FeedContentData.POST_TYPE_STREAM;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.GameStartActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.activity.LuckyWinnerActivity;
import com.app.itaptv.activity.PremiumActivity;
import com.app.itaptv.activity.WebViewActivity;
import com.app.itaptv.fragment.ViewAllPlayFragment;
import com.app.itaptv.structure.AdFieldsData;
import com.app.itaptv.structure.FeedCombinedData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.GameData;
import com.app.itaptv.structure.HomeSliderObject;
import com.app.itaptv.structure.SubscriptionDetails;
import com.app.itaptv.tv_fragment.BuyDetailFragment;
import com.app.itaptv.tv_fragment.ViewAllTvFragment;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.Analyticals;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.GameDateValidation;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Utility;

import java.util.ArrayList;

public class FeedClickHelper {
    HomeActivity homeActivity = HomeActivity.getInstance();

    public void handleFeedItemClick(Context context, FeedCombinedData feedCombinedData, Object o, int position) {
        switch (feedCombinedData.feedType) {
            case FEED_TYPE_BUY:
                if (o instanceof FeedContentData) {
                    FeedContentData feedContentData = (FeedContentData) o;
                    if (feedContentData.feedContentType.equals("viewall")) {
                        viewAll(feedCombinedData);
                    } else {
                        //showAd(feedContentData.feedTabType);
                        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
                        if (sd != null && sd.allow_rental != null) {
                            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                                if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                                    /*Intent intent = new Intent(context, BuyDetailActivity.class);
                                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                                    ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                                    homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
                                    //showAd(sliderId);
                                } else {
                                    playMediaItems(feedContentData);
                                    //playItems(feedCombinedData.arrayListBuyFeedContent, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);

                                }
                            } else {
                               /* Intent intent = new Intent(context, BuyDetailActivity.class);
                                intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                                ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                                homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
                            }
                        }
                    }
                }
                break;

            case FEED_TYPE_CHANNEL:
                //FeedContentData feedContentData = (FeedContentData) o;
                //showAd(feedContentData.feedTabType);
                //setPlaybackData(feedContentData, i, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                break;
            case FEED_TYPE_CONTENT:
                FeedContentData feedContentData = (FeedContentData) o;
                if (feedContentData.feedContentType.equals("viewall")) {
                    viewAll(feedCombinedData);
                } else {
                    checkValidity(feedCombinedData, position, context);
                }
                break;
            case FEED_TYPE_PROMOTION:
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    if (homeSliderObject.feedContentData != null) {
                        if (homeSliderObject.feedContentData.arrowLinking != null && !homeSliderObject.feedContentData.arrowLinking.isEmpty()) {
                            switch (homeSliderObject.feedContentData.arrowLinking) {
                                case SUBSCRIPTION_TAB:
                                    ((Activity) context).startActivity(new Intent(context, PremiumActivity.class).putExtra("title", context.getResources().getString(R.string.label_premium)));
                                    break;
                                case NORMAL:
                                    setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id), context);
                                    break;
                                default:
                                    setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id), context);
                                    break;
                            }
                        } else {
                            setActionOnSlider(homeSliderObject.feedContentData, String.valueOf(homeSliderObject.id), context);
                        }
                    } else {
                        setActionOnPlaySlider(homeSliderObject.gameData, context);
                    }
                }
                break;
            case FEED_TYPE_GAME:
                if (o instanceof GameData) {
                    GameData gameData = (GameData) o;
                    switch (gameData.quizType) {
                        case GameData.QUIZE_TYPE_LIVE:
                            String invalidDateMessage = GameDateValidation.getInvalidDateMsg(context, gameData.start, gameData.end);
                            if (invalidDateMessage.equals("")) {
                                if (gameData.playedGame) {
                                    AlertUtils.showToast(context.getString(R.string.msg_game_played), Toast.LENGTH_SHORT, context);
                                    return;
                                }
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                                ((Activity) context).startActivityForResult(new Intent(context, GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                            } else {
                                if (gameData.winnersDeclared) {
                                    ((Activity) context).startActivity(new Intent(context, LuckyWinnerActivity.class).putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id));
                                    return;
                                }
                                AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, ((Activity) context));
                            }
                            break;

                        case GameData.QUIZE_TYPE_TURN_BASED: {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                            ((Activity) context).startActivityForResult(new Intent(context, GameStartActivity.class).putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                        }
                        break;
                        case GameData.QUIZE_TYPE_HUNTER_GAMES: {
                            Intent intent = new Intent(context, WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                            ((Activity) context).startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
                        }
                        break;
                    }
                }
                break;
            case FEED_TYPE_AD:
                if (o instanceof HomeSliderObject) {
                    HomeSliderObject homeSliderObject = (HomeSliderObject) o;
                    setActionOnSliderAd(homeSliderObject.feedContentData.adFieldsData, Constant.BANNER, String.valueOf(homeSliderObject.id), context);
                }
                break;
        }
    }

    private void playMediaItems(FeedContentData feedContentData) {
        if (feedContentData != null) {
            String id = feedContentData.postId;

            switch (feedContentData.postType) {
                case FeedContentData.POST_TYPE_ORIGINALS:
                    setSeriesData(id, "0", "0", "false");
                    break;
                case FeedContentData.POST_TYPE_SEASON:
                    setSeriesData(feedContentData.seriesId, feedContentData.seasonId, "0", "false");
                    break;
                case POST_TYPE_EPISODE:
                    homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.POST_TYPE_PLAYLIST:
                    setPlaylistData(id);
                    break;
                case FeedContentData.POST_TYPE_POST:
                    HomeActivity.getInstance().getSingleMediaItem(id);
                    break;
            }
        }
    }

    private void setSeriesData(String seriesId, String seasonId, String episodeId, String iswatched) {
        HomeActivity.getInstance().showSeriesExpandedView(seriesId, seasonId, episodeId, iswatched);
    }

    private void setPlaylistData(String playlistId) {
        HomeActivity.getInstance().showPlaylistExpandedView(playlistId, "false");
    }

    private void checkValidity(FeedCombinedData feedCombinedData, int position, Context context) {
        SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
        //ArrayList<String> purchasedList = LocalStorage.getPurchasedArrayList("savedPurchased", context);
        if (feedCombinedData.arrayListFeedContent.get(position).contentType.equalsIgnoreCase(Constant.eSportsTitle)) {
            homeActivity.getUserRegisteredTournamentInfo(feedCombinedData.arrayListFeedContent.get(position).postId);
        } else if (sd != null && sd.allow_rental != null) {
            if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                if (feedCombinedData.arrayListFeedContent.get(position).postType.equals(POST_TYPE_ORIGINALS)) {
                   /* Intent intent = new Intent(context, BuyDetailActivity.class);
                    intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedCombinedData.arrayListFeedContent.get(position));
                    intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                    ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                    homeActivity.openFragment(BuyDetailFragment.getInstance(feedCombinedData.arrayListFeedContent.get(position),"sliderPageKey"));
                    //showAd(sliderId);
                } else {
                    playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                }
            } else {
                if (feedCombinedData.arrayListFeedContent.get(position).postExcerpt.equalsIgnoreCase("free")) {
                    playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                } else if (feedCombinedData.arrayListFeedContent.get(position).postExcerpt.equalsIgnoreCase("paid")) {
                   /* if (feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.size() > 0) {
                        if (feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).costType.equalsIgnoreCase("coins")) {
                            if (purchasedList != null && !purchasedList.isEmpty()) {
                                //for (int i = 0; i < purchasedList.size(); i++) {
                                if (purchasedList.contains(feedCombinedData.arrayListFeedContent.get(position).postId)) {
                                    //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                                    homeActivity.updatePurchasedList(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                                } else {
                                    String coins = feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).finalCost;
                                    //showPurchaseDialog(getContext(), coins, feedCombinedData, position);
                                }
                                // }
                            } else {
                                String coins = feedCombinedData.arrayListFeedContent.get(position).arrayListPriceData.get(0).finalCost;
                                //showPurchaseDialog(getContext(), coins, feedCombinedData, position);
                            }
                        } else {
                            //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                            playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                        }
                    } else {
                        //homeActivity.playMedia(feedCombinedData.arrayListFeedContent.get(position));
                        playItems(feedCombinedData.arrayListFeedContent, position, String.valueOf(feedCombinedData.id), Analyticals.CONTEXT_FEED);
                    }*/
                    homeActivity.openFragment(BuyDetailFragment.getInstance(feedCombinedData.arrayListFeedContent.get(position),"sliderPageKey"));
                }
            }
        }
    }

    private void playItems(ArrayList<FeedContentData> itemsToPlay, int position, String feedId, String pageType) {
        if (homeActivity != null) {
            if (itemsToPlay.get(position).contentOrientation.equalsIgnoreCase("vertical") && !Utility.isTelevision()) {
                homeActivity.openVerticalPlayer(itemsToPlay, position);
            } else {
                switch (itemsToPlay.get(position).postType) {
                    case POST_TYPE_PLAYLIST:
                        homeActivity.showPlaylistExpandedView(itemsToPlay.get(position).postId, String.valueOf(itemsToPlay.get(position).iswatchlisted));
                        break;
                    case POST_TYPE_ORIGINALS:
                        //homeActivity.showSeriesExpandedView(itemsToPlay.get(position).postId, "0", "0", String.valueOf(itemsToPlay.get(position).iswatchlisted));
                        homeActivity.openFragment(BuyDetailFragment.getInstance(itemsToPlay.get(position),"sliderPageKey"));
                        break;
                    case POST_TYPE_EPISODE:
                        homeActivity.showSeriesExpandedView(itemsToPlay.get(position).seriesId, itemsToPlay.get(position).seasonId, itemsToPlay.get(position).episodeId, "");
                        break;
                    case POST_TYPE_STREAM:
                        homeActivity.showChannelExpandedView(itemsToPlay.get(position), 1, feedId, pageType);
                        break;
                    case POST_TYPE_POST:
                        //homeActivity.makePlaylist(itemsToPlay, position, feedId, pageType, "");
                        homeActivity.showAudioSongExpandedView(itemsToPlay, position, feedId, pageType, "");
                        break;
                    case POST_TYPE_AD:
                        //homeActivity.makePlaylist(itemsToPlay, position, feedId, pageType, "");
                        homeActivity.showAudioSongExpandedView(itemsToPlay, position, feedId, pageType, "");
                        break;
                }
            }
        }
    }

    public void setActionOnSlider(FeedContentData feedContentData, String feedId, Context context) {
        if (feedContentData.postExcerpt.equalsIgnoreCase("paid")) {
            // logic for buy/paid item
            SubscriptionDetails sd = LocalStorage.getUserSubscriptionDetails();
            if (sd != null && sd.allow_rental != null && !sd.allow_rental.equals("") && !feedContentData.isTrailer) {
                if (sd.allow_rental.equalsIgnoreCase(Constant.YES)) {
                    if (feedContentData.postType.equals(POST_TYPE_ORIGINALS)) {
                       /* Intent intent = new Intent(context, BuyDetailActivity.class);
                        intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                        intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                        ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                        homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
                        //showAd(sliderId);
                    } else {
                        ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                        arrayListFeedContentData.add(feedContentData);
                        //homeActivity.showAudioSongExpandedView(arrayListFeedContentData, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                        switch (feedContentData.contentType) {
                            // logic for free item
                            case FeedContentData.CONTENT_TYPE_URL:
                                String url = feedContentData.url;
                                if (!url.startsWith("http://") && !url.startsWith("https://"))
                                    url = "https://" + url;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                ((Activity) context).startActivity(browserIntent);
                                break;

                            case FeedContentData.CONTENT_TYPE_AUDIO_POST:
                                ArrayList<FeedContentData> arrayListFeedContentData1 = new ArrayList<>();
                                arrayListFeedContentData1.add(feedContentData);
                                //activity.playItems(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                                homeActivity.showAudioSongExpandedView(arrayListFeedContentData1, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                                break;
                            case FeedContentData.CONTENT_TYPE_ALL_M_ORIGINALS:
                                //homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                                homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
                                break;
                            case FeedContentData.CONTENT_TYPE_SEASON:
                            case FeedContentData.CONTENT_TYPE_EPISODE:
                                homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                                break;
                            case FeedContentData.CONTENT_TYPE_ALL_M_STREAM:
                                //playChannelAPI(feedContentData, 1, sliderId, Analyticals.CONTEXT_SLIDER);
                                homeActivity.showChannelExpandedView(feedContentData, 1, feedId, Analyticals.CONTEXT_CHANNEL);
                                break;
                            case FeedContentData.CONTENT_TYPE_PLAYLIST:
                                homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                                break;
                        }
                    }
                }
            } else {
                // logic for buy/paid item
               /* Intent intent = new Intent(context, BuyDetailActivity.class);
                intent.putExtra(BuyDetailActivity.CONTENT_DATA, feedContentData);
                intent.putExtra(BuyDetailActivity.FROM_SLIDER, "sliderPageKey");
                ((Activity) context).startActivityForResult(intent, BuyDetailActivity.REQUEST_CODE);*/
                homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
            }
        } else {
            switch (feedContentData.contentType) {
                // logic for free item
                case FeedContentData.CONTENT_TYPE_URL:
                    String url = feedContentData.url;
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "https://" + url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    ((Activity) context).startActivity(browserIntent);
                    break;

                case FeedContentData.CONTENT_TYPE_AUDIO_POST:
                    ArrayList<FeedContentData> arrayListFeedContentData = new ArrayList<>();
                    arrayListFeedContentData.add(feedContentData);
                    //activity.playItems(arrayListFeedContentData, 0, sliderId, Analyticals.CONTEXT_SLIDER, "");
                    homeActivity.showAudioSongExpandedView(arrayListFeedContentData, 0, feedId, Analyticals.CONTEXT_SLIDER, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_ORIGINALS:
                    //homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, String.valueOf(feedContentData.iswatchlisted));
                    homeActivity.openFragment(BuyDetailFragment.getInstance(feedContentData,"sliderPageKey"));
                    break;
                case FeedContentData.CONTENT_TYPE_SEASON:
                case FeedContentData.CONTENT_TYPE_EPISODE:
                    homeActivity.showSeriesExpandedView(feedContentData.seriesId, feedContentData.seasonId, feedContentData.episodeId, "");
                    break;
                case FeedContentData.CONTENT_TYPE_ALL_M_STREAM:
                    //playChannelAPI(feedContentData, 1, sliderId, Analyticals.CONTEXT_SLIDER);
                    homeActivity.showChannelExpandedView(feedContentData, 1, feedId, Analyticals.CONTEXT_CHANNEL);
                    break;
                case FeedContentData.CONTENT_TYPE_PLAYLIST:
                    homeActivity.showPlaylistExpandedView(feedContentData.postId, String.valueOf(feedContentData.iswatchlisted));
                    break;
                case FeedContentData.CONTENT_TYPE_ESPORTS:
                    homeActivity.getUserRegisteredTournamentInfo(feedContentData.postId);
                    break;
            }
        }
    }

    private void setActionOnPlaySlider(GameData gameData, Context context) {

        switch (gameData.quizType) {
            case GameData.QUIZE_TYPE_LIVE:
                String invalidDateMessage = GameDateValidation.getInvalidDateMsg(context, gameData.start, gameData.end);
                if (invalidDateMessage.equals("")) {
                    if (gameData.playedGame) {
                        AlertUtils.showToast(context.getString(R.string.msg_game_played), Toast.LENGTH_SHORT, context);
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                    ((Activity) context).startActivityForResult(new Intent(context, GameStartActivity.class)
                            .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                } else {
                    if (gameData.winnersDeclared) {
                        ((Activity) context).startActivity(
                                new Intent(context, LuckyWinnerActivity.class)
                                        .putExtra(LuckyWinnerActivity.KEY_GAME_ID, gameData.id)
                        );
                        return;
                    }
                    AlertUtils.showToast(invalidDateMessage, Toast.LENGTH_SHORT, context);
                }
                break;

            case GameData.QUIZE_TYPE_TURN_BASED:
                Bundle bundle = new Bundle();
                bundle.putParcelable(GameStartActivity.GAME_DATA, gameData);
                ((Activity) context).startActivityForResult(new Intent(context, GameStartActivity.class)
                        .putExtra("Bundle", bundle), GameData.REQUEST_CODE_GAME);
                break;

            case GameData.QUIZE_TYPE_HUNTER_GAMES:
                Intent intent = new Intent(context, WebViewActivity.class).putExtra(WebViewActivity.GAME_URL, gameData.webviewUrl).putExtra(WebViewActivity.GAME_TITLE, gameData.postTitle);
                ((Activity) context).startActivityForResult(intent, WebViewActivity.GAME_REDIRECT_REQUEST_CODE);
                break;
        }
    }

    private void setActionOnSliderAd(AdFieldsData adFieldsData, String location, String subLocation, Context context) {
        APIMethods.addEvent(context, Constant.CLICK, adFieldsData.postId, location, subLocation);
        switch (adFieldsData.adType) {
            case FeedContentData.AD_TYPE_IN_APP:
                if (adFieldsData.contentType.equalsIgnoreCase(Constant.PAGES)) {
                    HomeActivity.getInstance().redirectToPage(adFieldsData.pageType);
                } else {
                    switch (adFieldsData.contentType) {
                        case FeedContentData.CONTENT_TYPE_ESPORTS:
                            HomeActivity.getInstance().getUserRegisteredTournamentInfo(adFieldsData.postId);
                            break;
                        default:
                            HomeActivity.getInstance().getMediaData(String.valueOf(adFieldsData.contentId), adFieldsData.contentType);
                    }
                }
                break;
            case FeedContentData.AD_TYPE_EXTERNAL:
                setActionOnAd(adFieldsData, context);
                break;
        }
    }

    private void setActionOnAd(AdFieldsData adFieldsData, Context context) {
        switch (adFieldsData.urlType) {
            case Constant.BROWSER:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adFieldsData.externalUrl));
                ((Activity) context).startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                ((Activity) context).startActivity(new Intent(context, BrowserActivity.class).putExtra("title", "").putExtra("posturl", adFieldsData.externalUrl));
                break;
        }
    }

    public void viewAll(FeedCombinedData feedCombinedData) {
        switch (feedCombinedData.feedType) {
            case FEED_TYPE_BUY:
            case FEED_TYPE_CHANNEL:
            case FEED_TYPE_CONTENT:
                ViewAllTvFragment viewAllTvFragment = ViewAllTvFragment.newInstance(feedCombinedData.id, feedCombinedData.title, feedCombinedData.tabType, "feeds", feedCombinedData.tileShape,
                        feedCombinedData.feedType, Constant.SCREEN_HOME);
                homeActivity.fragmentReplacer(R.id.container, viewAllTvFragment);
                break;
            case FEED_TYPE_GAME:
                ViewAllPlayFragment viewAllPlayFragment = ViewAllPlayFragment.newInstance(String.valueOf(feedCombinedData.id), feedCombinedData.postTitle, Constant.SCREEN_HOME);
                viewAllPlayFragment.fromCombinedTab = true;
                homeActivity.fragmentReplacer(R.id.container, viewAllPlayFragment);
                break;
        }
    }

}
