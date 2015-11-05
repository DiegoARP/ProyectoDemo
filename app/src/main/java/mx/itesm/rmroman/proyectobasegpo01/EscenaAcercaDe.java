package mx.itesm.rmroman.proyectobasegpo01;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;

import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * Created by rmroman on 11/09/15.
 */
public class EscenaAcercaDe extends EscenaBase
{
    // Regiones para imágenes
    private ITextureRegion regionFondo;
    // Sprite para el fondo
    private Sprite spriteFondo;

    // Sistema de partículas
    private ITextureRegion regionBurbuja;
    private ITextureRegion regionHumo;

    // Marcador alto
    private Text txtMarcador;
    private IFont fontMonster;

    @Override
    public void cargarRecursos() {
        regionFondo = cargarImagen("acercaDe/fondoAbout.png");
        regionBurbuja = cargarImagen("acercaDe/burbuja.png");
        regionHumo = cargarImagen("acercaDe/humo.png");
        // Marcador
        fontMonster = cargarFont("fonts/famirids.ttf",80,0xFF003366,"Marcdo my:0123456789");
    }

    @Override
    public void crearEscena() {
        spriteFondo = cargarSprite(ControlJuego.ANCHO_CAMARA/2, ControlJuego.ALTO_CAMARA/2, regionFondo);
        attachChild(spriteFondo);

        agregarMarcadorAlto();
        agregarBurbujas();
        agregarFuego();
        agregarHumo();

        actividadJuego.reproducirMusica("audio/acerca.mp3",true);
    }

    private void agregarMarcadorAlto() {
        // Obtener de las preferencias el marcador mayor
        SharedPreferences preferencias = actividadJuego.getSharedPreferences("marcadorAlto", Context.MODE_PRIVATE);
        int puntos = preferencias.getInt("puntos",0);

        txtMarcador = new Text(ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA-80,
                fontMonster,"Marcador mayor: "+puntos,actividadJuego.getVertexBufferObjectManager());
        attachChild(txtMarcador);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);

        spriteFondo.setSkew(0.5f, 0.1f);

    }

    @Override
    public void onBackKeyPressed() {

        actividadJuego.detenerMusica();

        // Regresar al menú principal
        admEscenas.crearEscenaMenu();
        admEscenas.setEscena(TipoEscena.ESCENA_MENU);
        admEscenas.liberarEscenaAcercaDe();
    }

    @Override
    public TipoEscena getTipoEscena() {
        return TipoEscena.ESCENA_ACERCA_DE;
    }

    @Override
    public void liberarEscena() {
        this.detachSelf();
        this.dispose();
    }

    @Override
    public void liberarRecursos() {
        regionFondo.getTexture().unload();
        regionFondo = null;

        regionBurbuja.getTexture().unload();
        regionBurbuja = null;

        regionHumo.getTexture().unload();
        regionHumo = null;
    }

    private void agregarHumo() {

        CircleParticleEmitter circulo = new CircleParticleEmitter(ControlJuego.ANCHO_CAMARA,
                ControlJuego.ALTO_CAMARA/2,10);
        BatchedSpriteParticleSystem sistema = new BatchedSpriteParticleSystem(circulo,
                20, 30, 200, regionHumo, actividadJuego.getVertexBufferObjectManager());

        // Velocidad de las partículas minX, maxX, minY, maxY
        sistema.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-50,-20,-30,30));
        // Aceleración
        sistema.addParticleInitializer(new AccelerationParticleInitializer<UncoloredSprite>(-20,0));

        float tiempoVida = 12;   // Segundos de vida de cada partícula
        // Tiempo para que las partículas expiren.
        sistema.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(tiempoVida));
        // Escala
        sistema.addParticleInitializer(new ScaleParticleInitializer<UncoloredSprite>(0.5f,1.5f));
        // Rotación
        sistema.addParticleModifier(new RotationParticleModifier<UncoloredSprite>(1,4,0,360));
        // Alpha de las partículas, recibe el rango de tiempo y el rango de alpha
        sistema.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(tiempoVida-2,tiempoVida+1,1,0.3f));

        // Se agrega a la escena, como cualquier Sprite
        attachChild(sistema);
    }

    private void agregarFuego() {

        IEntityFactory<Sprite> ief = new IEntityFactory<Sprite>() {
            @Override
            public Sprite create(float pX, float pY) {
                return new Sprite(pX,pY,regionHumo,actividadJuego.getVertexBufferObjectManager());
            }
        };

        PointParticleEmitter punto = new PointParticleEmitter(5*ControlJuego.ANCHO_CAMARA/8,
                0);
        final ParticleSystem<Sprite> sistema = new ParticleSystem<Sprite>(ief,punto,20,50,200);

        sistema.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(
                GLES20.GL_SRC_ALPHA,GLES20.GL_ONE));

        sistema.addParticleInitializer(new ColorParticleInitializer<Sprite>(1,0.5f,0));
        sistema.addParticleInitializer(new AlphaParticleInitializer<Sprite>(0));
        sistema.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-15,15,20,90));
        float tiempoVida = 4.5f;   // Segundos de vida de cada partícula
        sistema.addParticleInitializer(new ExpireParticleInitializer<Sprite>(tiempoVida));
        sistema.addParticleInitializer(new ScaleParticleInitializer<Sprite>(0.5f));
        sistema.addParticleInitializer(new RotationParticleInitializer<Sprite>(0, 360));
        sistema.addParticleModifier(new AlphaParticleModifier<Sprite>(0,0.5f,0,0.2f));
        sistema.addParticleModifier(new AlphaParticleModifier<Sprite>(tiempoVida-2,tiempoVida+1,0.2f,0f));
        sistema.addParticleModifier(new ScaleParticleModifier<Sprite>(tiempoVida-2,tiempoVida+1,0.5f,0f));
        sistema.addParticleModifier(new RotationParticleModifier<Sprite>(1,3,0,360));

        // Se agrega a la escena, como cualquier Sprite
        attachChild(sistema);
    }


    private void agregarBurbujas() {

        // El rectángulo de emisión, en esta zona se generan las burbujas
        // El constructor recibe x,y,ancho,alto
        RectangleParticleEmitter zonaEmision = new RectangleParticleEmitter(
                ControlJuego.ANCHO_CAMARA/4,0,
                ControlJuego.ANCHO_CAMARA/2,ControlJuego.ALTO_CAMARA/8);

        // El sistema de partículas, se construye con zona de emisión, rango de partículas emitidas por segundo,
        // máximo número de partículas en la pantalla, región de la imagen y el vertexBuffer
        BatchedSpriteParticleSystem sistema = new BatchedSpriteParticleSystem(
                zonaEmision, 2, 9, 200, regionBurbuja, actividadJuego.getVertexBufferObjectManager() );

        // Velocidad de las partículas minX, maxX, minY, maxY
        sistema.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-40,40,50,100));

        float tiempoVida = 12;   // Segundos de vida de cada partícula
        // Tiempo para que las partículas expiren.
        sistema.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(tiempoVida));

        // Alpha de las partículas, recibe el rango de tiempo y el rango de alpha
        sistema.addParticleModifier(new AlphaParticleModifier<UncoloredSprite>(tiempoVida-2,tiempoVida+1,1,0));

        // Se agrega a la escena, como cualquier Sprite
        attachChild(sistema);
    }
}
