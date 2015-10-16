package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.ArrayList;

/**
 * Created by rmroman on 15/09/15.
 */
public class EscenaScrollHorizontal extends EscenaBase
{
    //Fondo
    private ITextureRegion regionFondo;

    // Sprite
    private Sprite personaje;
    private ITextureRegion regionPersonaje;
    private Direccion direccion;

    // Textos
    private Text textoInicial;
    private Text textoFinal;
    private Text textoMedio;
    private IFont fontMonster;
    private Text marcador;

    // HUD (Heads-Up Display)
    private HUD hud;
    // Flechas
    private ITextureRegion regionIzquierda;
    private ITextureRegion regionDerecha;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("scroll/fondoScroll2.jpg");
        regionPersonaje = cargarImagen("scroll/runner.png");
        // Cargar font
        fontMonster = cargarFont("fonts/monster.ttf");
        // Flechas
        regionDerecha = cargarImagen("scroll/flechaDer.png");
        regionIzquierda = cargarImagen("scroll/flechaIzq.png");
    }

    // Crea y regresa un font que carga desde un archivo .ttf  (http://www.1001freefonts.com, http://www.1001fonts.com/)
    private Font cargarFont(String archivo) {

        // La imagen que contiene cada símbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);

        // Carga el archivo, tamaño 36, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, 44, true, 0xFF00FF00);
        tipoLetra.load();
        tipoLetra.prepareLetters("InicoFnalMed ".toCharArray());

        return tipoLetra;
    }


    @Override
    public void crearEscena() {

        setTouchAreaBindingOnActionDownEnabled(true);

        // Fondo
        Sprite spriteFondo = cargarSprite(regionFondo.getWidth()/2,
                ControlJuego.ALTO_CAMARA / 2, regionFondo);
        attachChild(spriteFondo);

        // Personaje
        personaje = new Sprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 8,
                regionPersonaje, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                Log.i("Personaje","touch");
                return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        registerTouchArea(personaje);
        attachChild(personaje);

        setTouchAreaBindingOnActionDownEnabled(false);

        // Dirección inicial
        direccion = Direccion.NINGUNA;

        // Agrega texto en los límites del mundo
        agregarTextos();

        // Agregar flechas
        agregarFlechas();
    }

    private void agregarFlechas() {
        hud = new HUD();
        Sprite flechaIzq = new Sprite(100,100,regionIzquierda,actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Log.i("onAreaTouch","Izquierda");
                if ( pSceneTouchEvent.isActionDown()) {
                    direccion = Direccion.IZQUIERDA;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        hud.attachChild(flechaIzq);
        hud.registerTouchArea(flechaIzq);

        Sprite flechaDer = new Sprite(ControlJuego.ANCHO_CAMARA-100,100,regionDerecha,actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Log.i("onAreaTouch","Derecha");
                if ( pSceneTouchEvent.isActionDown()) {
                    direccion = Direccion.DERECHA;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        hud.attachChild(flechaDer);
        hud.registerTouchArea(flechaDer);

        actividadJuego.camara.setHUD(hud);
    }

    private void agregarTextos() {
        textoInicial = new Text(100,ControlJuego.ALTO_CAMARA/2,fontMonster,"Inicio",actividadJuego.getVertexBufferObjectManager());
        attachChild(textoInicial);

        textoFinal = new Text(regionFondo.getWidth()-100,ControlJuego.ALTO_CAMARA/2,fontMonster,"Fin",actividadJuego.getVertexBufferObjectManager());
        attachChild(textoFinal);

        textoMedio = new Text(regionFondo.getWidth()/2,ControlJuego.ALTO_CAMARA/2,fontMonster,"Medio",actividadJuego.getVertexBufferObjectManager());
        attachChild(textoMedio);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {

        if (direccion == Direccion.IZQUIERDA
                && personaje.getX() > ControlJuego.ANCHO_CAMARA / 2) {
            personaje.setX(personaje.getX() - 5);
        }
        if (direccion == Direccion.DERECHA
                && personaje.getX() < regionFondo.getWidth() - ControlJuego.ANCHO_CAMARA/2) {
            personaje.setX(personaje.getX() + 5);
        }
        actividadJuego.camara.setCenter(personaje.getX(), ControlJuego.ALTO_CAMARA / 2);

        // Anima los textos
        textoInicial.setRotation(textoInicial.getRotation() + 1);
        textoFinal.setRotation(textoFinal.getRotation() + 6);
        textoMedio.setRotation(textoMedio.getRotation() + 3);
    }

    /*
    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        actividadJuego.camara.convertSceneTouchEventToCameraSceneTouchEvent(pSceneTouchEvent);
        if (pSceneTouchEvent.isActionDown() ) {
            if ( pSceneTouchEvent.getX()<ControlJuego.ANCHO_CAMARA/2) {
                // Izquierda
                direccion = Direccion.IZQUIERDA;
            } else {
                // Derecha
                direccion = Direccion.DERECHA;
            }
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);

    }
*/

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
