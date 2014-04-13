package ve.edu.ucab.modula.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DataBaseHelper controla la creación y la actualización de la base de datos,
 * además permite a la clase <DataBaseManager> realizar operaciones de escritura y lectura sobre
 * la base de datos.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Nombre del archivo SQLite.
     */
    public static final String NOMBRE = "modula.db";
    /**
     * Versión del esquema de la base de datos.
     */
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
