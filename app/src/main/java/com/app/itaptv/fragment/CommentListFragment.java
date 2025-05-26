package com.app.itaptv.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentListFragment extends BottomSheetDialogFragment {

    private String postId = "";
    private String postTitle = "";
    CommentAdaptor commentAdaptor = null;
    ArrayList<PostComment> commentData = new ArrayList<PostComment>();
    private RecyclerView rvComment;
    EditText edComment;
    Button btnSendComment;
    static private int currentPageIndex = 1;
    private boolean isNetworkBussy = false;
    private int lastVisibleIndex = 0;
    private String MSG = "";
    LinearLayoutManager layoutManager;
    String comment_count = "";
    public static int REQUEST_CODE = 123;
    View view;
    TextView tvComment;
    ImageView ivBack;
    CommentCount commentCount;

    public CommentListFragment(String postId, String postTitle,CommentCount commentCount) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.commentCount=commentCount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        currentPageIndex = 1;
        init();
        loadComments();
        return view;
    }

    private void loadComments() {
        //Log.e("avinash", "loadComments postId = " + postId);
        if (!postId.isEmpty()) {
            try {
                Map<String, String> params = new HashMap<>();
                String url = Url.GET_COMMENTS + "&pageNo=" + String.valueOf(currentPageIndex) + "&id=" + postId;
                APIRequest apiRequest = new APIRequest(url, Request.Method.GET, null, null, getContext());
                apiRequest.showLoader = false;
                isNetworkBussy = true;
                APIManager.request(apiRequest, new APIResponse() {
                    @Override
                    public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                        isNetworkBussy = false;
                        JSONObject jsonObjectResponse = null;
                        if (response != null) {
                            try {
                                jsonObjectResponse = new JSONObject(response);
                                String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                                if (type.equalsIgnoreCase("error")) {
                                    String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                                    Utility.showError(message, getContext());
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
                Utility.showError("Failed to comment.", getContext());
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
                APIRequest apiRequest = new APIRequest(Url.INSERT_COMMENT, Request.Method.POST, params, null, getContext());
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
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
                Utility.showError("Failed to comment.", getContext());
            }
        }
    }

    private void init() {
       /* postId = getActivity().getIntent().getStringExtra("postId");
        postTitle = getActivity().getIntent().getStringExtra("postTitle");*/
        rvComment = view.findViewById(R.id.rvComment);

        edComment = view.findViewById(R.id.edComment);
        edComment.setText(getString(R.string.try_something_here));
        layoutManager = new LinearLayoutManager(getContext());
        btnSendComment = view.findViewById(R.id.btnSendComment);
        tvComment = view.findViewById(R.id.tvComment);
        ivBack = view.findViewById(R.id.ivBack);

        tvComment.setText("Comments (" + postTitle + ")");

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edComment.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Enter Comment", Toast.LENGTH_SHORT).show();
                } else {
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
                        edComment.setText(getString(R.string.try_something_here));
                        MSG=edComment.getText().toString();
                    }
                } else {
                    //GOT FOCUS
                    if (String.valueOf(edComment.getText()).equalsIgnoreCase(MSG)) {
                        edComment.setText("");
                    }
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onStop() {
        if (!comment_count.equalsIgnoreCase("")) {
            commentCount.getCommentCount(comment_count,postId);
        }
        super.onStop();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface CommentCount{
        void getCommentCount(String commentCount, String songId);
    }
}