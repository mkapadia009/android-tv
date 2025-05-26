/*
package com.app.itap.trillbit;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.itap.R;
import com.app.itap.utils.Log;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class CouponActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView txtTitle;
    private TextView txtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        imageView = findViewById(R.id.image);
        txtTitle = findViewById(R.id.title);
        txtDesc = findViewById(R.id.desc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
           Serializable coupon = (CouponFragment) intent.getSerializableExtra("coupon");
            Log.d("avinash", String.valueOf(coupon));
            Log.d("avinash", ((CouponFragment) coupon).getImage());
            txtTitle.setText(((CouponFragment) coupon).getTitle());
            txtDesc.setText(((CouponFragment) coupon).getDescription());
            Picasso.get().load(((CouponFragment) coupon).getImage()).into(imageView);

        }else{
            Log.d("avinash", "Extras null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        startTrillService();
        super.onDestroy();
    }

    private void startTrillService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ComponentName componentName = new ComponentName(getApplicationContext(), TrillbitJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(01,componentName)
                    .setPersisted(true)
                    .setPeriodic( 30 * 1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(jobInfo);
        }else{
            Intent intent = new Intent(this, TrillbitService.class);
            startService(intent);
        }
        Log.d("avinash", "JobScheduler scheduled");
    }
}
*/
