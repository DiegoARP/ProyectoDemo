package mx.itesm.rmroman.proyectobasegpo01;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
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
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rmroman on 15/09/15.
 */
public class  EscenaJuegoDos extends EscenaBase
{
    //Fondo
    private ITextureRegion regionFondo;

    // Sprite animado
    private AnimatedSprite spritePersonaje;
    private TiledTextureRegion regionPersonajeAnimado;
    private int ANCHO_VIDA;
    private int vida;
    private Rectangle rectVida;
    private Rectangle rectVidaActual;

    // Banderas
    private boolean personajeSaltando = false;
    private boolean juegoCorriendo = true;

    // Enemigos
    private ArrayList<Enemigo> listaEnemigos;
    private ITextureRegion regionEnemigo;

    // Tiempo para generar un disparo de los enemigos
    private float tiempoEnemigos = 0;
    private float LIMITE_TIEMPO = 1.4f;

    // Proyectiles de los enemigos
    private ArrayList<Sprite> listaProyectilesEnemigo;
    private ITextureRegion regionBolaEnemigo;   // Disparo del enemigo

    // Fin del juego
    private ITextureRegion regionFin;

    // Proyectiles del personaje
    private ITextureRegion regionProyectil;
    private ArrayList<Sprite> listaProyectiles;

    // Escena de PAUSA
    private CameraScene escenaPausa;    // La escena que se muestra al hacer pausa
    private ITextureRegion regionPausa;
    private ITextureRegion regionBtnPausa;

    // Escena FIN
    private CameraScene escenaFin;
    private  ITextureRegion regionBtnContinuar;
    private ITextureRegion regionBtnSalir;

    // Puntos
    private Text txtPuntos;
    private IFont fontMonster;
    private int puntos = 0;

    // Efectos de sonido
    private Sound sonidoDisparo;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("spaceFondo.jpg");
        regionPersonajeAnimado = cargarImagenMosaico("kiki.png", 600, 158, 1, 4);
        // Enemigos
        regionEnemigo = cargarImagen("alienblaster.png");
        regionBolaEnemigo = cargarImagen("juego/bolaAcero.png");

        regionFin = cargarImagen("fin.png");
        regionProyectil = cargarImagen("laser.png");
        // Pausa
        regionBtnPausa = cargarImagen("juego/btnPausa.png");
        regionPausa = cargarImagen("juego/pausa.png");
        // Fin del juego
        regionBtnContinuar = cargarImagen("juego/continue.png");
        regionBtnSalir = cargarImagen("juego/exit.png");
        // Puntos
        fontMonster = cargarFont("fonts/monster.ttf",64,0xFFFFFF00,"Puntos: 0123456789");

