package ve.edu.ucab.modula.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Permite el Manejo y gestion del perfil (Foto y titulo)
 * de un chat determinado
 */
public class PerfilActivity extends ActionBarActivity {
    public static final int PERFIL_ID = 3;

    /**
     * View empleado para mostrar la foto capturada
     */
    private ImageView foto;

    /**
     * boton que permite el uso de la foto por defecto de un usuario
     */
    private ImageButton eliminar;

    /**
     * boton que permite el uso de la camara para la captura de fotos
     */
    private ImageButton camara;

    /**
     * barra de Texto para la introduccion del titulo del chat
     */
    private EditText titulo;

    /**
     * boton empleado para guardar los cambios
     */
    private Button aceptar;

    /**
     * variable auxiliar para el manejo de la BD
     */
    private DataBaseManager bd;

    /**
     * Almacena el BMP de la ultima foto tomada
     */
    private Bitmap img;

    /**
     * Directorio privado para almacenar las fotos de la app
     */
    private File dir;

    /**
     * id del chat (en la BD) que se esta manipulando
     */
    private long idChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        dir = getDir("Fotos", this.MODE_PRIVATE);
        if (!dir.exists())
            dir.mkdir();
        foto = (ImageView) findViewById(R.id.foto);
        eliminar = (ImageButton) findViewById(R.id.eliminar);
        camara = (ImageButton) findViewById(R.id.camara);
        titulo = (EditText) findViewById(R.id.titulo);
        aceptar = (Button) findViewById(R.id.aceptar);
        bd = new DataBaseManager(getApplicationContext());
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img = null;
                foto.setImageResource(R.drawable.user);
            }
        });
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toma = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(toma, 0);
            }
        });
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = titulo.getText().toString().trim();
                if (!str.equals("")) {
                    if (img != null) {
                        File file = new File(dir, String.valueOf(idChat) + ".png");
                        FileOutputStream out;
                        if (file.exists())
                            file.delete();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out = new FileOutputStream(file);
                            img.compress(Bitmap.CompressFormat.PNG, 0, out);
                            out.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bd.actualizarChat(idChat, str, file.getPath());
                    } else
                        bd.actualizarChat(idChat, str, "");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Agregue un título", Toast.LENGTH_SHORT).show();
                }
            }
        });
        idChat = getIntent().getExtras().getLong("id_chat");
        cargarDatosChat();
    }

    public void cargarDatosChat() {
        Cursor cur = bd.leerChat(idChat);
        String aux_foto = "";
        String aux_titulo = "";
        if (cur.moveToFirst()) {
            aux_foto = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_FOTO));
            aux_titulo = cur.getString(cur.getColumnIndex(DataBaseContract.ChatsTabla.COLUMN_NAME_TITULO));
        }
        if (aux_foto.equals("")) {
            foto.setImageResource(R.drawable.user);
            img = null;
        } else {
            foto.setImageDrawable(Drawable.createFromPath(aux_foto));
            img = BitmapFactory.decodeFile(aux_foto);
        }
        titulo.setText(aux_titulo);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            img = (Bitmap) (data.getExtras().get("data"));
            foto.setImageBitmap(img);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        finish();
        return true;
    }
}
