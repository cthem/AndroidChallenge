package DataModel;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by sissy on 14/7/2017.
 */

public class RealmSetUp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this.getApplicationContext())
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
