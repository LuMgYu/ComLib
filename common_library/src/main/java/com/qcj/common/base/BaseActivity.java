package com.qcj.common.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.qcj.common.AppConfig;
import com.qcj.common.AppManager;
import com.qcj.common.R;
import com.qcj.common.helper.SystemBarTintManager;
import com.qcj.common.interf.DialogControl;
import com.qcj.common.interf.UIInterface;
import com.qcj.common.util.Anim;
import com.qcj.common.util.DialogHelp;
import com.qcj.common.util.TDevice;

import java.io.Serializable;


/**
 * author：qiuchunjia
 */
public abstract class BaseActivity extends AppCompatActivity implements
        DialogControl, UIInterface, PopView.PopResultListener {
    public String TAG = this.getClass().toString();  //tag标志
    private boolean _isVisible = true;
    private ProgressDialog _waitDialog;
    protected LayoutInflater mInflater;
    private SystemBarTintManager mTintManager;
    private ViewGroup mRootLayout;  //根布局
    private Toolbar mToolbar;
    public BaseApplication mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeSetContentLayout();
        mInflater = LayoutInflater.from(this);
        mApp = BaseApplication.getInstance();
        AppManager.getAppManager().addActivity(this);
        initHint();
        setWindowView();
        init(savedInstanceState);
        initView();
        initData();
        setListener();
        _isVisible = true;
    }


    /**
     * 设置界面
     */
    private void setWindowView() {
        if (hasToolBar()) {
            setContentView(R.layout.activity_base);
            mRootLayout = (ViewGroup) findViewById(R.id.root_layout);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mInflater.inflate(getLayoutId(), mRootLayout);
            mToolbar.setTitle("");
            setSupportActionBar(mToolbar);
        } else {
            setContentView(getLayoutId());
        }
    }

    public void setToolbarTitle(String str) {
        if (mToolbar != null) {
            TextView textView = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
            textView.setText(str);
        }
    }

    public void setToolbarBackGone() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(null);
        }
    }

    protected void onBeforeSetContentLayout() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    /**
     * 初始化沉浸式菜单栏
     */
    protected void initHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        int[] ATTRS = {
                R.attr.colorPrimaryDark,
        };
        TypedArray typedArray = this.getTheme().obtainStyledAttributes(ATTRS);
        int color = typedArray.getColor(0, -1);
        typedArray.recycle();
        mTintManager.setTintColor(color);
    }

    /**
     * 设置沉浸式菜单
     *
     * @param on
     */
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 是否显示back键
     *
     * @param isShow
     */
    public void showBackIcon(boolean isShow) {
        if (!isShow && mToolbar != null) {
            mToolbar.setNavigationIcon(null);
        }
    }

    protected boolean hasToolBar() {
        return true;
    }

    protected abstract int getLayoutId();

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {
        //钩子方法子类想实现就实现
    }

    @Override
    public void setListener() {
        //钩子方法子类想实现就实现
    }

    @Override
    public void onClick(View v) {
        //钩子方法子类想实现就实现
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog("加载中...");
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    @Override
    public ProgressDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 替换fragment
     *
     * @param viewId
     * @param fragment
     */
    public void replaceFragment(int viewId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(viewId, fragment);
        ft.commit();
    }


    @Override
    public void finish() {
        super.finish();
        Anim.exit(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*************************
     * 设置返回上一个activity的数据 以及获取activity传来的数据
     ******************************/
    public static final int GET_DATA_FROM_ACTIVITY = 1; // 用于activity数据传递
    public static final int ACTIVTIY_TRANFER = 2; // 用于activity数据传递

    /**
     * 设置序列化返回
     * <p/>
     * 只要传递的值满足序列化就可以了！不管是对象还是对象集合
     * 该方法用于当前activity返回后给上一个acvivity传值 对应解析的方法为getReturnResultSeri
     *
     * @param serializable
     * @param flag
     */
    public void setReturnResultSeri(Serializable serializable, String flag) {
        String defaultFlag = AppConfig.ACTIVITY_TRANSFER_BUNDLE;
        if (flag != null) {
            defaultFlag = flag;
        }
        if (serializable != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(defaultFlag, serializable);
            intent.putExtras(bundle);
            this.setResult(BaseActivity.ACTIVTIY_TRANFER, intent);
        }
    }

    public void setReturnResultSeri(Serializable serializable) {
        setReturnResultSeri(serializable, null);
    }


    public void setReturnResultPar(Parcelable parcelable, String flag) {
        String defaultFlag = AppConfig.ACTIVITY_TRANSFER_BUNDLE;
        if (flag != null) {
            defaultFlag = flag;
        }
        if (parcelable != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable(defaultFlag, parcelable);
            intent.putExtras(bundle);
            this.setResult(BaseActivity.ACTIVTIY_TRANFER, intent);
        }
    }

    public void setReturnResultPar(Parcelable parcelable) {
        setReturnResultPar(parcelable, null);
    }

    /**
     * 获取结果
     *
     * @param flag 区分传的值
     * @return
     */
    public <T extends Serializable> T getReturnResultSeri(int resultCode, Intent intent,
                                                          String flag) {
        String defaultFlag = AppConfig.ACTIVITY_TRANSFER_BUNDLE;
        if (resultCode == BaseActivity.ACTIVTIY_TRANFER && intent != null) {
            if (flag != null) {
                defaultFlag = flag;
            }
            Bundle bundle = intent.getExtras();
            return (T) bundle.getSerializable(defaultFlag);
        }
        return null;
    }

    /**
     * 获取结果
     *
     * @return
     */
    public <T extends Serializable> T getReturnResultSeri(int resultCode, Intent intent) {
        return getReturnResultSeri(resultCode, intent, null);
    }

    /**
     * 把数据封装到bundel中 用于传递
     */
    public Bundle sendDataToBundle(Serializable serializable, String flag) {
        String defaultFlag = AppConfig.ACTIVITY_TRANSFER_BUNDLE;
        if (flag != null) {
            defaultFlag = flag;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(defaultFlag, serializable);
        return bundle;
    }

    /**
     * 把数据封装到bundel中 用于传递
     */
    public Bundle sendDataToBundle(Serializable serializable) {
        return sendDataToBundle(serializable, null);
    }

    /**
     * 从intent获取里面的bundle然后在获取里面的值
     *
     * @param intent
     * @param flag   可以传可以不传，有默认的
     * @return
     */
    public <T extends Serializable> T getDataFromIntent(Intent intent, String flag) {
        String defaultFlag = AppConfig.ACTIVITY_TRANSFER_BUNDLE;
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (flag != null) {
                    defaultFlag = flag;
                }
                return (T) bundle.getSerializable(defaultFlag);
            }
        }
        return null;
    }

    /**
     * 从intent获取里面的bundle然后在获取里面的值
     *
     * @param intent
     * @return
     */
    public <T extends Serializable> T getDataFromIntent(Intent intent) {
        return getDataFromIntent(intent, null);
    }

    /*************************
     * 设置返回上一个activity的数据 end
     ******************************/

    /*************************
     * popwindow 返回到数据 end
     ******************************/
    @Override
    public Object onPopResult(Object object, int wtf) {
        return null;
    }


}
