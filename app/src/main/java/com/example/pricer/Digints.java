package com.example.pricer;

public class Digints {

    // check number or not
    static boolean tryParse(String s){

        try{
            Double.valueOf(s);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
}
