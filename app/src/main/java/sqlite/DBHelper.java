package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fixer.model.TokenModel;

import java.util.ArrayList;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {

    static final String TABLE_TOKEN = "token";
    static final String DB_NAME = "fixerdata";
    static final String CUSTOMER_TOKEN = "token";

    private static final int MAX_LENGTH = 32;
    StringBuilder randomStringBuilder;
    private String token_get = "";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_TOKEN +  "(token text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TOKEN);
        onCreate(db);
    }

    public String getRandom_token() {
        Random generator = new Random();
        randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public void getAll_token() {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TOKEN, null);
        cursor.moveToFirst();

        TokenModel model = new TokenModel();
        model.setToken(cursor.getString(0));

        token_get = model.getToken();
        db.close();
    }

    public void insert_token () {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CUSTOMER_TOKEN,getRandom_token());

        db.insert(TABLE_TOKEN, null, values);
        db.close();
    }

    public void delete_token () {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_TOKEN);
        db.close();
    }
}
