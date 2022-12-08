package com.example.pricer;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;

// Class-helper. Needed for automatic resizing of EditText. Passes the text to the
// hidden TextView where the resizing takes place. After that, this text size is divided
// by a constant and set to the main EditText
public class EditTextResize {

    static void setAuto(EditText resizebleET, TextView invisibleTV){

        resizebleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                invisibleTV.setText(s.toString());
                float size=invisibleTV.getTextSize();
                size= (float) (size/2);
                resizebleET.setTextSize(size);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    static void setAuto(TextInputEditText resizebleET, TextView invisibleTV){

        resizebleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                invisibleTV.setText(s.toString());
                float size=invisibleTV.getTextSize();
                size= (float) (size/4);
                resizebleET.setTextSize(size);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // This version of the method, after all, calculates the exchanged price
    static void setAuto(EditText recognizedPrice, EditText rate,  TextView invisibleTV, TextView priceWithExchange){

        recognizedPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Calculate and set text size
                invisibleTV.setText(s.toString());
                float size=invisibleTV.getTextSize();
                size= (float) (size/2);
                recognizedPrice.setTextSize(size);

                // Calculate and set price after exchange
                if(Digints.tryParse(recognizedPrice.getText().toString())
                        && Digints.tryParse(rate.getText().toString())) {

                    double exchangeResult = (double)Math.round(Double.valueOf(recognizedPrice.getText()
                            .toString()) * Double.valueOf(rate.getText().toString()) * 100) / 100;

                    if(exchangeResult>1000000){
                        priceWithExchange.setText(String.valueOf(BigDecimal.valueOf(exchangeResult)));
                    }
                    else {
                        priceWithExchange.setText(String.valueOf(exchangeResult));
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
