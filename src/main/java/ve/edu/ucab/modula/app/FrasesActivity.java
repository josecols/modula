package ve.edu.ucab.modula.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FrasesActivity extends ActionBarActivity {

    private ListView vistaFrases;
    private ArrayList<TextView> frasesSeleccionadas;
    private ActionMode mActionMode;
    private DataBaseManager mDbManager;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private Menu actionMenu;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.frases_context, menu);
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
                case R.id.action_modificarfrase:
                    crearDialogo(vistaFrases.getCheckedItemIds()[0], mDbManager.getFrase(vistaFrases.getCheckedItemIds()[0]));
                    mode.finish();
                    return true;
                case R.id.action_eliminarfrase:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDbManager.eliminarFrases(vistaFrases.getCheckedItemIds());
                            adapter.changeCursor(mDbManager.leerFrases());
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
            for (TextView view : frasesSeleccionadas)
                view.setActivated(false);
            vistaFrases.clearChoices();
            vistaFrases.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mActionMode = null;
            actionMenu = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frases);
        this.vistaFrases = (ListView) findViewById(R.id.vista_frases);
        this.frasesSeleccionadas = new ArrayList<TextView>();
        this.mDbManager = new DataBaseManager(getApplicationContext());
        cargarLista();
    }

    @Override
    protected void onDestroy() {
        this.cursor.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_nuevafrase) {
            crearDialogo(-1, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarLista() {
        this.cursor = this.mDbManager.leerFrases();
        String[] from = new String[]{DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        int[] to = new int[]{android.R.id.text1};
        this.adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, cursor, from, to, 0);
        this.vistaFrases.setAdapter(this.adapter);

        this.vistaFrases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vistaFrases.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                if (mActionMode != null)
                    return false;
                mActionMode = startActionMode(mActionModeCallback);
                vistaFrases.setItemChecked(position, true);
                frasesSeleccionadas.add((TextView) view);
                return true;
            }
        });

        this.vistaFrases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionMenu != null) {
                    MenuItem item = actionMenu.findItem(R.id.action_modificarfrase);
                    frasesSeleccionadas.add((TextView) view);
                    if (vistaFrases.getCheckedItemCount() > 1)
                        item.setVisible(false);
                    else
                        item.setVisible(true);
                }
            }
        });
    }

    private void crearDialogo(final long id, String frase) {
        final EditText input = new EditText(this);
        if (frase != null)
            input.setText(frase);
        new AlertDialog.Builder(this)
                .setTitle("Ingresa la frase")
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (id == -1)
                            mDbManager.insertarFrase(input.getText().toString());
                        else
                            mDbManager.actualizarFrase(id, input.getText().toString());
                        adapter.changeCursor(mDbManager.leerFrases());
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }
}