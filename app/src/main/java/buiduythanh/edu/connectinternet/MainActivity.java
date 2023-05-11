package buiduythanh.edu.connectinternet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private EditText mBookInput;
    private Button mBtnSearch;
    private TextView mAuthorText;
    private TextView mTitleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBook(view);
            }
        });
    }

    private void addControls() {
        mBookInput = findViewById(R.id.mTxtBook);
        mBtnSearch = findViewById(R.id.mBtnSearch);
        mTitleText = findViewById(R.id.mTitleBook);
        mAuthorText = findViewById(R.id.mAuthor);
    }

    public void searchBook(View view){
        String queryString = mBookInput.getText().toString();
        new FetchBook(mTitleText,mAuthorText).execute(queryString);
        mAuthorText.setText("");
        mTitleText.setText("Loading...");
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null){
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connMgr != null){
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected() && queryString.length()!=0){
            new FetchBook(mTitleText,mAuthorText).execute(queryString);
            mAuthorText.setText("");
            mTitleText.setText("Loading...");
        }else{
            if(queryString.length() == 0){
                mAuthorText.setText("");
                mTitleText.setText("Bạn chưa nhập gì");
            }else{
                mAuthorText.setText("");
                mTitleText.setText("No internet connect available !");
            }
        }
    }

    public class FetchBook extends AsyncTask<String, Void, String>{
//        private WeakReference<TextView> mTitleText;
//        private WeakReference<TextView> mAuthorText;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i = 0;
                String title = null;
                String authors = null;
                while(i<itemsArray.length() && authors == null && title == null){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    try {
                        title = volumeInfo.getString("title");
                        authors = volumeInfo.getString("authors");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }
                if (title != null && authors != null){
                    mTitleText.setText(title);
                    mAuthorText.setText(authors);
                }else{
                    mTitleText.setText("NO RESULTS");
                    mAuthorText.setText("");
                }
            }catch(Exception e){
                mTitleText.setText("NO RESULTS");
                mAuthorText.setText("");
                e.printStackTrace();
            }
        }

        public FetchBook(TextView mTitleText, TextView mAuthorText) {
            mTitleText = mTitleText;
            mAuthorText = mAuthorText;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            return NetworkUtils.getBookInfo(strings[0]);
        }
    }
}