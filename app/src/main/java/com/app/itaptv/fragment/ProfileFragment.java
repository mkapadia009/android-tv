package com.app.itaptv.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.app.itaptv.API.APIManager;
import com.app.itaptv.API.APIRequest;
import com.app.itaptv.API.APIResponse;
import com.app.itaptv.API.Url;
import com.app.itaptv.R;
import com.app.itaptv.activity.BrowserActivity;
import com.app.itaptv.activity.HomeActivity;
import com.app.itaptv.custom_interface.IOnBackPressed;
import com.app.itaptv.structure.User;
import com.app.itaptv.utils.LocalStorage;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
/*import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements IOnBackPressed {

    View view;
    ImageView ivUserImage, ivEditImage, ivEditMobile;
    TextView tvMobile;
    EditText etUserName;
   /* LinearLayout llbtsave;
    Button btsave;*/

    Bitmap bitmap;
    String fileBase64 = "";
    public static String image_path = "";
    private String userChoosenTask;
    private File pic = null;
    File file;
    private Uri picUri;
    Bitmap photo;
    protected int LOAD_IMAGE_CAMERA = 0;
    protected int LOAD_IMAGE_GALLARY = 2;
    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] image_permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MY_PERMISSIONS_REQUEST_IMG = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static int PICK_IMAGE_REQUEST = 1;
    public final static int MY_PERMISSIONS_REQUEST_CODE = 1;
    private boolean isRemoveImage = false;
    boolean isSuccess = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        return view;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Back", "Back");
        setProfileInfo();
       /* if ((HomeActivity) getActivity() != null) {
            ((HomeActivity) getActivity()).showToolbarIcon();
            ((HomeActivity) getActivity()).showToolbarTitle(false);
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                updateProfile();
                hideKeyboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        ((HomeActivity) getActivity()).setToolbar(true);
        ((HomeActivity) getActivity()).showToolbarBackButton(R.drawable.white_arrow);
        ((HomeActivity) getActivity()).showToolbarTitle(true);
        ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.label_profile));
        ((HomeActivity) getActivity()).setCustomizedTitle(0);
        setHasOptionsMenu(true);
        ivUserImage = view.findViewById(R.id.ivUserImage);
        ivEditImage = view.findViewById(R.id.ivEditImage);
        ivEditMobile = view.findViewById(R.id.ivEditMobile);
        tvMobile = view.findViewById(R.id.tvMobile);
        etUserName = view.findViewById(R.id.etname);

       /* btsave = view.findViewById(R.id.btSave);
        llbtsave = view.findViewById(R.id.llbtsave);*/

        //  Utility.setShadeBackground(llbtsave);

        ivEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), image_permissions, MY_PERMISSIONS_REQUEST_IMG);
                } else {*/
                final CharSequence[] options = {
                        getString(R.string.action_take_photo),
                        getString(R.string.action_choose_from_gallery),
                        getString(R.string.action_select_avatar),
                        getString(R.string.action_remove_profile_photo)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.label_set_profile_image);
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals(getString(R.string.action_take_photo))) {
                            userChoosenTask = getString(R.string.action_take_photo);
                            if (Utility.checkPermission(getActivity(), arrPermissions)) {
                                camera();
                            } else {
                                Utility.requestPermission(getActivity(), MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                            }

                        } else if (options[item].equals(getString(R.string.action_choose_from_gallery))) {
                            userChoosenTask = getString(R.string.action_take_photo);
                            gallery();
                        } else if (options[item].equals(getString(R.string.action_select_avatar))) {
                            userChoosenTask = getString(R.string.action_select_avatar);
                            openWebView();
                        } else if (options[item].equals(getString(R.string.action_remove_profile_photo))) {
                            userChoosenTask = getString(R.string.action_remove_profile_photo);
                            isRemoveImage = true;
                            new ImageUpload().execute();
                        }
                    }
                });
                builder.show();
            }
        });

        ivEditMobile.setOnClickListener(this::setOpenEditMobile);
        setProfileInfo();
        Gson gson = new Gson();
        String userdata = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User user1 = gson.fromJson(userdata, User.class);

        if (getActivity() != null) {
            Glide.with(getActivity())
                    .load(user1.img)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                    .into(ivUserImage);
        }

    }

    private void setProfileInfo() {
        //Local USER DATA
        Gson gson = new Gson();
        String userdata = LocalStorage.getValue(LocalStorage.KEY_USER_DATA, "", String.class);
        User user = gson.fromJson(userdata, User.class);
        //Set User Data
        etUserName.setText(user.displayName);
        tvMobile.setText(Utility.checkNullStringForHyphen(user.mobile));

        if (!user.img.isEmpty()) {
            Glide.with(getActivity())
                    .load(user.img)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions().dontAnimate().diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(R.drawable.user).skipMemoryCache(true))
                    .into(ivUserImage);
        }
    }

    public void setOpenEditMobile(View view1) {
        if (view1 instanceof ImageView) {
            VerifyEmailMobile verifyEmailMobile = new VerifyEmailMobile();
            Bundle bundle = new Bundle();
            bundle.putString("edit", "mobile");
            verifyEmailMobile.setArguments(bundle);
            openFragment(verifyEmailMobile);
        }
    }

    /**
     * Open specific fragment on BottomNavigationView item selected
     *
     * @param fragment Fragment object to display the Fragment
     */
    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {
                cropImage();
            } else if (requestCode == LOAD_IMAGE_GALLARY) {
                if (data != null) {
                    picUri = data.getData();
                    android.util.Log.e("PIC URL", "" + picUri);
                    cropImage();
                }
            } /*else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    file = new File(resultUri.getPath());
                    image_path = file.getAbsolutePath();
                    new ImageUpload().execute();
                    *//*try {
                        photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        android.util.Log.e("Width", "" + photo.getWidth());
                        android.util.Log.e("Height", "" + photo.getHeight());
                        String encodedImage = image();
                        fileBase64 = Utility.getBase64Image(encodedImage, image_path, fileBase64, getActivity(), ivUserImage, photo);
                        android.util.Log.e("FILE::--", fileBase64);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*//*
                    if (pic != null) {
                        // To delete original image taken by camera
                        if (pic.delete()) ;
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }*/
        }
    }

    private class ImageUpload extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = new ProgressDialog(getActivity());
            //dialog.setCancelable(false);
            //dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String token = LocalStorage.getToken();
            String appVersion = LocalStorage.getAppVersion();
            if (token == null) {
                token = "";
            }
            if (appVersion == null) {
                appVersion = "";
            }

            final MediaType MEDIA_TYPE = image_path.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody body;
            if (!isRemoveImage) {
                RequestBody file_body = RequestBody.create(MEDIA_TYPE, file);
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", isRemoveImage ? "" : image_path.substring(image_path.lastIndexOf("/") + 1), file_body)
                        .addFormDataPart("remove_image", "false")
                        .build();
            } else {
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("remove_image", "true")
                        .build();
            }

            Log.i("body", "" + body);
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Url.UPDATE_PROFILE_IMAGE)
                    .header("X-AUTH-TOKEN", token)
                    .header("X-APP-VERSION", appVersion)
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObjectResponse = null;
            if (response != null && response.isSuccessful()) {
                Log.d("Response: ", String.valueOf(response));
                try {
                    String jsonData = response.body().string();
                    try {
                        jsonObjectResponse = new JSONObject(jsonData);
                        String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                        if (type.equalsIgnoreCase("error")) {
                            isSuccess = false;
                            String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                            showError(message);
                        } else if (type.equalsIgnoreCase("ok")) {
                            JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                            if (jsonObject.length() > 0) {
                                JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                                if (jsonObjectuser.length() > 0) {
                                    isSuccess = true;
                                    User user = new User(jsonObjectuser);
                                    LocalStorage.setUserData(user);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Response: ", jsonData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!isSuccess) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getActivity() != null) {
                        Glide.with(getActivity())
                                .load(isRemoveImage ? R.drawable.user : file)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .into(ivUserImage);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getActivity() != null) {
                        Glide.with(getActivity())
                                .load(file)
                                .thumbnail(0.1f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .into(ivUserImage);
                    }
                });
            }
        }
    }

    private void updateProfile() {
        String username = etUserName.getText().toString();
        if (username.equals("")) {
            showError(getString(R.string.please_enter_your_name));
            return;
        }
        updateProfileAPI(username);
    }

    /**
     * Update Profile Using API call
     *
     * @param username
     */
    private void updateProfileAPI(String username) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("display_name", username);
            Log.d("params", params.toString());
            APIRequest apiRequest = new APIRequest(Url.UPDATE_PROFILE, Request.Method.POST, params, null, getActivity());
            apiRequest.showLoader = true;
            APIManager.request(apiRequest, new APIResponse() {
                @Override
                public void onResponse(String response, Exception error, Map<String, String> headers, int statusCode) {
                    handleUpdateProfileResponse(response, error);
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * This method handles Update Profile
     *
     * @param response api response. Can be null
     * @param error    error message in case of error. Can be null
     */
    private void handleUpdateProfileResponse(String response, Exception error) {
        try {
            if (error != null) {
                showError(error.getMessage());
            } else {
                if (response != null) {
                    Log.e("response", response);
                    JSONObject jsonObjectResponse = new JSONObject(response);
                    String type = jsonObjectResponse.has("type") ? jsonObjectResponse.getString("type") : "";
                    if (type.equalsIgnoreCase("error")) {
                        //String message = jsonObjectResponse.has("msg") ? jsonObjectResponse.getString("msg") : APIManager.GENERIC_API_ERROR_MESSAGE;
                        String message = jsonObjectResponse.has("msg") ? Utility.getMessage(getContext(),jsonObjectResponse.get("msg").toString()) : APIManager.GENERIC_API_ERROR_MESSAGE;
                        showError(message);
                    } else if (type.equalsIgnoreCase("ok")) {
                        JSONObject jsonObject = jsonObjectResponse.has("msg") ? jsonObjectResponse.getJSONObject("msg") : new JSONObject();
                        if (jsonObject.length() > 0) {
                            JSONObject jsonObjectuser = jsonObject.has("user") ? jsonObject.getJSONObject("user") : new JSONObject();
                            if (jsonObjectuser.length() > 0) {
                                User user = new User(jsonObjectuser);
                                LocalStorage.setUserData(user);
                                getFragmentManager().popBackStack();
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    /**
     * This method shows error alert
     *
     * @param errorMessage message to be displayed in alert dialog
     */
    private void showError(@Nullable String errorMessage) {
        if (errorMessage == null) errorMessage = APIManager.GENERIC_API_ERROR_MESSAGE;
        //AlertUtils.showAlert(getString(R.string.label_error), errorMessage, null, getContext(), null);
    }

    void camera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pic = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)),
                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            } else {
                pic = new File(Environment.getExternalStorageDirectory(),
                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            }

            //  picUri = Uri.fromFile(pic);
            picUri = FileProvider.getUriForFile(getActivity(),
                    "com.app.itap.fileprovider",
                    pic);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            cameraIntent.putExtra("return-data", true);
            startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    void gallery() {
        pic = new File(Environment.getExternalStorageDirectory(),
                "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE_GALLARY);
    }

    protected void cropImage() {
       /* Intent intent = CropImage.activity(picUri)
                .setAspectRatio(1, 1)
                .setRequestedSize(150, 150, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);*/
        //CropImage.activity(picUri)
        //        .setAspectRatio(1, 1)
        //        .setRequestedSize(150, 150, CropImageView.RequestSizeOptions.RESIZE_EXACT)
        //   .start(getActivity());     //for fragment, dont use getActivity() for passing context
        //      .start(getContext(), this); // For Fragment (DO NOT use `getActivity())
    }

    /*public static String image() {
        Bitmap bm = BitmapFactory.decodeFile(image_path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);

        byte[] byteArrayImage = baos.toByteArray();

        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    }*/

    @Override
    public boolean onBackPressed() {
        //updateProfile();
        getFragmentManager().popBackStack();
        return true;
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void openWebView() {
        if (Utility.checkPermission(getContext(), arrPermissions)) {
            startActivity(new Intent(getContext(), BrowserActivity.class).putExtra("title", "Ready Player Me").putExtra("posturl", Url.READY_PLAYER));
        } else {
            Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
        }
    }
}
