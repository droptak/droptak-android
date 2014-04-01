package edu.purdue.maptak.admin.fragments;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.TakFragmentManager;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainMenuFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mainmenu, container, false);

        if (v != null) {
            Drawable bg = getResources().getDrawable(R.drawable.galaxybg);
            if (Build.VERSION.SDK_INT >= 16) {
                v.setBackground(bg);
            } else {
                v.setBackgroundDrawable(bg);
            }

            Button buMapList = (Button) v.findViewById(R.id.mainmenu_bu_maplist);
            Button buQRCode = (Button) v.findViewById(R.id.mainmenu_bu_qrscanner);
            Button buLogin = (Button) v.findViewById(R.id.mainmenu_bu_login);

            buMapList.setOnClickListener(this);
            buQRCode.setOnClickListener(this);
            buLogin.setOnClickListener(this);

        }

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.mainmenu_bu_maplist:
                TakFragmentManager.switchToMapList(getActivity());
                break;
            case R.id.mainmenu_bu_qrscanner:

                break;
            case R.id.mainmenu_bu_login:
                TakFragmentManager.switchToLogin(getActivity());
        }
    }
}
