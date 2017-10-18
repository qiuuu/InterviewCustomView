package com.pangge.interviewcustomview.internetService;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by iuuu on 17/10/7.
 */

public interface ImageService {
    @GET
    Observable<ResponseBody> downloadImage(@Url String imageUrl);

}
