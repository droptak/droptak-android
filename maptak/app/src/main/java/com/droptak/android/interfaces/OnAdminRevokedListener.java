package com.droptak.android.interfaces;

import com.droptak.android.data.MapID;
import com.droptak.android.data.User;

public interface OnAdminRevokedListener {

    public void onAdminRevoked(MapID id, User user);

}
