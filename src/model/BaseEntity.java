package model;

/**
 * Abstract root for all persistent entities.
 * INHERITANCE: every model class extends this.
 */
public abstract class BaseEntity {
    protected int id;

    public int  getId()        { return id; }
    public void setId(int id)  { this.id = id; }

    /** POLYMORPHISM: each subclass returns its own meaningful display string. */
    public abstract String getDisplayLabel();

    @Override
    public String toString() { return getDisplayLabel(); }
}
