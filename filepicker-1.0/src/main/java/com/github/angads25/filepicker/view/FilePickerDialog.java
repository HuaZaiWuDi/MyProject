/*
 * Copyright (C) 2016 Angad Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.angads25.filepicker.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.R;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.controller.NotifyItemChecked;
import com.github.angads25.filepicker.controller.adapters.FileListAdapter;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.github.angads25.filepicker.utils.ExtensionFilter;
import com.github.angads25.filepicker.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**<p>
 * Created by Angad Singh on 09-07-2016.
 * </p>
 */

public class FilePickerDialog extends Dialog implements AdapterView.OnItemClickListener
{   private Context context;
    private ListView listView;
    private TextView dname, dir_path, title;
    private DialogProperties properties;
    private DialogSelectionListener callbacks;
    private ArrayList<FileListItem> internalList;
    private ExtensionFilter filter;
    private FileListAdapter mFileListAdapter;
    private Button select;
    private String titleStr=null;

    public static final int EXTERNAL_READ_PERMISSION_GRANT=112;

    public FilePickerDialog(Context context)
    {   super(context);
        this.context=context;
        properties=new DialogProperties();
        filter=new ExtensionFilter(properties);
        internalList=new ArrayList<>();
    }

    public FilePickerDialog(Context context, DialogProperties properties)
    {   super(context);
        this.context=context;
        this.properties=properties;
        filter=new ExtensionFilter(properties);
        internalList=new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_main);
        listView=(ListView)findViewById(R.id.fileList);
        select=(Button) findViewById(R.id.select);
        dname=(TextView)findViewById(R.id.dname);
        title=(TextView)findViewById(R.id.title);
        dir_path=(TextView)findViewById(R.id.dir_path);
        Button cancel = (Button) findViewById(R.id.cancel);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*  Select Button is clicked. Get the array of all selected items
                 *  from MarkedItemList singleton.
                 */
                String paths[]=MarkedItemList.getSelectedPaths();
                //NullPointerException fixed in v1.0.2
                if(callbacks!=null) {
                    callbacks.onSelectedFilePaths(paths);
                }
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mFileListAdapter=new FileListAdapter(internalList, context, properties);
        mFileListAdapter.setNotifyItemCheckedListener(new NotifyItemChecked() {
            @Override
            public void notifyCheckBoxIsClicked() {
                /*  Handler function, called when a checkbox is checked ie. a file is
                 *  selected.
                 */
                int size=MarkedItemList.getFileCount();
                if(size==0)
                {   select.setText(context.getResources().getString(R.string.choose_button_label));
                }
                else
                {   String button_label=context.getResources().getString(R.string.choose_button_label)+" ("+size+") ";
                    select.setText(button_label);
                }
                if(properties.selection_mode==DialogConfigs.SINGLE_MODE)
                {   /*  If a single file has to be selected, clear the previously checked
                     *  checkbox from the list.
                     */
                    mFileListAdapter.notifyDataSetChanged();
                }
            }
        });
        listView.setAdapter(mFileListAdapter);

