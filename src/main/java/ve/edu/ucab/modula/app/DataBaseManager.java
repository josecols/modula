package ve.edu.ucab.modula.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * DataBaseManager es la interfaz que actúa entre la vista y la base de datos para realizar
 * operaciones de gestión sobre los elementos de la aplicación.
 */
public final class DataBaseManager {

    /**
     * Helper de la base de datos, permite realizar operaciones de lectura y escritura sobre la misma.
     */
    private DataBaseHelper mDbHelper;
    /**
     * Actúa como interfaz hacia el manejador de la base de datos, permite ejecutar SQL.
     */
    private SQLiteDatabase sqLiteDatabase;

    public DataBaseManager(Context context) {
        this.mDbHelper = new DataBaseHelper(context);
    }

    /**
     * Retorna el texto de la frase cuyo ID es el proporcionado.
     *
     * @param id ID de la frase en la base de datos.
     * @return Texto de la frase.
     */
    public String leerFrase(long id) {
        String[] columnas = {DataBaseContract.FrasesTabla._ID, DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        Cursor cursor = this.sqLiteDatabase.query(DataBaseContract.FrasesTabla.TABLE_NAME, columnas,
                DataBaseContract.FrasesTabla._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO));
        }
        return null;
    }

    /**
     * Consulta todas las frases registradas en la base de datos.
     *
     * @return Cursor con todos los registros.
     */
    public Cursor leerFrases() {
        String[] columnas = {DataBaseContract.FrasesTabla._ID, DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        return this.sqLiteDatabase.query(DataBaseContract.FrasesTabla.TABLE_NAME, columnas, null, null, null, null, null);
    }

    /**
     * Inserta una nueva frase en la base de datos.
     *
     * @param titulo Texto de la nueva frase.
     */
    public void insertarFrase(String titulo) {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO, titulo);
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        this.sqLiteDatabase.insert(DataBaseContract.FrasesTabla.TABLE_NAME, null, values);
    }

    /**
     * Actualiza el texto de la frase dada.
     *
     * @param id     ID de la frase en la base de datos.
     * @param titulo Nuevo texto de la frase.
     */
    public void actualizarFrase(long id, String titulo) {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO, titulo);
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        this.sqLiteDatabase.update(DataBaseContract.FrasesTabla.TABLE_NAME, values,
                DataBaseContract.FrasesTabla._ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Elimina un conjunto de frases de la base de datos.
     *
     * @param ids Arreglo de ID de las frases que se van a eliminar.
     */
    public void eliminarFrases(long ids[]) {
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        for (long id : ids)
            this.sqLiteDatabase.delete(DataBaseContract.FrasesTabla.TABLE_NAME,
                    DataBaseContract.FrasesTabla._ID + "=" + String.valueOf(id), null);
    }

    public Cursor leerMensajes(long id_chat) {
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        String[] columnas = {DataBaseContract.MensajesTabla.COLUMN_NAME_TEXTO, DataBaseContract.MensajesTabla.COLUMN_NAME_ENVIADO};
        return this.sqLiteDatabase.query(DataBaseContract.MensajesTabla.TABLE_NAME, columnas, DataBaseContract.MensajesTabla.COLUMN_NAME_CHAT_ID + "=" + String.valueOf(id_chat), null, null, null, null);
    }

    public void insertarMensaje(long id_chat, Mensaje msj) {
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.MensajesTabla.COLUMN_NAME_CHAT_ID, id_chat);
        values.put(DataBaseContract.MensajesTabla.COLUMN_NAME_TEXTO, msj.getTexto());
        values.put(DataBaseContract.MensajesTabla.COLUMN_NAME_ENVIADO, msj.enviado());
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        this.sqLiteDatabase.insert(DataBaseContract.MensajesTabla.TABLE_NAME, null, values);
    }

    public Cursor leerChats() {
        String[] columnas = {
                DataBaseContract.ChatsTabla._ID,
                DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO,
                DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO
        };
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        return this.sqLiteDatabase.query(DataBaseContract.ChatsTabla.TABLE_NAME, columnas, null, null, null, null, null);
    }

    public Cursor leerChat(long id) {
        this.sqLiteDatabase = this.mDbHelper.getReadableDatabase();
        String[] columnas = {DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO, DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO};
        return this.sqLiteDatabase.query(DataBaseContract.ChatsTabla.TABLE_NAME, columnas, DataBaseContract.ChatsTabla._ID + "=" + String.valueOf(id), null, null, null, null);
    }

    public void actualizarChat(long id, String titulo, String foto) {
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO, titulo);
        values.put(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO, foto);
        this.sqLiteDatabase.update(DataBaseContract.ChatsTabla.TABLE_NAME, values, DataBaseContract.ChatsTabla._ID + "=" + String.valueOf(id), null);
    }

    public long crearChat() {
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO, df.format(cal.getTime()));
        values.put(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO, "");
        return this.sqLiteDatabase.insert(DataBaseContract.ChatsTabla.TABLE_NAME, null, values);
    }

    public void eliminarChats(long ids[]) {
        this.sqLiteDatabase = this.mDbHelper.getWritableDatabase();
        for (long id : ids)
            this.sqLiteDatabase.delete(DataBaseContract.ChatsTabla.TABLE_NAME,
                    DataBaseContract.ChatsTabla._ID + "=" + String.valueOf(id), null);
    }
}
