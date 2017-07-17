package gr.uoa.di.androidchallenge;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import DataModel.RealmRecyclerViewAdapter;
import POJO.Repository;
import io.realm.Realm;


/**
 * Created by sissy on 14/7/2017.
 */

public class RepositoryAdapter extends RealmRecyclerViewAdapter<Repository>
{
    final Activity context;
    private Realm realm;
    private LayoutInflater inflater;
    private Repository currentRepository;

    public void setSearchList(List<Repository> searchList) {
        this.searchList = searchList;
    }

    private List<Repository> searchList;


    public RepositoryAdapter(Activity context, Realm realm)
    {
        this.context = context;
        this.realm = realm;
        searchList = new ArrayList<>();
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position)
    {
        if (searchList.isEmpty())
        {
            Toast.makeText(context, "No repositories to show", Toast.LENGTH_SHORT).show();
            return;
        }
        currentRepository = this.searchList.get(position);


        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        //set the title and the snippet
        holder.textTitle.setText(currentRepository.getName());
        holder.textAuthor.setText(currentRepository.getOwner().getLogin());
        holder.textDescription.setText(currentRepository.getDescription());

        // load the background image
      Glide.with(context)
                .load(currentRepository.getOwner().getAvatarUrl().replace("https", "http"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .error(R.id.ivProfilePic)
                .into(holder.avatar);


        //show more details about selected repository
        holder.card.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                try
                {
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View content = inflater.inflate(R.layout.selected_repository, null);
                    String repoTitle = holder.textTitle.getText().toString();
                    Repository repo = realm.where(Repository.class).equalTo("name", repoTitle).findFirst();
                    final TextView tvName = (TextView)content.findViewById(R.id.tvName);
                    final TextView tvUrl = (TextView)content.findViewById(R.id.tvUrl);
                    final TextView tvDescription = (TextView)content.findViewById(R.id.tvDescription);
                    final TextView tvForks = (TextView)content.findViewById(R.id.tvForks);
                    final TextView tvOpenIssues = (TextView)content.findViewById(R.id.tvOpenIssues);

                    final ImageView ivProfilePic = (ImageView)content.findViewById(R.id.ivProfilePic);
                    final TextView tvOwnerName = (TextView) content.findViewById(R.id.tvOwnerName);
                    final TextView tvProfileUrl = (TextView)content.findViewById(R.id.tvProfileUrl);
                    final TextView tvUserType = (TextView)content.findViewById(R.id.tvUserType);

                    tvName.setText(String.format("Repository Name: %s",  repo.getName()));
                    tvUrl.setText(String.format("URL: %s", repo.getUrl()));
                    tvDescription.setText(String.format("Short Description: %s", repo.getDescription()));
                    tvForks.setText(String.format("Forks: %s", Integer.toString(repo.getForks())));
                    tvOpenIssues.setText(String.format("Open Issues: %s", Integer.toString(repo.getOpenIssues())));

                    tvOwnerName.setText(String.format("Username: %s", repo.getOwner().getLogin()));
                    tvProfileUrl.setText(String.format("Profile URL: %s", repo.getOwner().getUrl()));
                    tvUserType.setText(String.format("User Type: %s", repo.getOwner().getType()));

                    Glide.with(context)
                            .load(repo.getOwner().getAvatarUrl().replace("https", "http"))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .error(R.id.ivProfilePic)
                            .into(ivProfilePic);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(content)
                            .setTitle("Details");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    Log.e("RepoAdapter",e.getMessage());
                }
            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

//        if (getRealmAdapter() != null) {
//            return getRealmAdapter().getCount();
//        }
        return searchList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder
    {

        public CardView card;
        public TextView textTitle;
        public TextView textAuthor;
        public TextView textDescription;
        public ImageView avatar;

        public CardViewHolder(View itemView)
        {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_repositories);
            textTitle = (TextView) itemView.findViewById(R.id.repository_title);
            textAuthor = (TextView) itemView.findViewById(R.id.repository_author);
            textDescription = (TextView) itemView.findViewById(R.id.repository_description);
            avatar = (ImageView) itemView.findViewById(R.id.profile_pic);
        }
    }
}
