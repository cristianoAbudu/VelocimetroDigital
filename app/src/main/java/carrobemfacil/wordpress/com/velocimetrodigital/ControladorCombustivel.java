package carrobemfacil.wordpress.com.velocimetrodigital;

import android.location.Location;

import java.io.Serializable;

public class ControladorCombustivel implements Serializable {
    private float kmLitroUltimoTanque = 0;
    private long odometroUltimoTanque = 0;
    private float litrosDesdeUltimoTanque = 0;

    private long odometroAutonomia = 0;

    private float litrosTanqueCheio = 0;


    public ControladorCombustivel(float kmLitroUltimoTanque, long odometroUltimoTanque, float litrosDesdeUltimoTanque, long odometroAutonomia, float litrosTanqueCheio) {

        this.kmLitroUltimoTanque = kmLitroUltimoTanque;
        this.odometroUltimoTanque = odometroUltimoTanque;
        this.litrosDesdeUltimoTanque = litrosDesdeUltimoTanque;
        this.odometroAutonomia = odometroAutonomia;
        this.litrosTanqueCheio = litrosTanqueCheio;

    }

    public void abastecer(long odometro, float precoLitro, float valorAbastecido, float quantidadeLitros, Location locationPosto, boolean tanqueVazio, boolean tanqueCheio) {


        if(tanqueVazio){
            tratarTanqueVazio(odometro, quantidadeLitros);
        }else if (tanqueCheio){
            tratarTanqueCheio(odometro, quantidadeLitros);
        }else{
            litrosDesdeUltimoTanque += quantidadeLitros;
            odometroAutonomia += (quantidadeLitros * kmLitroUltimoTanque);
        }
    }

    private void tratarTanqueCheio(long odometro, float quantidadeLitros) {
        kmLitroUltimoTanque = calcularDistanciaPercorridaUltimoTanque(odometro) / (litrosDesdeUltimoTanque + quantidadeLitros);

        odometroUltimoTanque = odometro;
        litrosDesdeUltimoTanque = 0;

        atualizaOdometroAutonomia(litrosTanqueCheio);
    }

    private void tratarTanqueVazio(long odometro, float quantidadeLitros) {
        if(litrosDesdeUltimoTanque != 0 ) {
            kmLitroUltimoTanque = calcularDistanciaPercorridaUltimoTanque(odometro) / litrosDesdeUltimoTanque;
        }

        odometroUltimoTanque = odometro;
        litrosDesdeUltimoTanque = quantidadeLitros;

        atualizaOdometroAutonomia(litrosDesdeUltimoTanque);
    }

    private float calcularDistanciaPercorridaUltimoTanque(long odometro) {
        return odometro - odometroUltimoTanque;
    }

    public float getKmLitroUltimoTanque() {
        return kmLitroUltimoTanque;
    }

    public void atualizaOdometroAutonomia(float litros){
        odometroAutonomia = Math.round(odometroUltimoTanque + (litros * kmLitroUltimoTanque));
    }

    public long getOdometroAutonomia() {
        return odometroAutonomia;
    }

    public float getAutonomiaRestante(int odometroAtual) {
        return getOdometroAutonomia() - odometroAtual;
    }

    public long getOdometroUltimoTanque() {
        return odometroUltimoTanque;
    }

    public float getLitrosDesdeUltimoTanque() {
        return litrosDesdeUltimoTanque;
    }

    public float getLitrosTanqueCheio() {
        return litrosTanqueCheio;
    }

    public void setLitrosTanqueCheio(float litrosTanqueCheio) {
        this.litrosTanqueCheio = litrosTanqueCheio;
    }

}
