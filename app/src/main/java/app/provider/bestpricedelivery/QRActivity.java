package app.provider.bestpricedelivery;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import app.provider.bestpricedelivery.Constants.Constants;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRActivity extends AppCompatActivity {
    ImageView qrImage;
    TextView qrCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        qrImage = findViewById(R.id.qrImage);
        qrCodeText = findViewById(R.id.qrCodeText);
        findViewById(R.id.menuImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setQrImage();
    }

    void setQrImage() {
        ImageView qrImage = findViewById(R.id.qrImage);

        try {
            Bitmap bitmap = encodeAsBitmap(getIntent().getStringExtra("qrcode"));
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Bitmap bitmap = null;
            try {
                bitmap = encodeAsBitmap("My Treat");
            } catch (WriterException e1) {
                e1.printStackTrace();
            }
            qrImage.setImageBitmap(bitmap);
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        qrCodeText.setText(str);
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h);
        return bitmap;
    }

    void updateWorkerStatus() {
        try {
            qrCodeText.setText(Constants.getSavedPreferences(QRActivity.this, "qrcode", ""));
            Glide.with(QRActivity.this)
                    .load(Constants.getSavedPreferences(QRActivity.this, "qimage", ""))
                    .into(qrImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
