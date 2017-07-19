## Android Challenge

# Short Description

This application shows a list of GitHub repositories giving the possibility to search a specific repository. Each item of this list has the same layout, on the left appears profile picture of repository's owner, and on the right the title, owner's username and a short description appear. When a repository is clicked, an AlertDialog opens where a more detailed information is given to user about the selected repository.

# Implementation

In order to fetch each time yesterday's repositories, a RestClient and a RestAPI are implemented. In RestClient, two factories are used, one is Gson Factory and the second is the RxJava2CallAdapterFactory. Therefore, in RestAPI, instead of using Call<T>, the signature changed into Observable<T>. In MainActivity, in function getGithubRepositories the RestAPI interface is used in order to retrive the relevant repositories, which were stored in Realm DB. This means that this information is kept in Android side and there is no need to perform any new call unless there is a change, which will be detected by observer. Related to Realm, a class is implemented for the configuration, a class named RealmController where some common queries are implemented and also a class named RealmRepositoryAdapter, which extends RealmModelAdapter and is used in MainActivity in order to set up the adapter. 

The data in MainActivity are displayed using a RecyclerView, and for this purpose I have used class RepositoryAdapter which extends RealmRecyclerViewAdapter. The first screen displays all repositories via a CardViewHolder and respective layout is the list_item.xml. Instead of showing all items, I have used a list of repositories, searchlist, which shows all items if user has not typed anything in search field or if he has erased all text from this field. Via CardHolder, I store the information to be shown for each repository, which is the title, owner's username, short description and the profile picture of the owner. In order to show the photo, I have used Glide library, performing also the caching strategy. Finally, for each item there is an OnClickListener, and when a repository is chosen, a new layout is used(selected_repository.xml) in an AlertDialog window. Now the application shows more details to user, related both to the repository and the owner.

