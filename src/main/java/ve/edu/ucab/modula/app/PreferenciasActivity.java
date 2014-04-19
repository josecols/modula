package ve.edu.ucab.modula.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;


public class PreferenciasActivity extends ActionBarActivity {
    private Spinner fuente;
    private Spinner tamano;
    private Switch enviar;
    private SharedPreferences preferencias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        fuente = (Spinner) findViewById(R.id.fuente);
        tamano = (Spinner) findViewById(R.id.tamano);
        preferencias = getSharedPreferences("Preferencias",MODE_PRIVATE);
        fuente.setSelection(preferencias.getInt("fuente",0));
        tamano.setSelection(preferencias.getInt("tamano",0));
        fuente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               preferencias.edit().putInt("fuente",i).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        tamano.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferencias.edit().putInt("tamano",i).commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(RESULT_OK);
        finish();
        return true;
    }

}
