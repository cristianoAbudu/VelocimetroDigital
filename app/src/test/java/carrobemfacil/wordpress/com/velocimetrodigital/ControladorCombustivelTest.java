package carrobemfacil.wordpress.com.velocimetrodigital;

import android.location.Location;

import org.junit.Test;

import static org.junit.Assert.*;

public class ControladorCombustivelTest {
    @Test
    public void abastecerTanqueVazio() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0,0,0,0,0);

        controladorCombustivel.abastecer(7080, 4.2f, 10f, 10f/4.2f, new Location("test"), true, false);

        assertEquals(10/4.2, controladorCombustivel.getLitrosDesdeUltimoTanque(), 0.1);

    }
    @Test
    public void abastecerTanqueIntermediario() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0,0,0,0,0);

        controladorCombustivel.abastecer(7080, 4.2f, 10f, 10f/4.2f, new Location("test"), true, false);

        controladorCombustivel.abastecer(7082, 4.2f, 15f, 15f/4.2f, new Location("test"), false, false);

        assertEquals((10/4.2) + (15/4.2), controladorCombustivel.getLitrosDesdeUltimoTanque(), 0.1);

    }

    @Test
    public void verificarAutonomia() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0,0,0,0,0);

        controladorCombustivel.abastecer(7080, 4, 8, 2, new Location("test"), true, false);

        controladorCombustivel.abastecer(7082, 4, 12, 3, new Location("test"), false, false);

        controladorCombustivel.abastecer(7105, 4, 20, 5, new Location("test"), true, false);

        assertEquals(5, controladorCombustivel.getKmLitroUltimoTanque(), 0.1);

        assertEquals(7130, controladorCombustivel.getOdometroAutonomia(), 0.1);

        assertEquals(5, controladorCombustivel.getAutonomiaRestante(7125), 0.1);
    }

    @Test
    public void verificarDeserealizacao() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0,0,0,0,0);

        controladorCombustivel.abastecer(7080, 4, 8, 2, new Location("test"), true, false);

        controladorCombustivel.abastecer(7082, 4, 12, 3, new Location("test"), false, false);

        controladorCombustivel.abastecer(7105, 4, 20, 5, new Location("test"), true, false);

        controladorCombustivel.setLitrosTanqueCheio(57);

        assertEquals(5, controladorCombustivel.getKmLitroUltimoTanque(), 0.1);

        assertEquals(7105, controladorCombustivel.getOdometroUltimoTanque(), 0.1);

        assertEquals(5, controladorCombustivel.getLitrosDesdeUltimoTanque(), 0.1);

        assertEquals(57, controladorCombustivel.getLitrosTanqueCheio(), 0.1);

        assertEquals(7130, controladorCombustivel.getOdometroAutonomia());

        ControladorCombustivel controladorCombustivelDeserealizado = new ControladorCombustivel(5,  7105, 5, 7130, 57);

        assertEquals(5, controladorCombustivelDeserealizado.getKmLitroUltimoTanque(), 0.1);

        assertEquals(7105, controladorCombustivelDeserealizado.getOdometroUltimoTanque(), 0.1);

        assertEquals(5, controladorCombustivelDeserealizado.getLitrosDesdeUltimoTanque(), 0.1);

        assertEquals(57, controladorCombustivel.getLitrosTanqueCheio(), 0.1);

        assertEquals(7130, controladorCombustivel.getOdometroAutonomia());

    }

    @Test
    public void verificarAutonomiaTanqueCheio() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0, 0, 0, 0 ,0);

        controladorCombustivel.setLitrosTanqueCheio(57);

        controladorCombustivel.abastecer(0, 4, (57*4), 57, new Location("test"), false, true);

        controladorCombustivel.abastecer(100, 4, (10*4), 10, new Location("test"), false, true);

        assertEquals(670, controladorCombustivel.getOdometroAutonomia(), 0.1);

        assertEquals(570, controladorCombustivel.getAutonomiaRestante(100), 0.1);
    }


    @Test
    public void verificarAutonomiaTanqueCheioIntermediarioCheio() {
        ControladorCombustivel controladorCombustivel = new ControladorCombustivel(0, 0, 0, 0 ,0);

        controladorCombustivel.setLitrosTanqueCheio(57);

        controladorCombustivel.abastecer(0, 4, (57*4), 57, new Location("test"), false, true);

        controladorCombustivel.abastecer(200, 4, (10*4), 10, new Location("test"), false, false);

        controladorCombustivel.abastecer(300, 4, (20*4), 20, new Location("test"), false, true);

        assertEquals(10, controladorCombustivel.getKmLitroUltimoTanque(), 0.1);

        assertEquals(870, controladorCombustivel.getOdometroAutonomia(), 0.1);

        assertEquals(570, controladorCombustivel.getAutonomiaRestante(300), 0.1);


    }

}