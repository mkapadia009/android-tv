package com.app.itaptv.holder;

import static com.app.itaptv.activity.ProductDetailsActivity.PRODUCT_DATA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.itaptv.API.APIMethods;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.ProductDetailsActivity;
import com.app.itaptv.custom_widget.DotsIndicatorDecoration;
import com.app.itaptv.structure.CommerceProductData;
import com.app.itaptv.structure.FeedContentData;
import com.app.itaptv.structure.MediaData;
import com.app.itaptv.utils.AnalyticsTracker;
import com.app.itaptv.utils.Constant;
import com.app.itaptv.utils.CustomLinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import java.util.ArrayList;

public class ProductHolder extends KRecyclerViewHolder {

    ImageView ivProduct;
    TextView tvDiscount, tvProductName, tvProductPrice, tvDiscountProductPrice;
    Button btBuyNow, bt_knowMore;
    ConstraintLayout clVideo;
    VideoView videoView;
    ImageView ivVolumeUp;
    ImageView ivVolumeOff;
    MediaPlayer mediaPlayer;
    View views;
    Context mContext;
    private RecyclerView rvSliderProduct;
    private CustomLinearLayoutManager layoutManager;
    private KRecyclerViewAdapter adapterSliderProduct;
    private boolean flag = false;
    private boolean flagDecoration = false;
    private int sliderPosition = -1;
    private boolean shouldSlide = true;
    private int visibleItemPosition;
    private ArrayList<MediaData> mediaDataArrayList = new ArrayList<>();
    private CommerceProductData productData;

