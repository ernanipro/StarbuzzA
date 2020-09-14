package com.example.starbuzza;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.CheckBox;
import android.content.ContentValues;
import android.os.AsyncTask;

public class DrinkActivity extends Activity {

    public static final String EXTRA_DRINKNO = "drinkNo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        //Get the drink from the intent
        int drinkNo = (Integer)getIntent().getExtras().get(EXTRA_DRINKNO);

        // Cria um cursor
        try {SQLiteOpenHelper starbuzzDatabaseHelper=new StarbuzzDatabaseHelper(this);
            SQLiteDatabase db=starbuzzDatabaseHelper.getWritableDatabase();
            Cursor cursor=db.query("DRINK",
                    new String[]{"NAME","DESCRIPTION","IMAGE_RESOURCE_ID","FAVORITE"},
                    "_id=?",
                    new String[]{Integer.toString(drinkNo)},
                    null,null,null);

        // Vai para o primeiro registro no cursor
            if (cursor.moveToFirst()){
                // Obtém os detalhes da bebida do cursor
                String nameText=cursor.getString(0);
                String descriptionText=cursor.getString(1);
                int photoId=cursor.getInt(2);
                boolean isFavorite=(cursor.getInt(3)==1);

                //Populate the drink name
                TextView name = (TextView)findViewById(R.id.name);
                name.setText(nameText);

                //Populate the drink description
                TextView description = (TextView)findViewById(R.id.description);
                description.setText(descriptionText);

                //Populate the drink image
                ImageView photo = (ImageView)findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);

                //Atualiza o banco de dados quando a caixa de seleção é clicada
                CheckBox favorite=(CheckBox)findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            };
            cursor.close();
            db.close();
        } catch (SQLiteException e){
            Toast toast=Toast.makeText(this,"Database unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Atualiza o banco de dados quando a caixa de seleção é clicada
    public void onFavoriteClicked(View view){
        int drinkNo=(Integer)getIntent().getExtras().get("drinkNo");

        //**Banco de dados rodando na thread principal**
        //CheckBox favorite=(CheckBox)findViewById(R.id.favorite);
        //ContentValues drinkValues=new ContentValues();
        //drinkValues.put("FAVORITE",favorite.isChecked());
        //SQLiteOpenHelper starbuzzDatabaseHelper=
          //      new StarbuzzDatabaseHelper(DrinkActivity.this);
        //try {
          //  SQLiteDatabase db=starbuzzDatabaseHelper.getWritableDatabase();
            //db.update("DRINK", drinkValues,
              //      "_id=?",new String[]{Integer.toString(drinkNo)});
            //db.close();

        //} catch (SQLiteException e){
          //  Toast toast=Toast.makeText(this,"Database unavailable",Toast.LENGTH_SHORT);
            //toast.show();
        //}
        new UpdateDrinkTask().execute(drinkNo);
    }
     // Classe interna para atualizar a bebida
    private class UpdateDrinkTask extends AsyncTask<Integer,Void,Boolean>{
        ContentValues drinkValues;
        protected void onPreExecute(){
            CheckBox favorite =(CheckBox)findViewById(R.id.favorite);
            drinkValues=new ContentValues();
            drinkValues.put("FAVORITE", favorite.isChecked());
        }

         @Override
         protected Boolean doInBackground(Integer... drinks) {
            int drinkNo=drinks[0];
            SQLiteOpenHelper starbuzzDatabaseHelper=
                    new StarbuzzDatabaseHelper(DrinkActivity.this);
            try {
                SQLiteDatabase db=starbuzzDatabaseHelper.getWritableDatabase();
                db.update("DRINK",drinkValues,"_id=?",new String[]{Integer.toString(drinkNo)});
                db.close();
                return true;
            }catch (SQLiteException e){
                return false;
         }
     }
     protected void onPostExecute(Boolean success){
        if (!success){
            Toast toast=Toast.makeText(DrinkActivity.this,
                    "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        }
     }
}

