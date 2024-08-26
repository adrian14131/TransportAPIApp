package pl.ozog.transportapiapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import pl.ozog.transportapiapp.LinesTabFragment;
import pl.ozog.transportapiapp.StopsTabFragment;

public class DataViewerTabsPagerAdapter extends FragmentStateAdapter {

    public DataViewerTabsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1: return new StopsTabFragment();
            default: return new LinesTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
