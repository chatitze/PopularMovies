package com.chatitze.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chatitze.android.popularmovies.MovieDetailsActivity;
import com.chatitze.android.popularmovies.R;
import com.chatitze.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


/**
 * Created by chatitze on 02/02/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private String[]  mImageUrls;

    final private ListItemClickListener mOnClickListener;


    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    /**
     * Constructor for ImageAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param context
     * @param imageUrls
     */
    public ImageAdapter(Context context, String [] imageUrls, ListItemClickListener listener) {
        this.mImageUrls = imageUrls;
        this.mContext   = context;
        this.mOnClickListener = listener;
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ImageViewHolder that holds the View for each grid item
     */
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, viewGroup, shouldAttachToParentImmediately);
        ImageViewHolder viewHolder = new ImageViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our movie's app
     */
    @Override
    public int getItemCount() {
        return mImageUrls.length;
    }


    public String getItem(int position) {
        return mImageUrls[position];
    }

    /**
     * Cache of the children views for a list item.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * ImageView and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link ImageAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item_image);

            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate image within a grid item.
         * @param imageUrlIndex Position of the image in the grid
         */
        void bind(int imageUrlIndex) {
            String url = NetworkUtils.MOVIES_POSTER_ENDPOINT + getItem(imageUrlIndex);
            Picasso.with(mContext).load(url).into(imageView);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}