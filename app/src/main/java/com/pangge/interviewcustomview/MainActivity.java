package com.pangge.interviewcustomview;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.customView)
    Button toCustomView;
    @BindView(R.id.image)
    Button toLoadimage;
    private CompositeDisposable compositeDisposable;

    private PopupWindow pT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(RxView.clicks(toCustomView)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> setToCustomView()));
        compositeDisposable.add(RxView.clicks(toLoadimage)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> setToLoadimage()));
        if(isExternalStorageReadable()){
            Log.i("1", Environment.getExternalStorageDirectory().getAbsolutePath());
            Log.i("2",this.getExternalFilesDir("images").getAbsolutePath());
           // Log.i("3",Environment.getExternalStorageDirectory().getCanonicalPath().);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    private void setToCustomView(){
        Intent customIntent = new Intent(MainActivity.this, CustomViewActivity.class);
        startActivity(customIntent);


    }
    private void setToLoadimage(){
        Intent loadIntent = new Intent(MainActivity.this, LoadImageActivity.class);
        startActivity(loadIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
