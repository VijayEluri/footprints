package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import java.util.logging.Logger;

public final class Footprints
  implements EntryPoint
{
  private static final Logger LOG = Logger.getLogger( Footprints.class.getName() );

  public void onModuleLoad()
  {
    Window.alert( "Hello from GWT" );
  }
}
