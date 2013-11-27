package footprints.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.google.web.bindery.event.shared.binder.GenericEvent;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.service.code_metrics.JavaNcss;
import java.util.List;
import javax.inject.Inject;
import org.realityforge.replicant.client.AsyncCallback;
import org.realityforge.replicant.client.AsyncErrorCallback;

public class SimpleUI
  extends Composite
{
  public interface SimpleEventBinder
    extends EventBinder<SimpleUI>
  {
  }

  public static class ResponseEvent
    extends GenericEvent
  {
  }

  @Inject
  private JavaNcss _service;
  private EventBus _eventBus;
  private SimpleEventBinder _eventBinder;

  @Inject
  public void setEventBus( final EventBus eventBus )
  {
    _eventBus = eventBus;
    bindEvents();
  }

  @Inject
  public void setSimpleEventBinder( final SimpleEventBinder eventBinder )
  {
    Window.alert( "setSimpleEventBinder" );
    _eventBinder = eventBinder;
    bindEvents();
  }

  @EventHandler
  public void onResponseEvent( final ResponseEvent event )
  {
    Window.alert( "Response Processed!" );
  }

  @Override
  public Widget asWidget()
  {
    final SimplePanel panel = new SimplePanel();

    final Button button = new Button( "Place Pizza Order" );
    button.addClickHandler( new ClickHandler()
    {
      public void onClick( ClickEvent event )
      {
        _service.getCollections( new AsyncCallback<List<CollectionDTO>>()
                                 {
                                   @Override
                                   public void onSuccess( final List<CollectionDTO> result )
                                   {
                                     Window.alert( "Success - RPC" );
                                     _eventBus.fireEvent( new ResponseEvent() );
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
    panel.add( button );
    return panel;
  }

  private void bindEvents()
  {
    if ( null != _eventBinder && null != _eventBus )
    {
      _eventBinder.bindEventHandlers( this, _eventBus );
    }
  }
}
