package me.shouheng.timepartner.selector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.PictureActivity;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.utils.TpDisp;

public class MultiImageSelectorFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "MultiImageSelectorFragment";
    private ArrayList<String> resultList;
    private ArrayList<Folder> mResultFolder;
    private GridView mGridView;
    private Callback mCallback;
    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;
    private ListPopupWindow mFolderPopupWindow;
    private TextView mCategoryText;
    private View mPopupAnchorView;
    private boolean hasFolderGened;
    private File mTmpFile;
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mCallback = ((Callback) getActivity());
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mis_fragment_multi_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int mode = selectMode();
        if (mode == 1) {
            ArrayList<String> tmp = getArguments().getStringArrayList(Intents.EXTRA_DEFAULT_SELECTED_LIST);
            if ((tmp != null) && (tmp.size() > 0)) {
                this.resultList = tmp;
            }
        }
        mImageAdapter = new ImageGridAdapter(getActivity(), showCamera(), 3);
        mImageAdapter.showSelectIndicator(mode == 1);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mCategoryText = ((TextView) view.findViewById(R.id.category_btn));
        mCategoryText.setText(R.string.mis_folder_all);
        mCategoryText.setOnClickListener(this);
        mGridView = ((GridView) view.findViewById(R.id.grid));
        mGridView.setAdapter(mImageAdapter);
        // 按下右上指示的单击事件
        mImageAdapter.setOnIndicatorClickListener(new ImageGridAdapter.OnIndicatorClickListener() {
            @Override
            public void onItemClick(int position) {
                if (MultiImageSelectorFragment.this.mImageAdapter.isShowCamera()) {
                    if (position == 0) {
                        showCameraAction();
                    } else {
                        Image image = mImageAdapter.getItem(position);
                        selectImageFromGrid(image, mode);
                    }
                } else {
                    Image image = mImageAdapter.getItem(position);
                    selectImageFromGrid(image, mode);
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                if (MultiImageSelectorFragment.this.mImageAdapter.isShowCamera()) {
                    if (i == 0) {
                        showCameraAction();
                    } else {
                        Image image = mImageAdapter.getItem(i);
                        Picture picture = new Picture();
                        picture.setPicturePath(image.path);
                        PictureActivity.startForSingle(getContext(), picture);
                    }
                } else {
                    Image image = mImageAdapter.getItem(i);
                    Picture picture = new Picture();
                    picture.setPicturePath(image.path);
                    PictureActivity.startForSingle(getContext(), picture);
                }
            }
        });
        this.mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 2) {
                    Glide.with(view.getContext());
                } else {
                    Glide.with(view.getContext());
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });
        this.mFolderAdapter = new FolderAdapter(getActivity());
    }

    /**
     * 创建弹出的文件夹列表 */
    private void createPopupFolderList() {
        Point point = TpDisp.getWindowSize(getContext());
        int width = point.x;
        int height = (int)(point.y * 0.5625F);
        this.mFolderPopupWindow = new ListPopupWindow(getActivity());
        this.mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(-1));
        this.mFolderPopupWindow.setAdapter(mFolderAdapter);
        this.mFolderPopupWindow.setContentWidth(width);
        this.mFolderPopupWindow.setWidth(width);
        this.mFolderPopupWindow.setHeight(height);
        this.mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        this.mFolderPopupWindow.setModal(true);
        this.mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setSelectIndex(position);
                final int index = position;
                final AdapterView v = parent;

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mFolderPopupWindow.dismiss();
                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(0, null, mLoaderCallback);
                            mCategoryText.setText(R.string.mis_folder_all);
                            mImageAdapter.setShowCamera(showCamera());
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                mCategoryText.setText(folder.name);
                                if ((resultList != null) && (resultList.size() > 0)) {
                                    mImageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            mImageAdapter.setShowCamera(false);
                        }
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100L);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("key_temp_file", this.mTmpFile);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            this.mTmpFile = ((File) savedInstanceState.getSerializable("key_temp_file"));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(0, null, this.mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == -1) {
                if ((this.mTmpFile != null) && (this.mCallback != null)) {
                    this.mCallback.onCameraShot(this.mTmpFile);
                }
            } else {
                while ((this.mTmpFile != null) && (this.mTmpFile.exists())) {
                    boolean success = this.mTmpFile.delete();
                    if (success) {
                        this.mTmpFile = null;
                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if ((this.mFolderPopupWindow != null) && (this.mFolderPopupWindow.isShowing())) {
            this.mFolderPopupWindow.dismiss();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void showCameraAction() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                this.mTmpFile = FileUtils.createTmpFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ((this.mTmpFile != null) && (this.mTmpFile.exists())) {
                intent.putExtra("output", Uri.fromFile(this.mTmpFile));
                startActivityForResult(intent, 100);
            } else {
                TpDisp.showToast(getActivity(), R.string.mis_error_image_not_exist);
            }
        } else {
            TpDisp.showToast(getActivity(), R.string.mis_msg_no_camera);
        }
    }

    private void selectImageFromGrid(Image image, int mode) {
        if (image != null) {
            if (mode == 1) {
                if (this.resultList.contains(image.path)) {
                    this.resultList.remove(image.path);
                    if (this.mCallback != null) {
                        this.mCallback.onImageUnselected(image.path);
                    }
                } else {
                    if (selectImageCount() == this.resultList.size()) {
                        TpDisp.showToast(getActivity(), R.string.mis_msg_amount_limit);
                        return;
                    }
                    this.resultList.add(image.path);
                    if (this.mCallback != null) {
                        this.mCallback.onImageSelected(image.path);
                    }
                }
                this.mImageAdapter.select(image);
            } else if ((mode == 0) && (this.mCallback != null)) {
                this.mCallback.onSingleImageSelected(image.path);
            }
        }
    }

    public MultiImageSelectorFragment() {

        this.resultList = new ArrayList<>();

        this.mResultFolder = new ArrayList<>();

        this.hasFolderGened = false;

        this.mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            private final String[] IMAGE_PROJECTION = { "_data", "_display_name", "date_added", "mime_type", "_size", "_id" };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == 0) {
                    cursorLoader = new CursorLoader(MultiImageSelectorFragment.this.getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, this.IMAGE_PROJECTION[4] + ">0 AND " + this.IMAGE_PROJECTION[3] + "=? OR " + this.IMAGE_PROJECTION[3] + "=? ", new String[] { "image/jpeg", "image/png" }, this.IMAGE_PROJECTION[2] + " DESC");
                } else if (id == 1) {
                    cursorLoader = new CursorLoader(MultiImageSelectorFragment.this.getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this.IMAGE_PROJECTION, this.IMAGE_PROJECTION[4] + ">0 AND " + this.IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, this.IMAGE_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if ((data != null) && (data.getCount() > 0)) {
                    List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(this.IMAGE_PROJECTION[2]));
                        if (fileExist(path)) {
                            Image image = null;
                            if (!TextUtils.isEmpty(name)) {
                                image = new Image(path, name, dateTime);
                                images.add(image);
                            }
                            if (!MultiImageSelectorFragment.this.hasFolderGened) {
                                File folderFile = new File(path).getParentFile();
                                if ((folderFile != null) && (folderFile.exists())) {
                                    String fp = folderFile.getAbsolutePath();
                                    Folder f = MultiImageSelectorFragment.this.getFolderByPath(fp);
                                    if (f == null) {
                                        Folder folder = new Folder();
                                        folder.name = folderFile.getName();
                                        folder.path = fp;
                                        folder.cover = image;
                                        List<Image> imageList = new ArrayList<>();
                                        imageList.add(image);
                                        folder.images = imageList;
                                        MultiImageSelectorFragment.this.mResultFolder.add(folder);
                                    } else {
                                        f.images.add(image);
                                    }
                                }
                            }
                        }
                    } while (data.moveToNext());
                    MultiImageSelectorFragment.this.mImageAdapter.setData(images);
                    if ((MultiImageSelectorFragment.this.resultList != null) && (MultiImageSelectorFragment.this.resultList.size() > 0)) {
                        MultiImageSelectorFragment.this.mImageAdapter.setDefaultSelected(MultiImageSelectorFragment.this.resultList);
                    }
                    if (!MultiImageSelectorFragment.this.hasFolderGened) {
                        MultiImageSelectorFragment.this.mFolderAdapter.setData(MultiImageSelectorFragment.this.mResultFolder);
                        MultiImageSelectorFragment.this.hasFolderGened = true;
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {}

            private boolean fileExist(String path) {
                if (!TextUtils.isEmpty(path)) {
                    return new File(path).exists();
                }
                return false;
            }
        };
    }

    private Folder getFolderByPath(String path) {
        if (this.mResultFolder != null) {
            for (Folder folder : this.mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    private boolean showCamera() {
        return (getArguments() == null) || (getArguments().getBoolean(Intents.EXTRA_SHOW_CAMERA, true));
    }

    private int selectMode() {
        return getArguments() == null ? 1 : getArguments().getInt(Intents.EXTRA_SELECT_MODE);
    }

    private int selectImageCount() {
        return getArguments() == null ? 9 : getArguments().getInt(Intents.EXTRA_SELECT_COUNT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category_btn:
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
                break;
        }
    }

    public static abstract interface Callback {

        public abstract void onSingleImageSelected(String paramString);

        public abstract void onImageSelected(String paramString);

        public abstract void onImageUnselected(String paramString);

        public abstract void onCameraShot(File paramFile);
    }
}
