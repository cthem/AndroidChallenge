package DataModel;

import POJO.GithubRepositoriesResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sissy on 14/7/2017.
 */

public interface RestAPI
{
    //&q=created:
    @GET("search/repositories")
    Observable<GithubRepositoriesResponse> getRepositories (@Query("sort") String stars, @Query("order") String desc, @Query("q") String yesterday);
}
