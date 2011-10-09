package footprints.javancss.imit;

import footprints.javancss.imit.EntityChangeEvent.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings( { "JavaDoc" } )
public final class EntityChangeBroker
    implements EntityChangeListener
{
  private static final Logger LOG = Logger.getLogger( EntityChangeBroker.class.getName() );

  private static final EntityChangeListener[] EMPTY_LISTENER_SET = new EntityChangeListener[ 0 ];

  /**
   * Count of users who have asked this object to suspend event transmission.
   */
  private int _count;

  /**
   * Backlog of events we still have to send
   */
  private List<EntityChangeEvent> _deferredEvents;

  private EntityChangeListener[] _globalListeners = EMPTY_LISTENER_SET;
  private final Map<Object, EntityChangeListener[]> _objectListeners = new HashMap<Object, EntityChangeListener[]>();
  private final Map<Object, Map<Object, EntityChangeListener[]>> _featureListeners = new HashMap<Object, Map<Object, EntityChangeListener[]>>();
  private final Map<Object, EntityChangeListener[]> _classListeners = new HashMap<Object, EntityChangeListener[]>();
  private final Map<Object, Map<Object, EntityChangeListener[]>> _classFeatureListeners = new HashMap<Object, Map<Object, EntityChangeListener[]>>();

  public final void addChangeListener( final EntityChangeListener listener )
  {
    _globalListeners = doAddChangeListener( getGlobalListeners(), listener );
  }

  public final void addChangeListener( final Class clazz, final EntityChangeListener listener )
  {
    addChangeListener( _classListeners, clazz, listener );
  }

  public final void addChangeListener( final Object object, final EntityChangeListener listener )
  {
    addChangeListener( _objectListeners, object, listener );
  }

  public final void addChangeListener( final Object object, final String feature, final EntityChangeListener listener )
  {
    addChangeListener( _featureListeners, object, feature, listener );
  }

  /**
   * Only use this when the feature is an attribute of the given class
   * <p/>
   * IE - listening for changes in attributes or outbound relationships
   * <p/>
   * If you want notification of the addition or a related entity then use
   * {@link #addRelationshipChangeListener} instead
   */
  public final void addChangeListener( final Class clazz, final String feature, final EntityChangeListener listener )
  {
    addChangeListener( _classFeatureListeners, clazz, feature, listener );
  }

  /**
   * Adds a listener that will be notified if an instance of 'clazz' has a new
   * instance of 'relatedClazz' related to it via the relationship named 'feature'.
   * <p/>
   * Note that this does NOT register for relationship notifications for subclasses
   * of 'relatedClazz', you have to register for each of them individually.
   * <p/>
   * Note that it DOES register for relationship notifications for subclasses of
   * 'clazz'.  Wahoo!
   */
  public final void addRelationshipChangeListener( final Class clazz,
                                                   final String feature,
                                                   final Class relatedClazz,
                                                   final EntityChangeListener listener )
  {
    addChangeListener( _classFeatureListeners, clazz, relatedClazz.getName() + feature, listener );
  }

  public final void removeChangeListener( final EntityChangeListener listener )
  {
    _globalListeners = doRemoveAttributeChangeListener( getGlobalListeners(), listener );
  }

  public final void removeChangeListener( final Object object, final EntityChangeListener listener )
  {
    removeChangeListener( _objectListeners, object, listener );
  }

  public final void removeChangeListener( final Class clazz, final EntityChangeListener listener )
  {
    removeChangeListener( _classListeners, clazz, listener );
  }

  public final void removeChangeListener( final Class clazz, final String name, final EntityChangeListener listener )
  {
    removeChangeListener( _classFeatureListeners, clazz, name, listener );
  }

  public final void removeChangeListener( final Object object, final String name, final EntityChangeListener listener )
  {
    removeChangeListener( _featureListeners, object, name, listener );
  }

  public final void attributeChanged( final EntityChangeEvent event )
  {
    sendTypedEvent( event, Type.ATTRIBUTE_CHANGED );
  }

  public final void relatedAdded( final EntityChangeEvent event )
  {
    sendTypedEvent( event, Type.RELATED_ADDED );
  }

  public final void relatedRemoved( final EntityChangeEvent event )
  {
    sendTypedEvent( event, Type.RELATED_REMOVED );
  }

  public final void objectDeleted( final EntityChangeEvent event )
  {
    sendTypedEvent( event, Type.OBJECT_DELETED );
  }

  private void sendTypedEvent( final EntityChangeEvent event, final Type expected )
  {
    verifyType( event, expected );
    sendEvent( event );
  }

  private void verifyType( final EntityChangeEvent event,
                           final Type expected )
  {
    final Type type = event.getType();
    if( expected != type )
    {
      final String message = "Attempting to distribute event with type " + type + " when expecting " + expected;
      throw new IllegalArgumentException( message );
    }
  }

  private boolean isActive()
  {
    return _count == 0;
  }

  public final void activate()
  {
    _count--;
    if( isActive() )
    {

      EntityChangeEvent[] deferredEvents = null;
      if( null != _deferredEvents )
      {
        deferredEvents = _deferredEvents.toArray( new EntityChangeEvent[ _deferredEvents.size() ] );
        _deferredEvents = null;
      }

      if( deferredEvents != null )
      {
        for( final EntityChangeEvent event : deferredEvents )
        {
          doSendEvent( event );
        }
      }
    }
  }

  public final void deactivate()
  {
    _count++;
  }

  private void sendEvent( final EntityChangeEvent event )
  {
    if( isActive() )
    {
      doSendEvent( event );
    }
    else
    {
      if( null == _deferredEvents )
      {
        _deferredEvents = new ArrayList<EntityChangeEvent>();
      }
      _deferredEvents.add( event );
    }
  }

  private void doSendEvent( final EntityChangeEvent event )
  {
    if( LOG.isLoggable( Level.FINE ) )
    {
      LOG.fine( "Sending event " + event );
    }

    final Object object = event.getObject();
    final Type type = event.getType();

    String name = event.getName();

    if( type == Type.RELATED_ADDED || type == Type.RELATED_REMOVED )
    {
      final Object other = event.getValue();
      name = other.getClass().getName() + name;
    }

    Class clazz;

    // Cache all listeners
    final ArrayList<EntityChangeListener[]> classListenersCopy = new ArrayList<EntityChangeListener[]>();
    final ArrayList<EntityChangeListener[]> classFeatureListenersCopy = new ArrayList<EntityChangeListener[]>();
    final EntityChangeListener[] listenersCopy = copyListeners( getGlobalListeners() );
    final EntityChangeListener[] objectListenersCopy = copyListeners( getListeners( _objectListeners, object ) );
    final EntityChangeListener[] featureListenersCopy = copyListeners( getListeners( _featureListeners, object, name ) );
    clazz = object.getClass();
    while( clazz != Object.class )
    {
      classListenersCopy.add( copyListeners( getListeners( _classListeners, clazz ) ) );
      classFeatureListenersCopy.add( copyListeners( getListeners( _classFeatureListeners, clazz, name ) ) );
      clazz = clazz.getSuperclass();
    }

    doSendEvent( listenersCopy, event );
    doSendEvent( objectListenersCopy, event );
    doSendEvent( featureListenersCopy, event );

    clazz = object.getClass();
    int i = 0;
    while( clazz != Object.class )
    {
      doSendEvent( classListenersCopy.get( i ), event );
      doSendEvent( classFeatureListenersCopy.get( i ), event );
      i++;
      clazz = clazz.getSuperclass();
    }
  }

  private void doSendEvent( final EntityChangeListener[] listenersCopy,
                            final EntityChangeEvent event )
  {
    for( final EntityChangeListener listener : listenersCopy )
    {
      final Type type = event.getType();
      try
      {
        switch( type )
        {
          case ATTRIBUTE_CHANGED:
            listener.attributeChanged( event );
            break;
          case RELATED_ADDED:
            listener.relatedAdded( event );
            break;
          case RELATED_REMOVED:
            listener.relatedRemoved( event );
            break;
          case OBJECT_DELETED:
            listener.objectDeleted( event );
            break;
          default:
            throw new IllegalStateException( "Unknown type: " + type );
        }
      }
      catch( final Throwable t )
      {
        LOG.log( Level.SEVERE, "Error sending event to listener: " + listener, t );
      }
    }
  }

  private EntityChangeListener[] copyListeners( final EntityChangeListener[] listeners )
  {
    final EntityChangeListener[] listenersCopy = new EntityChangeListener[ listeners.length ];
    System.arraycopy( listeners, 0, listenersCopy, 0, listeners.length );
    return listenersCopy;
  }

  private EntityChangeListener[] getListeners( final Map<Object, EntityChangeListener[]> map, final Object object )
  {
    final EntityChangeListener[] listeners = map.get( object );
    if( null == listeners )
    {
      return EMPTY_LISTENER_SET;
    }
    else
    {
      return listeners;
    }
  }

  private EntityChangeListener[] getListeners( final Map<Object, Map<Object, EntityChangeListener[]>> masterMap,
                                               final Object masterKey,
                                               final String feature )
  {
    final Map<Object, EntityChangeListener[]> map = masterMap.get( masterKey );
    if( null == map )
    {
      return EMPTY_LISTENER_SET;
    }
    else
    {
      final EntityChangeListener[] listeners = map.get( feature );
      if( null == listeners )
      {
        return EMPTY_LISTENER_SET;
      }
      else
      {
        return listeners;
      }
    }
  }

  private EntityChangeListener[] getGlobalListeners()
  {
    return _globalListeners;
  }

  private EntityChangeListener[] doAddChangeListener( final EntityChangeListener[] listeners,
                                                      final EntityChangeListener listener )
  {
    final List<EntityChangeListener> list = new ArrayList<EntityChangeListener>();
    list.addAll( Arrays.asList( listeners ) );
    if( !list.contains( listener ) )
    {
      list.add( listener );
    }
    return list.toArray( new EntityChangeListener[ list.size() ] );
  }

  private EntityChangeListener[] doRemoveAttributeChangeListener( final EntityChangeListener[] listeners,
                                                                  final EntityChangeListener listener )
  {
    final List<EntityChangeListener> list = new ArrayList<EntityChangeListener>();
    list.addAll( Arrays.asList( listeners ) );
    list.remove( listener );
    return list.toArray( new EntityChangeListener[ list.size() ] );
  }

  private void removeChangeListener( final Map<Object, EntityChangeListener[]> map,
                                     final Object object,
                                     final EntityChangeListener listener )
  {
    final EntityChangeListener[] listenersSet = getListeners( map, object );
    final EntityChangeListener[] listeners = doRemoveAttributeChangeListener( listenersSet,
                                                                              listener );
    if( 0 == listeners.length )
    {
      map.remove( object );
    }
    else
    {
      map.put( object, listeners );
    }
  }

  private void removeChangeListener( final Map<Object, Map<Object, EntityChangeListener[]>> masterMap,
                                     final Object object,
                                     final String feature,
                                     final EntityChangeListener listener )
  {
    final EntityChangeListener[] listenersSet = getListeners( masterMap, object, feature );
    final EntityChangeListener[] listeners = doRemoveAttributeChangeListener( listenersSet, listener );
    final Map<Object, EntityChangeListener[]> objectMap = masterMap.get( object );
    if( null != objectMap )
    {
      if( 0 == listeners.length )
      {
        objectMap.remove( object );
      }
      else
      {
        objectMap.put( object, listeners );
      }
    }
  }

  private void addChangeListener( final Map<Object, Map<Object, EntityChangeListener[]>> masterMap,
                                  final Object object,
                                  final String feature,
                                  final EntityChangeListener listener )
  {
    final EntityChangeListener[] listenerSet = getListeners( masterMap, object, feature );
    final EntityChangeListener[] listeners = doAddChangeListener( listenerSet, listener );
    Map<Object, EntityChangeListener[]> objectMap = masterMap.get( object );
    if( null == objectMap )
    {
      objectMap = new HashMap<Object, EntityChangeListener[]>();
      masterMap.put( object, objectMap );
    }
    objectMap.put( feature, listeners );
  }

  private void addChangeListener( final Map<Object, EntityChangeListener[]> map,
                                  final Object object,
                                  final EntityChangeListener listener )
  {
    final EntityChangeListener[] listenerSet = getListeners( map, object );
    final EntityChangeListener[] listeners = doAddChangeListener( listenerSet, listener );
    map.put( object, listeners );
  }
}
