package mx.itesm.rmroman.proyectobasegpo01;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * La escena de espera
 *
 * @author Roberto Martínez Román
 */
public class EscenaCargando extends EscenaBase
{
    // Imágenes
    private ITextureRegion regionFondo;
    // Sprites sobre la escena
    private Sprite spriteFondo;

    // Carga todos los recursos para ESTA ESCENA.
    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("cargando/espera.png");
    }

    // Arma la escena que se presentará en pantalla
    @Override
    public void crearEscena() {
        // Crea el(los) sprite(s) de la escena
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionFondo);

        // Crea el fondo de la pantalla
        Background fondo = new Background(1,1,1);
        setBackground(fondo);
        attachChild(spriteFondo);
        setBackgroundEnabled(true);

        // Programa la carga de la segunda escena, después de cierto tiempo
        actividadJuego.getEngine().registerUpdateHandler(new TimerHandler(0.5f,
                new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        actividadJuego.getEngine().unregisterUpdateHandler(pTimerHandler); // Invalida el timer
                        admEscenas.crearEscenaScroll();
                        admEscenas.setEscena(TipoEscena.ESCENA_SCROLL);
                        admEscenas.liberarEscenaCargando();
                    }
                }));
    }

    // La escena se debe actualizar en este método que se repite "varias" veces por segundo
    // Aquí es donde programan TODA la acción de la escena (movimientos, choques, disparos, etc.)
    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        spriteFondo.setRotation(spriteFondo.getRotation()+5);
    }

    @Override
    public void onBackKeyPressed() {
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_CARGANDO;
    }

    // Libera la escena misma del engine
    @Override
    public void liberarEscena() {
        // Libera las imágenes
        liberarRecursos();
        this.detachSelf();      // La escena se deconecta del engine
        this.dispose();         // Libera la memoria
    }

    // Libera cada una de las regiones asignadas para esta escena
    @Override
    public void liberarRecursos() {
        // Estas dos instrucciones por cada región inicializada
        regionFondo.getTexture().unload();
        regionFondo = null;
    }
}
