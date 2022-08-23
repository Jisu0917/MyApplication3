package com.example.myapplication2.api;

import com.example.myapplication2.api.dto.FeedbacksData;
import com.example.myapplication2.api.dto.FriendsData;
import com.example.myapplication2.api.dto.LoginRequestDto;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @POST("/api/login")
    Call<LoginRequestDto> postLoginToken(@Body LoginRequestDto param);

    @GET("/api/users/{id}")
    Call<UserInfoData> getUserInfo(@Path("id") Long id);

    ///////////////////////

    // 회원 검색
    @GET("/api/users/search")
    Call<UserInfoData> searchUserInfo(@Path("name") String name);

    // 친구 맺기
    @POST("/api/friends/{id}")
    Call<FriendsData> makeFriends(@Body FriendsData param);

    // 연습
    // 새 연습 만들기
    @POST("/api/practices")
    Call<Long> postNewPractice(@Body PracticesData param);

    // 연습 수정하기
    @POST("/api/practices/{practice_id}")
    Call<PracticesData> updatePractice(@Body PracticesData param);

    // 특정 연습 정보 가져오기
    @GET("/api/practices/{practice_id}")
    Call<PracticesData> getPracticeInfo(@Path("practice_id") Long id);

    // 연습 제목으로 내 연습 검색하기
    @GET("/api/practices/search/{user_id}")
    Call<PracticesData> searchPractice(@Path("user_id") String title);

    // 연습 삭제하기
    @DELETE("/api/practices/{practice_id}")
    Call<PracticesData> deletePractice(@Path("practice_id") Long id);

    // 커뮤니티 게시글
    // 게시글 작성하기
    @POST("/api/posts")
    Call<Long> makeNewPost(@Body PostsData param);

    // 전체 게시글 가져오기
    @GET("/api/posts")
    Call<ArrayList<PostsData>> getAllPosts();

    // 특정 게시글 가져오기
    @GET("/api/posts/{post_id}")
    Call<PostsData> getPostId(@Path("post_id") Long id);

    // 게시글 삭제하기
    @DELETE("/api/posts/{post_id}")
    Call<PostsData> deletePostId(@Path("post_id") Long id);

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
    Call<JsonObject> getPresignedURL(@Body Integer[] ids);

    //@PUT("")

    @POST("http://13.125.254.29:8080/analysis/{user_id}/{practice_id}/{gender}/{pose_sensitivity}/{eyes_sensitivity}")
    Call<StatusCallback> askAnalysis();
}
