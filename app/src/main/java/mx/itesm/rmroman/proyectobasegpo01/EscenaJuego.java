package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

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
public class EscenaJuego extends EscenaBase
{
    //Fondo
    private ITextureRegion regionFondo;
    private ITextureRegion regionFondoFrente;
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
    private float LIMITE_TIEMPO = 2.5f;

    // Energía del personaje
    private int energia = 100;

    // Fin del juego
    private ITextureRegion regionFin;

    // Escena de PAUSA
    private CameraScene escenaPausa;    // La escena que se muestra al hacer pausa
    private ITextureRegion regionPausa;
    private ITextureRegion regionBtnPausa;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("spaceFondo.jpg");
        regionFondoFrente = cargarImagen("starsFront.png");
        regionPersonajeAnimado = cargarImagenMosaico("kiki.png", 600, 158, 1, 4);
        regionEnemigo = cargarImagen("alienblaster.png");
        regionFin = cargarImagen("fin.png");
        // Pausa
        regionBtnPausa = cargarImagen("juego/btnPausa.png");
        regionPausa = cargarImagen("juego/pausa.png");
    }

    @Override
    public void crearEscena() {
        // Lista de enemigos que aparecen del lado derecho
        listaEnemigos = new ArrayList<>();

        // Fondo animado
        AutoParallaxBackground fondoAnimado = new AutoParallaxBackground(1, 1, 1, 5);

        // Fondo atrás
        Sprite spriteFondoAtras = cargarSprite(ControlJuego.ANCHO_CAMARA/2,
                ControlJuego.ALTO_CAMARA/2, regionFondo);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-3, spriteFondoAtras));
        // Fondo frente
        Sprite spriteFondofrente = cargarSprite(ControlJuego.ANCHO_CAMARA/2,
                ControlJuego.ALTO_CAMARA / 2, regionFondoFrente);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-8, spriteFondofrente));

        setBackground(fondoAnimado);

        // Personaje animado
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(200);   // 200ms entre frames, 1000/200 fps
        attachChild(spritePersonaje);

        // Crea el botón de PAUSA y lo agrega a la escena
        Sprite btnPausa = new Sprite(regionBtnPausa.getWidth(), ControlJuego.ALTO_CAMARA - regionBtnPausa.getHeight(),
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    pausarJuego();
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
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

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        if (!juegoCorriendo) {
            return;
        }

        // Acumular tiempo
        tiempoEnemigos += pSecondsElapsed;
        if (tiempoEnemigos>LIMITE_TIEMPO) {
            // Se cumplió el tiempo
            tiempoEnemigos = 0;
            if (LIMITE_TIEMPO>0.5f) {
                LIMITE_TIEMPO -= 0.15f;
            }
            Sprite spriteEnemigo = cargarSprite(ControlJuego.ANCHO_CAMARA+regionEnemigo.getWidth(),
                    (float)(Math.random()*ControlJuego.ALTO_CAMARA-regionEnemigo.getHeight())+regionEnemigo.getHeight(),regionEnemigo);
            Enemigo nuevoEnemigo = new Enemigo(spriteEnemigo);
            listaEnemigos.add(nuevoEnemigo);
            attachChild(nuevoEnemigo.getSprite());
            Log.i("Tamaño", "Datos: " + listaEnemigos.size());
        }
        // Actualizar cada uno de los listaEnemigos y ver si alguno ya salió de la pantalla
        for (int i= listaEnemigos.size()-1; i>=0; i--) {
            Enemigo enemigo = listaEnemigos.get(i);

            enemigo.mover(-10,0);

            if (enemigo.getSprite().getX()<-enemigo.getSprite().getWidth()) {
                detachChild(enemigo.getSprite());
                listaEnemigos.remove(enemigo);
            }

            // Revisa si choca el personaje con el enemigo
            if (spritePersonaje.collidesWith(enemigo.getSprite())) {
                detachChild(enemigo.getSprite());
                listaEnemigos.remove(enemigo);
                energia -= 10;
                Log.i("ENERGIA","Energia: " + energia);
                if (energia<=0) {
                    juegoCorriendo=false;
                    // Agrega pantalla de fin
                    Sprite spriteFin = new Sprite(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2,
                            regionFin,actividadJuego.getVertexBufferObjectManager()) {
                        @Override
                        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                            if (pSceneTouchEvent.isActionUp()) {
                                onBackKeyPressed();
                            }
                            return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                        }
                    };
                    registerTouchArea(spriteFin);
                    attachChild(spriteFin);
                }
            }
        }
    }

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        if (pSceneTouchEvent.isActionDown() && !personajeSaltando) {
            personajeSaltando = true;
            // Animar sprite central
            JumpModifier salto = new JumpModifier(1, spritePersonaje.getX(), spritePersonaje.getX(),
                    spritePersonaje.getY(), spritePersonaje.getY(),-200);
            RotationModifier rotacion = new RotationModifier(1, 360, 0);
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
        }

        if (pSceneTouchEvent.isActionDown()) {
            // El usuario toca la pantalla
            float x = pSceneTouchEvent.getX();
            float y = pSceneTouchEvent.getY();
            spritePersonaje.setPosition(x, y);
        }
        if (pSceneTouchEvent.isActionMove()) {
            // El usuario mueve el dedo sobre la pantalla
            float x = pSceneTouchEvent.getX();
            float y = pSceneTouchEvent.getY();
            spritePersonaje.setPosition(x, y);
        }
        if (pSceneTouchEvent.isActionUp()) {
            // El usuario deja de tocar la pantalla
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);
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
        actividadJuego.getEngine().disableAccelerationSensor(actividadJuego);

        regionFondo.getTexture().unload();
        regionFondo = null;
        regionFondoFrente.getTexture().unload();
        regionFondoFrente = null;
        regionPersonajeAnimado.getTexture().unload();
        regionPersonajeAnimado = null;
        regionEnemigo.getTexture().unload();
        regionEnemigo = null;
        regionFin.getTexture().unload();
        regionFin = null;
        regionBtnPausa.getTexture().unload();
        regionBtnPausa = null;
        regionPausa.getTexture().unload();
        regionPausa = null;
    }
}
