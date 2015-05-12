package com.example.imageloadingdemo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ListActivity {


    static final String[] urls = {"https://koteshnavuluri.files.wordpress.com/2015/05/throwsystem1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/wow1.jpeg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/salute2.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/enough1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/fuck1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/dog1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/ekkadanunchi1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/aahaa1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/cry1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/brahmanandam1.jpg",
            "https://koteshnavuluri.files.wordpress.com/2015/05/awesome1.jpg"};
    private static final int SHARE = 143;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
        getListView().setAdapter(new ImageAdapter(this));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_URL, urls[position]);
                startActivity(intent);
            }
        });
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SHARE, 0, "Share");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case SHARE:
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(urls[info.position]);
                try {
                    shareImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void shareImage(Bitmap icon) throws IOException {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            bytes.flush();
            bytes.close();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    private class ImageAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public String getItem(int position) {
            return urls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_list_image, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText("Item " + (position + 1));
            ImageLoader.getInstance().displayImage(urls[position], holder.image, options, animateFirstListener);

            return view;
        }

        private class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

            final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    ImageView imageView = (ImageView) view;
                    boolean firstDisplay = !displayedImages.contains(imageUri);
                    if (firstDisplay) {
                        FadeInBitmapDisplayer.animate(imageView, 500);
                        displayedImages.add(imageUri);
                    }
                }
            }
        }
    }

    static class ViewHolder {
        @InjectView(R.id.text)
        TextView text;
        @InjectView(R.id.image)
        ImageView image;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

}
