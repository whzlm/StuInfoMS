package com.cc2019.stuinfoms.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.ejlchina.okhttps.Config;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.HttpTask;

import java.util.concurrent.atomic.AtomicInteger;


public class OkHttpsConfig implements Config {


    // 绑定到主线程的 Handler
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ProgressDialog loading = null;
    private AtomicInteger loadings = new AtomicInteger(0);

    @Override
    public void with(HTTP.Builder builder) {
        // 在这里对 HTTP.Builder 做一些自定义的配置
        //baseUrl，或传入全路径则baseUrl不生效，
        builder.baseUrl("https://api.domo.com")
                // 配置默认回调在主线程执行
                .callbackExecutor(run -> mainHandler.post(run))
                // 实现生命周期绑定
                .addPreprocessor(chain -> {
                    HttpTask<?> task = chain.getTask();
                    Object bound = task.getBound();
                    task.bind(new BoundWrapper(task, bound));
                    chain.proceed();
                })
                .addPreprocessor(chain -> {
                    HttpTask<?> task = chain.getTask();
                    // 根据标签判断是否显示加载框
                    if (task.isTagged(Tags.LOADING)) {
                        showLoading(context(task));
                    }
                    chain.proceed();
                })
                // 错误码统一处理
                .responseListener((HttpTask<?> task, HttpResult result) -> {
                    // 刷新 Token 的接口可以例外
                    if (result.isSuccessful()) {
                        // 这里演示的是 HTTP 状态码的处理，如果有自定义的 code, 也可以进行深层次的判断task.getUrl().contains(Urls.TOKEN_REFRESH)
                        //                            ||
                        return true;            // 继续接口的业务处理
                    }
                    // 向用户展示接口的错误信息（视情况是否需要放到主线程执行）
                    showMsgToUser(task, result.getBody().toString());
                    return false;               // 阻断
                })
                .completeListener((HttpTask<?> task, HttpResult.State state) -> {
                    Object bound = task.getBound();
                    if (bound instanceof BoundWrapper) {
                        // 放到主线程执行
                        mainHandler.post(() -> ((BoundWrapper) bound).unbind());
                    }
                    // 网络错误统一处理（视情况判断是否需要放到主线程上执行）
                    switch (state) {
                        case TIMEOUT:
                            showMsgToUser(task, "网络连接超时");
                            break;
                        case NETWORK_ERROR:
                            showMsgToUser(task, "网络错误，请检查WIFI或数据是否开启");
                            break;
                        case EXCEPTION:
                            showMsgToUser(task, "接口请求异常: " + task.getUrl());
                            break;
                    }
                    if (task.isTagged(Tags.LOADING)) {
                        hideLoading();          // 关闭加载框
                    }
                    return true;
                });
        ;

    }

    /**
     * 获取 Context 对象
     **/
    private Context context(HttpTask<?> task) {
        Object bound = task.getBound();
        if (bound instanceof BoundWrapper) {
            bound = ((BoundWrapper) bound).bound;
        }
        if (bound instanceof Context) {
            return (Context) bound;
        }
        if (bound instanceof Fragment) {
            return ((Fragment) bound).getActivity();
        }
        // 还可以添加更多获取 Context 的策略，比如从全局 Application 里取
        return null;
    }


    static class BoundWrapper implements LifecycleObserver {

        HttpTask<?> task;
        Lifecycle lifecycle;
        Object bound;

        BoundWrapper(HttpTask<?> task, Object bound) {
            this.task = task;
            if (bound instanceof LifecycleOwner) {
                lifecycle = ((LifecycleOwner) bound).getLifecycle();
                lifecycle.addObserver(this);
            }
            this.bound = bound;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop() {
            task.cancel();  // 在 ON_STOP 事件中，取消对应的 HTTP 任务
        }

        void unbind() {
            if (lifecycle != null) {
                lifecycle.removeObserver(this);
            }
        }

    }

    // 显示加载框
    private void showLoading(Context ctx) {
        if (loading == null) {
            // 这里就用 ProgressDialog 来演示了，当然可以替换成你喜爱的加载框
            loading = new ProgressDialog(ctx);
            loading.setMessage("正在加载，请稍等...");
        }
        // 增加加载框显示计数
        loadings.incrementAndGet();
        loading.show();
    }

    // 关闭加载框
    private void hideLoading() {
        // 判断是否所有显示加载框的接口都已完成
        if (loadings.decrementAndGet() <= 0
                && loading != null) {
            loading.dismiss();
            loading = null;
        }
    }

    /**
     * 向用户展示信息
     **/
    private void showMsgToUser(HttpTask<?> task, String message) {
        // 这里就简单用 Toast 示例一下，有更高级的实现可以替换
        Context ctx = context(task);
        if (ctx != null) {
            mainHandler.post(() -> {
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            });
        }
    }

}
