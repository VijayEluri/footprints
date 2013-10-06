package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import footprints.client.ioc.FootprintsGinjector;
import footprints.shared.service.code_metrics.GwtJavaNcssAsync;
import java.util.List;

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
        //final RestGwtJavaNcss service = injector.getRestGwtJavaNcss();
        //service.getCollections( new OverlayCallback<JsArray<JsoCollectionDTO>>()
        //{
        //  @Override
        //  public void onFailure( final Method method, final Throwable exception )
        //  {
        //    Window.alert( "Failed - REST" );
        //  }
        //
        //  @Override
        //  public void onSuccess( final Method method, final JsArray<JsoCollectionDTO> response )
        //  {
        //    Window.alert( "Success - REST" );
        //  }
        //} );
        //Window.alert( "Order Placed via Rest" );

        final GwtJavaNcssAsync service2 = injector.getGwtJavaNcss();
        service2.getCollections( new AsyncCallback<List<String>>()
        {
          @Override
          public void onFailure( final Throwable caught )
          {
            Window.alert( "Failed - RPC" );
          }

          @Override
          public void onSuccess( final List<String> result )
          {
            Window.alert( "Success - RPC" );
          }
        } );
        Window.alert( "Order Placed via RPC" );
      }

    } );
    RootPanel.get().add( button );
  }
}
