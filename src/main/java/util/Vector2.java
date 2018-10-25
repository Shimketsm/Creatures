package util;



public class Vector2 {
    
    private float x,y;
    
    public Vector2(float x, float y) {
        setX(x);
        setY(y);
    }
    
    public Vector2 scale(float scalar) {
        x*=scalar;
        y*=scalar;
        return this;
    }
    
    public Vector2 add(Vector2 toAdd) {
        x+=toAdd.x;
        y+=toAdd.y;
        return this;
    }
    
    public Vector2 sub(Vector2 toSub) {
        x-=toSub.x;
        y-=toSub.y;
        return this;
    }
    
    public Vector2 mul(Vector2 toMul) {
        x*=toMul.x;
        y*=toMul.y;
        return this;
    }
    
    public Vector2 mul(float scalar) {
        return scale(scalar);
    }
    
    public float dot(Vector2 toDot) {
        return x*toDot.x + y*toDot.y;
    }
    
    public float angle(Vector2 toAngle) {
        return (float) Math.toDegrees(Math.acos((x*toAngle.x + y*toAngle.y)/(getMagnitude()*toAngle.getMagnitude())));
    }
    
    public Vector2 normalize() {
        float le = getMagnitude();
        x/=le;
        y/=le;
        return this;
    }
    
    public float getSqrMagnitude() {
        return (x*x) + (y*y);
    }
    
    public float getMagnitude() {
        // a^2 + b^2 = c^2
        
        return (float) Math.sqrt((x*x) + (y*y));
    }
    
    public Vector2 set(Vector2 toSet) {
        return set(toSet.x, toSet.y);
    }
    
    public Vector2 set(float xt, float yt) {
        x = xt;
        y = yt;
        return this;
    }
    
    public Vector2 copy() {
        return new Vector2(x, y);
    }
    
    public void copy(Vector2 into) {
        into.set(this);
    }

    public final void setX(float x) {
        this.x = x;
    }

    public final void setY(float y) {
        this.y = y;
    }
    
    public final float getX() {
        return x;
    }
    
    public final float getY() {
        return y;
    }
}
