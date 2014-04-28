package ve.edu.ucab.modula.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.ListView;
import android.database.Cursor;

/**
 * Actividad que sirve de interfaz para el manejo del chat o bien de una conversacion
 * particular
 */
public class ChatActivity extends ActionBarActivity {

    /**
     * ListView que maneja los mensajes involucrados en el chat
     */
    private ListView lista;

    /**
     * Boton encargado de tomar el texto introducido por el usuario y traducirlo a voz
     */
    private ImageButton botonHablar;

    /**
     * Boton encargado de iniciar el proceso de "escucha" de la aplicacion
     */
    private ImageButton botonEscuchar;

    /**
     * Arreglo de Mensajes manejados en el Chat
     */
    private ArrayList<Mensaje> mensajes;

    /**
     * Barra de Texto que permite la introducion del texto a modular
     */
    private EditText texto;

    /**
     * Instancia de la clase Adaptador que sirve para el manejo de la interfaz del ListView Lista
     */
    private Adaptador adaptador;

    /**
     * Permite la conversion de texto a voz
     */
    private TextoAVoz locutor;

    /**
     * Permite la conversion de Voz a Texto
     */
    private VozATexto traductor;

    /**
     * Manejador de la informacion presente en la BD
     */
    private DataBaseManager bd;

    /**
     * id (en la BD) del chat gestionado
     */
    private long idChat;

    /**
     * ultimo mensaje
     */
    private int ult_mensaje;

    /**
     * Variable auxliar para manejar los resultados de una consulta
     */
    private Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ult_mensaje = -1;
        Callable call = new Callable() {
            @Override
            public Object call() throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mensajes.get(++ult_mensaje).setListo(true);
                        adaptador.notifyDataSetChanged();
                        lista.setSelection(mensajes.size() - 1);
                    }
                });
                return null;
            }
        };
        locutor = new TextoAVoz(this, new TTSCallback(null, null, call, null));
        traductor = new VozATexto(this);
        botonHablar = (ImageButton) findViewById(R.id.hablar);
        botonEscuchar = (ImageButton) findViewById(R.id.escuchar);
        texto = (EditText) findViewById(R.id.edit);
        mensajes = new ArrayList<Mensaje>();
        adaptador = new Adaptador(this, mensajes);
        bd = new DataBaseManager(getApplicationContext());
        idChat = getIntent().getExtras().getLong("id_chat");
        lista = (ListView) findViewById(R.id.lista);
        lista.setAdapter(adaptador);
        botonHablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = texto.getText().toString().trim();
                texto.setText("");
                if (str.length() > 0) {
                    Mensaje msj = new Mensaje(str, true);
                    addNewMessage(msj);
                    bd.insertarMensaje(idChat, msj);
                    locutor.pronunciar(str);
                }
            }
        });
        botonEscuchar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locutor.estaHablando())
                    locutor.parar();
                traductor.traducir();
            }
        });
        cargarMensajes();
    }


    /**
     * Carga los Mensajes almacenados en la Base de Datos de un chat Correspondiente
     */
    private void cargarMensajes() {
        cur = bd.leerMensajes(idChat);
        if (cur.moveToFirst()) {
            int col_texto = cur.getColumnIndex(DataBaseContract.MensajesTabla.COLUMN_NAME_TEXTO);
            int col_enviado = cur.getColumnIndex(DataBaseContract.MensajesTabla.COLUMN_NAME_ENVIADO);
            do {
                Mensaje msj = new Mensaje(cur.getString(col_texto), cur.getString(col_enviado).equals("1"));
                msj.setListo(true);
                mensajes.add(msj);
                ult_mensaje++;
            } while (cur.moveToNext());
            adaptador.notifyDataSetChanged();
            lista.setSelection(mensajes.size() - 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VozATexto.CONVERSION_ID: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> texto = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Mensaje msj = new Mensaje(texto.get(0), false);
                    ult_mensaje++;
                    addNewMessage(msj);
                    bd.insertarMensaje(idChat, msj);
                }
                break;
            }
            case FrasesActivity.FRASES_ID: {
                if (resultCode == RESULT_OK && null != data) {
                    String str = data.getStringExtra("frase");
                    Mensaje msj = new Mensaje(str, true);
                    addNewMessage(msj);
                    bd.insertarMensaje(idChat, msj);
                    locutor.pronunciar(str);
                }
                break;
            }
            case PerfilActivity.PERFIL_ID: {
                if (resultCode == RESULT_OK) {
                    cargarDatosChat();
                }
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        cargarDatosChat();
        return true;
    }

    /**
     * Carga la informacion del chat (Titulo y Foto) y los presenta en la interfaz
     */
    public void cargarDatosChat() {
        cur = bd.leerChat(idChat);
        String foto = "";
        String titulo = "";
        if (cur.moveToFirst()) {
            foto = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO));
            titulo = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO));
        }
        if (foto.equals(""))
            getActionBar().setLogo(R.drawable.user);
        else {
            getActionBar().setLogo(Drawable.createFromPath(foto));
        }
        getActionBar().setTitle(titulo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_frases:
                Intent frases = new Intent(this, FrasesActivity.class);
                frases.putExtra("chat", true);
                startActivityForResult(frases, FrasesActivity.FRASES_ID);
                return true;
            default:
                Intent perfil = new Intent(this, PerfilActivity.class);
                perfil.putExtra("id_chat", idChat);
                startActivityForResult(perfil, PerfilActivity.PERFIL_ID);
                return true;
        }
    }

    @Override
    protected void onPause() {
        if (isFinishing() && mensajes.size() < 1) bd.eliminarChats(new long[]{idChat});
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (locutor != null) locutor.finalizar();
        if (cur != null) cur.close();
        super.onDestroy();
    }

    /**
     * Añade un nuevo mensaje al arreglo y avisa al adaptador que ha ocurrido un cambio
     * para que refresque la interfaz
     * @param m
     *      mensaje a añadir en el arreglo
     */
    private void addNewMessage(Mensaje m) {
        mensajes.add(m);
        adaptador.notifyDataSetChanged();
        lista.setSelection(mensajes.size() - 1);
    }
}
