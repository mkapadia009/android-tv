package com.app.itaptv.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.PastLiveData;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

public class PastLiveHolder extends KRecyclerViewHolder {

    View view;
    ImageView imageBanner, ivLiveImage;
    TextView tvTitle, tvTime, tvScheduledTimeAndDuration, tvViewCount, tvDescription;
    String streamDate;
    boolean isLive;

    public PastLiveHolder(View itemView, boolean isLive) {
        super(itemView);
        view = itemView;
        imageBanner = itemView.findViewById(R.id.ivImage);
        tvTitle = itemView.findViewById(R.id.tvStoreName);
        tvScheduledTimeAndDuration = itemView.findViewById(R.id.tvTime);
        tvViewCount = itemView.findViewById(R.id.tvViewers);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        ivLiveImage = itemView.findViewById(R.id.ivLive);
        this.isLive = isLive;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);
        if (itemObject instanceof PastLiveData) {
            if (!isLive) {
                ivLiveImage.setVisibility(View.INVISIBLE);
            } else {
                ivLiveImage.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(((PastLiveData) itemObject).title);
            streamDate = Utility.formatDate("yyyy-MM-DD HH:mm:ss", "EE MMM dd, hh:mm a", ((PastLiveData) itemObject).time).replace("AM", "am").replace("PM", "pm");
            tvScheduledTimeAndDuration.setText(streamDate + " | " + ((PastLiveData) itemObject).duration + "mins");
            tvViewCount.setText(((PastLiveData) itemObject).viewers);
            tvDescription.setText(((PastLiveData) itemObject).description);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need fix imageview height
            int devicewidth = (int) (displaymetrics.widthPixels);
            imageBanner.getLayoutParams().height = (int) (devicewidth / 2);
            Glide.with(context)
                    .load(((PastLiveData) itemObject).imageUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.tab_grey)
                    .into(imageBanner);
        }
    }
}
