package ve.edu.ucab.modula.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String NOMBRE = "modula.db";
    public static final int VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, NOMBRE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataBaseContract.ChatsTabla.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.MensajesTabla.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.FrasesTabla.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(DataBaseContract.ChatsTabla.SQL_DELETE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.MensajesTabla.SQL_DELETE_TABLE);
        sqLiteDatabase.execSQL(DataBaseContract.FrasesTabla.SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
