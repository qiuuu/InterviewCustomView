package com.pangge.interviewcustomview.internetService;

import com.google.gson.JsonObject;


import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iuuu on 17/10/16.
 */

public interface WordService {
    @GET("bdc/search/?")
    Observable<Response<JsonObject>> getDefinition(
            @Query("word") String word);
}
