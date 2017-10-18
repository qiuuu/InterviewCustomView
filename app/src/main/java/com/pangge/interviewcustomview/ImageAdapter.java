package com.pangge.interviewcustomview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pangge.interviewcustomview.model.Image;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by iuuu on 17/10/7.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder> {
    private List<Image> images;
    private ImageAdapterViewHolder imageViewHolder;
    private Context mContext;





    public ImageAdapter(Context context,List<Image> images){
        this.mContext = context;
        this.images = images;

        Log.i("5","Constructor sucessful");


    }


    @Override
    public ImageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent,false);

        imageViewHolder = new ImageAdapterViewHolder(view);

        Log.i("5","create Viewholder");


        return imageViewHolder;
    }


    @Override
    public void onBindViewHolder(ImageAdapter.ImageAdapterViewHolder holder, int position) {

        Log.i("5","bind Viewholder");

        GlideApp.with(mContext)
                .load(images.get(position).getImage())
                .into(holder.image);

    }


    @Override
    public int getItemCount() {
        Log.i("5",images.size()+"--size");

        return images.size();
    }

    public class ImageAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.list_item)
        ImageView image;
        public ImageAdapterViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);

            Log.i("5","AdapterViewHolder");

        }
    }
}
