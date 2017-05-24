package dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import digitalbath.fansproject.R;

/**
 * Created by unexpected_err on 29/04/2017.
 */

public class FragmentNews extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public FragmentNews() {}

    public static FragmentNews newInstance(int sectionNumber) {

        FragmentNews fragment = new FragmentNews();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format,
            getArguments().getInt(ARG_SECTION_NUMBER)));

        return rootView;
    }
}