package DataModel;

import android.app.Activity;
import android.app.Application;

import POJO.Repository;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sissy on 14/7/2017.
 */

public class RealmController
{
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Repository.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<Repository> getRepositories() {

        return realm.where(Repository.class).findAll();
    }

    //query a single item with the given id
    public Repository getRepositories(String id) {

        return realm.where(Repository.class).equalTo("id", id).findFirst();
    }

    //check if Book.class is empty
    public boolean hasRepositories() {

        return !realm.allObjects(Repository.class).isEmpty();
    }

    public Repository selectByTitle(String repoName)
    {
        return realm.where(Repository.class).equalTo("name", repoName).findFirst();
    }
    //query example
    public RealmResults<Repository> queryedRepositories() {

        return realm.where(Repository.class)
                .contains("owner", "Owner 0")
                .or()
                .contains("name", "Realm")
                .findAll();

    }
}