    public ProductHolder(View itemView) {
        super(itemView);
        views = itemView;
        ivProduct = views.findViewById(R.id.iv_product);
        rvSliderProduct = views.findViewById(R.id.rvSliderProduct);
        tvDiscount = views.findViewById(R.id.tv_discount);
        tvProductName = views.findViewById(R.id.tv_productName);
        tvProductPrice = views.findViewById(R.id.tv_productPrice);
        tvDiscountProductPrice = views.findViewById(R.id.tv_discountProductPrice);
        btBuyNow = views.findViewById(R.id.bt_buyNow);
        bt_knowMore = views.findViewById(R.id.bt_knowMore);
        clVideo = views.findViewById(R.id.clVideo);
        videoView = views.findViewById(R.id.videoView);
        ivVolumeUp = views.findViewById(R.id.ivVolumeUp);
        ivVolumeOff = views.findViewById(R.id.ivVolumeOff);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        mContext = context;

        if (itemObject instanceof CommerceProductData) {
            productData = (CommerceProductData) itemObject;

            if (productData.arrayMedia.size() > 1) {
                ivProduct.setVisibility(View.GONE);
                clVideo.setVisibility(View.GONE);
                rvSliderProduct.setVisibility(View.VISIBLE);
                updateSliders(productData.arrayMedia);
            } else {
                rvSliderProduct.setVisibility(View.GONE);
                if (productData.arrayMedia.get(0).type.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_IMAGE)) {
                    ivProduct.setVisibility(View.VISIBLE);
                    clVideo.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(productData.arrayMedia.get(0).file)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivProduct);
                } else if (productData.arrayMedia.get(0).type.equalsIgnoreCase(FeedContentData.MEDIA_TYPE_VIDEO)) {
                    ivProduct.setVisibility(View.GONE);
                    clVideo.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(productData.arrayMedia.get(0).file));
                    videoView.requestFocus();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.start();
                            mp.setVolume(0.0f, 0.0f);
                            mediaPlayer = mp;
                        }
                    });
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            videoView.start();
                        }
                    });
                }
            }

            tvDiscount.setText(productData.discount);
            tvProductName.setText(productData.postTitle);
            if (productData.discountPrice.isEmpty()) {
                tvDiscountProductPrice.setText("₹" + productData.price);
            } else {
                tvDiscountProductPrice.setText("₹" + productData.discountPrice);

                SpannableString spannableString = new SpannableString("₹" + productData.price);
                StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                spannableString.setSpan(strikethroughSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvProductPrice.setText(spannableString);
            }


            bt_knowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIMethods.addCommerceEvent(mContext, Constant.VIEW, productData.ID);
                    context.startActivity(new Intent(context, ProductDetailsActivity.class).putExtra(PRODUCT_DATA, productData));
                }
            });

            btBuyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIMethods.addCommerceEvent(mContext, Constant.CLICK, productData.ID);
                    openWeb(productData);
                }
            });

            ivProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIMethods.addCommerceEvent(mContext, Constant.CLICK, productData.ID);
                    openWeb(productData);
                }
            });

            tvProductName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIMethods.addCommerceEvent(mContext, Constant.CLICK, productData.ID);
                    openWeb(productData);
                }
            });

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIMethods.addCommerceEvent(mContext, Constant.CLICK, productData.ID);
                    openWeb(productData);
                }
            });

            ivVolumeUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivVolumeUp.setVisibility(View.GONE);
                    ivVolumeOff.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(0.0f, 0.0f);
                    }
                }
            });
            ivVolumeOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivVolumeOff.setVisibility(View.GONE);
                    ivVolumeUp.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                }
            });

        }
    }

    private void updateSliders(ArrayList<MediaData> mediaDataList) {
        setSliderRecyclerView();
        flag = true;
        mediaDataArrayList.clear();
        new Thread(() -> {
            mediaDataArrayList.addAll(mediaDataList);

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapterSliderProduct != null)
                    adapterSliderProduct.notifyDataSetChanged();
                //startSliding();
            });
        }).start();
    }

    private void setSliderRecyclerView() {
        adapterSliderProduct = new KRecyclerViewAdapter(mContext, mediaDataArrayList, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_media, viewGroup, false);

                adapterSliderProduct.getItemCount();
                adapterSliderProduct.getSelectedIndexes();
                return new ProductMediaHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {
                if (o instanceof MediaData) {
                    APIMethods.addCommerceEvent(mContext, Constant.CLICK, productData.ID);
                    openWeb(productData);
                }
            }
        });

        layoutManager = new CustomLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rvSliderProduct.setLayoutManager(layoutManager);
        rvSliderProduct.setNestedScrollingEnabled(false);
        rvSliderProduct.setOnFlingListener(null);
        rvSliderProduct.setAdapter(adapterSliderProduct);

        // Dot indicator for banner
        final int radius = mContext.getResources().getDimensionPixelSize(R.dimen.dot_radius);
        final int dotsHeight = mContext.getResources().getDimensionPixelSize(R.dimen.dot_height);
        final int color = ContextCompat.getColor(mContext, R.color.colorAccent);
        final int inActiveColor = ContextCompat.getColor(mContext, R.color.game_gray);
        if (!flagDecoration) {
            rvSliderProduct.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 2, dotsHeight, inActiveColor, color));
            flagDecoration = true;
        }

        new PagerSnapHelper().attachToRecyclerView(rvSliderProduct);

        rvSliderProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                shouldSlide = (newState == RecyclerView.SCROLL_STATE_IDLE);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    sliderPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    //startSliding();
                } else {
                    //stopSliding();
                }
            }
        });
    }

    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private long secondsToWait = 4000;

    private void startSliding() {
        if (sliderHandler == null) {
            sliderHandler = new Handler();
        }
        if (sliderRunnable == null) {
            sliderRunnable = this::changeSliderPage;
        }
        sliderHandler.postDelayed(sliderRunnable, secondsToWait);
    }

    private void stopSliding() {
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeSliderPage() {
        if (mediaDataArrayList.size() <= 1) return;
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvSliderProduct.getLayoutManager();
            if (layoutManager != null) {
                visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (visibleItemPosition > -1 && visibleItemPosition < mediaDataArrayList.size()) {
                    if (visibleItemPosition == mediaDataArrayList.size() - 1) {
                        // Scroll to first item
                        rvSliderProduct.smoothScrollToPosition(0);
                    } else {
                        // Scroll to next item
                        rvSliderProduct.smoothScrollToPosition(visibleItemPosition + 1);
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void openWeb(@NonNull CommerceProductData productData) {
        AnalyticsTracker.pauseTimer(AnalyticsTracker.SHOP);
        switch (productData.openIn) {
            case Constant.EXTERNAL:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(productData.link));
                mContext.startActivity(browserIntent);
                break;
            case Constant.WEBVIEW:
                mContext.startActivity(new Intent(mContext, BrowserActivity.class).putExtra("title", "").putExtra("posturl", productData.link));
                break;
        }
    }
}
