package com.embednet.wdluo.JackYan.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.embednet.wdluo.JackYan.Constants;
import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.ui.SweetDialog;
import com.embednet.wdluo.JackYan.util.L;
import com.embednet.wdluo.JackYan.util.RxLocationUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lab.dxythch.com.netlib.rx.RxSubscriber;
import lab.dxythch.com.netlib.utils.RxNetUtils;
import laboratory.dxy.jack.com.jackupdate.ui.RxToast;

public abstract class BaseLocationActivity extends BaseActivity {

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        gpsCheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public abstract void getGps(Location location);

    private void getLocation() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (Constants.latitude == 0 && RxNetUtils.isAvailable(BaseLocationActivity.this)) {
                    L.d("【定位改变】:经度" + location.getLongitude());
                    L.d("【定位改变】:海拔" + location.getAltitude());
                    L.d("【定位改变】:维度" + location.getLatitude());
                    getGps(location);
                } else if (!RxNetUtils.isAvailable(BaseLocationActivity.this)) {
                    RxToast.warning("网络不可用");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                L.d("【onStatusChanged】:" + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                L.d("当前GPS设备已打开");
            }

            @Override
            public void onProviderDisabled(String provider) {
                L.d("当前GPS设备已关闭");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            L.d("checkSelfPermission");
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, -1, 100, mLocationListener);
    }

    //----------------------------------------------------------------------------------------------检测GPS是否已打开 start
    private void gpsCheck() {
        if (!RxLocationUtils.isGpsEnabled(this)) {
            new SweetDialog(this)
                    .setTitleText("GPS未打开")
                    .setContentText("您需要在系统设置中打开GPS方可采集数据")
                    .setConfirmText("前往")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            RxLocationUtils.openGpsSettings(sweetAlertDialog.getContext());
                        }
                    })
                    .show();
        } else {
            sheckPromission();
        }
    }


    private void sheckPromission() {
        L.d("开启权限请求");
        //定位权限
        if (Build.VERSION.SDK_INT >= 23)
            new RxPermissions(this)
                    .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new RxSubscriber<Permission>() {
                        @Override
                        protected void _onNext(Permission permission) {
                            if (permission.granted) {
                                // 用户已经同意该权限
                                getLocation();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』，提醒用户手动打开权限
                                RxToast.error(getString(R.string.NoPromiss));
                            }
                        }
                    });
        else
            getLocation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
