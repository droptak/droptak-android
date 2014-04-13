package edu.purdue.maptak.admin.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import edu.purdue.maptak.admin.data.MapID;
import edu.purdue.maptak.admin.data.MapObject;
import edu.purdue.maptak.admin.data.TakID;
import edu.purdue.maptak.admin.data.TakMetadata;
import edu.purdue.maptak.admin.data.TakObject;
import edu.purdue.maptak.admin.data.UserID;

/** Statically generates dummy data which can be used for testing elsewhere in the app */
public class DummyData {

    /** Creates a random map object with an ID */
    public static MapObject createDummyMapObject() {
        Random r = new Random();

        String name = UUID.randomUUID().toString().substring(0,12);
        String id = UUID.randomUUID().toString();

        List<TakObject> taks = new ArrayList<TakObject>();
        for (int i = 0; i < 25; i++) {
            taks.add(createDummyTakObject());
        }

        MapObject obj = new MapObject();
        obj.setName(name);
        obj.setID(new MapID(id));
        obj.setTaks(taks);
        obj.setIsPublic(false);
        obj.setOwner(new UserID("Fake Owner ID", "Fake Owner Name"));
        obj.setManagers(new ArrayList<UserID>());

        return obj;

    }

    /** Creates a random tak with an ID */
    public static TakObject createDummyTakObject() {
        Random r = new Random();

        String name = "Random tak name " + r.nextInt(100);
        float lat = (r.nextFloat()*20)+10;
        float lng = (r.nextFloat()*20)+10;

        TakObject t = new TakObject();
        t.setName(name);
        t.setID(new TakID("random ID"));
        t.setLat(lat);
        t.setLat(lng);
        t.setMetadata(new HashMap<String, TakMetadata>());

        return t;
    }

}
