package com.example.pricer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 83854;

    private ImageView preview;

    double tPrice = 0;
    double serializePriceSecond = 0;
    double serializePriceFirst = 0;
    int counter = 0;
    float fResult;
    float[] yCallResult;

    // create camera object
    CameraCall cam = new CameraCall();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set layout to variables
        preview = findViewById(R.id.preview);
        Button recognize = findViewById(R.id.recognize);
        EditText rPrice = findViewById(R.id.setPrice);
        TextView invisibleView = findViewById(R.id.hideViewForFirstEdit);
        TextInputLayout totalPriceInputLayout = findViewById(R.id.totalPriceInputLayout);
        TextInputEditText totalPrice = findViewById(R.id.totalPrice);
        Button addPrice = findViewById(R.id.cartAdd);
        TextInputLayout rateInputLayout = findViewById(R.id.rateInputLayout);
        TextInputEditText rate = findViewById(R.id.rate);
        TextView invisibleTotalPrice = findViewById(R.id.invisibleTotalPrice);
        TextView invisibleRate = findViewById(R.id.invisibleRate);
        TextView priceWithExchange = findViewById(R.id.priceWithExchange);
        Spinner spinner = findViewById(R.id.valAsStr);

        //check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            //initializeCamera();
            cam.initialize(preview, this, MainActivity.this);

        }

        // create yolo object
        YoloCall yCall = new YoloCall(this, "yolo5s_digits.ptl");

        // set resize edit text
        EditTextResize.setAuto(totalPrice, invisibleTotalPrice);
        EditTextResize.setAuto(rate, invisibleRate);
        EditTextResize.setAuto(rPrice, rate, invisibleView, priceWithExchange);

        // set default currencies to spinner
        GetARate getARate = new GetARate(this, new String[]{"USD", "GBP", "JPY"});

        ArrayAdapter<String> rateAdapter = new ArrayAdapter<String>(this,
                R.layout.my_spinner_text, getARate.abbCurNamesCouples);

        spinner.setAdapter(rateAdapter);
        spinner.setSelection(0);

        // set currency selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // get rate by chosen position
                String gottenRate = getARate.getRate(MainActivity.this, position);

                if (Digints.tryParse(gottenRate)) {
                    // if gtRate return digits
                    rate.setText(gottenRate);
                    String s = getARate.abbCurNamesCouples.get(position).split("/")[0];
                    rateInputLayout.setHint("1 " + s);
                } else {
                    // if getRate return error
                    Toast.makeText(getBaseContext(), gottenRate, Toast.LENGTH_SHORT).show();
                    rateInputLayout.setHint("Set rate");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // set recognize button listener
        recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);

                if (cam.cropBitmap != null) {

                    yCallResult = yCall.Call(cam.cropBitmap, 416, 416);
                    String sResult = "";
                    if (yCallResult.length > 0) {
                        for (int i = 0; i < yCallResult.length; ++i) {

                            sResult = sResult + (int) yCallResult[i];
                        }
                        fResult = Integer.parseInt(sResult);
                        rPrice.setText(sResult);
                    }
                } else {

                }

                view.setEnabled(true);
            }
        });

        // set add to cart listener
        addPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);

                // if price recognized and return digits
                if (Digints.tryParse(priceWithExchange.getText().toString())) {

                    // serialize value that returned before
                    serializePriceSecond = serializePriceFirst;
                    serializePriceFirst = tPrice;

                    //update cart value
                    tPrice = (double) Math.round((tPrice + Double.valueOf(priceWithExchange.getText().toString())) * 100) / 100;
                    if (tPrice > 100000) {
                        totalPrice.setText(String.valueOf(BigDecimal.valueOf(tPrice)));
                    } else {
                        totalPrice.setText(String.valueOf(tPrice));
                    }
                    // set TextInput hint
                    totalPriceInputLayout.setHint("Double press to back.");
                }
                counter = 0;

                view.setEnabled(true);
            }
        });

        // set listener for click on Total Price view
        totalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serializePriceFirst == 0 && serializePriceSecond == 0) {
                    counter = 2;
                }
                switch (counter) {
                    case 0:

                        // set hint and return serialized value
                        totalPriceInputLayout.setHint("Double press to back.");
                        totalPrice.setText(String.valueOf(serializePriceFirst));

                        // checking stored values
                        if (serializePriceFirst != 0 && serializePriceSecond == 0) {
                            // if serialized only one value next click clear the field
                            counter = 2;
                        } else {
                            // if serialized two values next click return another serialized value
                            counter++;
                        }

                        totalPrice.clearFocus();
                        break;

                    case 1:
                        totalPriceInputLayout.setHint("Double press to clear.");
                        totalPrice.setText(String.valueOf(serializePriceSecond));
                        counter++;
                        totalPrice.clearFocus();
                        break;

                    default:
                        totalPriceInputLayout.setHint("Double press to back.");
                        totalPrice.setText("Total price");
                        counter = 0;
                        tPrice = 0;
                        totalPrice.clearFocus();
                        break;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            cam.initialize(preview, this, MainActivity.this);
            //initializeCamera();


        }
    }
}