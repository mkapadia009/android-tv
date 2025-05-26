package com.app.itaptv.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.custom_interface.OnSelectListener;
import com.app.itaptv.font_awesome.FontAwesome;
import com.app.itaptv.holder.LanguageHolder;
import com.app.itaptv.structure.LanguageData;
import com.app.itaptv.utils.AlertUtils;
import com.app.itaptv.utils.EmptyStateManager;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.kalpesh.krecycleradapter.Interface.KRecyclerItemClickListener;
import com.kalpesh.krecycleradapter.Interface.KRecyclerViewHolderCallBack;
import com.kalpesh.krecycleradapter.KRecyclerViewAdapter;
import com.kalpesh.krecycleradapter.KRecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LanguagePrefActivity extends BaseActivity implements OnSelectListener {
    LinearLayout llHeaderLabel;

    KRecyclerViewAdapter adapter;
    TextView doneBtn;

    ArrayList<LanguageData> arrayListLanguageData = new ArrayList<>();
    ArrayList<Integer> arrayListLanguageId = new ArrayList<>();

    EmptyStateManager emptyState;

    boolean isFromProfilePage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_pref);
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(false);
        init();
    }

    private void init() {

        if (getIntent().getExtras() != null) {
            isFromProfilePage = getIntent().getExtras().getBoolean("isFromProfilePage");
        }

        llHeaderLabel = findViewById(R.id.llHeaderLabel);
        doneBtn = findViewById(R.id.buttonDone);
        setUpEmptyState();
        setLanguageRecyclerView();
        getLanguagesAPI();
        doneBtn.setOnClickListener(v -> setLanguagePrefAPI());
    }

    /**
     * Initialization of Empty State
     */
    void setUpEmptyState() {
        emptyState = EmptyStateManager.setUpInActivity(this, action -> {
            if (action.equals(EmptyStateManager.ACTION_RETRY)) {
                getLanguagesAPI();
            }
        });
    }

    /**
     * Update of Empty State
     */
    private void updateEmptyState(String error) {
        if (error == null) {
            if (arrayListLanguageData.isEmpty()) {
                emptyState.setImgAndMsg(getString(R.string.no_language_found), FontAwesome.FA_Exclamation);
                llHeaderLabel.setVisibility(View.INVISIBLE);
            } else {
                llHeaderLabel.setVisibility(View.VISIBLE);
                emptyState.hide();
            }
        } else {
            llHeaderLabel.setVisibility(View.INVISIBLE);
            if (Utility.isConnectingToInternet(this)) {
                emptyState.setMessage(APIManager.GENERIC_API_ERROR_MESSAGE);
            } else {
                emptyState.showNoConnectionState();
            }
        }
    }

    /**
     * This method gets all languages to set the language preference
     */
    private void getLanguagesAPI() {

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        arrayListLanguageData.clear();
        Map<String, String> params = new HashMap<>();
        try {
            APIRequest apiRequest = new APIRequest(Url.GET_LANGUAGES, Request.Method.POST, params, null, LanguagePrefActivity.this);
            apiRequest.showLoader = false;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    progressBar.setVisibility(View.GONE);
                    handleLanguageResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method allows user to set the language preferences
     */
    private void setLanguagePrefAPI() {
        if (arrayListLanguageId.size() == 0) {
            showError(getString(R.string.error_select_language_preference));
            return;
        }
        LocalStorage.saveLanguageArrayList(arrayListLanguageId, "savedLanguages", this);
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < arrayListLanguageId.size(); i++) {
            params.put("prefs[" + i + "]", arrayListLanguageId.get(i).toString());
        }
        try {
            APIRequest apiRequest = new APIRequest(Url.SET_LANG_PREF, Request.Method.POST, params, null, LanguagePrefActivity.this);
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, (response, error, headers, statusCode) -> handleLanguageResponse(response, error));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles language data response and shows language listing
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleLanguageResponse(@Nullable String response, @Nullable Exception error) {
        try {
            if (error != null) {
                //showError(error.getMessage());
                updateEmptyState(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        // showError(message);
                        updateEmptyState(jsonObjectResponse.getString("msg"));
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayMsg = jsonObjectResponse.getJSONArray("msg");
                        if (jsonArrayMsg != null) {
                            for (int i = 0; i < jsonArrayMsg.length(); i++) {
                                LanguageData languageData = new LanguageData(jsonArrayMsg.getJSONObject(i));
                                arrayListLanguageData.add(languageData);
                            }
                            adapter.notifyDataSetChanged();
                            updateEmptyState(null);
                        }
                    } else if (type.equalsIgnoreCase("success")) {
                        if (isFromProfilePage) {
                            Intent i = new Intent(this, SplashActivity.class);
                            new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.note))
                                    .setMessage(getString(R.string.reset_your_language_preferences))
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            i.setAction(getString(R.string.no_action));
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                    })
                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(this.getResources().getDrawable(R.drawable.ic_new_alert))
                                    .show();
                        } else {
                            /*startActivity(new Intent(this, HomeActivity.class).setAction(getString(R.string.no_action)));
                            finish();*/
                            finishFlow();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finishFlow() {
        Intent intent = new Intent();
        intent.putExtra("USER_REFER", 0);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Initializes language recycler view
     */
    private void setLanguageRecyclerView() {
        RecyclerView rvLanguage = findViewById(R.id.rvLanguage);
        rvLanguage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new KRecyclerViewAdapter(this, arrayListLanguageData, new KRecyclerViewHolderCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public KRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_language, viewGroup, false);
                return new LanguageHolder(layoutView);
            }
        }, new KRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(KRecyclerViewHolder kRecyclerViewHolder, Object o, int i) {

            }
        });

        rvLanguage.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, this, null);
    }

    @Override
    public void addSelectedValue(int id) {
        arrayListLanguageId.add(id);
    }

    @Override
    public void removeDeselectedValue(int id) {
        for (Integer languageId : new ArrayList<>(arrayListLanguageId)) {
            if (languageId == id) {
                arrayListLanguageId.remove(languageId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.language, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // 'Done' menu action
        final MenuItem menuItemDone = menu.findItem(R.id.actionDone);
        View viewDone = menuItemDone.getActionView();
        Button btDone = viewDone.findViewById(R.id.btDone);
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItemDone);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionDone:
                setLanguagePrefAPI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
