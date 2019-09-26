//package online.hualin.ipmsg.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import online.hualin.ipmsg.R;
//
//public class MainContentFrag extends Fragment {
//    public static final String ARGS_PAGE = "args_page";
//    private int mPage;
//    private String[] tabTitiles = new String[]{"设备","历史","文件"};
//
//
//    public MainContentFrag(int page) {
////        mPage=page;
//        Bundle args = new Bundle();
//        args.putInt(ARGS_PAGE, page);
//        MainContentFrag fragment = new MainContentFrag();
//        fragment.setArguments(args);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mPage = getArguments().getInt(ARGS_PAGE);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
////        }
//    }
//}