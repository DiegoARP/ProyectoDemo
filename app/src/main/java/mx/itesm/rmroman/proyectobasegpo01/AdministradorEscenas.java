package mx.itesm.rmroman.proyectobasegpo01;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Administra la escena que se verá en la pantalla
 */
public class AdministradorEscenas
{
    // Instancia única
    private static final AdministradorEscenas INSTANCE =
            new AdministradorEscenas();
    protected ControlJuego actividadJuego;

    // Declara las distintas escenas que forman el juego
    private EscenaBase escenaSplash;
    private EscenaBase escenaMenu;
    private EscenaBase escenaAcercaDe;
    private EscenaBase escenaJuego;
    private EscenaBase escenaHistoria;
    private EscenaBase escenaScroll;
    private EscenaBase escenaCargando;

    // El tipo de escena que se está mostrando
    private TipoEscena tipoEscenaActual = TipoEscena.ESCENA_SPLASH;
    // La escena que se está mostrando
    private EscenaBase escenaActual;
    // El engine para hacer el cambio de escenas
    private Engine engine;

    // Asigna valores iniciales del administrador
    public static void inicializarAdministrador(ControlJuego actividadJuego, Engine engine) {
        getInstance().actividadJuego = actividadJuego;
        getInstance().engine = engine;
    }

    // Regresa la instancia del administrador de escenas
    public static AdministradorEscenas getInstance() {
        return INSTANCE;
    }

    // Regresa el tipo de la escena actual
    public TipoEscena getTipoEscenaActual() {
        return tipoEscenaActual;
    }

    // Regresa la escena actual
    public EscenaBase getEscenaActual() {
        return escenaActual;
    }

    /*
     * Pone en la pantalla la escena que llega como parámetro y guarda el nuevo estado
     */
    private void setEscenaBase(EscenaBase nueva) {
        engine.setScene(nueva);
        escenaActual = nueva;
        tipoEscenaActual = nueva.getTipoEscena();
    }

    /**
     * Cambia a la escena especificada en el parámetro
     * @param nuevoTipo la nueva escena que se quiere mostrar
     */
    public void setEscena(TipoEscena nuevoTipo) {
        switch (nuevoTipo) {
            case ESCENA_SPLASH:
                setEscenaBase(escenaSplash);
                break;
            case ESCENA_MENU:
                setEscenaBase(escenaMenu);
                break;
            case ESCENA_ACERCA_DE:
                setEscenaBase(escenaAcercaDe);
                break;
            case ESCENA_JUEGO:
                setEscenaBase(escenaJuego);
                break;
            case ESCENA_HISTORIA:
                setEscenaBase(escenaHistoria);
                break;
            case ESCENA_SCROLL:
                setEscenaBase(escenaScroll);
                break;
            case ESCENA_CARGANDO:
                setEscenaBase(escenaCargando);
                break;
        }
    }

    //*** Crea la escena de Splash
    public void crearEscenaSplash() {
        // Carga los recursos
        escenaSplash = new EscenaSplash();
    }

    //*** Libera la escena de Splash
    public void liberarEscenaSplash() {
        escenaSplash.liberarEscena();
        escenaSplash = null;
    }

    // ** MENU
    //*** Crea la escena de MENU
    public void crearEscenaMenu() {
        // Carga los recursos
        escenaMenu = new EscenaMenu();
    }

    //*** Libera la escena de MENU
    public void liberarEscenaMenu() {
        escenaMenu.liberarEscena();
        escenaMenu = null;
    }

    //*** Crea la escena de Acerca De
    public void crearEscenaAcercaDe() {
        // Carga los recursos
        escenaAcercaDe = new EscenaAcercaDe();
    }

    //*** Libera la escena de AcercDe
    public void liberarEscenaAcercaDe() {
        escenaAcercaDe.liberarEscena();
        escenaAcercaDe = null;
    }

    //*** Crea la escena de JUEGO
    public void crearEscenaJuego() {
        // Carga los recursos
        escenaJuego = new EscenaJuegoDos();
    }

    //*** Libera la escena de JUEGO
    public void liberarEscenaJuego() {
        escenaJuego.liberarEscena();
        escenaJuego = null;
    }

    //*** Crea la escena de HISTORIA
    public void crearEscenaHistoria() {
        // Carga los recursos
        escenaHistoria = new EscenaHistoria();
    }

    //*** Libera la escena de JUEGO
    public void liberarEscenaHistoria() {
        escenaHistoria.liberarEscena();
        escenaHistoria = null;
    }

    public void crearEscenaScroll() {
        escenaScroll = new EscenaScrollHorizontal();
    }

    public void liberarEscenaScroll() {
        escenaScroll.liberarEscena();
        escenaScroll = null;
    }

    //*** Crea la escena de cargando
    public void crearEscenaCargando() {
        // Carga los recursos
        escenaCargando = new EscenaCargando();
    }

    //*** Libera la escena de cargando
    public void liberarEscenaCargando() {
        escenaCargando.liberarEscena();
        escenaCargando = null;
    }
}
