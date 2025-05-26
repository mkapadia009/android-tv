package com.app.itaptv.holder;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.LiveNowData;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

/**
 * Created by poonam on 7/2/19.
 */

public class PresenterLiveHolder extends KRecyclerViewHolder {
    View view;
    ImageView ivImage;
    TextView tvTitle;
    TextView tvTime;
    TextView tvViewers;
    TextView tvDescription;
    ImageView ivLive;
    private String sourceDate = "yyyy-MM-dd HH:mm:ss";
    private String destinationDate = "EEEE MMM dd, hh:mm a";

    public PresenterLiveHolder(View itemView) {
        super(itemView);
        view = itemView;
        ivImage = view.findViewById(R.id.ivImage);
        tvTitle = view.findViewById(R.id.tvStoreName);
        tvTime = view.findViewById(R.id.tvTime);
        tvViewers = view.findViewById(R.id.tvViewers);
        tvDescription = view.findViewById(R.id.tvDescription);
        ivLive = view.findViewById(R.id.ivLive);
    }

    @Override
    protected void setData(Context context, Object itemObject) {
        super.setData(context, itemObject);

        if (itemObject instanceof LiveNowData) {
            LiveNowData liveNowData = (LiveNowData) itemObject;
            String formattedDateTime = Utility.formatDate(sourceDate, destinationDate, liveNowData.time)
                    .replace("AM", "am")
                    .replace("PM", "pm");
            String scheduledTime = String.format(context.getString(R.string.scheduled_time), formattedDateTime, liveNowData.duration);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need fix imageview height
            int devicewidth = (int) (displaymetrics.widthPixels);
            int deviceheight = (int) (devicewidth / 2);
            ivImage.getLayoutParams().height = deviceheight;

            Glide.with(context)
                    .load(liveNowData.imageUrl)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImage);
            tvTitle.setText(liveNowData.title);
            tvTime.setText(scheduledTime);
            tvDescription.setText(liveNowData.description);
            tvViewers.setVisibility(View.GONE);
            ivLive.setVisibility(View.GONE);

        }
    }
}
