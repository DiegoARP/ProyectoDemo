package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;

/**
 * Created by rmroman on 15/09/15.
 */
public class EscenaScrollHorizontal extends EscenaBase
{
    //Fondo
    private ITextureRegion regionFondo;

    // Sprite animado
    private Sprite personaje;
    private ITextureRegion regionPersonaje;
    private Direccion direccion;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("scroll/fondoScroll.jpg");
        regionPersonaje = cargarImagen("scroll/runner.png");
    }

    @Override
    public void crearEscena() {

        Sprite spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2,
                ControlJuego.ALTO_CAMARA / 2, regionFondo);
        attachChild(spriteFondo);

        // Personaje animado
        personaje = new Sprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 8,
                regionPersonaje, actividadJuego.getVertexBufferObjectManager());

        attachChild(personaje);

        // Dirección inicial
        direccion = Direccion.NINGUNA;
    }


    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {



            if (direccion==Direccion.IZQUIERDA
                    && personaje.getX()>ControlJuego.ANCHO_CAMARA/2) {
                personaje.setX( personaje.getX()-5);
            }
            if (direccion==Direccion.DERECHA
                    && personaje.getX()<regionFondo.getWidth()-ControlJuego.ANCHO_CAMARA) {
                personaje.setX( personaje.getX()+5);
            }
        actividadJuego.camara.setCenter(personaje.getX(),ControlJuego.ALTO_CAMARA/2);
    }


    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
        Log.i("onTouch","TOUCH!!!!!");
        if (pSceneTouchEvent.isActionDown() ) {
            if ( pSceneTouchEvent.getX()<ControlJuego.ANCHO_CAMARA/2) {
                direccion = Direccion.IZQUIERDA;
            } else {
                // Derecha
                direccion = Direccion.DERECHA;
            }
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);
    }



    @Override
    public void onBackKeyPressed() {
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaScroll();
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_SCROLL;
    }

    @Override
    public void liberarEscena() {
        liberarRecursos();
        this.detachSelf();
        this.dispose();
    }

    @Override
    public void liberarRecursos() {

        regionFondo.getTexture().unload();
        regionFondo = null;

        regionPersonaje.getTexture().unload();
        regionPersonaje = null;

    }

}
