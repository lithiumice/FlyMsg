package online.hualin.flymsg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.ramotion.circlemenu.CircleMenuView;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;

import java.util.ArrayList;
import java.util.List;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.Activity;
import online.hualin.flymsg.adapter.ChatHisAdapter;
import online.hualin.flymsg.db.ChatHistory;

import static online.hualin.flymsg.App.getApplication;

public class HistoryFrag extends Fragment {
    private RecyclerView recyclerView;
    private Activity activity;
    private View view;
    private ChatHisAdapter adapter;
    private List<ChatHistory> chatHistories = new ArrayList<>();
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingSearchView searchView;
    private DaoSession daoSession = ((App) getApplication()).getDaoSession();
    private CircleMenuView circleMenu;

    @Override
    public void onStart() {
        super.onStart();
//        circleMenu = getActivity().findViewById(R.id.circle_menu);
//        circleMenu.setEventListener(new CircleMenuView.EventListener() {
//            @Override
//            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
//                Log.d("D", "onMenuOpenAnimationStart");
//            }
//
//            @Override
//            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
//                Log.d("D", "onMenuOpenAnimationEnd");
//            }
//
//            @Override
//            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
//                Log.d("D", "onMenuCloseAnimationStart");
//            }
//
//            @Override
//            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
//                Log.d("D", "onMenuCloseAnimationEnd");
//            }
//
//            @Override
//            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int index) {
//                Log.d("D", "onButtonClickAnimationStart| index: " + index);
//            }
//
//            @Override
//            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
//                Log.d("D", "onButtonClickAnimationEnd| index: " + index);
//            }
//        });


        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();

        chatHistories = chatHistoryDao.loadAll();
//        List<String> listString = new ArrayList<>();
//        for (ChatHistory chatHistory : chatHistories) {
//            listString.add(chatHistory.getSendMsg());
//        }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,listString);
//        listView.setAdapter(arrayAdapter);
        adapter = new ChatHisAdapter(chatHistories);
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_frag, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
//        searchView = view.findViewById(R.id.search_view);
//        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, final String newQuery) {
//
//                //get suggestions based on newQuery
//
//                //pass them on to the search view
////                searchView.swapSuggestions(newSuggestions);
//            }
//
//        });
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
                    this.onStart();
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }
}
