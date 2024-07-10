package com.example.myapplication.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.ViewModelProvider;


import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityDeliveryBinding;
import com.example.myapplication.repository.OrderRepo;
import com.example.myapplication.viewmodel.DeliveryViewModel;
import com.example.myapplication.viewmodel.DeliveryViewModelFactory;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class DeliveryActivity extends AppCompatActivity {
    ActivityDeliveryBinding binding;
    String orderId,orderNo,orderAddress,orderLat,orderLng,orderCustomerName,orderDeliveryCost,consignmentStatus,damageType;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private ImageCapture imageCapture;
    private Preview preview;
    private ExecutorService cameraExecutor;
    private File photoFile;
    Bitmap bitmap;
    private DeliveryViewModel deliveryViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDeliveryBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        consignmentStatus = "Good";
        getData();
        setSpinner();
        setupViewModel();
        cameraCapture();
        onButtonClick();

    }

    private void setSpinner() {
        ArrayList<String>  damageTypes = new ArrayList<>();
        damageTypes.add("Type-A");
        damageTypes.add("Type-B");
        damageTypes.add("Type-C");
        damageTypes.add("Type-D");
        damageTypes.add("Type-E");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, damageTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDamagedList.setAdapter(spinnerAdapter);
    }

    private void setupViewModel() {
        OrderRepo repository = new OrderRepo(this);
        DeliveryViewModelFactory factory = new DeliveryViewModelFactory(repository);
        deliveryViewModel = new ViewModelProvider(this, factory).get(DeliveryViewModel.class);
    }

    private void onButtonClick() {

        binding.btnCapture.setOnClickListener(v -> takePhoto());

        binding.btnRetake.setOnClickListener(v -> {
            binding.pvCameraView.setVisibility(View.VISIBLE);
            binding.btnCapture.setVisibility(View.VISIBLE);
            binding.btnRetake.setVisibility(View.GONE);
            binding.ivOrderPic.setImageDrawable(null);
            binding.ivOrderPic.setVisibility(View.GONE);
            startCamera();
        });

        binding.rgDeliveryStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbGood) {
                consignmentStatus = "Good";
                binding.spinnerDamagedList.setVisibility(View.GONE);
            } else {
                consignmentStatus = "Damaged";
                binding.spinnerDamagedList.setVisibility(View.VISIBLE);
                }
            });


        binding.btnDeliverItem.setOnClickListener(v ->{
            if(validate()) {
                deliveryViewModel.updateOrder(
                        damageType,
                        binding.edtOrderCollectedAmount.getText().toString(),
                        binding.ivOrderPic.toString(),
                        consignmentStatus,
                        "Delivered",
                        orderId);
            }
        });


        binding.spinnerDamagedList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                damageType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(DeliveryActivity.this, "Please select damage type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validate(){
        if(binding.edtOrderCollectedAmount.getText().toString().isEmpty()){
            binding.edtOrderCollectedAmount.setError("Please enter amount");
            return false;
        } else if (binding.ivOrderPic.getDrawable() == null) {
            Toast.makeText(this, "Please take photo", Toast.LENGTH_SHORT).show();
            return false;
        } else if(
                consignmentStatus.equals("Good") &&
                !binding.tvDeliveryDeliveryCost.getText().toString().equals(binding.edtOrderCollectedAmount.getText().toString())
        ){
            Toast.makeText(this, "Please enter amount equal to delivery cost", Toast.LENGTH_SHORT).show();
            return false;
        } else if (
                consignmentStatus.equals("Damaged") &&
                (
                   Double.parseDouble(binding.tvDeliveryDeliveryCost.getText().toString()) <
                           Double.parseDouble(binding.edtOrderCollectedAmount.getText().toString())
                )
        ) {
            Toast.makeText(this, "Please enter amount less than delivery cost", Toast.LENGTH_SHORT).show();
            return false;
        } else if (consignmentStatus.equals("Damaged") && damageType == null){
            Toast.makeText(this, "Please select the damage type", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void cameraCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        } else {
             startCamera();
        }
    }
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the preview
                preview = new Preview.Builder().build();

                // Set up the image capture use case
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();

                // Bind the preview and image capture to the lifecycle
                preview.setSurfaceProvider(binding.pvCameraView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Unbind any previous use cases
                cameraProvider.unbindAll();

                // Bind use cases to lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String fileName = "IMG_" + sdf.format(new Date()) + ".jpg";

        photoFile = new File(getOutputDirectory(), fileName);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    Toast.makeText(DeliveryActivity.this, "Image saved successfully: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    displayImage();
                    binding.btnCapture.setVisibility(View.GONE);
                    binding.pvCameraView.setVisibility(View.GONE);
                    binding.btnRetake.setVisibility(View.VISIBLE);
                    binding.ivOrderPic.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }
    private void displayImage() {
        bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        binding.ivOrderPic.setImageBitmap(bitmap);
//        imagePreview.setVisibility(View.VISIBLE);
    }

    private File getOutputDirectory() {
        File mediaDir = new File(getExternalFilesDir(null), "DeliveryPhotos");
        if (!mediaDir.exists() && !mediaDir.mkdirs()) {
            Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
            return null;
        }
        return mediaDir;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
            cameraExecutor.shutdown();
        }
    }

    private void getData() {
        orderId = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderId));
        orderNo = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderNo));
        orderAddress = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderAddress));
        orderLat = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderLat));
        orderLng = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderLng));
        orderCustomerName = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderCustomerName));
        orderDeliveryCost = Objects.requireNonNull(getIntent().getExtras()).getString(getString(R.string.orderDeliveryCost));


        binding.tvDeliveryId.setText(orderId);
        binding.tvDeliveryNo.setText(orderNo);
        binding.tvDeliveryCustomerAddress.setText(orderAddress);
        binding.tvDeliveryCustomerName.setText(orderCustomerName);
        binding.tvDeliveryDeliveryCost.setText(orderDeliveryCost);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}