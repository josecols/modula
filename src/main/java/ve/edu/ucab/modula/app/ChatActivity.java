package ve.edu.ucab.modula.app;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.widget.Toast;


public class ChatActivity extends ActionBarActivity {
    private ListView lista;
    private ImageButton boton_hablar;
    private ImageButton boton_escuchar;
    private ArrayList<Mensaje> mensajes;
    private EditText texto;
    private Adaptador adaptador;
    private TextoAVoz locutor;
    private VozATexto traductor;
    private DataBaseManager bd;
    private long id_chat;
    private int ult_mensaje;

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
                                        lista.setSelection(mensajes.size()-1);
                                    }
                                });
                                return null;
                            }
                        };
        locutor = new TextoAVoz(this, new TTSCallback(null, null, call, null));
        traductor = new VozATexto(this);
        boton_hablar = (ImageButton) findViewById(R.id.hablar);
        boton_escuchar = (ImageButton) findViewById(R.id.escuchar);
        texto = (EditText) findViewById(R.id.edit);
        mensajes = new ArrayList<Mensaje>();
        adaptador = new Adaptador(this, mensajes);
        bd = new DataBaseManager(getApplicationContext());
        id_chat = getIntent().getExtras().getLong("id_chat");
        lista = (ListView) findViewById(R.id.lista);
        lista.setAdapter(adaptador);
        boton_hablar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = texto.getText().toString().trim();
                texto.setText("");
                if (str.length() > 0) {
                    Mensaje msj = new Mensaje(str, true);
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat, msj);
                    locutor.pronunciar(str);
                }
            }
        });
        boton_escuchar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                traductor.traducir();
            }
        });
        cargarMensajes();
    }


    private void cargarMensajes() {
        Cursor cur = bd.leerMensajes(id_chat);
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
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat, msj);
                }
                break;
            }
            case FrasesActivity.FRASES_ID: {
                if (resultCode == RESULT_OK && null != data) {
                    String str = data.getStringExtra("frase");
                    Mensaje msj = new Mensaje(str, true);
                    addNewMessage(msj);
                    bd.insertarMensaje(id_chat, msj);
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

    public void cargarDatosChat() {
        Cursor cur = bd.leerChat(id_chat);
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
                perfil.putExtra("id_chat", id_chat);
                startActivityForResult(perfil, PerfilActivity.PERFIL_ID);
                return true;
        }
    }

    @Override
    protected void onDestroy() {
        if (locutor != null) {
            locutor.finalizar();
        }
        super.onDestroy();
    }

    private void addNewMessage(Mensaje m) {
        mensajes.add(m);
        adaptador.notifyDataSetChanged();
        lista.setSelection(mensajes.size() - 1);
    }
}
