package com.example.pricer;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Size;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

// get picture from the camera, and work with it
public class CameraCall {

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    YUVtoRGB translator = new YUVtoRGB();

    public Bitmap cropBitmap;

    public void initialize(ImageView preview, MainActivity thisContext, MainActivity mainActivityContext) {

        cameraProviderFuture = ProcessCameraProvider.getInstance(thisContext);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // set resolution closest to the resolution of the pictures of the training dataset
                    // and strategy to work with picture
                    // The nearest camera resolution 640x480 in analyze we crop it to 480x480 this will allow
                    // not to overload the memory with an unreasonably large picture large picture
                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(416, 416))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    // back camera selection
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    // image transformation
                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(mainActivityContext),
                            new ImageAnalysis.Analyzer() {
                                @Override
                                public void analyze(@NonNull ImageProxy image) {
                                    Image img = image.getImage();

                                    // if image YUV translate it to RGB
                                    Bitmap bitmap = translator.translateYUV(img, mainActivityContext);

                                    Matrix matrix = new Matrix();

                                    // set matrix rotation by default image angle (it's 90)
                                    matrix.postRotate(image.getImageInfo().getRotationDegrees());

                                    // crop image to 480x480 and rotate it by matrix buffer
                                    cropBitmap= Bitmap.createBitmap(bitmap, (bitmap.getWidth()-bitmap.getHeight())/2,0, bitmap.getHeight() ,bitmap.getHeight(), matrix, true);

                                    // set cropped image as view0
                                    preview.setImageBitmap(cropBitmap);

                                    image.close();
                                }
                            });

                    cameraProvider.bindToLifecycle(mainActivityContext, cameraSelector, imageAnalysis);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(thisContext));
    }
}