        //Efectos de sonido
        sonidoDisparo = cargarEfecto("audio/disparoA.wav");
        //sonido.setVolume(0.5f,0.5f);
    }

    @Override
    public void crearEscena() {

        listaProyectiles = new ArrayList<>();
        listaProyectilesEnemigo = new ArrayList<>();
        listaEnemigos = new ArrayList<>();

        //  Agregar el fondo animado
        agregarFondo();

        // Agregar enemigos a la escena
        crearEnemigos();

        // Crear elementos de pausa
        agregarPausa();

        // Crear elementos de fin del juego
        agregarFinJuego();

        // agregar barra de vida
        agregarVida();

        // agregarPuntos
        agregarTextoPuntos();

        // Reproduce música de fondo
        actividadJuego.reproducirMusica("audio/espacio.mp3",true);
    }

    private void agregarFinJuego() {
        // Crear la escena de FIN, pero NO lo agrega a la escena
        escenaFin = new CameraScene(actividadJuego.camara);
        //Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 2,
                //regionPausa);
        Rectangle fondoFin = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,ControlJuego.ANCHO_CAMARA, ControlJuego.ALTO_CAMARA, actividadJuego.getVertexBufferObjectManager());
        fondoFin.setColor(0xAA000000);
        escenaFin.attachChild(fondoFin);

        // Crea el botón de CONTINUE y lo agrega a la escena
        Sprite btnContinuar = new Sprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionBtnContinuar, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    reiniciarJuego();
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaFin.attachChild(btnContinuar);
        escenaFin.registerTouchArea(btnContinuar);

        // Crea el botón de SALIR y lo agrega a la escena
        Sprite btnSalir = new Sprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/4,
                regionBtnSalir, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    onBackKeyPressed();
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        //btnContinuar.setAlpha(0.4f);
        escenaFin.attachChild(btnSalir);
        escenaFin.registerTouchArea(btnSalir);

        escenaFin.setBackgroundEnabled(false);
    }

    private void reiniciarJuego() {

    }

    private void agregarFondo() {
        // Fondo animado
        AutoParallaxBackground fondoAnimado = new AutoParallaxBackground(1, 1, 1, 5);

        Sprite spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA / 2,
                ControlJuego.ALTO_CAMARA / 2, regionFondo);
        fondoAnimado.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(6, spriteFondo));

        setBackground(fondoAnimado);

        // Personaje animado
        spritePersonaje = new AnimatedSprite(ControlJuego.ANCHO_CAMARA / 8, ControlJuego.ALTO_CAMARA / 8,
                regionPersonajeAnimado, actividadJuego.getVertexBufferObjectManager());
        spritePersonaje.animate(200);
        attachChild(spritePersonaje);
    }

    private void agregarPausa() {
        // Crea el botón de PAUSA y lo agrega a la escena
        Sprite btnPausa = new Sprite(regionBtnPausa.getWidth()/2, ControlJuego.ALTO_CAMARA - regionBtnPausa.getHeight()/2,
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    pausarJuego();
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        btnPausa.setAlpha(0.4f);
        attachChild(btnPausa);
        registerTouchArea(btnPausa);

        // Crear la escena de PAUSA, pero NO lo agrega a la escena
        escenaPausa = new CameraScene(actividadJuego.camara);
        Sprite fondoPausa = cargarSprite(ControlJuego.ANCHO_CAMARA / 2, ControlJuego.ALTO_CAMARA / 2,
                regionPausa);
        escenaPausa.attachChild(fondoPausa);

        // Crea el botón de PAUSA y lo agrega a la escena
        Sprite btnContinuar = new Sprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                regionBtnPausa, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    pausarJuego();
                    return true;
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        btnContinuar.setAlpha(0.4f);
        escenaPausa.attachChild(btnContinuar);
        escenaPausa.registerTouchArea(btnContinuar);

        escenaPausa.setBackgroundEnabled(false);
    }

    private void agregarVida() {
        // Vida
        ANCHO_VIDA = ControlJuego.ANCHO_CAMARA/2;
        vida = 100; // %
        // Fondo
        rectVida = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA-50,
                ANCHO_VIDA+10,ANCHO_VIDA/8,actividadJuego.getVertexBufferObjectManager());
        rectVida.setColor(0, 0, 0, 0.4f);
        attachChild(rectVida);
        // Nivel
        rectVidaActual = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA-50,
                ANCHO_VIDA,ANCHO_VIDA/8,actividadJuego.getVertexBufferObjectManager());
        rectVidaActual.setColor(0, 1, 0);

        attachChild(rectVidaActual);
    }

    private void agregarTextoPuntos() {
        txtPuntos = new Text(ControlJuego.ANCHO_CAMARA-200,ControlJuego.ALTO_CAMARA-30,
                fontMonster,"Puntos: 0          ",actividadJuego.getVertexBufferObjectManager());
        attachChild(txtPuntos);
    }

    private void pausarJuego() {
        if (juegoCorriendo) {
            setChildScene(escenaPausa, false, true, false);
            juegoCorriendo = false;
        } else {
            clearChildScene();
            juegoCorriendo = true;
        }
    }

    private void terminarJuego() {
        // Mostrar la pantalla de fin
        setChildScene(escenaFin, false, true, false);
        juegoCorriendo = false;
    }

    private void crearEnemigos() {
        for (int x = 700; x <= 1200; x += 100) {
            for (int y = 100; y <= 700; y += 100) {
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
        actualizarProyectilesEnemigo();
        actualizarVida();
        actualizarPuntos();

        if (vida<=0) {
            // PIERDE!!!
            terminarJuego();
        }

        if (listaEnemigos.size()==0) {
            // GANA!!!
        }
    }

    private void actualizarPuntos() {
        txtPuntos.setText("Puntos: " + puntos);
    }

    private void actualizarVida() {
        // Dibuja el rectángulo proporcional a la vida
        rectVidaActual.setWidth(ControlJuego.ANCHO_CAMARA / 2 * vida / 100);
        rectVidaActual.setColor(1-vida/100.0f,vida/100.0f,0);
    }

    private void actualizarProyectilesEnemigo() {
        // Se visita cada proyectil dentro de la lista, se recorre con el índice
        // porque se pueden borrar datos
        for (int i = listaProyectilesEnemigo.size() - 1; i >= 0; i--) {
            Sprite proyectil = listaProyectilesEnemigo.get(i);
            proyectil.setX(proyectil.getX() - 20);
            if (proyectil.getX() < 0) {
                detachChild(proyectil);
                listaProyectilesEnemigo.remove(proyectil);
                continue;
            }
            if (proyectil.collidesWith(spritePersonaje)) {
                // Baja puntos/vida
                vida -= 5;
                detachChild(proyectil);
                listaProyectilesEnemigo.remove(proyectil);
            }
        }
    }

    private void actualizarProyectiles(float tiempo) {

        // Se visita cada proyectil dentro de la lista, se recorre con el índice
        // porque se pueden borrar datos
        for (int i = listaProyectiles.size() - 1; i>=0; i--) {
            Sprite proyectil = listaProyectiles.get(i);
            proyectil.setX(proyectil.getX() + 10);
            if (proyectil.getX() > ControlJuego.ANCHO_CAMARA) {
                detachChild(proyectil);
                listaProyectiles.remove(proyectil);
                continue;
            }
            // Probar si colisionó con un enemigo
            // Se visita cada enemigo dentro de la lista, se recorre con el índice
            // porque se pueden borrar datos
            for (int k = listaEnemigos.size() - 1; k >= 0; k--) {
                Enemigo enemigo = listaEnemigos.get(k);
                if (proyectil.collidesWith(enemigo.getSprite())) {
                    // Lo destruye
                    detachChild(enemigo.getSprite());
                    listaEnemigos.remove(enemigo);
                    // desaparece el proyectil
                    detachChild(proyectil);
                    listaProyectiles.remove(proyectil);
                    // Aumenta los puntos
                    puntos += 100;
                    break;
                }
            }
        }
    }

    // Ahora se mueven hacia arriba-abajo
    private int DX_ENEMIGOS = 1;
    private void actualizarEnemigos(float tiempo) {
        boolean yaSalio = false;
        for (Enemigo enemigo : listaEnemigos) { // Visita cada enemigo de la lista
            enemigo.mover(0, DX_ENEMIGOS);
            // Pregunta si algún enemigo se salió de la pantalla
            if (!yaSalio && enemigo.getSprite().getY()>ControlJuego.ALTO_CAMARA || enemigo.getSprite().getY()<0) {
                yaSalio = true;
            } else {
                // Genera un disparo al azar
                double valor = Math.random();
                if (valor<0.004 || (listaEnemigos.size()<10 && valor<0.01) )  {
                    // Dispara
                    dispararEnemigo(enemigo);
                }
            }
        }
        if (yaSalio) {
            DX_ENEMIGOS *= -1;
        }
    }

    private void dispararEnemigo(Enemigo enemigo) {
        // Crearlo
        Sprite spriteProyectil = cargarSprite(enemigo.getSprite().getX(),
                enemigo.getSprite().getY(), regionBolaEnemigo);
        attachChild(spriteProyectil);   // Lo agrega a la escena
        listaProyectilesEnemigo.add(spriteProyectil);  // Lo agrega a la lista
    }

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {

        if (juegoCorriendo) { // No está en pausa
            if (pSceneTouchEvent.isActionDown() && !personajeSaltando && // es actionDown y no está saltando (para no saltar en el aire)
                    pSceneTouchEvent.getX() < ControlJuego.ANCHO_CAMARA / 2 // La mitad izquierda de la pantalla
                    && pSceneTouchEvent.getY() < ControlJuego.ALTO_CAMARA - regionBtnPausa.getWidth()) { // Debajo del botón de pausa

                personajeSaltando = true;   // Avisa que está saltando para no saltar en el aire
                // Animar sprite central
                JumpModifier salto = new JumpModifier(2, spritePersonaje.getX(), spritePersonaje.getX(),
                        spritePersonaje.getY(), spritePersonaje.getY(), -6 * ControlJuego.ALTO_CAMARA / 8);
                RotationModifier rotacion = new RotationModifier(2, 360, 0);
                ParallelEntityModifier paralelo = new ParallelEntityModifier(salto, rotacion) { // dos modificadores en paralelo, (saltar y rotar)
                    @Override
                    protected void onModifierFinished(IEntity pItem) {  // Cuando termina el salto
                        personajeSaltando = false;
                        unregisterEntityModifier(this);
                        super.onModifierFinished(pItem);
                    }
                };
                spritePersonaje.registerEntityModifier(paralelo);   // Ejecuta los modificadores en paralelo
            } else if (pSceneTouchEvent.isActionDown() &&
                    pSceneTouchEvent.getX() > ControlJuego.ANCHO_CAMARA / 2) {  // lado derecho de la pantalla
                // Solo 5 proyectiles en panatalla
                if (listaProyectiles.size() < 5) {
                    dispararProyectil();
                }
            }
        }

        return super.onSceneTouchEvent(pSceneTouchEvent);
    }

    private void dispararProyectil() {
        // Crearlo
        sonidoDisparo.play();   // Reproduce efecto de disparo
        Sprite spriteProyectil = cargarSprite(spritePersonaje.getX(), spritePersonaje.getY(), regionProyectil);
        attachChild(spriteProyectil);   // Lo agrega a la escena
        listaProyectiles.add(spriteProyectil);  // Lo agrega a la lista
    }

    @Override
    public void onBackKeyPressed() {

        guardarMarcadorAlto();

        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaJuego();
    }

    private void guardarMarcadorAlto() {
        // Abre preferencias y ve si el marcador actual es mayor que el guardado
        SharedPreferences preferencias = actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
        int anterior = preferencias.getInt("puntos",0);
        if (puntos>anterior) {
            // Nuevo valor mayor, guardarlo
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putInt("puntos",puntos);
            editor.commit();
        }
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

        regionFondo.getTexture().unload();
        regionFondo = null;

        regionProyectil.getTexture().unload();
        regionProyectil = null;

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
