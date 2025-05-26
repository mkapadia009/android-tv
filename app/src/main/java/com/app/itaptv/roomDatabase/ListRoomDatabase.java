package com.app.itaptv.roomDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Playlist.class, MediaDuration.class,AnalyticsData.class}, version = 12, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ListRoomDatabase extends RoomDatabase {

    public abstract MediaDAO mediaDAO();

    private static volatile ListRoomDatabase INSTANCE;
    private static final String DB_NAME = "word_database";

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `MediaDuration` (`id` INTEGER, " + "`duration` LONG, " +
                    "" + "`bufferedPosition` LONG, " + "`currentPosition` LONG, PRIMARY KEY(`id`))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `MediaDuration_new` (`id` STRING, " + "`duration` LONG, " +
                    "" + "`bufferedPosition` LONG, " + "`currentPosition` LONG, PRIMARY KEY(`id`))");

            database.execSQL("INSERT INTO MediaDuration_new (id, duration, bufferedPosition, currentPosition) " +
                    "SELECT id, duration, bufferedPosition, currentPosition FROM MediaDuration");

            database.execSQL("DROP TABLE MediaDuration");

            database.execSQL("ALTER TABLE MediaDuration_new RENAME TO MediaDuration");
        }
    };

    public static ListRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ListRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ListRoomDatabase.class, DB_NAME)
                            .addMigrations(MIGRATION_3_4)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                }
            };
}
