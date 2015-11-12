package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;

/**
 * Representa la escena del MENU PRINCIPAL
 *
 * @author Roberto Martínez Román
 */
public class EscenaMenu extends EscenaBase
{
    // Regiones para las imágenes de la escena
    private ITextureRegion regionFondo;
    private ITextureRegion regionBtnAcercaDe;   // Botones del menú
    private ITextureRegion regionBtnJugar;

    // Sprites sobre la escena
    private Sprite spriteFondo;

    // Un menú de tipo MenuScene
    private MenuScene menu;

    // Constantes para cada opción
    private final int OPCION_ACERCA_DE = 0;
    private final int OPCION_JUGAR = 1;

    // SpriteButton con estado ON/OFF
    private ButtonSprite btnEstado;
    private ITiledTextureRegion regionBtnEstado; // Imagen de mosaico con dos estados: normal-prendido

    @Override
    public void cargarRecursos() {
        // Fondo
        regionFondo = cargarImagen("fondoMenu2.png");
        // Botones del menú
        regionBtnAcercaDe = cargarImagen("btnAcercaDe.png");
        regionBtnJugar = cargarImagen("btnJugar.png");
        regionBtnEstado = cargarImagenMosaico("onoff.png",300,225,1,2);
    }

    @Override
    public void crearEscena() {
        // Creamos el sprite de fondo
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, regionFondo);

        // Crea el fondo de la pantalla
        SpriteBackground fondo = new SpriteBackground(1,1,1,spriteFondo);
        setBackground(fondo);
        setBackgroundEnabled(true);

        // Armar y agregar el menú
        agregarMenu();

        // Agregar botón de estado (prendido-apagado)
        agregarEstado();
    }

    private void agregarEstado() {

        // Toggle button
        btnEstado = new ButtonSprite(ControlJuego.ANCHO_CAMARA-200, ControlJuego.ALTO_CAMARA-200,
                regionBtnEstado,actividadJuego.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {
                    // Cambia el índice entre 0 y 1 ed manera alternada
                    btnEstado.setCurrentTileIndex((btnEstado.getCurrentTileIndex()+1)%2);
                }
                // 0-NORMAL, 1-PRESIONADO
                Log.i("Estado del botón", "" + btnEstado.getCurrentTileIndex());
                return false; // Regresa falso para que Android no cambie el botón
            }
        };
        // El estado inicial del botón se lee desde las preferencias o se toma un valor por default
        // en este demo, siempre inicia prendido
        btnEstado.setCurrentTileIndex(1);
        registerTouchArea(btnEstado);
        attachChild(btnEstado);
    }

    private void agregarFondoMenu() {
        Rectangle cuadro = new Rectangle(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2,
                0.75f*ControlJuego.ANCHO_CAMARA, 0.75f*ControlJuego.ALTO_CAMARA, actividadJuego.getVertexBufferObjectManager());
        cuadro.setColor(0.8f, 0.8f, 0.8f, 0.4f);
        attachChild(cuadro);
    }

    private void agregarMenu() {
        // Crea el objeto que representa el menú
        menu = new MenuScene(actividadJuego.camara);
        // Centrado en la pantalla
        menu.setPosition(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/2);
        // Crea las opciones (por ahora, acerca de y jugar) 1.5f escala seleccionado, 1 escala normal
        IMenuItem opcionAcercaDe = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_ACERCA_DE,
                regionBtnAcercaDe, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);
        IMenuItem opcionJugar = new ScaleMenuItemDecorator(new SpriteMenuItem(OPCION_JUGAR,
                regionBtnJugar, actividadJuego.getVertexBufferObjectManager()), 1.5f, 1);

        // Agrega las opciones al menú
        menu.addMenuItem(opcionAcercaDe);
        menu.addMenuItem(opcionJugar);

        // Termina la configuración
        menu.buildAnimations();
        menu.setBackgroundEnabled(false);   // Completamente transparente

        // Ubicar las opciones DENTRO del menú. El centro del menú es (0,0)
        opcionAcercaDe.setPosition(-200, 0);
        opcionJugar.setPosition(200, 0);

        // Registra el listener para atender cada opción del menú
        menu.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
                                             float pMenuItemLocalX, float pMenuItemLocalY) {
                // El parámetro pMenuItem indica la opción que se oprimió
                switch(pMenuItem.getID()) {
                    case OPCION_ACERCA_DE:
                        // Mostrar la escena AcercaDe
                        admEscenas.crearEscenaAcercaDe();
                        admEscenas.setEscena(TipoEscena.ESCENA_ACERCA_DE);
                        admEscenas.liberarEscenaMenu();
                        break;

                    case OPCION_JUGAR:

                        // Escena de espera
                        admEscenas.crearEscenaCargando();
                        admEscenas.setEscena(TipoEscena.ESCENA_CARGANDO);
                        admEscenas.liberarEscenaMenu();

                        /*
                        // Mostrar escena scroll
                        admEscenas.crearEscenaScroll();
                        admEscenas.setEscena(TipoEscena.ESCENA_SCROLL);
                        admEscenas.liberarEscenaMenu();
                        */

                        // Mostrar la pantalla de juego
                        // JUEGO DOS
                        /*
                        admEscenas.crearEscenaJuego();
                        admEscenas.setEscena(TipoEscena.ESCENA_JUEGO);
                        admEscenas.liberarEscenaMenu();
                        */

                        /*admEscenas.crearEscenaHistoria();
                        admEscenas.setEscena(TipoEscena.ESCENA_HISTORIA);
                        admEscenas.liberarEscenaMenu();*/

                        break;
                }
                return true;
            }
        });

        // Agrega este menú a la escena
        setChildScene(menu);
    }

    // La escena se debe actualizar en este método que se repite "varias" veces por segundo
    // Aquí es donde programan TODA la acción de la escena (movimientos, choques, disparos, etc.)
    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

    }


    @Override
    public void onBackKeyPressed() {
        // Salir del juego, no hacemos nada
    }

    // Indice el tipo de escena que estamos implementando
    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_MENU;
    }

    // Libera los recursos asignados.
    @Override
    public void liberarEscena() {
        this.detachSelf();      // La escena se deconecta del engine
        this.dispose();         // Libera la memoria
        liberarRecursos();
    }

    @Override
    public void liberarRecursos() {
        regionFondo.getTexture().unload();
        regionFondo = null;
        regionBtnAcercaDe.getTexture().unload();
        regionBtnAcercaDe = null;
        regionBtnJugar.getTexture().unload();
        regionBtnJugar = null;
    }
}
