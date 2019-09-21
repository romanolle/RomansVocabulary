package nf.co.olle.romansvocabulary.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import nf.co.olle.romansvocabulary.MainActivity;
import nf.co.olle.romansvocabulary.R;

public abstract class FragmentUtils {

    public static void changeFragmentContent(Fragment fragment, FragmentActivity activity) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null);
        ft.commit();
    }
}
