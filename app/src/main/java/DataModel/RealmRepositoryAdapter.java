package DataModel;

import android.app.Activity;

import POJO.Repository;
import io.realm.RealmResults;

/**
 * Created by sissy on 14/7/2017.
 */

public class RealmRepositoryAdapter extends RealmModelAdapter<Repository> {

    public RealmRepositoryAdapter(Activity context, RealmResults<Repository> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}