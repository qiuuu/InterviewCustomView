package com.pangge.interviewcustomview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pangge.interviewcustomview.internetService.ImageService;
import com.pangge.interviewcustomview.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by iuuu on 17/10/6.
 */

public class LoadImageActivity extends AppCompatActivity {
    @BindView(R.id.loadImage_list)
    RecyclerView mImageList;

    private List<Image> images = new ArrayList<>();

    private static final String BASE_URL = "https://static.baydn.com/media/media_store/image/";

    private static List<String> imageList;

    private CompositeDisposable compositeDisposable;

    private File file;

    private int i;

    // private LinearLayoutManager manager;
    private ImageAdapter adapter;

    private int saveImageNum = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ButterKnife.bind(this);
        file = this.getExternalFilesDir("image");


        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(prepareImage()

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new ImageAdapter(getApplicationContext(), images);

        mImageList.setHasFixedSize(true);
        mImageList.setLayoutManager(manager);
        mImageList.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private Observable<String> prepareImage() {
        Log.i("prepare--file",file.getAbsolutePath());
        //exist image in local
        if (isExternalStorageReadable() && file != null){
            Log.i("5", "external ok!");

            if(file.listFiles().length != 0) {

                Log.i("5", "local ok!");

                File[] files = file.listFiles();
                for(int j = 0; j< files.length; j++){
                    Log.i("5--"+j, files[j].getAbsolutePath());


                    Image image = new Image();
                    image.setImage(files[j].getAbsolutePath());
                    images.add(image);

                    //images.get(i).setImage(files[i].getAbsolutePath());
                }
                //imageUrl-->/storage/emulated/0/Android/data/com.pangge.interviewcustomview/files/images/

            } else {
                Log.i("5", " no image in local");

                //no images in local
                addImageUrl();
                Log.i("5", " Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)");

                downloadImages();
            }

        }else {
            Log.i("5", "external NO!!");
        }


        return Observable.just("prepare sucess");
    }

    private void addImageUrl() {
        imageList = new ArrayList<>();
        imageList.add("f1672263006c6e28bb9dee7652fa4cf6.jpg");
        imageList.add("8c997fae9ebb2b22ecc098a379cc2ca3.jpg");
        imageList.add("2a4616f067285b4bd59fe5401cd7106b.jpeg");
        imageList.add("b0e3ab329c8d8218d2af5c8dfdc21125.jpg");
        imageList.add("670abb28408a9a0fc3dd9666e5ca1584.jpeg");
        imageList.add("1e8d675468ab61f4e5bdebd4bcb5f007.jpeg");
        imageList.add("9b2f93cbfa104dae1e67f540ff14a4c2.jpg");
        imageList.add("f5e0631e00a09edbbf2eb21eb71b4d3c.jpeg");

    }


    private void downloadImages() {
        ImageService imageService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //add Rxjava
                //.addConverterFactory()
                .build().create(ImageService.class);

        for (i = 0; i<imageList.size(); i++) {
            Log.i("5-o--Sou", imageList.size()+"-----"+i);


            compositeDisposable.add(imageService.downloadImage(imageList.get(i))

                    .flatMap(new Function<ResponseBody, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(ResponseBody responseBody) throws Exception {
                            //Log.i("shaole---",saveImageToLocal(responseBody)+"");
                            /*if(saveImageToLocal(responseBody)){
                                addToFile();
                            }*/

                            return Observable.just(saveImageToLocal(responseBody));
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

                    .subscribe(o -> refresh()));



        }

    }
    private void refresh(){
        adapter.notifyDataSetChanged();

    }

    private boolean saveImageToLocal(ResponseBody responseBody){
      //  file.getAbsolutePath();
        Log.i("5-----------???", "save image to local");
        try {


            //System.currentTimeMillis()

            saveImageNum++;
            File imageFile = new File(file, saveImageNum+".jpg");
            FileOutputStream out = new FileOutputStream(imageFile);

            Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();
            out.close();
            Image image = new Image();
            Log.i("----",imageFile.getAbsolutePath());
            image.setImage(imageFile.getAbsolutePath());
            images.add(image);

            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;
        }



    }


    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}