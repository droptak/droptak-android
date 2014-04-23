package com.droptak.android.interfaces;


import com.droptak.android.data.MapID;
import com.droptak.android.data.User;

public interface OnAdminIDUpdateListener {

    public void onAdminIDUpdate(User newAdmin, MapID map);

}
