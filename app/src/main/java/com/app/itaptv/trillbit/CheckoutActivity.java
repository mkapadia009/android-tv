package com.app.itaptv.trillbit;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.app.itaptv.R;
import com.app.itaptv.utils.Log;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener , ExternalWalletListener{

    private Button btnPaynow;
    private EditText txtAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        init();
    }

    void init(){
        txtAmount = findViewById(R.id.txtAmount);
        btnPaynow = findViewById(R.id.btnPaynow);
        Checkout.preload(getApplicationContext());

        btnPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double a = Integer.valueOf(txtAmount.getText().toString()) * 100 ;
                initiatePayment(String.valueOf(a));
            }
        });
    }

    private void initiatePayment(String amount){
        final Activity activity = this;
        final Checkout instance = new Checkout();
        try {
            JSONObject options = new JSONObject();
            /**
             *   MERCHANT LOGO
             *   MERCHANT NAME
             *
             **/
            JSONArray wallets = new JSONArray();
            wallets.put("paytm");
            JSONObject externals = new JSONObject();
            externals.put("wallets", wallets);
            options.put("external", externals);

//  instance.setImage(R.drawable.logo);
            options.put("name", "iTap Corp");
            options.put("description", "Service Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            instance.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.error_payment) + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String response) {
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentError(int code, String response) {
        switch (code){
            case Checkout.NETWORK_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.there_was_a_network_error), Toast.LENGTH_LONG).show();
                break;

            case Checkout.PAYMENT_CANCELED:
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                break;

            case Checkout.INVALID_OPTIONS:
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                break;

            case Checkout.TLS_ERROR:
                Toast.makeText(getApplicationContext(), getString(R.string.device_does_not_have), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onExternalWalletSelected(String walletName, PaymentData paymentData) {
        Log.d("avinash","onExternalWalletSelected");
        Log.d("avinash", "walletName " + walletName);
        Log.d("avinash", "paymentData " + paymentData);

        if(walletName.equals("paytm")){
            //handle paytm payment
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d("avinash","onPointerCaptureChanged");
        Log.d("avinash", "hasCapture " + hasCapture);
    }
}
