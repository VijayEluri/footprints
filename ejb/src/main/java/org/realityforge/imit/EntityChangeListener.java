package org.realityforge.imit;


public interface EntityChangeListener
{
  void entityDeleted( EntityChangeEvent event );

  void attributeChanged( EntityChangeEvent event );

  void relatedAdded( EntityChangeEvent event );

  void relatedRemoved( EntityChangeEvent event );
}
