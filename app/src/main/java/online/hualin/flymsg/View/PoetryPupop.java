package online.hualin.flymsg.View;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.speedystone.greendaodemo.db.DaoSession;
import com.speedystone.greendaodemo.db.PoetryDao;

import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.data.PoetryGson;
import online.hualin.flymsg.db.Poetry;
import online.hualin.flymsg.utils.ViewUtils;
import razerdp.basepopup.BasePopupWindow;

import static online.hualin.flymsg.App.getApplication;

public class PoetryPupop extends BasePopupWindow {
    private View view;
    private PoetryGson poetryGson;

    public PoetryPupop(Context context, PoetryGson poetryGson) {
        super(context);
        this.poetryGson=poetryGson;
        setClipChildren(false);
    }

    @Override
    public View onCreateContentView() {
        view= createPopupById(R.layout.poetry_popup);
        TextView poetryContent = view.findViewById(R.id.poetry_popup_content);
        TextView poetryTitle = view.findViewById(R.id.poetry_popup_title);
        TextView poetryAuthor = view.findViewById(R.id.poetry_popup_author);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        PoetryDao poetryDao = daoSession.getPoetryDao();

        Poetry poetry= poetryDao.load((long) 1);
        poetryAuthor.setText(poetry.getAuthor());
        poetryContent.setText(poetry.getContent());
        poetryTitle.setText(poetry.getTitle());
        return view;
    }



    @Override
    protected Animator onCreateShowAnimator() {
        return createAnimator(true);
    }

    @Override
    protected Animator onCreateDismissAnimator() {
        return createAnimator(false);
    }

    private Animator createAnimator(boolean isShow) {
        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(),
                View.TRANSLATION_Y,
                isShow ? getHeight() * 0.75f : 0,
                isShow ? 0 : getHeight() * 0.75f);
        showAnimator.setDuration(500);
        showAnimator.setInterpolator(new OvershootInterpolator(isShow ? 6 : -6));
        return showAnimator;

    }

}
