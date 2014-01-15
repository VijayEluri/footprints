package footprints.client.services;

import org.realityforge.replicant.client.EntityChangeBrokerImpl;
import org.realityforge.replicant.client.EntityChangeListener;

public class FootprintsEntityChangeBroker
  extends EntityChangeBrokerImpl
{
  @Override
  protected void logEventHandlingError( final EntityChangeListener listener, final Throwable t )
  {
    throw new RuntimeException( "Error sending event to listener: " + listener, t );
  }
}
