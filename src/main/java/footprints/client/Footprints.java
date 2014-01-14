package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import footprints.client.ioc.FootprintsGinjector;

public final class Footprints
  implements EntryPoint
{
  public void onModuleLoad()
  {
    final FootprintsGinjector injector = GWT.create( FootprintsGinjector.class );
    RootPanel.get().add( injector.getSimpleUI().asWidget() );
  }
}
