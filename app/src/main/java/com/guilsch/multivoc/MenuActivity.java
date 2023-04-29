package com.guilsch.multivoc;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        ViewPager viewPager = findViewById(R.id.view_pager);

        FragmentPagerAdapter adapter = createFragmentPagerAdapter();
        viewPager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        utils.printNBCards();
    }


    public static class Tab1Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab1, container, false);

            MenuActivity parentActivity = (MenuActivity) getActivity();

            ImageView flag = view.findViewById(R.id.lang_flag);
            ImageView settingsIm = view.findViewById(R.id.setting_im);
            LinearLayout trainLayout = view.findViewById(R.id.train_layout);
            LinearLayout learnLayout = view.findViewById(R.id.learn_layout);
            TextView cardsToTrainNB = view.findViewById(R.id.cards_to_train_nb);

            flag.setImageDrawable(Param.FLAG_ICON_TARGET);
            cardsToTrainNB.setText(String.valueOf(Param.GLOBAL_DECK.getCardsToReviewNb()));
            flag.setOnClickListener(v -> parentActivity.changeActivity(MainActivity.class));
            settingsIm.setOnClickListener(v -> parentActivity.changeActivity(SettingsActivity.class));
            trainLayout.setOnClickListener(v -> parentActivity.preChangeToTrainActivity());
            learnLayout.setOnClickListener(v -> parentActivity.preChangeToLearnActivity());

            return view;
        }
    }

    public static class Tab2Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab2, container, false);

            MenuActivity parentActivity = (MenuActivity) getActivity();

            LinearLayout translationLayout = view.findViewById(R.id.translation_layout);
            LinearLayout newCardLayout = view.findViewById(R.id.new_card_layout);
            LinearLayout exploreLayout = view.findViewById(R.id.explore_layout);

            translationLayout.setOnClickListener(v -> parentActivity.changeActivity(TranslationActivity.class));
            newCardLayout.setOnClickListener(v -> parentActivity.changeActivity(NewCardActivity.class));
            exploreLayout.setOnClickListener(v -> parentActivity.changeActivity(ExploreActivity.class));

            return view;
        }
    }

    public void preChangeToLearnActivity(){
        if (Param.CARDS_TO_LEARN_NB == 0) {
            utils.showToast(MenuActivity.this, getString(R.string.toast_msg_no_cards_to_learn));
        }
        else {
            changeActivity(LearnActivity.class);
        }
    }

    public void preChangeToTrainActivity(){
        if (Param.CARDS_TO_REVIEW_NB == 0) {
            utils.showToast(MenuActivity.this, getString(R.string.toast_msg_no_cards_to_train));
        }
        else {
            changeActivity(RevisionActivity.class);
        }
    }

    public FragmentPagerAdapter createFragmentPagerAdapter() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new Tab1Fragment();
                    case 1:
                        return new Tab2Fragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

        };

        return adapter;
    }

    /**
     * Called when pressing somewhere to change activity
     */
    public void changeActivity(Class newActivityClass) {
        Intent newActivity = new Intent(this, newActivityClass);
        startActivity(newActivity);
        finish();
    }
}