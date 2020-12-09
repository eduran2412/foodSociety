package com.erenduran.foodsociety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }
    public void upload (View view){

    }
    public void selectImage (View view){
        //izin var mı onu kontrol ediyoruz
        // API 23 ve öncesinde uyumlu olabilmesi için "ContextCompat" kullanıldı 23 öncesine izin istemek gerekmiyor sadece manifest e eklemek yeterli

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            // eğer izin yoksa ActivityCompat çağırılır,requestPermissions ile izinler istendi
            //yeni oluşturulan dizinin içine hangi izin istenecek ise o yazıldı
            //requestCode bu izinin istendiği kod

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            // bu izin daha önceden verilmiş ise bir intent yapılır
            // bu sefer activity belirtmek yerine ,birşeyi PICK yapacağız (alacağız)
            // nereden alacağımızı ise MediaStore.Images.Media...

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // sonra bir sonuç için bu aktivite yi başlat deyip

            startActivityForResult(intentToGallery,2);

        }


    }
    // istenilen izinlerin sonucu ne oldu ?
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* izin istendi ve verildi , 1 defaya mahsus bu method çağırılır,çünkü bu methodu override edemez isek galeri ye gidemeyiz ,sadece
        izin alınmış olur tekrar select image a tıklamak zorunda kalır , bu da user experience açısından iyi olmaz,bizim izin istendiği gibi
        götürmemiz gerekiyor */

        // request kod kontrol edilir

        if(requestCode == 1){

            /*yukarıda bize verilen integer dizisi (grandReslts) kontrol edilir ,bunların içinde verilen bir sonuç var ise işlem yapılır ve
            verilen sonuçlerdan dizi içerisindeki ilki(grandResults[0] ,0.index) = PackageManager.PERMISSION_GRANTED ise yani izin verildiyse */
            //hem izin olacak , hem izin verilmiş olacak.Eğer izin verilmiş ise 35. satırda yapılanlar yapılır.

            if(grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);

            }
        }
    }

    //başlatılan aktivitenin sonucu burada veriliyor (Yukarıda ki startActivity nin)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}