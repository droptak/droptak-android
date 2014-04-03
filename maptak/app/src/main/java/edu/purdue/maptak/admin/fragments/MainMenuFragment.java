package edu.purdue.maptak.admin.fragments;

import edu.purdue.maptak.admin.R;
import edu.purdue.maptak.admin.managers.TakFragmentManager;
import edu.purdue.maptak.admin.data.MapTakDB;
import edu.purdue.maptak.admin.qrcode.IntentIntegrator;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
            Button buClearDB = (Button) v.findViewById(R.id.mainmenu_bu_cleardb);
            Button buRevoke = (Button) v.findViewById(R.id.mainmenu_bu_revoke);

            buMapList.setOnClickListener(this);
            buQRCode.setOnClickListener(this);
            buLogin.setOnClickListener(this);
            buClearDB.setOnClickListener(this);
            buRevoke.setOnClickListener(this);

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
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
                break;
            case R.id.mainmenu_bu_login:
                TakFragmentManager.switchToLogin(getActivity());
                break;
            case R.id.mainmenu_bu_cleardb:
                Toast.makeText(getActivity(), "Clearing database of all information.", Toast.LENGTH_SHORT).show();
                MapTakDB db = MapTakDB.getDB(getActivity());
                db.destroy();
                break;
            case R.id.mainmenu_bu_revoke:
                LoginFragment l = new LoginFragment();
                l.revokeGplusAccess();
                break;
        }
    }


}
