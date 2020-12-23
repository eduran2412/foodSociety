package com.erenduran.foodsociety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.collect.ArrayTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore; //(1)

    //(15.)reycler için stringler tanımlandı
    ArrayList<String> userEmailFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageFromFB;

    FeedRecyclerAdapter feedRecyclerAdapter;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.foodsociety_options_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post) {
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        } else if (item.getItemId() == R.id.signout) {


            firebaseAuth.signOut();

            Intent intentToSignUp = new Intent(FeedActivity.this, SignUpActivity.class);
            startActivity(intentToSignUp);
            finish();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //(16.)globalde tanımlanan stringler initialize edildi.
        userCommentFromFB = new ArrayList<>();
        userEmailFromFB = new ArrayList<>();
        userImageFromFB = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance(); //(2) firebaseFirestore initialize edilir getInstance ile birlikte
        getDataFromFirestore();//(4) aşağıda oluşturulan methodu onCreate altında çağırırız..

        //(3!) oluşturulan feedRecyclerAdapter sayesinde buradaki data FeedActivity de kullanılabilecek

        // RecyclerView tanımlanacak
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // RecyclerViev in kendi layout manager i oluyo,anasayfa da RecyclerView i aşağı doğru vertical ineceği belirtilecek,
        // row ların nasıl sıralanacağı belirtilecek
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       feedRecyclerAdapter = new FeedRecyclerAdapter(userEmailFromFB,userCommentFromFB,userImageFromFB);
       // birbirlerine bağnama işlemi yapılır
        recyclerView.setAdapter(feedRecyclerAdapter);

    }

    //(3)yeni bir method açıldı uzun bir işlem olacağı için
    public void getDataFromFirestore() {
        //(5)burada yukarıda oluşturulan fireStore değişkeni kullanılacak..

        //(6)fireStore daki referansı bulmak için oluşturulan bir sınıf CollectionReference
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");

        //(7)bütün veriler queryDocumentSnapshot içinde yüklü aynı zamanda veri tabanı değişse bile veriler de güncellenir..
        //(14.)date'e göre dizme işlemi orderBy tarafından yapılır. DESCENDING azalarak çekmeyi sağlar.
        // (ASCENDING olsaydı artarak olurdu,date azalarak..[direction bu])
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                //(8) eğer data okunamaz ise ..
                if (e != null) { //(9) eğer e boş ise..
                    Toast.makeText(FeedActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                } //(9)bu dizinin içinde dökümanlar var collection içinde..bu dökümanlara ulaşmaya çalışıyoruz,
                // aşağıdaki diziyi tek tek ele almak gerekiyo bu yüzden for loop yapılır..
                if (queryDocumentSnapshots != null) {

                    //(10)DocumentSnapshot dedik çünkü bize tek DocumentSnapshot olarak gelecek , snapshot dedikten sonra
                    // : yani hangi dizi içerisinden alınacak. getDocuments bize liste içinde DocumentSnapshot veriyo,
                    // o yüzden for içinde DocumentSnapshots objesi oluşturuldu..

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        //(11) bu snapshot bize Map<String,Object> veriyo hashMap ı veriyo
                        Map<String, Object> data = snapshot.getData(); //(12) bu şekilde kaydedilen data ya ulaşılır aşağıda bkz.

                        //(13)aşağıda aldığımız "comment" obje olduğu için string e çevirmeliyiz, biz strink istiyoruz(String) yapılır
                        // casting..
                        String comment = (String) data.get("comment");
                        String userEmail = (String) data.get("useremail");
                        String downloadUrl = (String) data.get("downloadurl");


                        //(17.) tanımlanan stringlerin hepsi buraya kaydedildi.[bunlar recycle view de gösterilecek]
                        userCommentFromFB.add(comment);
                        userEmailFromFB.add(userEmail);
                        userImageFromFB.add(downloadUrl);

                        // içeri veri gelince adapter e uyarı yapılır
                        feedRecyclerAdapter.notifyDataSetChanged();



                    }


                }

            }
        });


    }


}