package gr.uoa.di.androidchallenge;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DataModel.RealmController;
import DataModel.RealmRepositoryAdapter;
import DataModel.RestAPI;
import DataModel.RestClient;
import POJO.GithubRepositoriesResponse;
import POJO.Repository;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

import static gr.uoa.di.androidchallenge.R.id.recycler;

public class MainActivity extends AppCompatActivity
{
    private RepositoryAdapter adapter;
    private Realm realm;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;

    private ArrayList<Repository> repositories;
    private Repository repository;

    public ArrayList<Repository> getRepositories() {
        return repositories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
            recyclerView = (RecyclerView)findViewById(recycler);
//
//        //get realm instance
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
            Realm.setDefaultConfiguration(realmConfiguration);
            this.realm = RealmController.with(this).getRealm();
//
//        //set toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);





            getGithubRepositories();

            setupRecycler();
            setRealmAdapter();
            System.out.println();

        }
        catch(Exception ex)
        {
            Log.e("",ex.getMessage());
        }
//        finally
//        {
//            finish();
//        }
    }

    // to test observer/able
    public void test_obs()
    {
        ObservableOnSubscribe<Integer> src = new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        };

        // create the observable on the data generator
        io.reactivex.Observable<Integer> obs = io.reactivex.Observable.create(src);



        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("", "onSubscribe: ");
            }

            @Override
            public void onNext(Integer value) {
                Log.i("", "onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("", "onError: ");
            }

            @Override
            public void onComplete() {
                Log.e("", "onComplete: All Done!");
            }
        };

//Create our subscription//
        obs.subscribe(observer);

    }
    public void setRealmAdapter() {

        RealmResults<Repository> repositories = realm.allObjects(Repository.class);
        RealmRepositoryAdapter realmAdapter = new RealmRepositoryAdapter(this, repositories, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // get data : followed this: http://camposha.info/source/android-realm-recyclerview-saveretrieveshow/
//        List<String> reponames = new ArrayList<>();
//        RealmResults<Repository> repos = realm.allObjects(Repository.class);
//        for(Repository rep : repos) reponames.add(rep.getName());

        // create an empty adapter and add it to the recycler view
        adapter = new RepositoryAdapter(this, realm);
        recyclerView.setAdapter(adapter);
    }





    private void getGithubRepositories()
    {
        // fetch data
        RestAPI restAPI = RestClient.getClient().create(RestAPI.class);
        Observable<GithubRepositoriesResponse> observable = restAPI.getRepositories("stars", "desc", getYesterdayDateString());

        final List<Repository> repositories = new ArrayList<>();

        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .blockingSubscribe(
                        new Observer<GithubRepositoriesResponse>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                Log.i("Realm", String.format("Repositories observer subscribed."));
                            }

                            @Override
                            public void onNext(@NonNull GithubRepositoriesResponse repoResponse) {
//                                String reposstr = "";
//                                for (Repository rep : repoResponse.getItems())
//                                {
//                                    reposstr += rep.getName() + " : " + rep.getUrl() + " , " ;
//                                }
//                                Log.i("","onNext : " + reposstr);
                                Log.i("Realm", String.format("Got %d github repositories", repoResponse.getItems().size()));
                                repositories.addAll(repoResponse.getItems());
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                                Log.i("Realm", "Error fetching repositories: " + e.getMessage());

                            }

                            @Override
                            public void onComplete() {
                                Log.i("Realm", "Completed fetching repositories.");
                            }
                        }
                );
//        observable.s
//        List<GithubRepositoriesResponse> repos_ = observable.toList().blockingGet();
        Log.i("Realm", String.format("Persisting %d repositories", repositories.size()));

        for( Repository repo : repositories)
        {
            try {
                // Persist the data easily
                Log.i("Realm", "Persisting " + repo.getId() + " | " + repo.getUrl());
                realm.beginTransaction();
                realm.copyToRealm(repo);
                realm.commitTransaction();
            }
            catch (RealmException ex)
            {
                // If primary key constraint, ignore and continue
                Log.e("Realm","Exception while inserting repo:" + ex.getMessage());
                realm.cancelTransaction();

            }
        }

    }

    private Date yesterday()
    {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String yestdate = dateFormat.format(yesterday());
        Log.i("", String.format("Using yesterday date of %s", yestdate));
        return "created:"+yestdate;
    }
}

