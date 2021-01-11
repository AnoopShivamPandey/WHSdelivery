package app.provider.bestpricedelivery.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    String TAG = "ApiService";

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/send-otp")
    Call<String> sendotp(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/send-otp")
    Call<String> sendotprestaurant(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/validate-otp")
    Call<String> validate_otp(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/validate-otp")
    Call<String> validate_otprestaurant(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/order-list")
    Call<String> getStallListing(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/orderDetail")
    Call<String> getOrderDetailForRestaurant(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/order-detail")
    Call<String> getOrderDetail(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/order-detail")
    Call<String> getStallDetails(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/orderDetail")
    Call<String> getOrderDetails(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/update-profile-status")
    Call<String> updateProfileStatus(
            @Query("api_token") String token,
            @Body String body);


    @Headers("Content-Type: application/json")
    @POST("customer/general-settings")
    Call<String> getGeneralSettings(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/logOut")
    Call<String> logoutUser(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/manage-order")
    Call<String> acceptrejectOrder(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/acceptOrder")
    Call<String> acceptOrder(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/cancelOrder")
    Call<String> cancelOrder(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/update-latlong")
    Call<String> updateUserLocation(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/tender-stall-list")
    Call<String> getStallList(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/restaurantDishList")
    Call<String> getInventoryList(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/update-stall-products")
    Call<String> putUpdatedProducts(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("restaurant/restaurantOrder")
    Call<String> getOrderList(
            @Query("api_token") String token,
            @Body String body);

    @Headers("Content-Type: application/json")
    @POST("deliveryboy/change-order-status")
    Call<String> changeOrderStatus(
            @Query("api_token") String token,
            @Body String body);

    //
//
//    @Headers("Content-Type: application/json")
//    @POST("company-signup")
//    Call<String> companyRegistration(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("candidate-signup")
//    Call<String> candidateRegistration(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("candidate/send-otp-for-both")
//    Call<String> forgotPassword(@Body String userInfo);
//
//
//    @Headers("Content-Type: application/json")
//    @POST("login")
//    Call<String> login(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("create-new-password")
//    Call<String> resetPassword(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("otp-verification")
//    Call<String> passOtp(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("getDropDown")
//    Call<String> getDropDown(@Body String userInfo);
//
//    @Headers("Content-Type: application/json")
//    @POST("siteverify")
//    Call<String> getRecaptchValidation(@Field("secret") String secret,
//                                       @Field("response") String response);
//
//
//    @Multipart
//    @POST("upload_file")
//    Call<ResponseBody> upload_file(@Part MultipartBody.Part input_files,
//                                   @Part("token_id") RequestBody token_id,
//                                   @Part("session_id") RequestBody session_id,
//                                   @Part("microstep_id") RequestBody microstep_id);

}
