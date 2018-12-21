package carrobemfacil.wordpress.com.velocimetrodigital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private TextView velocidadeTextView;
    private TextView distanciaParcialTextView;
    private TextView odometroTextView;

    private Location locationInicial = null;
    private float distanciaParcialMeters;
    private float odometroMeters;
    private float odometroEditavel;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    private Integer abastecerPrecoCentavos;
    private Integer abastecerValorCentavos;

    private float abastecerLitrosCentavos;
    
    private ControladorCombustivel controladorCombustivel;

    private float ultimoOdometroAutonomia;

    private boolean switchesInitialized = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        velocidadeTextView = (TextView) findViewById(R.id.velocidade);
        distanciaParcialTextView = (TextView) findViewById(R.id.distanciaParcial);
        odometroTextView = (TextView) findViewById(R.id.odometro);

        ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // Carrega Odometro
        sharedPreferences = getSharedPreferences("VelocimetroDigital", 0);
        odometroMeters = sharedPreferences.getFloat("odometroMeters", 0f);
        editor = sharedPreferences.edit();
        atualizarOdometro();
        ultimoOdometroAutonomia = odometroMeters;

        //Carrega Mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.g_map);
        mapFragment.getMapAsync(this);

        // Carrega controle de combustivel

        controladorCombustivel = new ControladorCombustivel(
                sharedPreferences.getFloat("kmLitroUltimoTanque", 5f),
                sharedPreferences.getLong("odometroUltimoTanque", 0),
                sharedPreferences.getFloat("totalLitrosUltimoTanque", 0f),
                sharedPreferences.getLong("odometroAutonomia", 0),
                sharedPreferences.getFloat("litrosTanqueCheio", 57f)
        );
        atualizaAutonomia();
    }


    @Override
    public void onLocationChanged(Location location) {
        velocidadeTextView.setText(decimalFormat.format(location.getSpeed() * 3.6));
        if (locationInicial == null) {
            locationInicial = location;
        } else {
            float metrosPercorridos = locationInicial.distanceTo(location);
            ;
            distanciaParcialMeters += metrosPercorridos;
            odometroMeters += metrosPercorridos;

            distanciaParcialTextView.setText(decimalFormat.format(distanciaParcialMeters));

            locationInicial = location;

            atualizarOdometro();
        }

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(
                                    new LatLng(location.getLatitude(), location.getLongitude())
                            ).bearing(
                            location.getBearing()
                    ).zoom(
                            mMap.getCameraPosition().zoom
                    ).build()
                    )
            );
        }

        if((odometroMeters - ultimoOdometroAutonomia) > 1000){
            atualizaAutonomia();
        }
    }

    private void atualizarOdometro() {
        odometroTextView.setText(decimalFormat.format(odometroMeters));
        editor.putFloat("odometroMeters", odometroMeters);
        editor.commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void editarOdometro(View view) {
        ((RelativeLayout) findViewById(R.id.dialogoOdometro)).setVisibility(View.VISIBLE);
        odometroEditavel = odometroMeters;
        atualizaOdometroEdit();
    }

    private void atualizaOdometroEdit() {
        ((TextView) findViewById(R.id.odometroEdit)).setText(decimalFormat.format(odometroEditavel));
    }

    public void aumentarOdometro(View view) {
        odometroEditavel += 1000;
        atualizaOdometroEdit();
    }

    public void aumentarOdometro100km(View view) {
        odometroEditavel += 100000;
        atualizaOdometroEdit();
    }

    public void aumentarOdometro1000km(View view) {
        odometroEditavel += 1000000;
        atualizaOdometroEdit();
    }


    public void diminuirOdometro(View view) {
        odometroEditavel -= 1000;
        atualizaOdometroEdit();
    }

    public void diminuirOdometro100km(View view) {
        odometroEditavel -= 100000;
        atualizaOdometroEdit();
    }


    public void diminuirOdometro1000km(View view) {
        odometroEditavel -= 1000000;
        atualizaOdometroEdit();
    }


    public void salvarOdometro(View view) {
        odometroMeters = odometroEditavel;
        atualizarOdometro();
        atualizaAutonomia();
        ((RelativeLayout) findViewById(R.id.dialogoOdometro)).setVisibility(View.INVISIBLE);
    }

    public void zerarOdometro(View view) {
        odometroEditavel = 0;
        atualizaOdometroEdit();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(
                    new LatLng(-23.533773, -46.625290)
                )
                .zoom(
                        17
                ).build()
                )
        );
    }

    public void abastecer(View view) {
        inicializarAbastecerPrecoCentavos();
        inicializarAbastecerValorCentavos();
        inicializarAbastecerLitrosCentavos();
        inicializarSwitches();
        ((TextView) findViewById(R.id.kmlValueTextView)).setText(decimalFormat.format(controladorCombustivel.getKmLitroUltimoTanque()/1000)+"km/l");
        ((RelativeLayout) findViewById(R.id.abastecerDialogo)).setVisibility(View.VISIBLE);
    }

    private void inicializarSwitches() {

        if(!switchesInitialized) {

            final Switch tanqueVazioSwitch = (Switch) findViewById(R.id.tanqueVazioSwitch);
            final Switch tanqueCheioSwitch = (Switch) findViewById(R.id.tanqueCheioSwitch);

            tanqueVazioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        tanqueCheioSwitch.setChecked(false);
                    }
                }
            });

            tanqueCheioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        tanqueVazioSwitch.setChecked(false);
                    }
                }
            });

            switchesInitialized = true;
        }
    }
    public void abastecerCancelar(View view) {
        ((RelativeLayout) findViewById(R.id.abastecerDialogo)).setVisibility(View.INVISIBLE);
    }

    public void abastecerOK(View view) {
        atualizarAbastecerPrecoCentavos();
        atualizarAbastecerValorCentavos();
        atualizarAbastecerLitrosCentavos();

        try {
            controladorCombustivel.abastecer(
                    Math.round(odometroMeters),
                    abastecerPrecoCentavos / 100,
                    abastecerValorCentavos / 100,
                    abastecerLitrosCentavos,
                    locationInicial,
                    ((Switch) findViewById(R.id.tanqueVazioSwitch)).isChecked(),
                    ((Switch) findViewById(R.id.tanqueCheioSwitch)).isChecked()
            );
        }catch(Exception e){
            e.printStackTrace();
        }

        salvarControladorCombustivel();
        atualizaAutonomia();
        ((RelativeLayout) findViewById(R.id.abastecerDialogo)).setVisibility(View.INVISIBLE);
    }

    private void salvarControladorCombustivel(){
        editor.putFloat("kmLitroUltimoTanque", controladorCombustivel.getKmLitroUltimoTanque());
        editor.putLong("odometroUltimoTanque", controladorCombustivel.getOdometroUltimoTanque());
        editor.putFloat("totalLitrosUltimoTanque", controladorCombustivel.getLitrosDesdeUltimoTanque());
        editor.putLong("odometroAutonomia", controladorCombustivel.getOdometroAutonomia());
        editor.putFloat("litrosTanqueCheio", controladorCombustivel.getLitrosTanqueCheio());
        editor.commit();
    }


    private void inicializarAbastecerPrecoCentavos() {
        abastecerPrecoCentavos = sharedPreferences.getInt("abastecerPrecoCentavos", 400);
        atualizarAbastecerPrecoCentavosTextView();
    }


    private void atualizarAbastecerPrecoCentavos(){
        editor.putInt("abastecerPrecoCentavos", abastecerPrecoCentavos);
        editor.commit();
    }

    private void atualizarAbastecerPrecoCentavosTextView() {
        ((TextView) findViewById(R.id.abastecerPrecoTextView)).setText(formatarDinheiro(abastecerPrecoCentavos));
    }


    public void abastecerPrecoAumentar(View view) {
        abastecerPrecoCentavos++;
        atualizarAbastecerPrecoCentavosTextView();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerPrecoDiminuir(View view) {
        abastecerPrecoCentavos--;
        atualizarAbastecerPrecoCentavosTextView();
        atualizarAbastecerQuantidadeLitros();
    }

    private void inicializarAbastecerValorCentavos() {
        abastecerValorCentavos = sharedPreferences.getInt("abastecerValorCentavos", 1000);
        atualizarAbastecerValorCentavosTextView();
    }

    private void atualizarAbastecerValorCentavos(){
        editor.putInt("abastecerValorCentavos", abastecerValorCentavos);
        editor.commit();
    }

    private void atualizarAbastecerValorCentavosTextView() {
        ((TextView) findViewById(R.id.abastecerValorTextView)).setText(formatarDinheiro(abastecerValorCentavos));
    }

    private String formatarDinheiro(Integer valor) {
        return new DecimalFormat("R$ #.00").format(new BigDecimal(valor).movePointLeft(2));
    }

    public void abastecerValorAumentar(View view) {
        abastecerValorCentavos+=100;
        atualizarAbastecerValorCentavosTextView();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerValorDiminuir(View view) {
        abastecerValorCentavos-=100;
        atualizarAbastecerValorCentavosTextView();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerValor10(View view) {
        abastecerValor(1000);
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerValor30(View view) {
        abastecerValor(3000);
        atualizarAbastecerQuantidadeLitros();
    }
    public void abastecerValor50(View view) {
        abastecerValor(5000);
        atualizarAbastecerQuantidadeLitros();
    }

    private void abastecerValor(int valor) {
        abastecerValorCentavos=valor;
        atualizarAbastecerValorCentavosTextView();
    }

    private void inicializarAbastecerLitrosCentavos() {
        abastecerLitrosCentavos = sharedPreferences.getFloat("abastecerLitrosCentavos", 3f);
        atualizarAbastecerLitrosCentavosTextView();
    }


    private void atualizarAbastecerLitrosCentavos(){
        editor.putFloat("abastecerLitrosCentavos", abastecerLitrosCentavos);
        editor.commit();
    }

    private void atualizarAbastecerLitrosCentavosTextView() {
        ((TextView) findViewById(R.id.abastecerLitrosTextView)).setText(decimalFormat.format(abastecerLitrosCentavos)+"L");
    }


    public void abastecerLitrosAumentar(View view) {
        abastecerLitrosCentavos++;
        atualizarAbastecerLitrosCentavosTextView();
        atualizarAbastecerValor();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerLitrosDiminuir(View view) {
        abastecerLitrosCentavos--;
        atualizarAbastecerLitrosCentavosTextView();
        atualizarAbastecerValor();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerLitros2(View view) {
        abastecerLitros(2f);
        atualizarAbastecerValor();
        atualizarAbastecerQuantidadeLitros();
    }

    public void abastecerLitros6(View view) {
        abastecerLitros(6f);
        atualizarAbastecerValor();
        atualizarAbastecerQuantidadeLitros();
    }
    public void abastecerLitros10(View view) {
        abastecerLitros(10f);
        atualizarAbastecerValor();
        atualizarAbastecerQuantidadeLitros();
    }

    private void abastecerLitros(float litros) {
        abastecerLitrosCentavos=litros;
        atualizarAbastecerLitrosCentavosTextView();
    }

    private void atualizarAbastecerQuantidadeLitros(){
        abastecerLitros(abastecerValorCentavos/abastecerPrecoCentavos);
    }

    private void atualizarAbastecerValor(){
        abastecerValor(Math.round(abastecerLitrosCentavos*abastecerPrecoCentavos));
    }

    private void atualizaAutonomia(){
        double autonomia = controladorCombustivel.getOdometroAutonomia() - odometroMeters;
        ultimoOdometroAutonomia = odometroMeters;
        TextView numeroAutonomiaTextView = (TextView) findViewById(R.id.numeroAutonomia);
        numeroAutonomiaTextView.setText(""+Math.round(autonomia/1000));
        if(autonomia<0){

        }
    }

}