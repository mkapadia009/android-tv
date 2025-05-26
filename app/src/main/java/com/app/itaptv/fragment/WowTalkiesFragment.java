package com.app.itaptv.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.itaptv.R;

//import org.apache.commons.io.FileUtils;


/*import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;*/

public class WowTalkiesFragment extends Fragment {

    public static WowTalkiesFragment newInstance() {
        return new WowTalkiesFragment();
    }

    View view;
    //private FlutterEngine flutterEngine;
    CardView singleFaceSwap, gifFaceSwap, twoFaceSwap, faceFilter, arAvatar;
    DownloadManager downloadManager;
    String[] arrPermissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static int requestCodeFaceSwap = 10010;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_wowtalkies, container, false);
           // init();
        }
        return view;
    }

  /*  public void init() {
        singleFaceSwap = view.findViewById(R.id.singleFaceSwap);
        gifFaceSwap = view.findViewById(R.id.gifFaceSwap);
        twoFaceSwap = view.findViewById(R.id.twoFaceSWap);
        faceFilter = view.findViewById(R.id.faceFilter);
        arAvatar = view.findViewById(R.id.arAvatar);

        flutterEngine = new FlutterEngine(requireActivity());

        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        final MethodChannel channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "com.wowtalkies.plugin");
        initializeColorandLogo(channel);

        singleFaceSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    Snackbar.make(view, "Launching Face swap..", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    // Image -one face Sample Call

                    startActivityForResult(FlutterActivity.createDefaultIntent(requireActivity()).setAction("com.wowtalkies.plugin.faceswap")
                            .putExtra("image", "https://wowt.mypinata.cloud/ipfs/QmYjTeF9bLxNpcoLUfceqBiwt9RZty9YBbaSFBYye8faLE")
                            .putExtra("target", "anand1.jpg")
                            .putExtra("specific", (String) null)
                            .putExtra("specific1", (String) null)
                            .putExtra("utilitytype", "faceswapimage"), requestCodeFaceSwap);
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
            }
        });

        gifFaceSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    Snackbar.make(view, "Launching Gif", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startActivityForResult(FlutterActivity.createDefaultIntent(getContext()).setAction("com.wowtalkies.plugin.faceswap")
                            .putExtra("image", "https://wowt.mypinata.cloud/ipfs/QmP6hdq1QqNpfkzeZ7YPuJctxCwmb4qj9mftoiYXPdGtkX")
                            .putExtra("target", "hrithik.gif")
                            .putExtra("specific", (String) null)
                            .putExtra("specific1", (String) null)
                            .putExtra("utilitytype", "faceswapgif"), requestCodeFaceSwap);
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
            }
        });

        twoFaceSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    Snackbar.make(view, "Launching Two Face Swap", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //  .putExtra("specific", (String) null)
                    startActivityForResult(FlutterActivity.createDefaultIntent(getContext()).setAction("com.wowtalkies.plugin.faceswap")
                            .putExtra("image", "https://wowt.mypinata.cloud/ipfs/QmPYQGv6hHcBgNMVXHCmvoWzWo9rMiw1DMZxpPmLVHvX8x")
                            .putExtra("target", "coolie.jpg")
                            .putExtra("specific", "coolie.png")
                            //  .putExtra("specific", (String) null)
                            .putExtra("specific1", (String) null)
                            .putExtra("utilitytype", "faceswapimage"), requestCodeFaceSwap);
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
            }
        });

        faceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    ARFilterDetails mARFilterDetails = new ARFilterDetails();
                    mARFilterDetails.setmFilterImageUrl("https://firebasestorage.googleapis.com/v0/b/web3nft-283d2.appspot.com/o/sdkimages%2Fkrish.png?alt=media&token=b909dfcb-62f4-4318-9dc9-7ee1a5b5decf");
                    mARFilterDetails.setmFilterText("Super hero");
                    List<ARFilterDetails> mListAr = new ArrayList<>();
                    mListAr.add(mARFilterDetails);
                    LinkedHashMap<String, List<ARFilterDetails>> arFiltersMapAR;
                    arFiltersMapAR = new LinkedHashMap<>();
                    arFiltersMapAR.put("Face filter", mListAr);
                    Intent intent = new Intent(getActivity(), AugmentedFacesActivity.class);
                    intent.putExtra("FaceFilterValues", new Wrapper<>(arFiltersMapAR));
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
            }
        });

        arAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utility.checkPermission(getContext(), arrPermissions)) {
                    ARFilterDetails mARFilterDetails = new ARFilterDetails();
                    mARFilterDetails.setmFilterImageUrl("https://firebasestorage.googleapis.com/v0/b/web3nft-283d2.appspot.com/o/sdkimages%2FReadyPlayerMe-Avatar.png?alt=media&token=d99ef51a-90b9-4d00-86a9-5a8b0475e3f9");
                    mARFilterDetails.setmFilterText("My Avatar");
                    List<ARFilterDetails> mListAr = new ArrayList<>();
                    mListAr.add(mARFilterDetails);
                    LinkedHashMap<String, List<ARFilterDetails>> arFiltersMapAR;
                    arFiltersMapAR = new LinkedHashMap<>();
                    arFiltersMapAR.put("AR Avatar", mListAr);

                    Intent intent = new Intent(getActivity(), ArStickersActivity.class);
                    intent.putExtra("AvatarDetails", new Wrapper<>(arFiltersMapAR));
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Utility.requestPermission(getContext(), BrowserActivity.MY_PERMISSIONS_REQUEST_CODE, arrPermissions);
                }
            }
        });
    }


    void initializeColorandLogo(MethodChannel channel) {
        HashMap<String, String> initializeColorandLogo = new HashMap<String, String>();
        initializeColorandLogo.put("buttoncolour", "12a9f6");
        initializeColorandLogo.put("logourl", "https://firebasestorage.googleapis.com/v0/b/wowtweb3sdk.appspot.com/o/PLAY-05A-p-500.png?alt=media&token=8d21cc23-8ef2-4a8a-89c8-91ebd3c5f71a");
        initializeColorandLogo.put("pagecolour", "0a1119");
        initializeColorandLogo.put("appbarcolor", "2d2d2d");
        initializeColorandLogo.put("buttonbordercolor", "12a9f6");
        initializeColorandLogo.put("textcolor", "ffffff");
        initializeColorandLogo.put("succeswalletimage", "https://firebasestorage.googleapis.com/v0/b/wowtweb3sdk.appspot.com/o/SuccessImage.png?alt=media&token=d561630f-e23e-43ae-99d1-dc72b872ba83");
        initializeColorandLogo.put("securewalletimage", "https://firebasestorage.googleapis.com/v0/b/wowtweb3sdk.appspot.com/o/wallettab.png?alt=media&token=d34bdc0e-ee59-4109-ab57-b976ad958573");
        initializeColorandLogo.put("seedscreenimage", "https://firebasestorage.googleapis.com/v0/b/wowtweb3sdk.appspot.com/o/blur.png?alt=media&token=64273ebd-217a-4aa4-8746-e697f2df7482");
        channel.invokeMethod("Colors", initializeColorandLogo, new MethodChannel.Result() {
            @Override
            public void success(@Nullable Object result) {
                Log.d("MethodChannelCallback", "result is " + result);

            }

            @Override
            public void error(@NonNull String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

                Log.e("MethodChannelCallback", "erroe is " + errorMessage);
            }

            @Override
            public void notImplemented() {

                Log.d("MethodChannelCallback", "result is notimplemented");

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("wowtalkies", "" + requestCode);
        if (WowTalkiesFragment.requestCodeFaceSwap == requestCode) {
            if (resultCode == RESULT_OK) {
                String status = data.getStringExtra("status");
                if (status != null && status.equals("Swapped")) {
                    String image = data.getStringExtra("image");

                    com.app.itap.utils.Log.d("Faceswaped image", image);
                    downloadImage(image);
                }
            }

            if (resultCode == RESULT_CANCELED) {
                com.app.itap.utils.Log.d("callback", "user cancelled");
            }

        }
    }

    public void downloadImage(String sourcePath) {
        if (sourcePath != null && !sourcePath.isEmpty()) {
            File source = new File(sourcePath);
            File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + getString(R.string.avatar) + System.currentTimeMillis() + ".jpg");
            try {
                FileUtils.copyFile(source, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}