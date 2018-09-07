package com.example.cas.firebaseauthentication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final Class[] CLASSES=new Class[]{
            GoogleSignIn.class,
            PhoneAuthActivity.class
    };
    private static final int[] DESCRIPTION_IDS=new int[]{
            R.string.desc_google_sign_in,
            R.string.desc_phone

    };

    //onCreate START
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        //Setup ListView and Adapter
        ListView listView=findViewById(R.id.list_view);

        MyArrayAdapter adapter=new MyArrayAdapter(this,android.R.layout.simple_list_item_2,CLASSES);
        adapter.setDescriptionIDs(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }//onCreate END

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked=CLASSES[position];
        startActivity(new Intent(this,clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class>{

        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIDs;

        //constructor
        public MyArrayAdapter(@NonNull Context mContext, int resource, @NonNull Class[] objects) {
            super(mContext, resource, objects);
            this.mContext = mContext;
            mClasses = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view=convertView;
            if(convertView==null){
                LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view=inflater.inflate(android.R.layout.simple_list_item_2,null);
            }
            ((TextView)view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            ((TextView)view.findViewById(android.R.id.text2)).setText(mDescriptionIDs[position]);

            return view;
        }

        public void setDescriptionIDs(int[] descriptionIDs){
            mDescriptionIDs=descriptionIDs;
        }
    }
}
