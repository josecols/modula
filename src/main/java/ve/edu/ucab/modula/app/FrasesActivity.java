package ve.edu.ucab.modula.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import java.util.concurrent.Callable;

/**
 * FrasesActivity es la vista que permite gestionar las frases favoritas del usuario,
 * ya se para su rápida reproducción o para ser introducidas en una conversación.
 */
public class FrasesActivity extends ActionBarActivity {

    /**
     * Lista de frases para la GUI, definido en el XML.
     */
    private ListView vistaFrases;
    /**
     * Arreglo de cada una de las filas, de la lista de frases, resaltadas por el usuario.
     */
    private ArrayList<TextView> frasesSeleccionadas;
    /**
     * Bandera para determinar el modo contextual de la vista.
     */
    private ActionMode mActionMode;
    /**
     * Interfaz para realizar operaciones sobre la base de datos.
     */
    private DataBaseManager mDbManager;
    /**
     * Cursor para desplazarse en la lista de todas las frases de la base de datos.
     */
    private Cursor cursor;
    /**
     * Adaptador para alimentar la <vistaFrases> con el contenido de <cursor>.
     */
    private SimpleCursorAdapter adapter;
    /**
     * Referencia al manejador de pronunciación de texto.
     */
    private TextoAVoz pronunciador;
    /**
     * Encapsula los procedimientos a ejecutar dependiendo del estado del servicio de pronunciación.
     */
    private TTSCallback callback;
    /**
     * Menú contextual que es activado cuando el usuario realiza un LongClick sobre <vistaFrases>.
     */
    private Menu actionMenu;
    /**
     * Agrupa las operaciones que se pueden realizar con el menú contextual.
     */
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
                    /* Debido a que eliminar una gran cantidad de registros es una actividad pesada,
                     * hacemos la ejecución en un hilo separado para mantener el rendimiento.
                     */
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
        this.callback = new TTSCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                cargarLista();
                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("Modula", "Hablando");
                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("Modula", "Texto pronunciado");

                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("Modula", "Ocurrió un error al pronunciar el texto");
                return null;
            }
        }
        );
        this.vistaFrases = (ListView) findViewById(R.id.vista_frases);
        this.frasesSeleccionadas = new ArrayList<TextView>();
        this.mDbManager = new DataBaseManager(getApplicationContext());
        this.pronunciador = new TextoAVoz(this, this.callback);
    }

    @Override
    protected void onDestroy() {
        if (this.cursor != null)
            this.cursor.close();
        this.pronunciador.finalizar();
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

    /**
     * Este método se encarga de leer los registros de la base de datos y llenar la lista de
     * frases <vistaFrase> con la información leída. Además, define el control de selección de los
     * elementos de la lista.
     */
    public void cargarLista() {
        this.cursor = this.mDbManager.leerFrases();
        String[] from = new String[]{DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
        int[] to = new int[]{android.R.id.text1};
        this.adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, cursor, from, to, 0);
        this.vistaFrases.setAdapter(this.adapter);
        // Cuando se realiza un LongClick se abre el menú contextual.
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
                } else {
                    pronunciador.pronunciar(((TextView) view).getText().toString());
                }
            }
        });
    }

    /**
     * Genera un diálogo con un EditText con el fin de solicitarle al usuario el texto de la frase.
     *
     * @param id    ID de la frase a modificar, se espera -1 si es una inserción.
     * @param frase Texto de la frase a modificar, se espera null si es una inserción.
     */
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