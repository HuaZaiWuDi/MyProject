//package com.embednet.wdluo.bleplatformsdkdemo.dfu;
//
//import android.app.LoaderManager;
//import android.app.NotificationManager;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.CursorLoader;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.Loader;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.content.LocalBroadcastManager;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.embednet.wdluo.bleplatformsdk.dfu.SFTP;
//import com.embednet.wdluo.bleplatformsdkdemo.app.BaseAvtivity;
//import com.embednet.wdluo.bleplatformsdkdemo.R;
//import com.embednet.wdluo.bleplatformsdkdemo.scanner.ScannerFragment;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import no.nordicsemi.android.error.GattError;
//
//public class DfuActivity extends BaseAvtivity implements LoaderManager.LoaderCallbacks<Cursor>, ScannerFragment.OnDeviceSelectedListener,
//        UploadCancelFragment.CancelFragmentListener {
//    private static final String EXTRA_URI = "uri";
//    private Context context = this;
//    private Button getFileList, selectDevice, updateFirmware;
//    private FileListAdapter mFileListAdapter;
//    private ListView mFileListView;
//    private BluetoothDevice mSelectedDevice;
//    private ProgressBar mProgressBar;
//    private TextView mStatusTextView;
//    private boolean getFileListFlag = false, downloadFileFlag = false, selectDeviceFlag = false;
//    private String firmwareFileName = "";
//    List<String> firmwareFileList = new ArrayList<>();
//
//    private final BroadcastReceiver mDfuUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            System.out.println("----------->BroadcastReceiver");
//            // DFU is in progress or an error occurred
//            final String action = intent.getAction();
//
//            if (DfuService.BROADCAST_PROGRESS.equals(action)) {
//                final int progress = intent.getIntExtra(DfuService.EXTRA_DATA, 0);
//                final int currentPart = intent.getIntExtra(DfuService.EXTRA_PART_CURRENT, 1);
//                final int totalParts = intent.getIntExtra(DfuService.EXTRA_PARTS_TOTAL, 1);
//                updateProgressBar(progress, currentPart, totalParts, false, false);
//            } else if (DfuService.BROADCAST_ERROR.equals(action)) {
//                final int error = intent.getIntExtra(DfuService.EXTRA_DATA, 0);
//                final boolean connectionStateError = intent.getIntExtra(DfuService.EXTRA_ERROR_TYPE, DfuService.ERROR_TYPE_OTHER) == DfuService.ERROR_TYPE_COMMUNICATION_STATE;
//                updateProgressBar(error, 0, 0, true, connectionStateError);
//
//                // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // if this activity is still open and upload process was completed, cancel the notification
//                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.cancel(DfuService.NOTIFICATION_ID);
//                    }
//                }, 200);
//            } else if (SFTP.SFTP_PROGRESS.equals(action)) {
//                final int sftpProgress = intent.getIntExtra(SFTP.EXTRA_DATA, 0);
//                updateProgressBar(sftpProgress);
//            }
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dfu);
//        getFileList = (Button) findViewById(R.id.getFirmwareFileButton);
//        selectDevice = (Button) findViewById(R.id.selectDeviceButton);
//        updateFirmware = (Button) findViewById(R.id.updateFirmwareButton);
//        mFileListView = (ListView) findViewById(R.id.fileListView);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mStatusTextView = (TextView) findViewById(R.id.statusTextView);
//        hiddenProgressBar(true);
//        // Initializes list view adapter.
//        mFileListAdapter = new FileListAdapter();
//        mFileListView.setAdapter(mFileListAdapter);
////        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                firmwareFileName = firmwareFileList.get(i);
////                System.out.println(firmwareFileName);
////            }
////        });
//        updateFirmware.setEnabled(false);
//        //get firmware file list
//        //
//        // ��ȡbin�ļ�
//        getFileList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //��ȡbin�ļ�
//                showProgressBar();
//                GetListFileFromFtp mGetListFileFromFtp = new GetListFileFromFtp();
//                mGetListFileFromFtp.start();
//            }
//        });
//        //select device
//        selectDevice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDeviceScanningDialog();
//            }
//        });
//        //����OTA
//        updateFirmware.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showProgressBar();
//                UpdateFirmwareThread mUpdateFirmwareThread = new UpdateFirmwareThread();
//                mUpdateFirmwareThread.start();
//            }
//        });
//    }
//
//    private void showDeviceScanningDialog() {
//        final ScannerFragment dialog = ScannerFragment.getInstance(DfuActivity.this, null, false); // Device that is advertising directly does not have the GENERAL_DISCOVERABLE nor LIMITED_DISCOVERABLE flag set.
//        dialog.show(getSupportFragmentManager(), "scan_fragment");
//    }
//
//
//    @Override
//    public void onDeviceSelected(final BluetoothDevice device, final String name) {
//        mSelectedDevice = device;
//        System.out.println(name);
//        selectDeviceFlag = true;
//        if (getFileListFlag && downloadFileFlag && selectDeviceFlag) {
//            updateFirmware.setEnabled(true);
//        }
////        mUploadButton.setEnabled(mStatusOk);
////        mDeviceNameView.setText(name);
//    }
//
//    @Override
//    public void onDialogCanceled() {
//        // do nothing
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
//        final Uri uri = args.getParcelable(EXTRA_URI);
//        /*
//         * Some apps, f.e. Google Drive allow to select file that is not on the device. There is no "_data" column handled by that provider. Let's try to obtain
//		 * all columns and than check which columns are present.
//		 */
//        // final String[] projection = new String[] { MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA };
//        return new CursorLoader(this, uri, null /* all columns, instead of projection */, null, null, null);
//    }
//
//    @Override
//    public void onLoaderReset(final Loader<Cursor> loader) {
//
//    }
//
//    @Override
//    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
//        if (data != null && data.moveToNext()) {
//            /*
//             * Here we have to check the column indexes by name as we have requested for all. The order may be different.
//			 */
////            final String fileName = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
////            final int fileSize = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);
////            String filePath = null;
////            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
////            if (dataIndex != -1)
////                filePath = data.getString(dataIndex /* 2 DATA */);
////            if (!TextUtils.isEmpty(filePath))
////                mFilePath = filePath;
////
////            updateFileInfo(fileName, fileSize, mFileType);
//        } else {
//
//        }
//    }
//
//    @Override
//    public void onCancelUpload() {
//        mProgressBar.setIndeterminate(true);
//        //mTextUploading.setText(R.string.dfu_status_aborting);
//        //mTextPercentage.setText(null);
//    }
//
//
//    //���������ļ�
//    class GetListFileFromFtp extends Thread {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            SFTP mSFTP = new SFTP(context);
//            firmwareFileList.clear();
//            firmwareFileList = mSFTP.listFirmwareFiles();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mFileListAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//    }
//
//
//    //����Bin�ļ�
//    class DownloadFileFromFtp extends Thread {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            SFTP mSFTP = new SFTP(context);
//            mSFTP.downloadFirmware(firmwareFileName);
//        }
//    }
//
//
//    //��ʼOTA
//    class UpdateFirmwareThread extends Thread {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            final Intent service = new Intent(context, DfuService.class);
//            service.putExtra(DfuService.EXTRA_DEVICE_ADDRESS, mSelectedDevice.getAddress());
//            service.putExtra(DfuService.EXTRA_DEVICE_NAME, mSelectedDevice.getName());
//            service.putExtra(DfuService.EXTRA_FILE_NAME, firmwareFileName);
//            try {
//                startService(service);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //�����㲥
//        // We are using LocalBroadcastReceiver instead of normal BroadcastReceiver for optimization purposes
//        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
//        broadcastManager.registerReceiver(mDfuUpdateReceiver, makeDfuUpdateIntentFilter());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
//        broadcastManager.unregisterReceiver(mDfuUpdateReceiver);
//    }
//
//    private static IntentFilter makeDfuUpdateIntentFilter() {
//        final IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(DfuService.BROADCAST_PROGRESS);
//        intentFilter.addAction(DfuService.BROADCAST_ERROR);
//        intentFilter.addAction(DfuService.BROADCAST_LOG);
//        intentFilter.addAction(SFTP.SFTP_PROGRESS);
//        return intentFilter;
//    }
//
//    private void updateProgressBar(final int progress) {
//        switch (progress) {
//            case SFTP.PROGRESS_CONNECTING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_sftp_status_connecting);
//                break;
//            case SFTP.PROGRESS_CONNECTED_SUCCESS:
//                mProgressBar.setIndeterminate(false);
//                mStatusTextView.setText(R.string.dfu_sftp_status_connected_success);
//                break;
//            case SFTP.PROGRESS_CONNECTED_FAILD:
//                hiddenProgressBar(false);
//                showToast(R.string.dfu_sftp_status_connected_faild);
//                break;
//            case SFTP.PROGRESS_LIST_FILE:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_sftp_status_list_file);
//                break;
//            case SFTP.PROGRESS_LIST_FILE_SUCCESS:
//                getFileListFlag = true;
//                mProgressBar.setIndeterminate(false);
//                mStatusTextView.setText(R.string.dfu_sftp_status_list_file_success);
//                hiddenProgressBar(false);
//                showToast(R.string.dfu_sftp_status_list_file_success);
//                break;
//            case SFTP.PROGRESS_LIST_FILE_FAILD:
//                hiddenProgressBar(false);
//                showToast(R.string.dfu_sftp_status_list_file_faild);
//                break;
//            case SFTP.PROGRESS_DOWNLOADING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_sftp_status_downloading);
//                break;
//            case SFTP.PROGRESS_DOWNLOAD_SUCCESS:
//                downloadFileFlag = true;
//                if (getFileListFlag && downloadFileFlag && selectDeviceFlag) {
//                    updateFirmware.setEnabled(true);
//                }
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_sftp_status_download_success);
//                hiddenProgressBar(false);
//                showToast(R.string.dfu_sftp_status_download_success);
//                break;
//            case SFTP.PROGRESS_DOWNLOAD_FAILD:
//                hiddenProgressBar(false);
//                showToast(R.string.dfu_sftp_status_download_faild);
//            default:
//                break;
//        }
//    }
//
//    private void updateProgressBar(final int progress, final int part, final int total, final boolean error, final boolean connectionError) {
//        switch (progress) {
//            case DfuService.PROGRESS_CONNECTING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_status_connecting);
//                break;
//            case DfuService.PROGRESS_STARTING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_status_starting);
//                break;
//            case DfuService.PROGRESS_ENABLING_DFU_MODE:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_status_switching_to_dfu);
//                break;
//            case DfuService.PROGRESS_VALIDATING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_status_validating);
//                break;
//            case DfuService.PROGRESS_DISCONNECTING:
//                mProgressBar.setIndeterminate(true);
//                mStatusTextView.setText(R.string.dfu_status_disconnecting);
//                break;
//            case DfuService.PROGRESS_COMPLETED:
//                mStatusTextView.setText(R.string.dfu_status_completed);
//                // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onTransferCompleted();
//
//                        // if this activity is still open and upload process was completed, cancel the notification
//                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.cancel(DfuService.NOTIFICATION_ID);
//                    }
//                }, 200);
//                break;
//            case DfuService.PROGRESS_ABORTED:
//                mStatusTextView.setText(R.string.dfu_status_aborted);
//                // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onUploadCanceled();
//
//                        // if this activity is still open and upload process was completed, cancel the notification
//                        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        manager.cancel(DfuService.NOTIFICATION_ID);
//                    }
//                }, 200);
//                break;
//            default:
//                mProgressBar.setIndeterminate(false);
//                if (error) {
//                    showErrorMessage(progress, connectionError);
//                } else {
//                    mProgressBar.setProgress(progress);
//                    if (total > 1)
//                        mStatusTextView.setText(getString(R.string.dfu_status_uploading_part, part, total));
//                    else
//                        mStatusTextView.setText(R.string.dfu_status_uploading);
//                }
//                break;
//        }
//    }
//
//    private void onTransferCompleted() {
//        hiddenProgressBar(true);
//        showToast(R.string.dfu_success);
//    }
//
//    public void onUploadCanceled() {
//        hiddenProgressBar(false);
//        showToast(R.string.dfu_aborted);
//    }
//
//    private void showProgressBar() {
//        mProgressBar.setVisibility(View.VISIBLE);
//        mStatusTextView.setVisibility(View.VISIBLE);
//        mStatusTextView.setText(null);
//    }
//
//    private void showErrorMessage(final int code, final boolean connectionError) {
//        hiddenProgressBar(false);
//        if (connectionError)
//            showToast("Upload failed: " + GattError.parseConnectionError(code));
//        else
//            showToast("Upload failed: " + GattError.parse(code));
//    }
//
//    private void hiddenProgressBar(final boolean clearDevice) {
//        mProgressBar.setVisibility(View.INVISIBLE);
//        mStatusTextView.setVisibility(View.INVISIBLE);
//        if (clearDevice) {
//            mSelectedDevice = null;
//        }
//        // Application may have lost the right to these files if Activity was closed during upload (grant uri permission). Clear file related values.
//    }
//
//    private void showToast(final int messageResId) {
//        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
//    }
//
//    private void showToast(final String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    private class FileListAdapter extends BaseAdapter {
//        // private AntilostDevice mLeDevices;
//        private LayoutInflater mInflator;
//        HashMap<String, Boolean> states = new HashMap<String, Boolean>();//���ڼ�¼ÿ��RadioButton��״̬������ֻ֤��ѡһ��
//
//        public FileListAdapter() {
//            super();
//            mInflator = getLayoutInflater();
//        }
//
//        public void clear() {
//            // mLeDevices.clear();
//        }
//
//        @Override
//        public int getCount() {
//            return firmwareFileList.size();
//        }
//
//        @Override
//        public View getView(final int i, View view, ViewGroup viewGroup) {
//            final ViewHolder viewHolder;
//
//            //System.out.println("getView getView getView");
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.file_list_item, null);
//                viewHolder = new ViewHolder();
//                viewHolder.item_check = (RadioButton) view.findViewById(R.id.radioButton);
//                viewHolder.item_file_name = (TextView) view.findViewById(R.id.textView);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//            if (viewHolder.item_file_name != null) {
//                viewHolder.item_file_name.setText(firmwareFileList.get(i));
//            }
//            viewHolder.item_check.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //���ã�ȷ�����ֻ��һ�ѡ��
//                    for (String key : states.keySet()) {
//                        states.put(key, false);
//                    }
//                    states.put(String.valueOf(firmwareFileList.get(i)), viewHolder.item_check.isChecked());
//                    FileListAdapter.this.notifyDataSetChanged();
//
//                    firmwareFileName = firmwareFileList.get(i);
//                    System.out.println(firmwareFileName);
//                    showProgressBar();
//                    DownloadFileFromFtp mDownloadFileFromFtp = new DownloadFileFromFtp();
//                    mDownloadFileFromFtp.start();
//                }
//            });
//            boolean res;
//            if (states.get(firmwareFileList.get(i)) == null || states.get(firmwareFileList.get(i)) == false) {
//                res = false;
//                states.put(firmwareFileList.get(i), false);
//            } else {
//                res = true;
//            }
//            viewHolder.item_check.setChecked(res);
//
//            return view;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            // TODO Auto-generated method stub
//            return firmwareFileList.get(i);
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        class ViewHolder {
//            RadioButton item_check;
//            TextView item_file_name;
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_dfu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
