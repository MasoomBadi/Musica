package com.thegalaxysoftware.musica.SubFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thegalaxysoftware.musica.R;
import com.thegalaxysoftware.musica.Utils.Constants;
import com.thegalaxysoftware.musica.Utils.NavigationUtils;
import com.thegalaxysoftware.musica.Widgets.MultiViewPager;

public class StyleSelectorFragment extends Fragment {

    public String ACTION = "action";
    private FragmentStatePagerAdapter adapter;
    private MultiViewPager pager;
    private SubStyleSelectorFragment selectorFragment;
    private SharedPreferences preferences;

    public static StyleSelectorFragment newInstance(String what) {
        StyleSelectorFragment fragment = new StyleSelectorFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SETTINGS_STYLE_SELECTOR_WHAT, what);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            ACTION = getArguments().getString(Constants.SETTINGS_STYLE_SELECTOR_WHAT);
        }
        preferences = getActivity().getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_style_selector, container, false);

        if(ACTION.equals(Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING))
        {

        }
        pager = (MultiViewPager) rootView.findViewById(R.id.pager);

        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                selectorFragment = SubStyleSelectorFragment.newInstance(i, ACTION);
                return selectorFragment;
            }

            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }
        };
        pager.setAdapter(adapter);
        scrollToCurrentStyle();

        return rootView;
    }

    public void updateCurrentStyle()
    {
        if (selectorFragment != null) {
            adapter.notifyDataSetChanged();
            scrollToCurrentStyle();
            Toast.makeText(getActivity(), "Style Selected ", Toast.LENGTH_SHORT).show();
        }
    }

    public void scrollToCurrentStyle() {
        String fragmentID = preferences.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.MUSICA3);
        pager.setCurrentItem(NavigationUtils.getIntForCurrentNowplaying(fragmentID));
    }
}
