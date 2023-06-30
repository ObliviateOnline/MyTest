package com.example.mytest;

import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.constant.PermissionConstants;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.Manifest;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class QrScanActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private PreviewView previewView;
    private TextView resultTextView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private BarcodeScanner barcodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qr_scan_activity);

        request();

        setupCamera();


    }

    private void setupCamera() {
        previewView = findViewById(R.id.preview_view);
        resultTextView = findViewById(R.id.result_text_view);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

                // 创建BarcodeScanner实例
                barcodeScanner = BarcodeScanning.getClient();

                // 创建ImageAnalysis实例
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // 设置ImageAnalysis分析器
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
                    @Override
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    public void analyze(@NonNull  ImageProxy image) {
                        // 获取ImageProxy对象
                        Image mediaImage = image.getImage();
                        if (mediaImage != null) {
                            // 创建InputImage对象
                            InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                            // 使用BarcodeScanner识别二维码
                            barcodeScanner.process(inputImage)
                                    .addOnSuccessListener(barcodes -> {
                                        for (Barcode barcode : barcodes) {
                                            if (barcode.getFormat() == Barcode.FORMAT_QR_CODE) {
                                                String qrCodeValue = barcode.getRawValue();
                                                // 在UI线程中更新TextView
                                                runOnUiThread(() -> resultTextView.setText(qrCodeValue));
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // 处理扫描失败的情况
                                        Log.e("QrScanActivity", "Failed to scan barcode", e);
                                    })
                                    .addOnCompleteListener(task -> image.close());
                        } else {
                            image.close();
                        }
                    }
                });

                // 绑定ImageAnalysis实例到相机生命周期中
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("QrScanActivity", "Failed to initialize camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void request() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                //可以添加其他的权限，用来判断
        };
        //判断有没有权限
        if (EasyPermissions.hasPermissions(this, permissions)) {
            // 有权限，需要做什么
        } else {
            // 没有权限, 申请权限
            EasyPermissions.requestPermissions(
                    this,
                    "摄像机需要用户允许才能调用，请开启相关权限（理由）",
                    1,
                    permissions
            );
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        for (String temp : perms) {
            if (temp.equals(Manifest.permission.CAMERA)) {
                Log.e("QrScanActivity", "CAMERA onPermissionsGranted");
            }
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("QrScanActivity", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        String permissions = Manifest.permission.CAMERA;
        if (perms.contains(permissions)) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this)
                        .setTitle("The permission has been denied by you")
                        .setRationale("If you do not open the permission, you cannot use this function, click OK to open the permission")
                        .setRequestCode(12345)
                        .build()
                        .show();
            }
        }
    }
}

