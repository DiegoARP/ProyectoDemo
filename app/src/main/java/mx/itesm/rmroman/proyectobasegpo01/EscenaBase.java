package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.util.GLState;

import java.io.IOException;

/**
 * Define el comportamiento de las ESCENAS.
 * Cada escena del juego DEBE heredar de esta clase y sobrescribir los métodos:
 *
 * @author Roberto Martínez Román
 */
public abstract class EscenaBase extends Scene
{
    // Variable protegida para que se pueda acceder desde las subclases
    // Administrador de escenas
    protected AdministradorEscenas admEscenas;
    // Actividad principal del juego
    protected ControlJuego actividadJuego;

    public EscenaBase() {
        admEscenas = AdministradorEscenas.getInstance();
        // Llama al método que crea la escena
        this.actividadJuego = admEscenas.actividadJuego;
        cargarRecursos(); // Este método debe implementarse en la subclase
        crearEscena();  // Este método debe implementarse en la subclase
    }

    // Método auxiliar para cargar las imágenes de las regiones
    protected ITextureRegion cargarImagen(String archivo) {

        ITextureRegion region = null;
        try {
            ITexture textura = new AssetBitmapTexture(
                    actividadJuego.getTextureManager(), actividadJuego.getAssets(),archivo);
            textura.load();
            region = TextureRegionFactory.
                    extractFromTexture(textura);
        } catch (IOException e) {
            Log.i("cargarImagen()", "No se puede cargar: " + archivo);
        }
        return region;
    }

    // Método auxiliar para cargar las imágenes de las regiones de tipo MOSAICO
    protected TiledTextureRegion cargarImagenMosaico(String archivo, int ancho, int alto, int renglones, int columnas) {
        // Carga las imágenes para el sprite Animado
        BuildableBitmapTextureAtlas texturaMosaico = new BuildableBitmapTextureAtlas(actividadJuego.getTextureManager(),ancho,alto);
        TiledTextureRegion region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                texturaMosaico, actividadJuego, archivo, columnas, renglones);
        texturaMosaico.load();
        try {
            texturaMosaico.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Log.d("cargarImagenMosaico()", "No se puede cargar la imagen: " + archivo);
        }

        return region;
    }

    // Método auxiliar para crear un Sprite.
    protected Sprite cargarSprite(float px, float py, final ITextureRegion regionFondo) {
        // Crea y regresa el Sprite
        return new Sprite(px, py, regionFondo, actividadJuego.getVertexBufferObjectManager()) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) { // Optimizando
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };
    }

    // ***** Cargar un font, disponible para todas las escenas

    // Crea y regresa un font que carga desde un archivo .ttf  (http://www.1001freefonts.com, http://www.1001fonts.com/)
    protected Font cargarFont(String archivo, int tamanio, int color, String letras) {
        // La imagen que contiene cada símbolo
        final ITexture fontTexture = new BitmapTextureAtlas(actividadJuego.getEngine().getTextureManager(),512,256);
        // Carga el archivo, tamaño 56, antialias y color
        Font tipoLetra = FontFactory.createFromAsset(actividadJuego.getEngine().getFontManager(),
                fontTexture, actividadJuego.getAssets(), archivo, tamanio, true, color);
        tipoLetra.load();
        tipoLetra.prepareLetters(letras.toCharArray());

        return tipoLetra;
    }

    // Métodos abstractos
    public abstract void cargarRecursos();  // Carga imágenes/audio/música/videos/etc.
    public abstract void crearEscena(); // Arma la escena
    public abstract void onBackKeyPressed(); // Atiende el botón de back
    public abstract TipoEscena getTipoEscena(); // Regresa el tipo de escena
    public abstract void liberarEscena();   // Libera los elementos de la escena
    public abstract void liberarRecursos(); // Libera imágenes/audio/música/videos/etc.
}

