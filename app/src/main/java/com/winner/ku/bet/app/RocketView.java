package com.winner.ku.bet.app;

import android.annotation.SuppressLint;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.unity3d.player.UnityPlayerActivity;
import com.winner.ku.bet.app.databinding.FragmentRocketViewBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RocketView extends Fragment {

    public static String url;

    RoundedProgressBar progressBar;
    WebView webView;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            Activity activity = getActivity();
            if (activity != null
                    && activity.getWindow() != null) {
                activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            }

        }
    };


    private FragmentRocketViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mHidePart2Runnable.run();
        binding = FragmentRocketViewBinding.inflate(inflater, container, false);

        webView = binding.viewWeb;
        progressBar = binding.advancedBar;

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.WHITE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);

        CookieManager cookieManager = CookieManager.getInstance();
        CookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        Chrome();
        WebClient();
        Reciever();

        webView.loadUrl(url);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {

                if (webView.canGoBack()) webView.goBack();
                else {
                    NavHostFragment.findNavController(RocketView.this)
                            .navigate(R.id.action_rocketView_to_rocketMain);
                }

            }
        });

        return binding.getRoot();
    }


    public void Chrome()
    {
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgressPercentage(newProgress, false);
                if (newProgress < 100 && progressBar.getVisibility() == progressBar.GONE) {
                    progressBar.setVisibility(progressBar.VISIBLE);
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(progressBar.GONE);
                }
            }

            private void SetDexter()
            {
                Dexter.withContext(getActivity())
                        .withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            }
                        }).check();
            }

            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                SetDexter();

                RocketForever.setCallBack(filePathCallback);
                Intent intentOne = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File filetoDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = null;
                try {
                    file = File.createTempFile("Rocket" +
                            new SimpleDateFormat("yyyyMMdd_HHmmss",
                                    Locale.getDefault()).
                                    format(new Date()) + "_", ".jpg", filetoDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(file != null)
                {
                    Uri fromFile = FileProvider(file);
                    RocketForever.setURL(fromFile);
                    intentOne.putExtra(MediaStore.EXTRA_OUTPUT, fromFile);
                    intentOne.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent intentTwo = new Intent(Intent.ACTION_GET_CONTENT);
                    intentTwo.addCategory(Intent.CATEGORY_OPENABLE);
                    intentTwo.setType("image/*");

                    Intent intentChooser = new Intent(Intent.ACTION_CHOOSER);
                    intentChooser.putExtra(Intent.EXTRA_INTENT, intentOne);
                    intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {intentTwo});

                    startActivityForResult(intentChooser, RocketForever.getCode());

                    return true;
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            Uri FileProvider(File file)
            {
                return FileProvider.getUriForFile(getActivity(), getActivity().getApplication().getPackageName() + ".provider", file);
            }

            @Override
            public void onPermissionRequestCanceled(android.webkit.PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Log.d("AAA", request.toString());
            }
        });
    }
    public void WebClient()
    {
        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String str = RocketForever.Firebase.getString(new String(Base64.decode(getActivity().getResources().getString(R.string.rocket_add), Base64.DEFAULT)));
                if (url.contains(str)) {
                    Intent i = new Intent(getActivity(), UnityPlayerActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
                else super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(TikTok(url)
                        && Instagram(url)
                        && Facebook(url)
                        && LinkedIn(url)
                        &&Twitter(url)
                        && Ok(url)
                        && Vk(url)
                        && Youtube(url))
                    view.loadUrl(url);
                return true;
            }

            boolean TikTok(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.tiktok), Base64.DEFAULT)));
            }
            boolean Facebook(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.facebook), Base64.DEFAULT)));
            }
            boolean Instagram(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.instagram), Base64.DEFAULT)));
            }
            boolean LinkedIn(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.linkedin), Base64.DEFAULT)));
            }
            boolean Twitter(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.twitter), Base64.DEFAULT)));
            }
            boolean Ok(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.ok), Base64.DEFAULT)));
            }
            boolean Vk(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.vk), Base64.DEFAULT)));
            }
            boolean Youtube(String url)
            {
                return !url.startsWith(new String(Base64.decode(getActivity().getResources().getString(R.string.youtube), Base64.DEFAULT)));
            }
        });
    }
    public void Reciever()
    {
        IntentFilter wildzooIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        wildzooIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getActivity().registerReceiver(new BroadcastReceiver() {
            public String url;
            public boolean check;
            ConnectivityManager manager;
            NetworkInfo info;
            @Override

            public void onReceive(Context context, Intent intent) {
                Manager();
                Info();
                check = info != null && info.isConnectedOrConnecting();
                if (check) {
                    if (url != null)
                        webView.loadUrl(url);
                } else {
                    url = webView.getUrl();
                    webView.loadUrl(new String(android.util.Base64.decode(context.getResources().getString(R.string.index), Base64.DEFAULT)));
                }
            }

            void Manager() {
                manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            }

            void Info() {
                info = manager.getActiveNetworkInfo();
            }
        }, wildzooIntentFilter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (RocketForever.getCode() == requestCode)
            if (RocketForever.getCallBack() == null) return;
        if (resultCode != -1) {
            RocketForever.getCallBack().onReceiveValue(null);
            return;
        }
        Uri wundResult = (data == null)? RocketForever.getURL() : data.getData();
        if(wundResult != null && RocketForever.getCallBack() != null)
            RocketForever.getCallBack().onReceiveValue(new Uri[]{wundResult});
        else if(RocketForever.getCallBack() != null)
            RocketForever.getCallBack().onReceiveValue(new Uri[] {RocketForever.getURL()});
        RocketForever.setCallBack(null);
        super.onActivityResult(requestCode, resultCode, data);
    }

}