package com.app.itaptv.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.adapter.CommentAdaptor;
import com.app.itaptv.structure.CommentData;
import com.app.itaptv.structure.PostComment;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentListActivity extends BaseActivity {
    private String postId;
    private String postTitle;
    CommentAdaptor commentAdaptor = null;
    ArrayList<PostComment> commentData = new ArrayList<PostComment>();
    private RecyclerView rvComment;
    EditText edComment;
    Button btnSendComment;
    static private int currentPageIndex = 1;
    private boolean isNetworkBussy = false;
    private int lastVisibleIndex = 0;
    private String MSG = getString(R.string.type_something_here);
    LinearLayoutManager layoutManager;
    String comment_count = "";
    public static int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPageIndex = 1;
        init();
        loadComments();
    }

    @Override
    public void onBackPressed() {
        if (!comment_count.equalsIgnoreCase("")) {
            Intent intent = new Intent();
            intent.putExtra("comment_count", comment_count);
            intent.putExtra("song_id", postId);
            setResult(Activity.RESULT_OK,intent);
        } else {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }

    private void loadComments() {
        //Log.e("avinash", "loadComments postId = " + postId);
        if (!postId.isEmpty()) {
            try {
                Map<String, String> params = new HashMap<>();
                String url = Url.GET_COMMENTS + "&pageNo=" + String.valueOf(currentPageIndex) + "&id=" + postId;
                APIRequest apiRequest = new APIRequest(url, Request.Method.GET, null, null, this);
                apiRequest.showLoader = false;
                isNetworkBussy = true;
                APIManager.request(apiRequest, new APIResponse() {
                    @Override
                    public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                        isNetworkBussy = false;
                        JSONObject jsonObjectResponse = null;
                        if(response !=null) {
                            try {
                                jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    Utility.showError(message, CommentListActivity.this);
                                } else if (type.equalsIgnoreCase("ok")) {
                                    CommentData cData = new CommentData(jsonObjectResponse);
                                    if (cData.getcomment().size() > 0) {
                                        for (int i = 0; i < cData.getcomment().size(); i++) {
                                            cData.getcomment();
                                            commentData.add(cData.getcomment(i));
                                        }
                                        rvComment.setLayoutManager(layoutManager);
                                        if (commentAdaptor == null) {
                                            commentAdaptor = new CommentAdaptor(commentData);
                                            rvComment.setAdapter(commentAdaptor);
                                        } else {
                                            commentAdaptor.updateCommentAdaptor(commentData);
                                            commentAdaptor.notifyDataSetChanged();
                                            if (currentPageIndex == 1) {
                                                rvComment.scrollToPosition(0);
                                            } else {
                                                rvComment.scrollToPosition(lastVisibleIndex + 1);
                                            }
                                        }
                                        currentPageIndex = currentPageIndex + 1;
                                    } else {
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                isNetworkBussy = false;
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                isNetworkBussy = false;
                Utility.showError(getString(R.string.failed_to_comment), CommentListActivity.this);
            }
        }

    }


    private void sendComment() {
        //Log.e("avinash", "sendComment");
        String msg = edComment.getText().toString();
        if (msg.isEmpty()) {
            edComment.requestFocus();
        } else {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("id", postId);
                params.put("msg", msg);
                APIRequest apiRequest = new APIRequest(Url.INSERT_COMMENT, Request.Method.POST, params, null, this);
                apiRequest.showLoader = true;
                isNetworkBussy = true;
                APIManager.request(apiRequest, new APIResponse() {
                    @Override
                    public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                        edComment.setText("");
                        isNetworkBussy = false;
                        JSONObject jsonObjectResponse = null;
                        try {
                            jsonObjectResponse = new JSONObject(response);
                            String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                            if (type.equalsIgnoreCase("error")) {
                                String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                Toast.makeText(CommentListActivity.this, message, Toast.LENGTH_LONG).show();
                            } else if (type.equalsIgnoreCase("ok")) {
                                Log.d("response", jsonObjectResponse.toString());
                                JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                                comment_count = jsonObject.has("comment_count") ? jsonObject.getString("comment_count") : "";
                                // COMMENT ADDED SUCCESSFULLY
                                currentPageIndex = 1;
                                commentData.clear();
                                loadComments();
                            }
                        } catch (JSONException e) {
                            isNetworkBussy = false;
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                edComment.setText("");
                isNetworkBussy = false;
                Utility.showError(getString(R.string.failed_to_comment), CommentListActivity.this);
            }
        }
    }

    private void init() {
        setContentView(R.layout.activity_comment_list);
        setToolbar(true);
        showToolbarBackButton(R.drawable.white_arrow);
        showToolbarTitle(true);
        setCustomizedTitle(0);
        postId = getIntent().getStringExtra("postId");
        postTitle = getIntent().getStringExtra("postTitle");
        rvComment = findViewById(R.id.rvComment);

        setToolbarTitle(getString(R.string.comments)+" "+getString(R.string.opening_bracket) + postTitle + getString(R.string.closing_bracket));
        edComment = findViewById(R.id.edComment);
        edComment.setText(MSG);
        layoutManager = new LinearLayoutManager(CommentListActivity.this);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edComment.getText().toString().isEmpty()){
                    Toast.makeText(CommentListActivity.this, getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else{
                    sendComment();
                }
            }
        });

        rvComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleIndex - 4 == commentData.size() - 5 && !isNetworkBussy) {
                    isNetworkBussy = true;
                    loadComments();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        edComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //LOST FOCUS
                    if (String.valueOf(edComment.getText()).equalsIgnoreCase("")) {
                        edComment.setText(MSG);
                    }
                } else {
                    //GOT FOCUS
                    if (String.valueOf(edComment.getText()).equalsIgnoreCase(MSG)) {
                        edComment.setText("");
                    }
                }
            }
        });
    }
}
