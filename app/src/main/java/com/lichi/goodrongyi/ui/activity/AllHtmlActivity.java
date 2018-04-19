package com.lichi.goodrongyi.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lichi.goodrongyi.R;
import com.lichi.goodrongyi.mvp.presenter.WebPresenter;
import com.lichi.goodrongyi.mvp.view.WebHtmlView;
import com.lichi.goodrongyi.ui.base.BaseActivity;
import com.lichi.goodrongyi.utill.AppManager;
import com.lichi.goodrongyi.utill.Constants;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * H5+分享页面
 */
public class AllHtmlActivity extends BaseActivity<WebHtmlView, WebPresenter> implements View.OnClickListener {

    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_shar)
    ImageView ivShar;
    @BindView(R.id.webview)
    WebView webview;
    private PopupWindow mPopWindow;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    public int ID;
    public String name;
    public String url;
    public boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO,根据ID查询对的H5页面
        ID = getIntent().getIntExtra(Constants.IntentParams.ID, -1);
        name = getIntent().getStringExtra(Constants.IntentParams.ID2);
        url = getIntent().getStringExtra(Constants.IntentParams.ID3);
        isShow = getIntent().getBooleanExtra(Constants.IntentParams.ID4, false);
        setContentView(R.layout.activity_all_html);
        ButterKnife.bind(this);
        init();
        tvTitle.setText(name);
        initWebView();
        if (isShow) {
            ivShar.setVisibility(View.VISIBLE);
        } else {
            ivShar.setVisibility(View.GONE);
        }

        webview.loadUrl(Constants.BASE_H5URL + "/dist/overview/index.html?id=" + ID + "&type=app");
        // webview.loadUrl("https://www.baidu.com");
    }

    private void initWebView() {
        WebSettings ws = webview.getSettings();
        //这句话去掉也没事。。只是设置了编码格式
        ws.setDefaultTextEncodingName("utf-8");
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 缩放比例 1
//        webView.setInitialScale(1);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        ws.setSupportMultipleWindows(true);
        ws.setDomStorageEnabled(true);//开启DOM storage API功能

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //ws.setTextZoom(100);
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.setWebViewClient(new MyWebViewClient());

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        /**
         * 当网页里a标签target="_blank"，打开新窗口时，这里会调用
         */
        @Override
        public boolean onCreateWindow(WebView webView, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(AllHtmlActivity.this);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            newWebView.setWebChromeClient(new MyWebChromeClient());
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    webview.loadUrl(url);
                    //防止触发现有界面的WebChromeClient的相关回调
                    return true;
                }
            });
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
//        return super.onCreateWindow(webView, isDialog, isUserGesture, message);
        }
    }

    public void init() {
        mShareListener = new AllHtmlActivity.CustomShareListener(AllHtmlActivity.this);
        /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(AllHtmlActivity.this).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
                            Toast.makeText(AllHtmlActivity.this, "复制文本按钮", Toast.LENGTH_LONG).show();
                        } else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
                            Toast.makeText(AllHtmlActivity.this, "复制链接按钮", Toast.LENGTH_LONG).show();

                        } else if (share_media == SHARE_MEDIA.SMS) {
                            new ShareAction(AllHtmlActivity.this).withText("来自分享面板标题")
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        } else {
                            UMWeb web = new UMWeb(Constants.BASE_H5URL + "/dist/overview/index.html?id=" + ID + "&navi=1" + "&type=app");
                            web.setTitle(name);
                            web.setDescription("融开心");
                            web.setThumb(new UMImage(AllHtmlActivity.this, url));
                            new ShareAction(AllHtmlActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
  /*                          UMImage imageurl = new UMImage(AllHtmlActivity.this, "https://mobile.umeng.com/images/pic/home/social/img-1.png");
                            //imageurl.setThumb(new UMImage(AllHtmlActivity.this, "https://mobile.umeng.com/images/pic/home/social/img-1.png"));
                            new ShareAction(AllHtmlActivity.this).withMedia(imageurl)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener).share();*/
                        }
                    }
                });

    }

    @OnClick({R.id.tv_back, R.id.iv_shar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                AllHtmlActivity.this.finish();
                break;
            case R.id.iv_shar:
/*                //设置contentView
                View contentView = LayoutInflater.from(AllHtmlActivity.this).inflate(R.layout.share_popup_window, null);
                mPopWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopWindow.setContentView(contentView);
                mPopWindow.setTouchable(true);
                mPopWindow.setOutsideTouchable(true);
                mPopWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                //设置各个控件的点击响应
                contentView.findViewById(R.id.tv_weibo).setOnClickListener(this);
                contentView.findViewById(R.id.wechat).setOnClickListener(this);
                contentView.findViewById(R.id.pengyouquan).setOnClickListener(this);
                contentView.findViewById(R.id.qq).setOnClickListener(this);
                contentView.findViewById(R.id.copy).setOnClickListener(this);
                contentView.findViewById(R.id.brows).setOnClickListener(this);
                contentView.findViewById(R.id.cancel).setOnClickListener(this);
                //显示PopupWindow
                View rootview = LayoutInflater.from(AllHtmlActivity.this).inflate(R.layout.activity_all_html, null);
                mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);*/
                ShareBoardConfig config = new ShareBoardConfig();
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
                config.setTitleText("分享");
                mShareAction.open(config);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<AllHtmlActivity> mActivity;

        private CustomShareListener(AllHtmlActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

            //Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public WebPresenter initPresenter() {
        return new WebPresenter();
    }
}