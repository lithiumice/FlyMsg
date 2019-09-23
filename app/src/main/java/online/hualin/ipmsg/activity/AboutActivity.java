package online.hualin.ipmsg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
//import com.vansuita.materialabout.builder.AboutBuilder;
//import com.vansuita.materialabout.views.AboutView;

import online.hualin.ipmsg.R;

public class AboutActivity extends MaterialAboutActivity {
    private boolean isAlive=false;

    public static void show(@NonNull Context context){
        Intent intent=new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive=true;
//        setContentView(R.layout.activity_about);
//        AboutView view = AboutBuilder.with(this)
//                .setPhoto(R.mipmap.profile_picture)
//                .setCover(R.mipmap.profile_cover)
//                .setName("Your Full Name")
//                .setSubTitle("Mobile Developer")
//                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
//                .setAppIcon(R.mipmap.ic_launcher)
//                .setAppName(R.string.app_name)
//                .addGooglePlayStoreLink("8002078663318221363")
//                .addGitHubLink("user")
//                .addFacebookLink("user")
//                .addFiveStarsAction()
//                .setVersionNameAsAppSubTitle()
//                .addShareAction(R.string.app_name)
//                .setWrapScrollView(true)
//                .setLinksAnimated(true)
//                .setShowAsCard(true)
//                .build();
//
//        addContentView(view, null);
//    }
    }


    @Override
    @NonNull
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard card=new MaterialAboutCard.Builder()
                .title("IPmsg")
                .addItem(new MaterialAboutActionItem.Builder()
                        .icon(R.drawable.ic_about)
                        .text("Version")
                        .build())
                .build();
//        MaterialAboutCard card2=new MaterialAboutCard.Builder();

        return new MaterialAboutList.Builder()
                .addCard(card)
//                .addCard(card2)
                .build(); // This creates an empty screen, add cards with .addCard()
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }
}
