package com.erenduran.foodsociety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    //Bitmap burada oluşturduk ki her yerde kullanabilelim
    Bitmap selectedImage;
    // imageView tanımlıyoruz ki seçilen görsel ile imageView i onActivityResult ta değiştirebilelim
    ImageView imageView;
    EditText commentText;

    // firebase storage tanımlanması, daha sonra da onCreate altına initialize edilir
    private FirebaseStorage firebaseStorage;

    // uri tanımlanır imageData diye
    Uri imageData;

    //storage da neyi nereye kaydedeceğimizi belirtmek için referans kullanılır bu yüzden referans tanımlanır
    // StorageReference sınıfında storageReference oluşturulur , daha sonra da onCreate altına initialize edilir
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //yukarıda (globalde oluşturulan ImageView ve EditText burada tanımlandı)

        imageView = findViewById(R.id.imageView);
        commentText = findViewById(R.id.commentText);

        //  globalde tanımlanan firebase storage a initialize edildi
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();


    }

    public void upload(View view) {
        // yukarıda initialization yapıldı burada da tıklandıktan sonra ne yapılacağı yapılır
        //storage reference ile bir görsel kaydedilebilir ve nereye kaydedildiği söylenebilir
        //storageReference.child() ile hangi klasör içine girileceği söylenir

        //.putFile ın içine koyacağımız uri aşağıda onActivityResult ta oluşturulmuştu
        // globalde imageData tanımlanır uri ile ve onActivityResult ta ki uri silinir ,zaten globale tanımladık her yerde kullanabilmek için

        //imageData boş değilse kullanıcı gerçekten bir şey seçtiyse diye if bloğu kullandık
        //ilk images klasör adı , images2 onun içindeki klasör , images2.jpg ise resmin kaydedilirken kullanılan adı
        if (imageData != null) {

            //universal unique id
            //UUID, rastgele bir id çıkar manasına gelir
            UUID uuid = UUID.randomUUID();

            //images klasörüne uuid'yi koy, sonuna da jpg ekle
            String imageName = "images/" + uuid + ".jpg";


            //image klasörünün içinde uydurma(random) bir id ve sonunda ise jpg uzantısının olduğu anlamına gelir.
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Download URL
                    //Download URL'yi almak için bir tane daha referans almak gerekiyor
                    //Upload edilen image'ın nereye kaydedildiğini depo içerisinde bul demek
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);

                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Url oluşturma
                            String downloadUrl = uri.toString();


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();

                }
            });

        }


    }

    public void selectImage(View view) {
        //izin var mı onu kontrol ediyoruz
        // API 23 ve öncesinde uyumlu olabilmesi için "ContextCompat" kullanıldı 23 öncesine izin istemek gerekmiyor sadece manifest e eklemek yeterli

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // eğer izin yoksa ActivityCompat çağırılır,requestPermissions ile izinler istendi
            //yeni oluşturulan dizinin içine hangi izin istenecek ise o yazıldı
            //requestCode bu izinin istendiği kod

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            // bu izin daha önceden verilmiş ise bir intent yapılır
            // bu sefer activity belirtmek yerine ,birşeyi PICK yapacağız (alacağız)
            // nereden alacağımızı ise MediaStore.Images.Media...

            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // sonra bir sonuç için bu aktivite yi başlat deyip

            startActivityForResult(intentToGallery, 2);

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

        if (requestCode == 1) {

            /*yukarıda bize verilen integer dizisi (grandReslts) kontrol edilir ,bunların içinde verilen bir sonuç var ise işlem yapılır ve
            verilen sonuçlerdan dizi içerisindeki ilki(grandResults[0] ,0.index) = PackageManager.PERMISSION_GRANTED ise yani izin verildiyse */
            //hem izin olacak , hem izin verilmiş olacak.Eğer izin verilmiş ise 35. satırda yapılanlar yapılır.

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);

            }
        }
    }

    //başlatılan aktivitenin sonucu burada veriliyor (Yukarıda ki startActivity nin)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // izinler alındı,galeriye gidildi,resim seçildi
        //requestCode kontrol edilir aynı mı?
        //resultCode == RESULT_OK yani kullanıcı cancel yapmadı gidip görseli seçti
        //data ! = null (data boş değil ise)
        //gerçekten geriye gelen bir görsel var ise

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            // verilen data bir uri a çevrilir (uri , url gibi bir şeydir, görselin nerede kayıtlı olduğunun adresi)

            imageData = data.getData(); //sadece bu verildi henüz Bitmap fln verilmedi (bir görsele çevirilmedi) , onu da aşağıda çeviriyoruz
            /* Uri imageData = data.getData(); diye tanımlamıştık globalde imageData yı tanımladığımız için
            Uri yi burada sildik  ve imageData = data.getData(); oldu */


            // globalde Bitmap tanımladık adına selectedImage dedik

            //imageData yı kullanarak Bitmap elde etmek istiyoruz , bunun için de
            //ContentResolver içerik çözümleyicisi ne yukarıda alınan imageData uri ını verdik
            // getBitmap hata verdi try&catch içinde yap dedi alt+enter yaparak try&cath ile yaptık
            try {
                // sdk kontrolü yapılıyor sdk 28 ve üstü ise (Bitmap sdk 28 ve  üstünde kullanmamak gerekiyo )
                if (Build.VERSION.SDK_INT >= 28) {
                    //ImageDecoder bir yöntemdir
                    // source adında oluşturulan ImageDecoder i almak için ImageDecoder.createSource()
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    // tek fark selectedImage yi alırken ImageDecoder.decodeBitmap() denir (getBitmap yerine decodeBitmap diyoruz kısaca)
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);

    }
}