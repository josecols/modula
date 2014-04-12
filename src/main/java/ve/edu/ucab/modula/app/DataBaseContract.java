package ve.edu.ucab.modula.app;

import android.provider.BaseColumns;

public final class DataBaseContract {
    public DataBaseContract() {
    }

    public static abstract class ChatsTabla implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String COLUMN_NAME_FOTO = "foto";
        public static final String COLUMN_NAME_TITULO = "titulo";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_FOTO + " TEXT," +
                COLUMN_NAME_TITULO + " TEXT" +
                " );";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class MensajesTabla implements BaseColumns {
        public static final String TABLE_NAME = "mensajes";
        public static final String COLUMN_NAME_CHAT_ID = "id_chat";
        public static final String COLUMN_NAME_TEXTO = "texto";
        public static final String COLUMN_NAME_ENVIADO = "enviado";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_CHAT_ID + " TEXT," +
                COLUMN_NAME_TEXTO + " INTEGER," +
                COLUMN_NAME_ENVIADO + " BOOLEAN," + "" +
                "FOREIGN KEY(" + COLUMN_NAME_CHAT_ID + ") REFERENCES " + ChatsTabla.TABLE_NAME + "(" + ChatsTabla._ID + ")" +
                " );";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class FrasesTabla implements BaseColumns {
        public static final String TABLE_NAME = "frases";
        public static final String COLUMN_NAME_TITULO = "titulo";
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TITULO + " TEXT" +
                " );";
        public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
