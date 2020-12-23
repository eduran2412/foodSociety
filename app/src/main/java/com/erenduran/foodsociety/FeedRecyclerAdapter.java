package com.erenduran.foodsociety;

import android.graphics.ImageDecoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder> {

    //(1!)arraylist ler oluşturulur,FeedRecyclerAdapter deki işlemleri bitirebilmek için FeedActivity deki arraylistler kullanılır
    private ArrayList<String> userEmailList;
    private ArrayList<String> userCommentList;
    private ArrayList<String> userImageList;
    //(2!)constructor oluşturulur,artık burada bi obje oluşturulurken ArrayListler verilmek zorunda


    public FeedRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> userCommentList, ArrayList<String> userImageList) {
        this.userEmailList = userEmailList;
        this.userCommentList = userCommentList;
        this.userImageList = userImageList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//viewHolder oluşturulunca ne yapacağı
           //recycler row ile bağlama işlemi yapılır.inflater ile
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {//viewHolder e bağlanınca ne yapılacağı
        holder.userEmailText.setText(userEmailList.get(position)); // holder yanındakilere referans ediyo,
                                                    //(5!)parantez içine kaçıncı pozisyondaysak onu getir diyoruz mesela ana sayfada
                                                   // recyclerView in ilk row unda isek 0 verir,rowdaki index 0 ise arraylist teki de 0 olur
        holder.commentText.setText(userCommentList.get(position));//(6!)yukarıdakinin aynısı


    }

    @Override
    //(1)recycler view da kaç tane row olacağını yazarız
    public int getItemCount() {

        return userEmailList.size(); //(4!) return 0 değil artık,bütün user.. ların size ı aynı birini kullanmak yeterli
                                    // [yukarıda private tanımlanan arraylistler]
    }

    //recycler_row.xml içerisinde oluşturulan görünümler burada tanımlanır.
    class PostHolder extends RecyclerView.ViewHolder { //VievHolder görünüm tutucu

        ImageView imageView;
        TextView userEmailText;
        TextView commentText;


        public PostHolder(@NonNull View itemView) { // parantez içine itemView verdiği  için
                                                   // aşağıda itemView kullanarak üst satırdakiler tanımlanır
            super(itemView);

            imageView = itemView.findViewById(R.id.recyclerview_row_imageview);
            userEmailText = itemView.findViewById(R.id.recyclerview_row_useremail_text);
            commentText = itemView.findViewById(R.id.recyclerview_row_comment_text);


        }
    }
}
