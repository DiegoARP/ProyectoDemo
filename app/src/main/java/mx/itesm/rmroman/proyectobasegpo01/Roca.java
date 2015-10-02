package mx.itesm.rmroman.proyectobasegpo01;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by rmroman on 11/03/15.
 */
public class Roca
{
    private Sprite spriteRoca;

    public Roca(float x, float y, ITextureRegion regionPiedra, VertexBufferObjectManager vbom) {
        spriteRoca = new Sprite(x,y,regionPiedra,vbom);
    }

    public void mover(float dx, float dy) {
        spriteRoca.setPosition(spriteRoca.getX()+dx, spriteRoca.getY()+dy);
    }

    public Sprite getSpriteRoca() {
        return spriteRoca;
    }

    public void girar(int grados) {
        spriteRoca.setRotation(spriteRoca.getRotation()+grados);
    }
}