        //Title logic added in version 1.0.5
        setTitle();
    }

    private void setTitle() {
        if(title==null||dname==null)
            return;
        if(titleStr!=null)
        {   if(title.getVisibility()==View.INVISIBLE)
            {   title.setVisibility(View.VISIBLE);
            }
            title.setText(titleStr);
            if(dname.getVisibility()==View.VISIBLE)
            {   dname.setVisibility(View.INVISIBLE);
            }
        }
        else
        {   if(title.getVisibility()==View.VISIBLE)
            {   title.setVisibility(View.INVISIBLE);
            }
            if(dname.getVisibility()==View.INVISIBLE)
            {   dname.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        select.setText(context.getResources().getString(R.string.choose_button_label));
        if(Utility.checkStorageAccessPermissions(context))
        {   File currLoc;
            if(properties.root.exists()&&properties.root.isDirectory())
            {   currLoc=new File(properties.root.getAbsolutePath());
            }
            else
            {   currLoc=new File(properties.error_dir.getAbsolutePath());
            }
            dname.setText(currLoc.getName());
            dir_path.setText(currLoc.getAbsolutePath());
            setTitle();
            internalList.clear();
            internalList=Utility.prepareFileListEntries(internalList,currLoc,filter);
            mFileListAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        {   if(internalList.size()>i) {
                FileListItem fitem = internalList.get(i);
                if (fitem.isDirectory()) {
                    if(new File(fitem.getLocation()).canRead()) {
                        File currLoc = new File(fitem.getLocation());
                        dname.setText(currLoc.getName());
                        setTitle();
                        dir_path.setText(currLoc.getAbsolutePath());
                        internalList.clear();
                        if (!currLoc.getName().equals(properties.root.getName())) {
                            FileListItem parent = new FileListItem();
                            parent.setFilename("...");
                            parent.setDirectory(true);
                            parent.setLocation(currLoc.getParentFile().getAbsolutePath());
                            parent.setTime(currLoc.lastModified());
                            internalList.add(parent);
                        }
                        internalList = Utility.prepareFileListEntries(internalList, currLoc, filter);
                        mFileListAdapter.notifyDataSetChanged();
                    }
                    else
                    {   Toast.makeText(context,"Directory cannot be accessed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public DialogProperties getProperties() {
        return properties;
    }

    public void setProperties(DialogProperties properties) {
        this.properties = properties;
        filter=new ExtensionFilter(properties);
    }

    public void setDialogSelectionListener(DialogSelectionListener callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void setTitle(CharSequence titleStr)
    {   if(titleStr!=null) {
            this.titleStr = titleStr.toString();
        }
        else
        {   this.titleStr=null;
        }
        setTitle();
    }

    //TODO:Make it work.
    public void markFiles(List<String> paths)
    {   if(paths!=null&&paths.size()>0)
        {   if(properties.selection_mode==DialogConfigs.SINGLE_MODE)
            {   File temp=new File(paths.get(0));
                switch (properties.selection_type) {
                    case DialogConfigs.DIR_SELECT:
                        if (temp.exists() && temp.isDirectory()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(true);
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;

                    case DialogConfigs.FILE_SELECT:
                        if (temp.exists() && temp.isFile()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(true);
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;

                    case DialogConfigs.FILE_AND_DIR_SELECT:
                        if (temp.exists() && (temp.isFile() || temp.isDirectory())) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(true);
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;
                }
            }
            else
            {   for (String path : paths) {
                    switch (properties.selection_type) {
                        case DialogConfigs.DIR_SELECT:
                            File temp = new File(path);
                            if (temp.exists() && temp.isDirectory()) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(true);
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;

                        case DialogConfigs.FILE_SELECT:
                            temp = new File(path);
                            if (temp.exists() && temp.isFile()) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(true);
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;

                        case DialogConfigs.FILE_AND_DIR_SELECT:
                            temp = new File(path);
                            if (temp.exists() && (temp.isFile() || temp.isDirectory())) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(true);
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void show() {
        if(!Utility.checkStorageAccessPermissions(context))
        {   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //TODO: Overlay Issue's Cause
                Toast.makeText(context,"Application needs you permission to access SD Card",Toast.LENGTH_LONG).show();
                ((Activity)context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_READ_PERMISSION_GRANT);
            }
        }
        else
        {   super.show();
        }
    }

    @Override
    public void onBackPressed() {
        //currentDirName is dependent on dname
        String currentDirName=dname.getText().toString();
        if(internalList.size()>0) {
            FileListItem fitem = internalList.get(0);
            File currLoc = new File(fitem.getLocation());
            if (currentDirName.equals(properties.root.getName())) {
                super.onBackPressed();
            }
            else
            {   dname.setText(currLoc.getName());
                dir_path.setText(currLoc.getAbsolutePath());
                internalList.clear();
                if (!currLoc.getName().equals(properties.root.getName())) {
                    FileListItem parent = new FileListItem();
                    parent.setFilename("...");
                    parent.setDirectory(true);
                    parent.setLocation(currLoc.getParentFile().getAbsolutePath());
                    parent.setTime(currLoc.lastModified());
                    internalList.add(parent);
                }
                internalList = Utility.prepareFileListEntries(internalList, currLoc, filter);
                mFileListAdapter.notifyDataSetChanged();
            }
            setTitle();
        }
        else
        {   super.onBackPressed();
        }
    }

    @Override
    public void dismiss() {
        MarkedItemList.clearSelectionList();
        internalList.clear();
        super.dismiss();
    }
}
