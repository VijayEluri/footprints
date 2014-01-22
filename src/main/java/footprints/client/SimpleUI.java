package footprints.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.service.FootprintsAsyncCallback;
import footprints.client.service.FootprintsAsyncErrorCallback;
import footprints.client.service.code_metrics.JavaNcss;
import java.util.List;
import javax.inject.Inject;

public class SimpleUI
  extends Composite
{
  @Inject
  private JavaNcss _service;

  @Override
  public Widget asWidget()
  {
    final VerticalPanel panel = new VerticalPanel();
    final Button button = new Button( "Place Pizza Order" );
    button.addClickHandler( new ClickHandler()
    {
      public void onClick( ClickEvent event )
      {
        _service.getCollections( new FootprintsAsyncCallback<List<CollectionDTO>>()
                                 {
                                   @Override
                                   public void onSuccess( final List<CollectionDTO> result )
                                   {
                                     Window.alert( "Success - RPC" );
                                   }
                                 }, new FootprintsAsyncErrorCallback()
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
    panel.add( button );
    return panel;
  }
}
