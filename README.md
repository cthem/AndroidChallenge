# Android Challenge

## Short Description

This application shows a list of GitHub repositories giving the possibility to search a specific repository. Each item of this list has the same layout: on the left, the profile picture of the repository's owner appears and on the right, the title, owner's username and a short description. When a repository is clicked, an AlertDialog opens where more detailed information is given to the user about the selected repository.

## Implementation

In order to fetch yesterday's repositories, a RestClient and a RestAPI are implemented. In RestClient, two factories are used: a Gson Factory and a RxJava2CallAdapterFactory. The first is used in order to deserialize the Json Object received by the server, while the second is used for composing asynchronous and event-based tasks by using observable sequences.

In MainActivity, in the function getGithubRepositories the RestAPI interface is used in order to retrieve the relevant repositories, which are subsequently stored in Realm DB. This means that this information is kept in the application side and there is no need to perform any new call unless there is a change, which will be detected by the observer. With regard to Realm, classes for configuration, common queries and an appropriate adapter class extending RealmModelAdapter are used. 

The data in MainActivity are displayed using a RecyclerView. For this purpose, RepositoryAdapter, a subclass RealmRecyclerViewAdapter, displays all repositories via a CardViewHolder in the first screen. The respective layout is the list_item.xml. Instead of showing all items in the Realm database, a list of repositories, searchlist, is created. When the search field is empty or when the user erases all inserted text, all repositories are displayed to her. On the other hand, when the user types some text, the searchlist is populated with only the repositories that start with that text. Via CardHolder, the information to be shown for each repository is displayed, such as the title, owner's username, short description and the profile picture of the owner. In order to show the photo, the Glide library is used, which also implements the caching strategy. Finally, an OnClickListener is attached to each item and when a repository is chosen, a new layout is used(selected_repository.xml) in an AlertDialog window. Now the application shows more details to user, related both to the repository and the owner as requested.

