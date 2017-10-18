package com.pangge.interviewcustomview.view;

import android.content.Context;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jakewharton.rxbinding2.view.RxView;
import com.pangge.interviewcustomview.R;
import com.pangge.interviewcustomview.internetService.WordService;
import com.pangge.interviewcustomview.model.Definition;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by iuuu on 17/10/13.
 */

public class SelectableText extends AppCompatTextView implements CustomTextView{
    //private SelectionChangeListener listener;

    public CustomTextView customTextView;


    private static final String BASE_URL = " https://api.shanbay.com/";

    private CompositeDisposable compositeDisposable;

    private CharSequence mText;
    private int touchX;
    private int touchY;
    private PopupWindow popupWindow = new PopupWindow();

    private static final int DEFAULT_WIDTH = -1;
    private static final int DEFAULT_HEIGHT = -1;

    private final Point currLoc = new Point();
    private final Point startLoc = new Point();

    private final Rect cbounds = new Rect();

    private MediaPlayer mediaPlayer;

    private TextView tipText;
    private TextView tipPron;
    private TextView tipDefinition;
    private ImageButton tipAudio;




    public SelectableText(Context context) {
        super(context);
       // this.customTextView = customTextView;
    }


    public SelectableText(Context context, AttributeSet attrs) {
        super(context, attrs);

        compositeDisposable = new CompositeDisposable();


        final LayoutInflater inflater = LayoutInflater.from(context);

        View popupView = inflater.inflate(R.layout.tip_activity,null);

        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        tipText = (TextView)popupView.findViewById(R.id.tip_text);
        tipPron = (TextView)popupView.findViewById(R.id.phon);
        tipDefinition = (TextView)popupView.findViewById(R.id.definition);

        tipAudio = (ImageButton)popupView.findViewById(R.id.volumeBtn);

        mText = "";



    }



    public void setCustomTextView(CustomTextView customTextView){
        this.customTextView = customTextView;
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if(customTextView != null){
            if(hasSelection()){
                onTextSelected();

               // getTouchX();

            }else {
                onTextUnselected();
                Log.i("--","Unselected");

            }
        }
    }

    @Override
    public void onTextSelected() {
        tipText.setText("");
        tipPron.setText("");
        tipDefinition.setText("");
        final View popupContent = popupWindow.getContentView();

        String selectedText = getSelectedText();

        getDefinition(selectedText);
        onGlobalLayout(this, ()->{
            popupWindow.showAtLocation(this, Gravity.TOP, 0, 0);

            onGlobalLayout(popupContent, ()-> {

                popupContent.getLocalVisibleRect(cbounds);

                int height = popupContent.getHeight();
                final int y = touchY > height ? touchY- height:touchY+cbounds.centerY();

                popupWindow.update(touchX, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);



            });

        });



    }



    @Override
    public void onTextUnselected() {
        popupWindow.dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            touchX = (int)event.getX();
            touchY = (int)event.getY();

        }
        return super.onTouchEvent(event);
    }

    @Override
    public String getSelectedText() {
        final int start = getSelectionStart();
        final int end = getSelectionEnd();
        mText = getText().toString();
        return String.valueOf(
                start > end ? mText.subSequence(end, start) : mText.subSequence(start, end));


    }

    private void getDefinition(String word){
        WordService wordService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //add Rxjava
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(WordService.class);


        compositeDisposable.add(wordService.getDefinition(word)
                .map(jsonObjectResponse -> jsonObjectResponse.body().get("data"))
                .flatMap(this::parseJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Definition.DataBean>() {
                    @Override
                    public void onNext(Definition.DataBean dataBean) {
                        Log.i("ss",dataBean.getContent()+"");
                        if(dataBean.getContent()==null)
                        {
                            //can not find any definition
                            Log.i("未找到","any definition");
                            tipText.setText(getSelectedText());
                            tipDefinition.setText("未找到任何释义");
                        }else {
                            String content = dataBean.getContent();
                            String pron = dataBean.getPronunciation();
                            String defi = dataBean.getDefinition();
                            String audio = dataBean.getAudio();

                            tipText.setText(content);
                            tipPron.setText(pron);
                            tipDefinition.setText(defi);
                            // mediaPlayer = MediaPlayer.create(ActionModePopupActivity.this, Uri.parse(audio));

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            // mediaPlayer.setDataSource(audio);

                            try {

                                mediaPlayer.setDataSource(audio);
                                mediaPlayer.prepare();
                                // mediaPlayer.prepareAsync();

                            }catch (Exception e){
                                Log.i("play_error",e.toString());
                            }
                            RxView.clicks(tipAudio).subscribe(o -> playMedia());

                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));


    }



    private void playMedia(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();

        }

    }

    private Observable<Definition.DataBean> parseJson(JsonElement jsonElement){
        return Observable.create(new ObservableOnSubscribe<Definition.DataBean>() {
            @Override
            public void subscribe(ObservableEmitter<Definition.DataBean> e) throws Exception {

                Gson gson = new Gson();
                Definition.DataBean dataBean = gson.fromJson(jsonElement, Definition.DataBean.class);
                e.onNext(dataBean);

            }

        }).subscribeOn(Schedulers.newThread());
    }

    public static void onGlobalLayout(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runnable.run();
            }

        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }


}
