package com.example.fuelcall;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

@Metadata(
        mv = {1, 5, 1},
        k = 1,
        d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014¨\u0006\u0007"},
        d2 = {"Lcom/example/fuelcall/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"}
)
public final class MainActivity extends AppCompatActivity {

    //Button For pdf print
    Button Print;

    //Page Dimensions
    int pageHeight = 320;
    int pageWidth = 219;

    //Bitmap for images
    Bitmap bmp, scalebmp;

    private static final int PERMISSION_REQUEST_CODE = 200;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        Print = findViewById(R.id.print);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scalebmp = Bitmap.createScaledBitmap(bmp, 120, 70, false);
        //For checking permissions for data storage and other stuff
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }


        //FOR UI
        EditText vehicle_no = (EditText) this.findViewById(R.id.vehicle_no);
        EditText cx_name = (EditText) this.findViewById(R.id.cx_name);
        final EditText rate = (EditText) this.findViewById(R.id.rate);
        final EditText rupees = (EditText) this.findViewById(R.id.rupees);
        final EditText liters = (EditText) this.findViewById(R.id.liters);
        Button calc = (Button) this.findViewById(R.id.calculate);
        Print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF(vehicle_no.getText().toString(),cx_name.getText().toString(),rate.getText().toString(),rupees.getText().toString(),liters.getText().toString());
            }
        });
        calc.setOnClickListener((OnClickListener) (new OnClickListener() {
            public final void onClick(View it) {
                EditText var10000 = rupees;
                Intrinsics.checkNotNullExpressionValue(var10000, "rupees");
                String var3 = var10000.getText().toString();
                boolean var4 = false;
                float stored_rupees = Float.parseFloat(var3);
                var10000 = rate;
                Intrinsics.checkNotNullExpressionValue(var10000, "rate");
                String var7 = var10000.getText().toString();
                boolean var5 = false;
                float stored_rate = Float.parseFloat(var7);
                if (stored_rate > (float) 0 && stored_rupees > (float) 0) {
                    float ltr = stored_rupees / stored_rate;

                    liters.setText((CharSequence) String.format("%.1f",ltr));
                } else {
                    Toast.makeText(MainActivity.this.getApplicationContext(), (CharSequence) "Error", Toast.LENGTH_SHORT).show();
                }

            }
        }));
    }

    private void generatePDF(String vehicle_no, String cx_name, String rate, String rupees, String liters) {

        //Initialising Variables
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        Paint ph_no = new Paint();
        Paint bill_details = new Paint();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date time = new Date();
        String time_today = format.format(time);
        Date date = new Date();
        String today = formatter.format(date);
        int random;
        random = new Random().nextInt(1000000000);
        int trns_random = new Random().nextInt(1000000000);
        int fp_id = new Random().nextInt(10);

        //Creating the PDF File
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        //Starting the page
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        //Canvas is used for drawing on the pdf page
        Canvas canvas = myPage.getCanvas();

        //Drawing the image
        canvas.drawBitmap(scalebmp, 50, 0, paint);


        //Section to add all the data in the PDF file
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(20);
        title.setTextAlign(Paint.Align.CENTER);
        ph_no.setTextSize(9);
        ph_no.setTextAlign(Paint.Align.CENTER);
        bill_details.setTextSize(10);
        bill_details.setTextAlign(Paint.Align.LEFT);
        bill_details.setTypeface(Typeface.create(Typeface.MONOSPACE,Typeface.NORMAL));


        //Data entered
        canvas.drawText("Fuel@Call", 109, 80, title);
        canvas.drawText("+91-8969439715", 109, 95, ph_no);
        canvas.drawText("Bill No",5,125,bill_details);
        canvas.drawText(":"+String.valueOf(random),100,125,bill_details);
        canvas.drawText("Trns.ID ",5,135,bill_details);
        canvas.drawText(":"+String.valueOf(trns_random),100,135,bill_details);
        canvas.drawText("Atnd.ID ",5,145,bill_details);
        canvas.drawText(":",100,145,bill_details);
        canvas.drawText("Receipt ",5,155,bill_details);
        canvas.drawText(":Physical Receipt",100,155,bill_details);
        canvas.drawText("Vehicle No. ",5,165,bill_details);
        if(vehicle_no.length()==0){
            canvas.drawText(":Not Entered",100,165,bill_details);
        }
        else{
            canvas.drawText(":"+String.valueOf(vehicle_no),100,165,bill_details);
        }
        canvas.drawText("Mob.No.",5,175,bill_details);
        canvas.drawText(":Not Entered ",100,175,bill_details);
        canvas.drawText("Date",5,185,bill_details);
        canvas.drawText(":"+today,100,185,bill_details);
        canvas.drawText("Time",5,195,bill_details);
        canvas.drawText(":"+time_today,100,195,bill_details);
        canvas.drawText("FP.ID",5,205,bill_details);
        canvas.drawText(":"+fp_id,100,205,bill_details);
        canvas.drawText("Nozl No",5,215,bill_details);
        canvas.drawText(":1",100,215,bill_details);
        canvas.drawText("Fuel:",5,225,bill_details);
        canvas.drawText(":Diesel",100,225,bill_details);
        canvas.drawText("Volume",5,235,bill_details);
        canvas.drawText(":"+liters+"Ltr",100,235,bill_details);
        canvas.drawText("Rate",5,245,bill_details);
        canvas.drawText(":Rs. "+rate,100,245,bill_details);
        canvas.drawText("Sale",5,255,bill_details);
        canvas.drawText(":Rs. "+rupees,100,255,bill_details);
        canvas.drawText("Customer Name",5,115,bill_details);
        canvas.drawText(":"+cx_name,100,115,bill_details);
        canvas.drawText("Thank you Please Visit Again!!",5,295,bill_details);




        //Ending the pdf file
        pdfDocument.finishPage(myPage);


        //Setting the save path
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Invoice.pdf");

        try {
            //writing our data in the pdf file
             pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, "Invoice Generated", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
