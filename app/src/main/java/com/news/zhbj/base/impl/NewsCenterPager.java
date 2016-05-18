package com.news.zhbj.base.impl;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.news.zhbj.activity.MainActivity;
import com.news.zhbj.base.BaseMenuDetailPager;
import com.news.zhbj.base.BasePager;
import com.news.zhbj.domain.NewsData;
import com.news.zhbj.fragment.LeftMenuFragment;
import com.news.zhbj.global.GlobalContants;
import com.news.zhbj.menudetail.InteractMenuDetailPager;
import com.news.zhbj.menudetail.NewsMenuDetailPager;
import com.news.zhbj.menudetail.PhotoMenuDetailPager;
import com.news.zhbj.menudetail.TopicMenuDetailPager;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import java.util.ArrayList;

/**
 * 新闻中心
 * Created by 赖上罗小贱 on 2016/5/18.
 */
public class NewsCenterPager extends BasePager{
    private ArrayList<BaseMenuDetailPager> mPagers;// 4个菜单详情页的集合
    private NewsData mNewsData;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("初始化新闻中心数据....");

        tvTitle.setText("新闻");
        setSlidingMenuEnable(true);// 打开侧边栏

        getDataFromServer();
    }

    /**
     * 从服务器获取数据
     */
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();

        // 使用xutils发送请求
        utils.send(HttpMethod.GET, GlobalContants.CATEGORIES_URL,
                new RequestCallBack<String>() {

                    // 访问成功
                    @Override
                    public void onSuccess(ResponseInfo responseInfo) {
                        String result = (String) responseInfo.result;
                        System.out.println("返回结果:" + result);

                        parseData(result);
                    }

                    // 访问失败
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
                                .show();
                        error.printStackTrace();
                    }

                });
    }

    /**
     * 解析网络数据
     *
     * @param result
     */
    protected void parseData(String result) {
        Gson gson = new Gson();
        mNewsData = gson.fromJson(result, NewsData.class);
        System.out.println("解析结果:" + mNewsData);

        // 刷新测边栏的数据
        MainActivity mainUi = (MainActivity) mActivity;
        LeftMenuFragment leftMenuFragment = mainUi.getLeftMenuFragment();
        leftMenuFragment.setMenuData(mNewsData);

        // 准备4个菜单详情页
        mPagers = new ArrayList<BaseMenuDetailPager>();
        mPagers.add(new NewsMenuDetailPager(mActivity,
                mNewsData.data.get(0).children));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotoMenuDetailPager(mActivity));
        mPagers.add(new InteractMenuDetailPager(mActivity));

        setCurrentMenuDetailPager(0);// 设置菜单详情页-新闻为默认当前页
    }

    /**
     * 设置当前菜单详情页
     */
    public void setCurrentMenuDetailPager(int position) {
        BaseMenuDetailPager pager = mPagers.get(position);// 获取当前要显示的菜单详情页
        flContent.removeAllViews();// 清除之前的布局
        flContent.addView(pager.mRootView);// 将菜单详情页的布局设置给帧布局

        // 设置当前页的标题
        NewsData.NewsMenuData menuData = mNewsData.data.get(position);
        tvTitle.setText(menuData.title);

        pager.initData();// 初始化当前页面的数据
    }

}
