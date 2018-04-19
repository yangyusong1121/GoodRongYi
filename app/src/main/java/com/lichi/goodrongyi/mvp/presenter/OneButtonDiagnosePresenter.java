package com.lichi.goodrongyi.mvp.presenter;

import com.lichi.goodrongyi.mvp.model.MainService;
import com.lichi.goodrongyi.mvp.model.StorJurisdictionBean;
import com.lichi.goodrongyi.mvp.view.IMainView;
import com.lichi.goodrongyi.mvp.view.OneButtonDiagnoseView;
import com.lichi.goodrongyi.utill.RXUtil;
import com.lichi.goodrongyi.utill.ServiceFactory;

import rx.Observer;
import rx.functions.Action0;

/**
 * Created by 默小小 on 2017/11/30.
 */

public class OneButtonDiagnosePresenter extends BasePresenter<OneButtonDiagnoseView> {
    private MainService mService;

    public OneButtonDiagnosePresenter() {
        mService = ServiceFactory.getMainService();
    }

    public void gainAppLsis(String token) {
    }

}
