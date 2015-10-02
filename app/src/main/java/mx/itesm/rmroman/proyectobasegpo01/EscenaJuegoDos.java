package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.util.ArrayList;

/**
 * Created by rmroman on 15/09/15.
 */
public class EscenaJuegoDos extends EscenaBase implements IAccelerationListener
{
    //Fondo
    private ITextureRegion regionFondo;
    private ITextureRegion regionFondoFrente;
    // Sprite animado
    private AnimatedSprite spriteHeroe;
    private TiledTextureRegion regionHeroeAnimado;

    // Banderas
    private boolean heroeSaltando = false;
    private boolean juegoCorriendo = true;

    // Enemigos
    private ArrayList<Enemigo> enemigos;
    private ITextureRegion regionEnemigo;

    // Tiempo para generar enemigos
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 2.5f;

    // Energía del personaje
    private int energia = 100;

    // Fin del juego
    private ITextureRegion regionFin;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("spaceFondo.jpg");
        regionFondoFrente = cargarImagen("starsFront.png");
        regionHeroeAnimado = cargarImagenMosaico("kiki.png", 600, 158, 1, 4);
        regionEnemigo = cargarImagen("alienblaster.png");
        regionFin = cargarImagen("fin.png");
    }

    @Override
    public void crearEscena() {

        enemigos = new ArrayList<>();

        Sprite spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2,
                ControlJuego.ALTO_CAMARA/2, regionFondo);
        //SpriteBackground fondo = new SpriteBackground(0,0,0,spriteFondo);
        //setBackground(fondo);

        // Fondo animado
        AutoParallaxBackground fondoAnimado = new AutoParallaxBackground(1, 1, 1, 5);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-3, spriteFondo));
        //setBackground(fondoAnimado);

        Sprite spriteFondofrente = cargarSprite(ControlJuego.ANCHO_CAMARA/2,
                ControlJuego.ALTO_CAMARA / 2, regionFondoFrente);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-8, spriteFondofrente));

        setBackground(fondoAnimado);

        // Héroe animado
        spriteHeroe = new AnimatedSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionHeroeAnimado, actividadJuego.getVertexBufferObjectManager());
        spriteHeroe.animate(200);
        attachChild(spriteHeroe);

        actividadJuego.getEngine().enableAccelerationSensor(actividadJuego, this);
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
            enemigos.add(nuevoEnemigo);
            attachChild(nuevoEnemigo.getSpriteEnemigo());
            Log.i("Tamaño","Datos: " + enemigos.size());
        }
        // Actualizar cada uno de los enemigos y ver si alguno ya salió de la pantalla
        for (int i=enemigos.size()-1; i>=0; i--) {
            Enemigo enemigo = enemigos.get(i);

            enemigo.mover(-10,0);

            if (enemigo.getSpriteEnemigo().getX()<-enemigo.getSpriteEnemigo().getWidth()) {
                detachChild(enemigo.getSpriteEnemigo());
                enemigos.remove(enemigo);
            }

            // Revisa si choca el personaje con el enemigo
            if (spriteHeroe.collidesWith(enemigo.getSpriteEnemigo())) {
                detachChild(enemigo.getSpriteEnemigo());
                enemigos.remove(enemigo);
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

        if (pSceneTouchEvent.isActionDown() && !heroeSaltando) {
            heroeSaltando = true;
            // Animar sprite central
            JumpModifier salto = new JumpModifier(1,spriteHeroe.getX(),spriteHeroe.getX(),
                    spriteHeroe.getY(),spriteHeroe.getY(),-200);
            RotationModifier rotacion = new RotationModifier(1, 360, 0);
            ParallelEntityModifier paralelo = new ParallelEntityModifier(salto,rotacion)
            {
                @Override
                protected void onModifierFinished(IEntity pItem) {
                    heroeSaltando = false;
                    unregisterEntityModifier(this);
                    super.onModifierFinished(pItem);
                }
            };
            spriteHeroe.registerEntityModifier(paralelo);
        }

        if (pSceneTouchEvent.isActionDown()) {
            // El usuario toca la pantalla
            float x = pSceneTouchEvent.getX();
            float y = pSceneTouchEvent.getY();
            spriteHeroe.setPosition(x,y);
        }
        if (pSceneTouchEvent.isActionMove()) {
            // El usuario mueve el dedo sobre la pantalla
            float x = pSceneTouchEvent.getX();
            float y = pSceneTouchEvent.getY();
            spriteHeroe.setPosition(x,y);
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
        regionHeroeAnimado.getTexture().unload();
        regionHeroeAnimado = null;
    }

    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        float dx = pAccelerationData.getX();
        float dy = pAccelerationData.getY();
        float dz = pAccelerationData.getZ();
        spriteHeroe.setX(spriteHeroe.getX()+dx );
        //spriteHeroe.setY(spriteHeroe.getY() + dy + 5);
    }
}
