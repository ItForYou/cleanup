package co.kr.itforone.cleanup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import util.BackPressCloseHandler;
import util.Common;


public class MainActivity extends AppCompatActivity {
    public WebView webView;
    public SwipeRefreshLayout webLayout;
    public static boolean execBoolean = true;
    private BackPressCloseHandler backPressCloseHandler;
    boolean isIndex = true;
    boolean isRefresh = true;
    final int FILECHOOSER_NORMAL_REQ_CODE = 1200,FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    ValueCallback<Uri> filePathCallbackNormal;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Uri mCapturedImageURI;
    String firstUrl = "";
    String Url;
    final int REQUEST_IMAGE_CODE = 1010;
    Context mContext;
    Activity mActivity;
    private Uri cameraImageUri;
    public static String no;
    int gpsCount=0;
    static final int PERMISSION_REQUEST_CODE = 1;
    String successStr="??????",cancelStr="??????";
    private LocationManager locationManager;
    private Location location;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };


    private boolean hasPermissions(String[] permissions) {
        // ????????? ??????
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){

                }else{
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                   /* LocationPosition.act=MainActivity.this;
                    LocationPosition.setPosition(this);
                    if(LocationPosition.lng==0.0){
                        LocationPosition.setPosition(this);
                    }
                    String place= LocationPosition.getAddress(LocationPosition.lat,LocationPosition.lng);
                    webView.loadUrl("javascript:getAddress('"+place+"')");*/
                }
                return;
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView=(WebView)findViewById(R.id.webView);
        webLayout=(SwipeRefreshLayout)findViewById(R.id.webLayout);

        String channelId = "cleanupFcm";
        String channelId_cancel = "cleanupFcm_cancel";



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //????????? ??????
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "service_matching", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.setDescription("");
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            notificationChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/service"), att);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{400, 400});
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            //?????? ?????????
            NotificationManager manager = getSystemService(NotificationManager.class);
            //???????????? ?????? ????????????
            manager.createNotificationChannel(notificationChannel);


            NotificationChannel notificationChannel2 = new NotificationChannel(channelId_cancel, "service_matching2", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel2.setShowBadge(true);
            notificationChannel2.setDescription("");
            AudioAttributes att2 = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            //????????? ??????
            notificationChannel2.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/cancel"), att2);
            notificationChannel2.enableVibration(true);
            notificationChannel2.setVibrationPattern(new long[]{400, 400});
            notificationChannel2.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            //???????????? ?????? ????????????
            manager.createNotificationChannel(notificationChannel2);

            //?????? ?????? ?????? ?????? ???
            NotificationChannel notificationChannel3 = new NotificationChannel("default", "service_matching3", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel3.setShowBadge(true);
            notificationChannel3.setDescription("");
            notificationChannel3.enableVibration(true);
            notificationChannel3.setVibrationPattern(new long[]{400, 400});
            notificationChannel3.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            //???????????? ?????? ????????????
            manager.createNotificationChannel(notificationChannel3);

        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //mHandler.sendEmptyMessage(0);
        mContext=this;
        mActivity=this;
        //???????????? ?????? ??? ???????????? ?????????
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //????????? ?????? ??????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        firstUrl=getString(R.string.url);
        Intent intent = getIntent();
        try{
            if(intent.getStringExtra("goUrl") != null){
                firstUrl=intent.getStringExtra("goUrl").toString();
            }else {
                Uri uriData = intent.getData();
                if(uriData!=null) {
                    firstUrl = uriData.getQueryParameter("url") + "?u_id=" + uriData.getQueryParameter("u_id");
                    Log.d("data11", firstUrl);
                }
            }
            /*if(!intent.getExtras().getString("url").equals("")){
                firstUrl=intent.getExtras().getString("go_url")+"?"+intent.getExtras().getString("mb_3");
            }*/
        }catch (Exception e){

        }

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if (hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }



        /* cookie */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        Intent startIntent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(startIntent);
        webViewSetting();
    }



    //????????? ?????????????????? ?????????

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR_MR1)
    public void webViewSetting() {
        Common.setTOKEN(this);
        WebSettings setting = webView.getSettings();//?????? ?????????
        if(Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setting.setAllowFileAccess(true);//????????? ?????? ?????? ??????
        setting.setAppCacheEnabled(true);//?????? ????????????
        setting.setGeolocationEnabled(true);//?????? ?????? ????????????
        setting.setDatabaseEnabled(true);//HTML5?????? db ????????????
        setting.setDomStorageEnabled(true);//HTML5?????? DOM ????????????
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);//?????? ???????????? LOAD_NO_CACHE??? ????????? ?????????????????? ???
        setting.setJavaScriptEnabled(true);//?????????????????? ????????????
        setting.setSupportMultipleWindows(false);//????????? ??? ???????????? ????????? ???????????? ?????? ????????? false??? ?????? ??? ??????
        setting.setUseWideViewPort(true);//????????? view port ????????????
        setting.setTextZoom(100);
        setting.setSupportZoom(true);



        webView.setWebChromeClient(chrome);//????????? ??????????????? ?????? ???????????? ????????? ?????? ?????????
        webView.setWebViewClient(client);//???????????? ????????? ????????? ????????? ????????? ??? ?????? ???????????? ????????? ?????? ??? ?????? ??????
        setting.setUserAgentString("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Mobile Safari/537.36"+"/AGold");
        webView.addJavascriptInterface(new WebJavascriptEvent(), "Android");
        //???????????? ????????? ????????? ??? ???????????? ?????????
        backPressCloseHandler = new BackPressCloseHandler(this);
        webView.loadUrl(firstUrl);
    }

    WebChromeClient chrome;
    {
        chrome = new WebChromeClient() {
            //?????? ????????? ??????
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return false;
            }

            //????????? ?????????
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setCancelable(false)
                        .setPositiveButton("??????",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                }).create().show();
                return true;
            }

            //?????? ?????????
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setCancelable(false)
                        .setPositiveButton("??????",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("??????",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.cancel();
                                    }
                                }).create().show();
                return true;
            }

            //?????? ?????? ?????? ???????????? ??????
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Should implement this function.
                final String myOrigin = origin;
                final GeolocationPermissions.Callback myCallback = callback;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Request message");
                builder.setMessage("Allow current location?");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, true, false);
                    }

                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, false, false);
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                filePathCallbackNormal = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
//                i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//                i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
            }

            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }


            // For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {

                //????????? ?????????????????? ???????????? ????????? ???????????? ???????????????.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// API 24 ?????? ?????????..
                    File imageStorageDir = new File(MainActivity.this.getFilesDir() + "/Pictures", "cleanup");
                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }
                    // Create camera captured image file path and name

                    //Toast.makeText(mainActivity.getApplicationContext(),imageStorageDir.toString(),Toast.LENGTH_LONG).show();
                    File file = new File(imageStorageDir, "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    Uri providerURI = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getPackageName() + ".provider", file);
                    mCapturedImageURI = providerURI;

                } else {// API 24 ?????? ?????????..

                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cleanup");
                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }
                    // Create camera captured image file path and name
                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mCapturedImageURI = Uri.fromFile(file);
                }
                if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
                filePathCallbackLollipop = filePathCallback;
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("content://media/internal/images/media"));
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
                return true;

            }

        };
    }

    WebViewClient client;
    {
        client = new WebViewClient() {

            //????????? ???????????? ??? (????????????) 6.0 ???????????? ?????? ??????
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("url",url);
                if (url.equals(getString(R.string.url)) || url.equals(getString(R.string.domain))) {
                    isIndex=true;
                } else {
                    isIndex=false;
                }
                if (url.startsWith("intent:")) {

                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        Log.d("error1",e.toString());
                        e.printStackTrace();
                    }
                }
                if (url.startsWith("tel")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.


                        }
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
                return false;
            }
            //????????? ????????? ??? ????????? ???
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webLayout.setRefreshing(false);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }
                Log.d("mb_id", Common.getPref(MainActivity.this,"ss_mb_id",""));
                //???????????? ???
                if(url.startsWith(getString(R.string.domain)+"/bbs/login.php")||url.startsWith(getString(R.string.domain)+"/bbs/register_form.php")){
                    Log.d("token",Common.TOKEN);
                    view.loadUrl("javascript:fcmKey('"+ Common.TOKEN+"')");
                }
                if (url.equals(getString(R.string.url)) || url.equals(getString(R.string.domain))) {
                    isIndex=true;
                } else {
                    isIndex=false;
                }
                Log.d("test_url", url);
                //???????????? ??????
                if( url.contains("register_form.php")|| url.contains("my_service.php") || url.contains("#hash-menu")){
                    Url = url;
                    isRefresh = true;
                }else{
                    isRefresh = false;
                }

                if(isIndex==false &&isRefresh == false ) {
                    webLayout.setEnabled(true);
                    webLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {

                            webView.clearCache(true);
                            webView.reload();
                            webLayout.setRefreshing(false);

                        }
                    });
                }else{
                    webLayout.setRefreshing(false);
                    webLayout.setEnabled(false);
                }
            }
            //????????? ????????? ?????? ??? 6.0 ???????????? ????????? ??????
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    //?????? ???????????? ???
    @Override
    protected void onResume() {
        super.onResume();
        /* cookie */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }



        execBoolean=true;
        Log.d("newtork","onResume");


        //netCheck.networkCheck();
    }
    //????????? ????????? ???????????? ????????? ???
    @Override
    protected void onPause() {
        super.onPause();
        /* cookie */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
        execBoolean=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //??????????????? ????????? ???
    public void onBackPressed() {
        //super.onBackPressed();
        //???????????? ??????????????? ??????????????? ???????????? ???
        Log.d("isIndex",isIndex+"");
        if(!isIndex) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else if (webView.canGoBack() == false) {
                backPressCloseHandler.onBackPressed();
            }
        }else{
            backPressCloseHandler.onBackPressed();
        }
    }

    public double getlat() {
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
//        Log.d("test_getlat", String.valueOf(location));
        if (location != null) {
            return location.getLatitude();
        } else return 0;
    }

    public double getlng() {
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if (location != null) {
            Log.d("test_location", String.valueOf(location.getLongitude()));
            return location.getLongitude();
        } else return 0;

    }


    //??????????????? ????????? ??? ????????? ?????? ????????? ????????? ??? ??????
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        Uri[] results = null;
        if(resultCode==RESULT_OK) {
            //????????? ?????? ?????? ????????? ?????? ????????????.
            if(requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE){
                Uri[] result = new Uri[0];
                Log.d("filePath1", mCapturedImageURI.toString());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                    if (resultCode == RESULT_OK) {
                        result = (intent == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
                    }
                    //filePathCallbackLollipop.onReceiveValue(result);
                    //?????? ??????????????? ??????
                    CropImage.activity(result[0])
                            .setGuidelines(CropImageView.Guidelines.ON)//?????????????????? ????????? ????????? ??????
                            .start(this);


                }


            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();
                    Uri[] result2 = new Uri[0];
                    result2 =  new Uri[]{resultUri} ;

                    Log.d("image-uri", resultUri.toString());
                    filePathCallbackLollipop.onReceiveValue(result2);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }else{
            try {
                if (filePathCallbackLollipop != null) {
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
            }catch (Exception e){

            }
        }

    }


    //????????? ????????????
    class WebJavascriptEvent{
        @JavascriptInterface
        public void setLogin(String mb_id){
            Log.d("login","?????????");
            Common.savePref(getApplicationContext(),"ss_mb_id",mb_id);
        }
        @JavascriptInterface
        public void setLogout(){
            Log.d("logout","????????????");
            Common.savePref(getApplicationContext(),"ss_mb_id","");
        }
        @JavascriptInterface
        public void doShare(String url){
            Intent sharedIntent = new Intent();
            sharedIntent.setAction(Intent.ACTION_SEND);
            sharedIntent.setType("text/plain");
            sharedIntent.putExtra(Intent.EXTRA_SUBJECT, "???????????????");
            sharedIntent.putExtra(Intent.EXTRA_TEXT, url);
            Intent chooser = Intent.createChooser(sharedIntent, "??????");
            startActivity(chooser);
        }
        @JavascriptInterface
        public void getnow() {
            Log.d("testtest", "test??????");
            double lat = getlat() * 1000000;
            double lng = getlng() * 1000000;
            lat = Math.ceil(lat) / 1000000;
            lng = Math.ceil(lng) / 1000000;
            final double finalLat = lat;
            final double finalLng = lng;
            Log.d("test_lat", String.valueOf(lat));
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:set_initlocate(" + finalLat + "," + finalLng + ");");

                }
            });
            //Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();


        }


    }
}