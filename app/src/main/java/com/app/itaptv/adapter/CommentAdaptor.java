package com.app.itaptv.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.itaptv.R;
import com.app.itaptv.structure.PostComment;
import com.app.itaptv.utils.Log;
import com.app.itaptv.utils.Utility;

import java.util.ArrayList;

public class CommentAdaptor extends RecyclerView.Adapter<CommentAdaptor.CommentHolder> {
    private ArrayList commentList = null;
    public CommentAdaptor(ArrayList list){
        commentList = list;
    }

    public void updateCommentAdaptor(ArrayList list){
        Log.e("avinash","updateCommentAdaptor");
        commentList = list;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.row_comment_item,viewGroup,false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder commentHolder, int i) {

        PostComment comment = (PostComment) commentList.get(i);
        if(comment.getAuthor().getDisplayName().isEmpty()){
            commentHolder.txtAuthorName.setVisibility(View.GONE);
            commentHolder.txtAuthorMobile.setVisibility(View.VISIBLE);
            commentHolder.txtAuthorMobile.setText(Utility.getMobileStrikeOutMiddle(comment.getAuthor().getMobile_no()));
        }else{
            commentHolder.txtAuthorName.setVisibility(View.VISIBLE);
            commentHolder.txtAuthorMobile.setVisibility(View.GONE);
            commentHolder.txtAuthorName.setText(comment.getAuthor().getDisplayName());
        }
       /* if(comment.getAuthor().getMobile_no().isEmpty()){
            commentHolder.txtAuthorMobile.setVisibility(View.GONE);
        }else{
            commentHolder.txtAuthorMobile.setVisibility(View.VISIBLE);
            commentHolder.txtAuthorMobile.setText(Utility.getMobileStrikeOutMiddle(comment.getAuthor().getMobile_no()));
        }*/
        commentHolder.txtComment.setText(comment.getCommentContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        TextView txtComment;
        TextView txtAuthorName;
        TextView txtAuthorMobile;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtAuthorName = itemView.findViewById(R.id.txtAuthorName);
            txtAuthorMobile = itemView.findViewById(R.id.txtAuthorMobileNo);
        }
    }
}
