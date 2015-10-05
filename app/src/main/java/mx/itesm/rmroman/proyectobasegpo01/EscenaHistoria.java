package mx.itesm.rmroman.proyectobasegpo01;

import android.util.Log;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import java.util.ArrayList;

/**
 * Created by rmroman on 28/09/15.
 */
public class EscenaHistoria extends EscenaBase
{
    // Imágenes de la historia
    private ArrayList<ITextureRegion> listaRegiones;
    private ArrayList<Sprite> listaSprites;
    private int indiceActual = 0;

    private static final String NOMBRE_ARCHIVO = "historia/historia_0";
    private static final int NUMERO_IMAGENES = 3;

    // Botones de next y previous
    private ButtonSprite btnNext;
    private ITiledTextureRegion regionBtnNext;

    @Override
    public void cargarRecursos() {

        listaSprites = new ArrayList<>();
        listaRegiones = new ArrayList<>();

        // Cargar regiones
        for (int i=1; i<=NUMERO_IMAGENES; i++) {
            listaRegiones.add(cargarImagen(NOMBRE_ARCHIVO + i + ".jpg"));
        }
        // Cargar sprites
        for (int i=1; i<=NUMERO_IMAGENES; i++) {
            listaSprites.add( cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, listaRegiones.get(i-1)) );
        }

        regionBtnNext = cargarImagenMosaico("historia/next.png",600,228,1,2);

        btnNext = new ButtonSprite(ControlJuego.ANCHO_CAMARA- regionBtnNext.getWidth(),
                regionBtnNext.getHeight(), regionBtnNext,
                actividadJuego.getVertexBufferObjectManager()){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if ( pSceneTouchEvent.isActionUp() ) {
                    cambiarSiguiente();
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        registerTouchArea(btnNext);

    }

    // Muestra siguiente imagen de la historia
    private void cambiarSiguiente() {

        if (indiceActual<NUMERO_IMAGENES-1) {
            final Sprite actual = listaSprites.get(indiceActual);
            indiceActual++;
            final Sprite nuevo = listaSprites.get(indiceActual);
            nuevo.setAlpha(0);
            attachChild(nuevo);


            // intercambiar el actual y el nuevo
            AlphaModifier ocultar = new AlphaModifier(1, 1, 0) {
                @Override
                protected void onModifierFinished(IEntity pItem) {
                    super.onModifierFinished(pItem);

                    AlphaModifier mostrar = new AlphaModifier(1, 0, 1) {
                        @Override
                        protected void onModifierFinished(IEntity pItem) {
                            super.onModifierFinished(pItem);
                            detachChild(actual);
                            unregisterEntityModifier(this);
                            btnNext.setZIndex(1);
                            sortChildren();
                        }
                    };

                    nuevo.registerEntityModifier(mostrar);
                    unregisterEntityModifier(this);

                }
            };


            actual.registerEntityModifier(ocultar);
        } else {
            // Fin de la historia
        }
    }

    @Override
    public void crearEscena() {

        setBackground(new Background(0.5f,0.5f,0.5f));
        indiceActual=0;
        attachChild(listaSprites.get(indiceActual));
        attachChild(btnNext);
    }

    @Override
    public void onBackKeyPressed() {
        //indiceActual++;
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_HISTORIA;
    }

    @Override
    public void liberarEscena() {
        detachSelf();
        dispose();
    }

    @Override
    public void liberarRecursos() {
        for (int i=0; i<listaSprites.size(); i++) {
            listaSprites.get(i).getTextureRegion().getTexture().unload();
        }
        listaRegiones.clear();
    }
}
