package ve.edu.ucab.modula.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class DataBaseManager {

    private DataBaseHelper mDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DataBaseManager(Context context) {
        this.mDbHelper = new DataBaseHelper(context);
    }

    public String getFrase(long id) {
        String[] columnas = {DataBaseContract.FrasesTabla._ID, DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        Cursor cursor = this.sqLiteDatabase.query(DataBaseContract.FrasesTabla.TABLE_NAME, columnas,
                DataBaseContract.FrasesTabla._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO));
        }
        return null;
    }

    public Cursor leerFrases() {
        String[] columnas = {DataBaseContract.FrasesTabla._ID, DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        return this.sqLiteDatabase.query(DataBaseContract.FrasesTabla.TABLE_NAME, columnas, null, null, null, null, null);
    }

    public void insertarFrase(String titulo) {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO, titulo);
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        this.sqLiteDatabase.insert(DataBaseContract.FrasesTabla.TABLE_NAME, null, values);
    }

    public void actualizarFrase(long id, String titulo) {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO, titulo);
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        this.sqLiteDatabase.update(DataBaseContract.FrasesTabla.TABLE_NAME, values,
                DataBaseContract.FrasesTabla._ID + "=?", new String[]{String.valueOf(id)});
    }

    public void eliminarFrases(long ids[]) {
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        for (long id : ids)
            this.sqLiteDatabase.delete(DataBaseContract.FrasesTabla.TABLE_NAME,
                    DataBaseContract.FrasesTabla._ID + "=" + String.valueOf(id), null);
    }
}
