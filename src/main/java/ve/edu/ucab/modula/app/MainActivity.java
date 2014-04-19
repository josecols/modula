package ve.edu.ucab.modula.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * MainActivity es la vista principal de la aplicación, la misma contiene el listado de coversaciones
 * y permite acceder a las frases favoritas y a los ajustes de la aplicación.
 */
public class MainActivity extends ActionBarActivity {

    private DataBaseManager mDbManager;
    private ListView vistaChats;
    private TextView vistaMensaje;
    private ArrayList<TextView> chatsSeleccionados;
    private Cursor cursor;
    private ActionMode mActionMode;
    private Menu actionMenu;
    private Context contexto;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.main_context, menu);
            actionMenu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_modificarchat:
                    Intent perfil = new Intent(contexto, PerfilActivity.class);
                    perfil.putExtra("id_chat", vistaChats.getCheckedItemIds()[0]);
                    startActivityForResult(perfil, PerfilActivity.PERFIL_ID);
                    mode.finish();
                    return true;
                case R.id.action_eliminarchat:
                    /* Debido a que eliminar una gran cantidad de registros es una actividad pesada,
                     * hacemos la ejecución en un hilo separado para mantener el rendimiento.
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDbManager.eliminarChats(vistaChats.getCheckedItemIds());
                            cargarChats();
                        }
                    });
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for (TextView view : chatsSeleccionados)
                view.setActivated(false);
            vistaChats.clearChoices();
            vistaChats.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mActionMode = null;
            actionMenu = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_ajustes:
                Intent ajustes = new Intent(this, PreferenciasActivity.class);
                startActivity(ajustes);
                return true;
            case R.id.action_frases:
                Intent intent = new Intent(this, FrasesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_nuevochat:
                Intent chat = new Intent(this, ChatActivity.class);
                chat.putExtra("id_chat", new DataBaseManager(this).crearChat());
                startActivity(chat);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (this.cursor != null)
            this.cursor.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        setContentView(R.layout.activity_main);
        this.vistaChats = (ListView) findViewById(R.id.vista_chats);
        this.vistaMensaje = (TextView) findViewById(R.id.vista_mensaje_main);
        this.mDbManager = new DataBaseManager(this);
        this.chatsSeleccionados = new ArrayList<TextView>();
        this.contexto = this;
        cargarChats();
    }

    public void cargarChats() {
        this.cursor = this.mDbManager.leerChats();
        if (this.cursor.moveToFirst()) {
            ocultarMensaje();
            String[] from = new String[]{DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO};
            int[] to = new int[]{android.R.id.text1};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, this.cursor, from, to, 0);
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int i) {
                    construirChat((TextView) view, cursor);
                    return true;
                }
            });
            this.vistaChats.setAdapter(adapter);
            this.vistaChats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    return fraseLongClick(i, (TextView) view);
                }
            });
            this.vistaChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    fraseClick(l, (TextView) view);
                }
            });
        } else {
            mostrarMensaje();
        }
    }

    private void construirChat(TextView view, Cursor cursor) {
        Drawable drawable = null;
        String foto = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO));
        view.setText(" "+cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO)));
        if (foto.equals(""))
            drawable = getResources().getDrawable(R.drawable.user);
        else
            drawable = Drawable.createFromPath(foto);
        Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), 100, 100, false);
        view.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
    }

    private void mostrarMensaje() {
        this.vistaMensaje.setVisibility(View.VISIBLE);
        this.vistaChats.setVisibility(View.GONE);
    }

    private void ocultarMensaje() {
        this.vistaMensaje.setVisibility(View.GONE);
        this.vistaChats.setVisibility(View.VISIBLE);
    }

    private Boolean fraseLongClick(int position, TextView view) {
        if (this.mActionMode != null)
            return false;
        this.vistaChats.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.mActionMode = startActionMode(this.mActionModeCallback);
        this.vistaChats.setItemChecked(position, true);
        this.chatsSeleccionados.add(view);
        return true;
    }

    private void fraseClick(long id, TextView view) {
        if (this.actionMenu != null) {
            MenuItem item = actionMenu.findItem(R.id.action_modificarchat);
            this.chatsSeleccionados.add(view);
            if (this.vistaChats.getCheckedItemCount() > 1)
                item.setVisible(false);
            else
                item.setVisible(true);
        } else {
            Intent chat = new Intent(this, ChatActivity.class);
            chat.putExtra("id_chat", id);
            startActivity(chat);
        }
    }
}
