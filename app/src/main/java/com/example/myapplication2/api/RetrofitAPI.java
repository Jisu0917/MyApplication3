package com.example.myapplication2.api;

import android.net.Uri;

import com.example.myapplication2.api.dto.FeedbacksData;
import com.example.myapplication2.api.dto.FriendIdCodeData;
import com.example.myapplication2.api.dto.FriendsData;
import com.example.myapplication2.api.dto.LoginRequestDto;
import com.example.myapplication2.api.dto.PointData;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @POST("/api/login")
    Call<LoginRequestDto> postLoginToken(@Body LoginRequestDto param);

    @GET("/api/users/{id}")
    Call<UserInfoData> getUserInfo(@Path("id") Long id);

    ///////////////////////

    // 회원 검색
    @GET("/api/users/search")
    Call<UserInfoData[]> searchUserInfos(@Query("name") String name);
    @GET("/api/users/search")
    Call<UserInfoData> searchUserInfo(@Query("name") String name);

    // 친구 맺기
    @POST("/api/friends/{user_id}")
    Call<Long> makeFriend(@Path("user_id") Long user_id, @Body FriendIdCodeData friendIdCode);

    // 연습
    // 새 연습 만들기
    @POST("/api/practices")
    Call<Long> postNewPractice(@Body PracticesData param);

    // 연습 수정하기
    @POST("/api/practices/{practice_id}")
    Call<Long> updatePractice(@Path("practice_id") Long id, @Body PracticesData param);

    // 특정 연습 정보 가져오기
    @GET("/api/practices/{practice_id}")
    Call<PracticesData> getPracticeInfo(@Path("practice_id") Long id);

    // 연습 제목으로 내 연습 검색하기
    @GET("/api/practices/search/{user_id}")
    Call<PracticesData> searchPractice(@Path("user_id") String title);

    // 연습 삭제하기
    @DELETE("/api/practices/{practice_id}")
    Call<Long> deletePractice(@Path("practice_id") Long id);

    // 커뮤니티 게시글
    // 게시글 작성하기
    @POST("/api/posts")
    Call<Long> makeNewPost(@Body PostsData param);

    // 전체 게시글 가져오기
    @GET("/api/posts")
    Call<ArrayList<PostsData>> getAllPosts();

    // 특정 게시글 가져오기
    @GET("/api/posts/{post_id}")
    Call<PostsData> getPost(@Path("post_id") Long post_id);

    // 게시글 삭제하기
    @DELETE("/api/posts/{post_id}")
    Call<Long> deletePostId(@Path("post_id") Long id);

    // 피드백
    // 피드백 작성하기
    @POST("/api/feedbacks")
    Call<Long> makeNewFeedback(@Body FeedbacksData param);

    // 특정 피드백 정보 가져오기
    @GET("/api/feedbacks/{feedback_id}")
    Call<FeedbacksData> getFeedbackId(@Path("feedback_id") Long id);

    // 특정 연습에 대한 친구의 피드백 모두 가져오기
    @GET("/api/feedbacks/friends/{practice_id}")
    Call<ArrayList<FeedbacksData>> getFeedbackOfFriends(@Path("practice_id") Long id);

    // 특정 연습에 대한 제3자의 피드백 모두 가져오기
    @GET("/api/feedbacks/users/{practice_id}")
    Call<ArrayList<FeedbacksData>> getFeedbackOfUsers(@Path("practice_id") Long id);

    //////////////

    // 영상/음성 분석 요청
    // 임시 URL 발급 받기
    @POST("https://fo5by90j34.execute-api.ap-northeast-2.amazonaws.com/default/getPresignedURL")
    Call<JsonObject> getPresignedURL(@Body Integer[] ids, @Header("x-amz-meta-userid") Long user_id, @Header("x-amz-meta-practiceid") Long practice_id);

    @PUT("/{presignedUrl}")
    Call<StatusCallback> uploadVideo(@Path("presignedUrl") String presignedUrl, @Body File file);

    @GET("http://15.164.229.184:8080/analysis/{user_id}/{practice_id}/{gender}/{pose_sensitivity}/{eyes_sensitivity}")
    Call<StatusCallback> askAnalysis(@Path("user_id") int user_id, @Path("practice_id") int practice_id, @Path("gender") String gender, @Path("pose_sensitivity") int pose_sensitivity, @Path("eyes_sensitivity") int eyes_sensitivity);

//    // 음성 파일 받아오기
    @GET("/{user_id}/{practice_id}.wav")
    Call<String> getWavFile(@Path("user_id") int user_id, @Path("practice_id") int practice_id);
//    @GET("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/{practice_id}/{user_id}_{practice_id}.wav")
//    Call<String> getWavFile(@Path("user_id") int user_id, @Path("practice_id") int practice_id);

    // 포인트 수정하기
    @POST("/api/users/point")
    Call<Long> updatePoint(@Body PointData pointData);
}
