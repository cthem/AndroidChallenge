package gr.uoa.di.androidchallenge;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
    private Repository selectedRepository;


    public RepositoryAdapter(Activity context, Realm realm)
    {
        this.context = context;
        this.realm = realm;
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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        // get the repository
        selectedRepository= getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        //set the title and the snippet
        holder.textTitle.setText(selectedRepository.getName());
        holder.textAuthor.setText(selectedRepository.getOwner().getLogin());
        holder.textDescription.setText(selectedRepository.getDescription());

        // load the background image
      Glide.with(context)
                .load(selectedRepository.getOwner().getAvatarUrl().replace("https", "http"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .error(R.id.ivProfilePic)
                .into(holder.avatar);


        //show selected repository
        holder.card.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = inflater.inflate(R.layout.selected_repository, null);

                final TextView tvName = (TextView)content.findViewById(R.id.tvName);
                final TextView tvUrl = (TextView)content.findViewById(R.id.tvUrl);
                final TextView tvDescription = (TextView)content.findViewById(R.id.tvDescription);
                final TextView tvForks = (TextView)content.findViewById(R.id.tvForks);
                final TextView tvOpenIssues = (TextView)content.findViewById(R.id.tvOpenIssues);

                final ImageView ivProfilePic = (ImageView)content.findViewById(R.id.ivProfilePic);
                final TextView tvOwnerName = (TextView) content.findViewById(R.id.tvOwnerName);
                final TextView tvProfileUrl = (TextView)content.findViewById(R.id.tvProfileUrl);
                final TextView tvUserType = (TextView)content.findViewById(R.id.tvUserType);

                tvName.setText(selectedRepository.getName());
                tvUrl.setText(selectedRepository.getUrl());
                tvDescription.setText(selectedRepository.getDescription());
                tvForks.setText(selectedRepository.getForks());
                tvOpenIssues.setText(selectedRepository.getOpenIssues());

                tvOwnerName.setText(selectedRepository.getOwner().getLogin());
                tvProfileUrl.setText(selectedRepository.getOwner().getUrl());
                tvUserType.setText(selectedRepository.getOwner().getType());

                Glide.with(context)
                        .load(selectedRepository.getOwner().getAvatarUrl().replace("https", "http"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .error(R.id.ivProfilePic)
                        .into(ivProfilePic);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setTitle("Details");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
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
