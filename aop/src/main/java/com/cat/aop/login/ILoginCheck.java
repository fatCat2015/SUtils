package com.cat.aop.login;

import android.app.Activity;
import android.content.Context;

public interface ILoginCheck {

    /**
     * 判断登录状态
     * @param context
     * @return
     */
    boolean isLoggedIn(Context context);

    /**
     * 启动登录页面之后立即调用 可以用于自定义页面切换动画
     * @param activity
     */
    void onStartLoginActivity(Activity activity);

}
