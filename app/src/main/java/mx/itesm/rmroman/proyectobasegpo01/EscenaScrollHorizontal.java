package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.modifier.ease.EaseBounceIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import java.text.DecimalFormat;
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
    private Direccion direccion;    // Izquierda, Derecha, etc...
    private boolean debeSaltar;
    private boolean estaSaltando;

    // Textos a lo largo del mundo
    private Text textoInicial;
    private Text textoFinal;
    private Text textoMedio;
    private Text txtMarcador; // Por ahora con valorMarcador
    private IFont fontMonster;  // Fuente a utilizar (.ttf)

    // Marcador (valorMarcador)
    private float valorMarcador;    // Aumenta 100 puntos por cada moneda

    // HUD (Heads-Up Display)
    private HUD hud;

    // Flechas
    private ITextureRegion regionIzquierda;
    private ITextureRegion regionDerecha;

    // Monedas
    private ITextureRegion regionMoneda;
    private ArrayList<Sprite> listaMonedas;
    private static final int NUM_MONEDAS = 30;

    // Plataforma
    private ITextureRegion regionPlataforma;
    private Sprite plataforma;
    private boolean estaSobrePlataforma = false;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("scroll/fondoScroll.jpg");
        regionPersonaje = cargarImagen("scroll/runner.png");
        fontMonster = cargarFont("fonts/monster.ttf");
        regionDerecha = cargarImagen("scroll/flechaDer.png");
        regionIzquierda = cargarImagen("scroll/flechaIzq.png");
        regionMoneda = cargarImagen("scroll/bolaAcero.png");
        regionPlataforma = cargarImagen("scroll/plataforma.jpg");
    }

    // Crea y regresa un font que carga desde un archivo .ttf  (http://www.1001freefonts.com, http://www.1001fonts.com/)
    private Font cargarFont(String archivo) {
        // La imagen que contiene cada símbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaño 56, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, 56, true, 0xFF00FF00);
        tipoLetra.load();
        tipoLetra.prepareLetters("InicoFnalMed 01234567890.".toCharArray());

        return tipoLetra;
    }

    @Override
    public void crearEscena() {
        // Fondo
        Sprite spriteFondo = cargarSprite(regionFondo.getWidth()/2,
                ControlJuego.ALTO_CAMARA / 2, regionFondo);
        attachChild(spriteFondo);

        // Personaje
        personaje = new Sprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 8,
                regionPersonaje, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                if ( pSceneTouchEvent.isActionDown() ) {
                    //direccion = Direccion.NINGUNA; // Por si quieren detener el movimiento
                    // Salta...
                    debeSaltar = true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };

        registerTouchArea(personaje);
        attachChild(personaje);
        debeSaltar = false;

        // Dirección inicial
        direccion = Direccion.NINGUNA;
        // Agrega texto en los límites del mundo
        agregarTextos();
        // Agregar flechas y el txtMarcador/valorMarcador
        agregarHUD();
        // Agregar monedas
        agregarMonedas();

        // Plataformas
        agregarPlataformas();
    }

    private void agregarPlataformas() {
        plataforma = cargarSprite(regionFondo.getWidth()/2, ControlJuego.ALTO_CAMARA/4, regionPlataforma);
        attachChild(plataforma);
        estaSobrePlataforma = false;
    }

    private void agregarMonedas() {
        // Agrega monedas a lo largo del mundo
        listaMonedas = new ArrayList<>(NUM_MONEDAS);
        for (int i=0; i<NUM_MONEDAS; i++) {
            float x = (float)(Math.random()*(regionFondo.getWidth()-ControlJuego.ANCHO_CAMARA))+ControlJuego.ANCHO_CAMARA/2;
            float y = (float)(Math.random()*(ControlJuego.ALTO_CAMARA-regionPersonaje.getHeight()))+regionPersonaje.getHeight()/2;
            Sprite moneda = new Sprite(x,y,regionMoneda,actividadJuego.getVertexBufferObjectManager());
            attachChild(moneda);
            listaMonedas.add(moneda);
        }
    }

    // Siempre está al frente y fija en la pantalla
    private void agregarHUD() {
        hud = new HUD();
        Sprite flechaIzq = new Sprite(100,100,regionIzquierda,actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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
                if ( pSceneTouchEvent.isActionDown()) {
                    direccion = Direccion.DERECHA;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        hud.attachChild(flechaDer);
        hud.registerTouchArea(flechaDer);

        // Marcador/valorMarcador
        txtMarcador = new Text(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA-100,
                fontMonster,"    0    ",actividadJuego.getVertexBufferObjectManager());
        hud.attachChild(txtMarcador);
        valorMarcador = 0;

        actividadJuego.camara.setHUD(hud);
    }

    // textos al inicio, mitad y final del mundo
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
        super.onManagedUpdate(pSecondsElapsed);

        if (debeSaltar) {
            debeSaltar = false;
            saltarPersonaje();
        }
        if (direccion == Direccion.IZQUIERDA
                && personaje.getX() > ControlJuego.ANCHO_CAMARA / 2) {
            personaje.setX(personaje.getX() - 5);
        }
        if (direccion == Direccion.DERECHA
                && personaje.getX() < regionFondo.getWidth() - ControlJuego.ANCHO_CAMARA/2) {
            personaje.setX(personaje.getX() + 5);
        }
        // La cámara se centra en la x del personaje
        actividadJuego.camara.setCenter(personaje.getX(), ControlJuego.ALTO_CAMARA / 2);

        // Anima los textos
        textoInicial.setRotation(textoInicial.getRotation() + 1);
        textoFinal.setRotation(textoFinal.getRotation() + 6);
        textoMedio.setRotation(textoMedio.getRotation() + 3);

        // Actualiza texto de txtMarcador
        DecimalFormat df = new DecimalFormat("##.##"); // Para formatear 2 decimales
        txtMarcador.setText(df.format(valorMarcador));

        // Actualizar monedas y verificar colision
        actualizarMonedas();

        // Verificar si cae sobre la plataforma
        verificarPlataforma();

    }

    private void verificarPlataforma() {
        if (!estaSobrePlataforma) {
            if (personaje.collidesWith(plataforma) ) {
                personaje.clearEntityModifiers();
                //estaSaltando = false;
                if (personaje.getY() > plataforma.getY()) {
                    // Chocó por arriba
                    estaSobrePlataforma = true;
                    estaSaltando = false;
                } else {
                    // Choco por abajo, regresarlo al piso
                    caer(0.1f,20);
                }
            }
        } else {
            // Está sobre la plataforma, ver que no caiga
            if ( !personaje.collidesWith(plataforma) ) {
                // debe caer
                caer(1,200);
                estaSobrePlataforma = false;
            }
        }
    }

    // Cae de la plataforma
    private void caer(float tiempo, int dx) {
        float x = personaje.getX();
        float y = personaje.getY();
        float offset = 0;

        if (direccion == Direccion.IZQUIERDA
                && personaje.getX() > ControlJuego.ANCHO_CAMARA / 2) {
            offset = Math.min(personaje.getX() - ControlJuego.ANCHO_CAMARA / 2,dx);
        }
        if (direccion == Direccion.DERECHA
                && personaje.getX() < regionFondo.getWidth() - ControlJuego.ANCHO_CAMARA/2) {
            offset = Math.min(-personaje.getX() + regionFondo.getWidth()
                    - ControlJuego.ANCHO_CAMARA/2,dx);
        }

        float x2 = direccion==Direccion.DERECHA?x+offset:x-offset;
        float y2 = ControlJuego.ALTO_CAMARA/8;
        JumpModifier salto = new JumpModifier(tiempo, x, x2, y, y2, 0){
            @Override
            protected void onModifierFinished(IEntity pItem) {
                estaSaltando = false;
                Log.i("modifier","regresó al piso");
                super.onModifierFinished(pItem);
            }
        };
        personaje.registerEntityModifier(salto);
    }

    private void saltarPersonaje() {
        if (!estaSaltando) {
            estaSaltando = true;
            float x = personaje.getX();
            float y = personaje.getY();
            float offset = 0;

            if (direccion == Direccion.IZQUIERDA
                    && personaje.getX() > ControlJuego.ANCHO_CAMARA / 2) {
                offset = Math.min(personaje.getX() - ControlJuego.ANCHO_CAMARA / 2, 300);
            }
            if (direccion == Direccion.DERECHA
                    && personaje.getX() < regionFondo.getWidth() - ControlJuego.ANCHO_CAMARA / 2) {
                offset = Math.min(-personaje.getX() + regionFondo.getWidth()
                        - ControlJuego.ANCHO_CAMARA / 2, 300);
            }

            float x2 = direccion == Direccion.DERECHA ? x + offset : x - offset;
            JumpModifier salto = new JumpModifier(2, x, x2, y, y, -650){
                @Override
                protected void onModifierFinished(IEntity pItem) {
                    estaSaltando = false;
                    super.onModifierFinished(pItem);
                }
            };
            personaje.registerEntityModifier(salto);
        }
    }

    private void actualizarMonedas() {
        for (int i=listaMonedas.size()-1; i>=0; i--) {
            final Sprite moneda = listaMonedas.get(i);
            moneda.setRotation(moneda.getRotation()+5);
            // Prueba colisión
            if (personaje.collidesWith(moneda)) {
                // Desaparecer moneda
                desaparecerMoneda(moneda);
            }
            // Salen las monedas que han desaparecido
            if (moneda.getScaleX()==0) {
                valorMarcador += 100;
                moneda.detachSelf();
                listaMonedas.remove(moneda);
            }
        }
    }

    // Recude el tamaño hasta desaparecer
    private void desaparecerMoneda(final Sprite monedaD) {
        ScaleModifier escala = new ScaleModifier(0.3f,1,0) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                unregisterEntityModifier(this);
                super.onModifierFinished(pItem);
            }
        };
        monedaD.registerEntityModifier(escala);
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
        actividadJuego.camara.setHUD(null); // Quita el HUD de la cámara
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

        regionIzquierda.getTexture().unload();
        regionIzquierda = null;

        regionDerecha.getTexture().unload();
        regionDerecha = null;

        regionMoneda.getTexture().unload();
        regionMoneda = null;
    }

}
