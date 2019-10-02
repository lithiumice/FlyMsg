//package online.hualin.flymsg.model;
//
//
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//import sample.kingja.loadsirbestpractice.app.Constants;
//import sample.kingja.loadsirbestpractice.model.entiy.Repository;
//import sample.kingja.loadsirbestpractice.model.entiy.SearchResult;
//
///**
// * 项目名称：和ApiService相关联
// * 类描述：TODO
// * 创建人：KingJA
// * 创建时间：2016/6/13 15:11
// * 修改备注：
// */
//public class Api {
//
//    private ApiService apiService;
//
//    public Api() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(20, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(client)
//                .build();
//
//
//        apiService = retrofit.create(ApiService.class);
//    }
//
//
//
//    public Observable<SearchResult<Repository>> getFollower(String user) {
//        return apiService.getFollower(user);
//    }
//
//
//}
