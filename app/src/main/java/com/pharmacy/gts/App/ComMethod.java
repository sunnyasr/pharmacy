package com.pharmacy.gts.App;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pharmacy.gts.Activities.LoginActivity;
import com.pharmacy.gts.PrefManager.LoginManager;
import com.pharmacy.gts.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.R.id.message;
import static android.content.Intent.ACTION_SEND;

public class ComMethod {

    Context context;
    LayoutInflater inflater;
    private Dialog dialog;
    private long time;
//    public final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//    public final static String usernamePattern = "[a-zA-Z0-9._-]";

    public ComMethod(Context context) {
        this.context = context;
        dialog = new Dialog(context);
    }

  /*  public void about(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.about_app_layout, null);
        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/about/index.htm");
        webView.getSettings().setJavaScriptEnabled(true);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    public String imageToString(Bitmap bitmap) {

        final int maxSize = 1000;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();


        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;

        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public String profileToString(Bitmap bitmap) {

        final int maxSize = 250;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();


        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;

        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public void share(Context context) {

        Intent sharingIntent = new Intent(ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Soda App :\nDownload now.\n https://play.google.com/store/apps/details?id=" + context.getPackageName());
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public void rate(Context context) {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName());
        Intent viewIntent =
                new Intent(Intent.ACTION_VIEW, uri);

        context.startActivity(viewIntent);

    }


    public void phoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public void sendMail(Context context) {
        String subject = "Regarding App(Jai MLM)";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "info@jaisoftwares.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    public void facebook(Context context) {
        Intent facebookAppIntent;
        try {
            facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/127702300627524"));
            context.startActivity(facebookAppIntent);
        } catch (ActivityNotFoundException e) {
            facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/goldentemplephotos/"));
            context.startActivity(facebookAppIntent);
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

   /* static public void shareImage( final Context context,String url) {
        Picasso.with(context).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image*//*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                i.putExtra(android.content.Intent.EXTRA_TEXT, "Golden Temple Photos:\nhttps://play.google.com/store/apps/details?id=genesistechnosoft.goldentemplephotos " );
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }*/

    /*  static public void shareImage(final Context context, String url) {
          Glide.with(context)
                  .load(url)
                  .asBitmap()
                  .into(new SimpleTarget<Bitmap>() {
                      @Override
                      public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                          Intent i = new Intent(Intent.ACTION_SEND);
                          i.setType("image/*");
                          i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                          i.putExtra(android.content.Intent.EXTRA_TEXT, "Golden Temple Photos:\nhttps://play.google.com/store/apps/details?id=genesistechnosoft.goldentemplephotos ");
                          context.startActivity(Intent.createChooser(i, "Share Image"));
                      }
                  });
      }
  */
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void logout() {
        LoginManager loginManager = new LoginManager(context);
        loginManager.setFirstTimeLaunch(true);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
//        context.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    public void alertDialog(final String title, String msg, boolean error, final Intent intent) {

        Button btnYes;
        final TextView tvTitle, tvMsg;
        ImageView ivStatus;
        dialog.setContentView(R.layout.payment_dialog_layout);
        tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        tvMsg = (TextView) dialog.findViewById(R.id.tv_msg);
        ivStatus = (ImageView) dialog.findViewById(R.id.iv_status);

        tvTitle.setText(title);
        tvMsg.setText(msg);
        if (!error)
            ivStatus.setBackgroundResource(R.drawable.success_icon);
        else ivStatus.setBackgroundResource(R.drawable.failed);

        btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme2;
        dialog.show();
    }

    public void openWhatsApp(Context ctx) {
//        PackageManager pm=getPackageManager();
//        try {
//            Intent waIntent = new Intent(Intent.ACTION_SEND);
//            waIntent.setType("text/plain");
//            String text = "This is  a Test"; // Replace with your own message.
//
//            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//            //Check if package exists or not. If not then code
//            //in catch block will be called
//            waIntent.setPackage("com.whatsapp");
//
//            waIntent.putExtra(Intent.EXTRA_TEXT, text);
//            startActivity(Intent.createChooser(waIntent, "Share with"));
//
//        } catch (PackageManager.NameNotFoundException e) {
//            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
//                    .show();
//        }catch(Exception e){
//            e.printStackTrace();
//        }


//        PackageManager pm=ctx.getPackageManager();
//        try {
//
//
//            String toNumber = "918498873939"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
//            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
//
//            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
//            sendIntent.setPackage("com.whatsapp");
//            ctx.startActivity(sendIntent);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(ctx,"It may be you dont have whats app",Toast.LENGTH_LONG).show();
//
//        }

        try {
            String text = "";// Replace with your message.

            String toNumber = "918968793256"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);

            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public void prepareShareIntent(Bitmap bmp, String type, String pname) {
        Uri bmpUri = getLocalBitmapUri(bmp); // see previous remote images section    // Construct share intent as described above based on bitmap
        Intent shareIntent = new Intent();
        if (type.equals("whatsapp"))
            shareIntent.setPackage("com.whatsapp");
//        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, pname);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Opportunity"));

    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmpUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public String dateConvert(String dateConvert) {
        DateFormat formatter = null;
        String result = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Date date = null;
                date = (Date) formatter.parse(dateConvert);
                time = date.getTime();
            }
            result = getDate(time).getTime().toLocaleString();


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Calendar getDate(long time) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return cal;
    }


}
