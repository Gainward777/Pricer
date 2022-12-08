package com.example.pricer;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.soloader.nativeloader.NativeLoader;
import com.facebook.soloader.nativeloader.SystemDelegate;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class YoloCall {

    static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};

    Module mModule=null;

    // create object
    public  YoloCall(Context context, String modelName){

        // initialize the library that contains nms
        // without it we can't use nms from torchvision included in model.ptl
        // and as result can't load model
        if (!NativeLoader.isInitialized()) {
            NativeLoader.init(new SystemDelegate());
        }
        NativeLoader.loadLibrary("torchvision_ops");

        // load model
        try {
            mModule = LiteModuleLoader.load(assetFilePath(context.getApplicationContext(), modelName));//Module.load(MainActivity.assetFilePath(getApplicationContext(), "best.torchscript (27).ptl")); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calling the model and getting recognized digits.
    // Thanks to nms and filtering output do not require post-processing
    public float[] Call(Bitmap cropedBitmap, int imgW, int imgH){

        // resize picture (closest camera resolution set in CameraCall 640x480
        // converted to 480x480, but we still need 416x416)
        Bitmap bitmap = Bitmap.createScaledBitmap(cropedBitmap, imgW, imgH, true);

        // transform image to tensor (with yolo we don't need any transformation, as mean etc)
        final Tensor t = TensorImageUtils.bitmapToFloat32Tensor(bitmap, NO_MEAN_RGB, NO_STD_RGB);

        // feed model and get result
        final Tensor output = mModule.forward(IValue.from(t)).toTensor();
        final float[] outputs = output.getDataAsFloatArray();

        return outputs;
    }

    // get model address
    private static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
