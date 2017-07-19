package gr.uoa.di.androidchallenge;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

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
    private RecyclerView recyclerView;
    private EditText search;
    private RealmResults<Repository> repositories;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            recyclerView = (RecyclerView)findViewById(recycler);

           //get realm instance
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
            Realm.setDefaultConfiguration(realmConfiguration);
            this.realm = RealmController.with(this).getRealm();

            //set toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle("");
            toolbar.setSubtitle("");

            //get repositories
            getGithubRepositories();

            //set RecyclerView and Realm
            setupRecycler();
            setRealmAdapter();

            System.out.println();

            //set up search field
            search = (EditText)findViewById(R.id.search);
            search.setSelected(false);
            search.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    ArrayList<Repository> repositoriesToShow = new ArrayList<>();
                    String textWritten = s.toString();
                    //if user has never written something in search box, or has erased the message, then all buttons must be visible to user
                    if(textWritten.isEmpty()) {
                        repositoriesToShow.addAll(repositories);
                    }
                    else {
                        textWritten = textWritten.toLowerCase();
                        for (Repository repo : repositories) {
                            if(repo.getName().startsWith(textWritten))
                                repositoriesToShow.add(repo);
                        }
                    }
                    adapter.setSearchList(repositoriesToShow);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s)
                {

                }
            });
        }
        catch(Exception ex)
        {
            Log.e("",ex.getMessage());
        }

    }

    @Override
    protected void onResume()
    {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onRestart()
    {
        adapter.notifyDataSetChanged();
        super.onRestart();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void setRealmAdapter() {

        repositories = realm.allObjects(Repository.class);
        RealmRepositoryAdapter realmAdapter = new RealmRepositoryAdapter(this, repositories, true);


        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.setSearchList(repositories);
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
                            public void onSubscribe(@NonNull Disposable d)
                            {
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

    private String getYesterdayDateString()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String yestdate = dateFormat.format(yesterday());
        Log.i("", String.format("Using yesterday date of %s", yestdate));
        return "created:"+yestdate;
    }
}

