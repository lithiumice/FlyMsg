//package online.hualin.flymsg.model.result;
//
//
//import com.orhanobut.logger.Logger;
//
//import java.util.List;
//
//import io.reactivex.observers.DefaultObserver;
//import sample.kingja.loadsirbestpractice.base.BaseView;
//import sample.kingja.loadsirbestpractice.model.entiy.SearchResult;
//
///**
// * Description：TODO
// * Create Time：2016/10/12 15:56
// * Author:KingJA
// * Email:kingjavip@gmail.com
// */
//public abstract class SearchResultObserver<T> extends DefaultObserver<SearchResult<T>> {
//    private BaseView baseView;
//
//    public SearchResultObserver(BaseView baseView) {
//        this.baseView = baseView;
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        baseView.showLoading();
//    }
//
//    @Override
//    public void onNext(SearchResult httpResult) {
//        if (httpResult.getItems().size()>0) {
//            onSuccess(httpResult.getItems());
//        } else {
//            baseView.showEmpty();
//        }
//    }
//
//    protected abstract void onSuccess(List<T> t);
//
//    @Override
//    public void onError(Throwable e) {
//        //显示错误信息
//        //show error
//        Logger.e(e.toString());
//        baseView.showError();
//    }
//
//    @Override
//    public void onComplete() {
//
//    }
//
//
//}
