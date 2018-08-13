package com.appdev.duoguidemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.appdev.duoguidemo.common.Constants;
import com.appdev.duoguidemo.entity.EditMode;
import com.appdev.duoguidemo.entity.Mark;
import com.appdev.duoguidemo.entity.MarkStyle;
import com.appdev.duoguidemo.entity.PointStyle;
import com.appdev.duoguidemo.fragment.BaseMarkFragment;
import com.appdev.duoguidemo.fragment.LineDialog;
import com.appdev.duoguidemo.fragment.PointDialog;
import com.appdev.duoguidemo.fragment.PolygonDialog;
import com.appdev.duoguidemo.listener.MapOperationListener;
import com.appdev.duoguidemo.presenter.IMarkPresenter;
import com.appdev.duoguidemo.presenter.impl.MarkPresenterImpl;
import com.appdev.duoguidemo.util.MarkUtil;
import com.appdev.duoguidemo.view.IMarkView;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.core.symbol.FillSymbol;
import com.esri.core.symbol.LineSymbol;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IMarkView {



    private Menu mMenuItem;
    private MapView mMapView;
    private RadioGroup rg_mark_choose;
    private LinearLayout ll_tool_operation;
    private ImageView iv_clear,iv_back_step,iv_save;
    private MapOperationListener mapListener;
    private boolean isShow;

    private IMarkPresenter iMarkPresenter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMapListener();
        initView();
        addLayer();
        iMarkPresenter = new MarkPresenterImpl(this);
    }

    private void initView() {
        rg_mark_choose = findViewById(R.id.rg_mark_choose);
        ll_tool_operation = findViewById(R.id.ll_tool_operation);
        iv_clear = findViewById(R.id.iv_clear);
        iv_back_step = findViewById(R.id.iv_back_step);
        iv_save = findViewById(R.id.iv_save);
        iv_clear.setOnClickListener(this);
        iv_back_step.setOnClickListener(this);
        iv_save.setOnClickListener(this);
        rg_mark_choose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_point:
                        actionClear();
                        if(isShow){
                            mapListener.setEditMode(EditMode.POINT);
                        }
                        break;
                    case R.id.rb_line:
                        actionClear();
                        mapListener.setEditMode(EditMode.POLYLINE);
                        break;
                    case R.id.rb_polygon:
                        actionClear();
                        mapListener.setEditMode(EditMode.POLYGON);
                        break;
                }
            }
        });
        rg_mark_choose.check(R.id.rb_point);

    }

    private void addLayer() {
        Layer layer = new ArcGISTiledMapServiceLayer(getResources().getString(R.string.map_url));
        mMapView.addLayer(layer);
    }


    private void initMapListener() {
        mMapView = findViewById(R.id.map_view);
        mapListener = new MapOperationListener(this,mMapView);
        mMapView.setOnTouchListener(mapListener);
        mapListener.setEditMode(EditMode.NONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        mMenuItem = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_clear:
                actionClear();
                break;
            case R.id.iv_back_step:
                actionBackStep();
                break;
            case R.id.iv_save:
                actionSave();
                break;
        }

    }

    private void actionClear() {
        mapListener.actionClear();
    }

    private void actionBackStep() {
        mapListener.actionBackStep();
    }

    private void actionSave() {
        iMarkPresenter.showAddMarkDialog(this,null);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                if(!isShow){
                    //开启图形绘制界面
                    //添加界面
                    rg_mark_choose.setVisibility(View.VISIBLE);
                    ll_tool_operation.setVisibility(View.VISIBLE);
                    //变换图标
                    setAction(R.id.action_add,R.mipmap.ic_action_cancel);
                    rg_mark_choose.check(R.id.rb_point);
                    mapListener.setEditMode(EditMode.POINT);
                }else {
                    //关闭图形绘制界面
                    //去除界面
                    rg_mark_choose.setVisibility(View.GONE);
                    ll_tool_operation.setVisibility(View.GONE);
                    //变换图标
                    setAction(R.id.action_add,R.mipmap.ic_action_new);
                    mapListener.actionClear();
                    mapListener.setEditMode(EditMode.NONE);
                }
                isShow=!isShow;
                return true;
            case R.id.action_search:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setAction(int resId, int icon) {
        MenuItem item = mMenuItem.findItem(resId);
        item.setIcon(icon);
    }

    @Override
    public void showAddPointDialog(final Mark mark, List<PointStyle> pointStyles) {
        //显示添加点的Dialog
        PointDialog pointDialog = PointDialog.newInstance(mark,pointStyles);
        pointDialog.setOnSaveButtonClickListener(new BaseMarkFragment.OnSaveButtonClickListener() {
                    @Override
                    public void onClick(MarkStyle allMarkInfo) {
                        Drawable drawable = MarkUtil.getPointDrawable(getApplicationContext(),allMarkInfo.getPointStyle());
                        MarkerSymbol symbol = new PictureMarkerSymbol(getApplicationContext(), drawable);
                        mark.setSymbol(symbol);
                        mark.setPointDrawableName(allMarkInfo.getPointStyle());
                        mark.setMarkMemo(allMarkInfo.getMarkMemo());
                        mark.setMarkName(allMarkInfo.getMarkName());
                        //保存到本地
                        iMarkPresenter.applyAdd(mark);
                    }
                });
        pointDialog.show(getSupportFragmentManager(),"PointDialog");
    }

    @Override
    public void showAddLineDialog(final Mark mark) {
        //显示添加线的Dialog
        LineDialog lineDialog = LineDialog.newInstance(mark);
        lineDialog.setOnSaveButtonClickListener(new BaseMarkFragment.OnSaveButtonClickListener() {
            @Override
            public void onClick(MarkStyle allMarkInfo) {
                //进行上传数据到服务端
                int width = Constants.DEFAULT_LINE_WIDTH;
                LineSymbol lineSymbol = new SimpleLineSymbol(allMarkInfo.getLineColor(), width);
                mark.setSymbol(lineSymbol);
                mark.setPointDrawableName(allMarkInfo.getPointStyle());
                mark.setMarkMemo(allMarkInfo.getMarkMemo());
                mark.setMarkName(allMarkInfo.getMarkName());
                iMarkPresenter.applyAdd(mark);
            }
        });
        lineDialog.show(getSupportFragmentManager(),"LineDialog");

    }

    @Override
    public void showAddPolygonDialog(final Mark mark) {
        //显示添加polygon的Dialog

        PolygonDialog polygonDialog = PolygonDialog.newInstance(mark);
        polygonDialog.setOnSaveButtonClickListener(new BaseMarkFragment.OnSaveButtonClickListener() {
            @Override
            public void onClick(MarkStyle allMarkInfo) {
                //进行上传数据到服务端
                FillSymbol fillSymbol = new SimpleFillSymbol(allMarkInfo.getInColor());
                LineSymbol lineSymbol = new SimpleLineSymbol(allMarkInfo.getLineColor(), 3);
                fillSymbol.setOutline(lineSymbol);
                mark.setSymbol(fillSymbol);
                mark.setPointDrawableName(allMarkInfo.getPointStyle());
                mark.setMarkMemo(allMarkInfo.getMarkMemo());
                mark.setMarkName(allMarkInfo.getMarkName());
                iMarkPresenter.applyAdd(mark);
            }
        });
        polygonDialog.show(getSupportFragmentManager(),"PolygonDialog");



    }
}
