package footprints.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.google.web.bindery.event.shared.binder.GenericEvent;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.service.FootprintsAsyncCallback;
import footprints.client.service.FootprintsAsyncErrorCallback;
import footprints.client.service.code_metrics.JavaNcss;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.realityforge.gwt.performance_timeline.client.NavigationTiming;
import org.realityforge.gwt.performance_timeline.client.PerformanceEntry.EntryType;
import org.realityforge.gwt.performance_timeline.client.PerformanceTimeline;
import org.realityforge.gwt.performance_timeline.client.ResourceTiming;

public class SimpleUI
  extends Composite
{
  private static final Logger LOG = Logger.getLogger( Footprints.class.getName() );

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
  @Inject
  private SimpleEventBinder _eventBinder;
  private HandlerRegistration _eventRegistration;

  @Inject
  public void setEventBus( final EventBus eventBus )
  {
    _eventBus = eventBus;
    _eventRegistration = _eventBinder.bindEventHandlers( this, _eventBus );
  }

  public void unbind()
  {
    if ( null != _eventRegistration )
    {
      _eventRegistration.removeHandler();
      _eventRegistration = null;
    }
  }

  @EventHandler
  public void onResponseEvent( final ResponseEvent event )
  {
    Window.alert( "Response Processed!" );
  }

  @Override
  public Widget asWidget()
  {
    final VerticalPanel panel = new VerticalPanel();

    {
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
                                       _eventBus.fireEvent( new ResponseEvent() );
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
    }

    {
      final Button button = new Button( "Place Pizza Order" );
      button.addClickHandler( new ClickHandler()
      {
        public void onClick( ClickEvent event )
        {
          final PerformanceTimeline timeline = PerformanceTimeline.get();
          if ( null == timeline )
          {
            Window.alert( "No timeline!" );
          }
          else
          {
            for ( final ResourceTiming entry : timeline.<ResourceTiming>getEntriesByType( EntryType.resource ) )
            {
              LOG.severe( entry.getName() +
                          " Duration: " + entry.getDuration() +
                          " InitiatorType: " + entry.getInitiatorType() );
            }
            for ( final NavigationTiming entry : timeline.<NavigationTiming>getEntriesByType( EntryType.navigation ) )
            {
              LOG.severe( entry.getName() +
                          " Duration: " + entry.getDuration() +
                          " InitiatorType: " + entry.getType() );
            }
          }
        }

      } );
      panel.add( button );
    }

    panel.add( new Login() );
    return panel;
  }
}
