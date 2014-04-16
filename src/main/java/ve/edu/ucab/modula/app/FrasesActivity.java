package ve.edu.ucab.modula.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

    public static final int FRASES_ID = 2;
    /**
     * Lista de frases para la GUI, definido en el XML.
     */
    private ListView vistaFrases;
    /**
     * Permite mostrar mensajes al usuario.
     */
    private TextView vistaMensaje;
    /**
     * Indica si existe conexión con el servicio TTS.
     */
    private Boolean servicioConectado = false;
    /**
     * Arreglo de cada una de las filas, de la lista de frases, resaltadas por el usuario.
     */
    private ArrayList<TextView> frasesSeleccionadas;
    /**
     * Instancia de la frase en ejecución.
     */
    private TextView fraseEnEjecucion;
    /**
     * Interfaz para realizar operaciones sobre la base de datos.
     */
    private DataBaseManager mDbManager;
    /**
     * Cursor para desplazarse en la lista de todas las frases de la base de datos.
     */
    private Cursor cursor;
    /**
     * Referencia al manejador de pronunciación de texto.
     */
    private TextoAVoz pronunciador;
    /**
     * Identifica que clase realizoó la llamada de <FrasesActivity>.
     */
    private Class parentActivityClass;
    /**
     * Bandera para determinar el modo contextual de la vista.
     */
    private ActionMode mActionMode;
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
                    crearDialogo(vistaFrases.getCheckedItemIds()[0], mDbManager.leerFrase(vistaFrases.getCheckedItemIds()[0]));
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
                            cargarLista();
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
        TTSCallback callback = new TTSCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                vistaFrases.setVisibility(View.VISIBLE);
                servicioConectado = true;
                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fraseEnEjecucion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_volume_on, 0);
                    }
                });
                return null;
            }
        }, new Callable() {
            @Override
            public Object call() throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fraseEnEjecucion.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                });
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
        this.vistaMensaje = (TextView) findViewById(R.id.vista_mensaje_frases);
        this.frasesSeleccionadas = new ArrayList<TextView>();
        this.mDbManager = new DataBaseManager(getApplicationContext());
        this.pronunciador = new TextoAVoz(this, callback);
        this.parentActivityClass = getIntent().getExtras() == null ? MainActivity.class : ChatActivity.class;
        cargarLista();
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
        switch (id) {
        case R.id.action_nuevafrase:
            crearDialogo(-1, null);
            return true;
        default:
            setResult(RESULT_OK);
            finish();
            return true;
        }
    }

    /**
     * Este método se encarga de leer los registros de la base de datos y llenar la lista de
     * frases <vistaFrase> con la información leída. Además, define el control de selección de los
     * elementos de la lista.
     */
    public void cargarLista() {
        this.cursor = this.mDbManager.leerFrases();
        if (cursor.moveToFirst()) {
            ocultarMensaje();
            String[] from = new String[]{DataBaseContract.FrasesTabla.COLUMN_NAME_TITULO};
            int[] to = new int[]{android.R.id.text1};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, cursor, from, to, 0);
            this.vistaFrases.setAdapter(adapter);
            // Cuando se realiza un LongClick se abre el menú contextual.
            this.vistaFrases.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return fraseLongClick(position, (TextView) view);
                }
            });
            this.vistaFrases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    fraseClick((TextView) view);
                }
            });
        } else {
            mostrarMensaje();
        }
    }

    private void mostrarMensaje() {
        this.vistaMensaje.setVisibility(View.VISIBLE);
        this.vistaFrases.setVisibility(View.GONE);
    }

    private void ocultarMensaje() {
        this.vistaMensaje.setVisibility(View.GONE);
        if (servicioConectado)
            this.vistaFrases.setVisibility(View.VISIBLE);
    }

    private Boolean fraseLongClick(int position, TextView view) {
        if (mActionMode != null)
            return false;
        vistaFrases.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.mActionMode = startActionMode(mActionModeCallback);
        this.vistaFrases.setItemChecked(position, true);
        this.frasesSeleccionadas.add(view);
        return true;
    }

    private void fraseClick(TextView view) {
        if (this.actionMenu != null) {
            MenuItem item = actionMenu.findItem(R.id.action_modificarfrase);
            this.frasesSeleccionadas.add(view);
            if (this.vistaFrases.getCheckedItemCount() > 1)
                item.setVisible(false);
            else
                item.setVisible(true);
        } else {
            if (this.parentActivityClass == ChatActivity.class) {
                Intent resultado = new Intent();
                resultado.putExtra("frase", view.getText().toString());
                setResult(RESULT_OK, resultado);
                finish();
            } else if (this.parentActivityClass == MainActivity.class) {
                if (this.fraseEnEjecucion != null)
                    this.fraseEnEjecucion.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.fraseEnEjecucion = view;
                pronunciador.pronunciar(this.fraseEnEjecucion.getText().toString());
            }
        }
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
                        cargarLista();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }
}