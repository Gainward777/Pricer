package com.example.pricer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

// class set currency list, and get rates
public class GetARate {

    // create list that will be added default currencies
    List<String> abbCurNamesCouples = new ArrayList<>();

    // filling currency list get locate, determinate locate's currency
    // and add it to list (get locate and get currency from the server
    // not implemented yet)
    GetARate(Context context, String[] abbCurNames){
        // if have got internet connection add locate's currency
        if(CheckINet.hasConnection(context)){
            abbCurNamesCouples.add("TRY/RUB(auto)");
        }
        // fill list with default currency
        for (String abbCurName: abbCurNames
             ) {
            abbCurNamesCouples.add(abbCurName+"/RUB");
        }

    }

    // when user chose currency get rate from the server
    // not implemented yet
    public String getRate(Context context, int position){

        if(CheckINet.hasConnection(context)){
            String[] currency = abbCurNamesCouples.get(position).split("/");
            return updateRate(currency[1]);
        }
        else{
            return "Has no internet connection. Please enter current rate.";
        }
    }

    private String updateRate(String cur){

        //method-plug. real update not implemented yet.
        if (cur.contains("auto")){
            return "3.5";
        }
        else {
            return "Not implemented yet. Please enter current rate.";
        }
    }
}
