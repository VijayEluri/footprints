package org.realityforge.imit;

public final class EntityChangeEvent
{
  public static enum Type
  {
    ATTRIBUTE_CHANGED, RELATED_ADDED, RELATED_REMOVED, OBJECT_DELETED
  }

  private final Type _type;
  private final Object _object;
  private final String _name;
  private final Object _value;

  public EntityChangeEvent( final Type type,
                            final Object object,
                            final String name,
                            final Object value )
  {
    _type = type;
    _object = object;
    _name = name;
    _value = value;
  }

  public final Type getType()
  {
    return _type;
  }

  public final Object getObject()
  {
    return _object;
  }

  public final String getName()
  {
    return _name;
  }

  public final Object getValue()
  {
    return _value;
  }

  public final String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append( "EntityChange[type=" );
    sb.append( getType().name() );
    sb.append( ",name=" );
    sb.append( getName() );
    sb.append( ",value=" );
    sb.append( getValue() );
    sb.append( ",object=" );
    sb.append( getObject() );
    sb.append( "]" );
    return sb.toString();
  }
}
