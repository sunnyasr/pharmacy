package com.pharmacy.gts.Database.Address;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = AddressItem.class, exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {
    public abstract AddressDAO addressDAO();

    private static AddressDatabase instance;

    public static AddressDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context, AddressDatabase.class, "Pharma-Address-Room").build();
        }
        return instance;
    }
}
