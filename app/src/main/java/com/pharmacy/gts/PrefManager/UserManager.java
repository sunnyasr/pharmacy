package com.pharmacy.gts.PrefManager;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "pharma-user";

    private static final String KEY_TOKEN = "isToken";
    private static final String KEY_USERNAME = "isUserName";
    private static final String KEY_FNAME = "isFName";
    private static final String KEY_LNAME = "isLName";
    private static final String KEY_MOBILE = "isMobile";
    private static final String KEY_EMAIL = "isEmail";
    private static final String KEY_PHOTO = "isPhoto";
    private static final String KEY_PASS = "isPass";
    private static final String KEY_MID = "isMid";
    private static final String KEY_COMPANY = "isCompany";
    private static final String KEY_OAREA = "isOarea";

    public UserManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public void setUsername(String name) {
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public void setFName(String name) {
        editor.putString(KEY_FNAME, name);
        editor.commit();
    }

    public String getFName() {
        return pref.getString(KEY_FNAME, "");
    }

    public void setLName(String name) {
        editor.putString(KEY_LNAME, name);
        editor.commit();
    }

    public String getLName() {
        return pref.getString(KEY_LNAME, "");
    }

    public void setMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public String getMobile() {
        return pref.getString(KEY_MOBILE, "9876543210");
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "test@gmail.com");
    }


    public void setPhoto(String photo) {
        editor.putString(KEY_PHOTO, photo);
        editor.commit();
    }

    public String getPhoto() {
        return pref.getString(KEY_PHOTO, "");
    }

    public void setPass(String name) {
        editor.putString(KEY_PASS, name);
        editor.commit();
    }

    public String getPass() {
        return pref.getString(KEY_PASS, "");
    }

    public void setMID(String name) {
        editor.putString(KEY_MID, name);
        editor.commit();
    }

    public String getMID() {
        return pref.getString(KEY_MID, "");
    }

    public void setCompany(String name) {
        editor.putString(KEY_COMPANY, name);
        editor.commit();
    }

    public String getCompany() {
        return pref.getString(KEY_COMPANY, "");
    }

    public void setOarea(String name) {
        editor.putString(KEY_OAREA, name);
        editor.commit();
    }

    public String getOarea() {
        return pref.getString(KEY_OAREA, "");
    }





}
