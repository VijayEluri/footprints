package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.ioc.FootprintsGinjector;
import footprints.client.service.code_metrics.JavaNcss;
import java.util.List;
import org.realityforge.replicant.client.AsyncCallback;
import org.realityforge.replicant.client.AsyncErrorCallback;

public final class Footprints
  implements EntryPoint
{
  public void onModuleLoad()
  {
    final FootprintsGinjector injector = GWT.create( FootprintsGinjector.class );

    final Button button = new Button( "Place Pizza Order" );
    button.addClickHandler( new ClickHandler()
    {
      public void onClick( ClickEvent event )
      {
        final JavaNcss service2 = injector.getJavaNcss();
        service2.getCollections( new AsyncCallback<List<CollectionDTO>>()
                                 {
                                   @Override
                                   public void onSuccess( final List<CollectionDTO> result )
                                   {
                                     Window.alert( "Success - RPC" );
                                   }
                                 }, new AsyncErrorCallback()
                                 {
                                   @Override
                                   public void onFailure( final Throwable caught )
                                   {
                                     Window.alert( "Failure - RPC" );
                                   }
                                 }
        );
        Window.alert( "Order Placed via RPC" );
      }

    } );
    RootPanel.get().add( button );
  }
}
