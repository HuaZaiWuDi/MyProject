package lab.dxythch.com.netlib.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lab.dxythch.com.netlib.utils.updateListener.OnUpdateFileListener;
import okhttp3.ResponseBody;

/**
 * 项目名称：MyProject
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/4/19
 */
public class RxUpdateFileUtils {

    String TAG = this.getClass().getSimpleName();


    String path;


    private OnUpdateFileListener onUpdateFileListenter;


    public RxUpdateFileUtils(String path) {
        this.path = path;
    }

    public boolean writeResponseBodyToDisk(ResponseBody body, OnUpdateFileListener onUpdateFileListenter) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            onUpdateFileListenter.start();
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);


                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        onUpdateFileListenter.end();
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    onUpdateFileListenter.progress((int) (fileSizeDownloaded / fileSize), fileSizeDownloaded, fileSize);

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                onUpdateFileListenter.error(e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            onUpdateFileListenter.error(e);
            return false;
        }
    }

}
