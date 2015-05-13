package com.example.client;



import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CommentActivity extends Activity {
	private TextView commentsBox;
	private EditText addCommentEditBox;
	private Button addNewCommentButton;
	private Button closeButton;
	private Context ctx;
	private com.example.database.DatabaseAdapter dba;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment);
		ctx = this;
		commentsBox = (TextView)findViewById(R.id.commentview);
		addCommentEditBox = (EditText)findViewById(R.id.addnewcommentbox);
		addNewCommentButton = (Button)findViewById(R.id.addnewcomment);
		//closeButton = (Button)findViewById(R.id.closecommentmenu);
		dba = new com.example.database.DatabaseAdapter(ctx);
		
		Bundle bundle = getIntent().getExtras();
		final String poiid = bundle.getString("poiid");
		final String username = bundle.getString("username");
		loadOldComments(poiid);
		
	
		//add a new comment
		addNewCommentButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				dba.open();
				String content = addCommentEditBox.getText().toString();
				dba.addNewComment(content, poiid, username);
				dba.close();
				loadOldComments(poiid);
				addCommentEditBox.setText("");
			}
			
		});
		//not allow to edit if not add new comments.
		if(!POIMain.getStateLogin()){
			addNewCommentButton.setEnabled(false);
			addCommentEditBox.setEnabled(false);
		}
	}
	
	private void loadOldComments(String poiid){
		dba.open();
		Cursor cur = dba.fetchOldComments(poiid);
		if(cur.moveToFirst()){
			StringBuffer sb = new StringBuffer();
			for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
				String displayUsernameOfOldTag = cur.getString(cur.getColumnIndex("username"));
				String dispplayContentOfOldTag = cur.getString(cur.getColumnIndex("content"));
				sb.append(displayUsernameOfOldTag);
				sb.append(" : ");
				sb.append(dispplayContentOfOldTag);
				//sb.append(" created by ");
				//sb.append(displayUsernameOfOldTag);
				sb.append("\n");
			}
			//Log.i("tag", "this is the old comment from the local database"+sb.toString());
			commentsBox.setText(sb.toString());
			
		}
		dba.close();
	}
}
