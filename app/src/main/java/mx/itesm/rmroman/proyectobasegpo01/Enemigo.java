package mx.itesm.rmroman.proyectobasegpo01;

import org.andengine.entity.sprite.Sprite;

/**
 * Created by rmroman on 23/09/15.
 */
public class Enemigo
{
    private Sprite sprite;

    public Enemigo(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void mover(int dx, int dy) {
        sprite.setPosition( sprite.getX()+dx, sprite.getY()+dy );
    }
}
