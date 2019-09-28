//package online.hualin.flymsg.fragment;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.ramotion.paperonboarding.PaperOnboardingFragment;
//import com.ramotion.paperonboarding.PaperOnboardingPage;
//import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;
//
//import java.util.ArrayList;
//
//import online.hualin.flymsg.R;
//
//public class IntroFrag extends Fragment {
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        PaperOnboardingPage scr1 = new PaperOnboardingPage("Hotels",
//                "All hotels and hostels are sorted by hospitality rating",
//                Color.parseColor("#678FB4"), R.drawable.hotels, R.drawable.key);
//        PaperOnboardingPage scr2 = new PaperOnboardingPage("Banks",
//                "We carefully verify all banks before add them into the app",
//                Color.parseColor("#65B0B4"), R.drawable.banks, R.drawable.wallet);
//        PaperOnboardingPage scr3 = new PaperOnboardingPage("Stores",
//                "All local stores are categorized for your convenience",
//                Color.parseColor("#9B90BC"), R.drawable.stores, R.drawable.shopping_cart);
//
//        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
//        elements.add(scr1);
//        elements.add(scr2);
//        elements.add(scr3);
//        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);
//        onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
//            @Override
//            public void onRightOut() {
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Fragment bf = new BlankFragment();
//                fragmentTransaction.replace(R.id.fragment_container, bf);
//                fragmentTransaction.commit();
//            }
//        });
//    }
//}
