package POJO;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sissy on 16/7/2017.
 */

public class GithubRepositoriesResponse extends RealmObject
{
    private RealmList<Repository> items;

    public RealmList<Repository> getItems() {
        return items;
    }

    public void setItems(RealmList<Repository> items) {
        this.items = items;
    }
}
