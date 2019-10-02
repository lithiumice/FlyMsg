package online.hualin.flymsg.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.ramotion.circlemenu.CircleMenuView;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;

import java.util.ArrayList;
import java.util.List;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.MainActivity;
import online.hualin.flymsg.adapter.ChatHisAdapter;
import online.hualin.flymsg.db.ChatHistory;
import online.hualin.flymsg.utils.CommonUtils;
import online.hualin.flymsg.view.ItemTouchHelperCallback;

import static online.hualin.flymsg.App.getApplication;
import online.hualin.flymsg.loadsir.callback.EmptyCallback;
public class HistoryFrag extends Fragment {
    private RecyclerView recyclerView;
    private View view;
    private ChatHisAdapter adapter;
    private List<ChatHistory> chatHistories = new ArrayList<>();
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DaoSession daoSession = ((App) getApplication()).getDaoSession();
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
//        setViewNums();

        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();
        chatHistories = chatHistoryDao.loadAll();
        if (chatHistories.size()==0){
            ImageView emptyGlass=getActivity().findViewById(R.id.empty_glass);
            emptyGlass.setVisibility(View.VISIBLE);
        }else
        {
            ImageView emptyGlass=getActivity().findViewById(R.id.empty_glass);
            emptyGlass.setVisibility(View.VISIBLE);
        }

        adapter = new ChatHisAdapter(chatHistories,null,view);
        recyclerView.setAdapter(adapter);
    }

    private void deleteAll(){
        ChatHistoryDao chatHistoryDao = daoSession.getChatHistoryDao();
        chatHistoryDao.deleteAll();
    }


    private void setViewNums(){
        if (CommonUtils.getScreenWidthDp(context) >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else if (CommonUtils.getScreenWidthDp(context) >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_frag, container, false);
        recyclerView = view.findViewById(R.id.his_recycle);

        context=getActivity();
        fab=getActivity().findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("删除全部")
                        .setMessage("你确定删除全部记录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAll();
                                chatHistories.clear();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();

            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
                    this.onStart();
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);

        return view;
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                fab.hide();
            } else {
                fab.show();
            }
        }
    };
}
