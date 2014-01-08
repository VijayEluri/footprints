package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import footprints.client.ioc.FootprintsGinjector;

public final class Footprints
  implements EntryPoint
{
  public void onModuleLoad()
  {
    final VerticalPanel panel = new VerticalPanel();
    final FootprintsGinjector injector = GWT.create( FootprintsGinjector.class );
    panel.add( injector.getSimpleUI().asWidget() );
    RootPanel.get().add( panel );
  }
}
