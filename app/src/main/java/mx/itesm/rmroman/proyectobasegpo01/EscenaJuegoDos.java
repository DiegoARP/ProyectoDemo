package mx.itesm.rmroman.proyectobasegpo01;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
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
public class EscenaJuegoDos extends EscenaBase
{
    //Fondo
    private ITextureRegion regionFondo;

    // Sprite animado
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;


    // Banderas
    private boolean personajeSaltando = false;
    private boolean juegoCorriendo = true;

    // Enemigos
    private ArrayList<Enemigo> listaEnemigos;
    private ITextureRegion regionEnemigo;

    // Tiempo para generar listaEnemigos
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 1.4f;

    // Fin del juego
    private ITextureRegion regionFin;


    // Proyectiles
    private ITextureRegion regionProyectil;
    private ArrayList<Sprite> listaProyectiles;

    // Escena de PAUSA
    private CameraScene escenaPausa;    // La escena que se muestra al hacer pausa
    private ITextureRegion regionPausa;
    private ITextureRegion regionBtnPausa;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("spaceFondo.jpg");
        regionPersonajeAnimado = cargarImagenMosaico("kiki.png", 600, 158, 1, 4);
        regionEnemigo = cargarImagen("alienblaster.png");
        regionFin = cargarImagen("fin.png");
        regionProyectil = cargarImagen("laser.png");
        // Pausa
        regionBtnPausa = cargarImagen("juego/btnPausa.png");
        regionPausa = cargarImagen("juego/pausa.png");
    }

    @Override
    public void crearEscena() {

        listaProyectiles = new ArrayList<>();
        listaEnemigos = new ArrayList<>();

        // Fondo animado
        AutoParallaxBackground fondoAnimado = new AutoParallaxBackground(1, 1, 1, 5);

        Sprite spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2,
                ControlJuego.ALTO_CAMARA/2, regionFondo);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-3, spriteFondo));

        setBackground(fondoAnimado);

        // Personaje animado
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA/8, ControlJuego.ALTO_CAMARA/8,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(200);
        attachChild(spritePersonaje);

        crearEnemigos();

        // Crea el botón de PAUSA y lo agrega a la escena
        Sprite btnPausa = new Sprite(regionBtnPausa.getWidth(), ControlJuego.ALTO_CAMARA - regionBtnPausa.getHeight(),
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    pausarJuego();
                }
                return true; //super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        attachChild(btnPausa);
        registerTouchArea(btnPausa);

        // Crear la escena de PAUSA, pero NO lo agrega a la escena
        escenaPausa = new CameraScene(actividadJuego.camara);
        Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionPausa);
        escenaPausa.attachChild(fondoPausa);
        escenaPausa.setBackgroundEnabled(false);
    }

    private void pausarJuego() {
        if (juegoCorriendo) {
            setChildScene(escenaPausa,false,true,false);
            juegoCorriendo = false;
        } else {
            clearChildScene();
            juegoCorriendo = true;
        }
    }

    private void crearEnemigos() {
        for(int x=700; x<=1200; x+=100) {
            for(int y=100; y<=700; y+=100) {
                Sprite nave = cargarSprite(x, y, regionEnemigo);
                attachChild(nave);
                Enemigo enemigo = new Enemigo(nave);
                listaEnemigos.add(enemigo);
            }
        }
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        if (!juegoCorriendo) {
            return;
        }

        actualizarEnemigos(pSecondsElapsed);

        actualizarProyectiles(pSecondsElapsed);
    }

    private void actualizarProyectiles(float tiempo) {

        for(int i=listaProyectiles.size()-1; i>=0; i--) {
            Sprite proyectil = listaProyectiles.get(i);
            proyectil.setX(proyectil.getX() + 10);
            if ( proyectil.getX()>ControlJuego.ANCHO_CAMARA ) {
                detachChild(proyectil);
                listaProyectiles.remove(proyectil);
                continue;
            }
            // probar si colisionó con un enemigo
            for(int k=listaEnemigos.size()-1; k>=0; k--) {
                Enemigo enemigo = listaEnemigos.get(k);
                if ( proyectil.collidesWith(enemigo.getSpriteEnemigo()) ) {
                    // Lo destruye
                    detachChild(enemigo.getSpriteEnemigo());
                    listaEnemigos.remove(enemigo);
                    // desaparece el proyectil
                    detachChild(proyectil);
                    listaProyectiles.remove(proyectil);
                    break;
                }
            }
        }
    }

    private void actualizarEnemigos(float tiempo) {
        for (Enemigo enemigo:listaEnemigos) {
            enemigo.mover(-1,1);
        }
    }

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        if (!juegoCorriendo) {
            return super.onSceneTouchEvent(pSceneTouchEvent);
        }

        if (pSceneTouchEvent.isActionDown() && !personajeSaltando &&
                pSceneTouchEvent.getX()<ControlJuego.ANCHO_CAMARA/2) {

            personajeSaltando = true;
            // Animar sprite central
            JumpModifier salto = new JumpModifier(2, spritePersonaje.getX(), spritePersonaje.getX(),
                    spritePersonaje.getY(), spritePersonaje.getY(),-6*ControlJuego.ALTO_CAMARA/8);
            RotationModifier rotacion = new RotationModifier(2, 360, 0);
            ParallelEntityModifier paralelo = new ParallelEntityModifier(salto,rotacion)
            {
                @Override
                protected void onModifierFinished(IEntity pItem) {
                    personajeSaltando = false;
                    unregisterEntityModifier(this);
                    super.onModifierFinished(pItem);
                }
            };
            spritePersonaje.registerEntityModifier(paralelo);
        } else if (pSceneTouchEvent.isActionDown() &&
                pSceneTouchEvent.getX()>ControlJuego.ANCHO_CAMARA/2) {
            if (listaProyectiles.size()<3) {
                dispararProyectil();
            }
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);
    }

    private void dispararProyectil() {

            // Crearlo
        Sprite spriteProyectil = cargarSprite(spritePersonaje.getX(), spritePersonaje.getY(), regionProyectil);
        attachChild(spriteProyectil);
        listaProyectiles.add(spriteProyectil);
    }

    @Override
    public void onBackKeyPressed() {
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaJuego();
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_JUEGO;
    }

    @Override
    public void liberarEscena() {
        liberarRecursos();
        this.detachSelf();
        this.dispose();
    }

    @Override
    public void liberarRecursos() {
        // Detiene el acelerómetro
        //actividadJuego.getEngine().disableAccelerationSensor(actividadJuego);

        regionFondo.getTexture().unload();
        regionFondo = null;

        regionPersonajeAnimado.getTexture().unload();
        regionPersonajeAnimado = null;

        regionProyectil.getTexture().unload();
        regionProyectil = null;
    }

}
