package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import footprints.client.ioc.FootprintsGinjector;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.realityforge.gwt.appcache.client.ApplicationCache;
import org.realityforge.gwt.appcache.client.event.CachedEvent;
import org.realityforge.gwt.appcache.client.event.CachedEvent.Handler;
import org.realityforge.gwt.appcache.client.event.CheckingEvent;
import org.realityforge.gwt.appcache.client.event.DownloadingEvent;
import org.realityforge.gwt.appcache.client.event.ErrorEvent;
import org.realityforge.gwt.appcache.client.event.NoUpdateEvent;
import org.realityforge.gwt.appcache.client.event.ObsoleteEvent;
import org.realityforge.gwt.appcache.client.event.ProgressEvent;
import org.realityforge.gwt.appcache.client.event.UpdateReadyEvent;

public final class Footprints
  implements EntryPoint
{
  private static final Logger LOG = Logger.getLogger( Footprints.class.getName() );

  public void onModuleLoad()
  {
    final ApplicationCache cache = ApplicationCache.get();
    if ( null == cache )
    {
      Window.alert( "No cache!" );
    }
    else
    {
      cache.addCachedHandler( new Handler()
      {
        @Override
        public void onCachedEvent( @Nonnull final CachedEvent event )
        {
          LOG.info( "onCachedEvent(" + event + ")" );
        }
      } );
      cache.addCheckingHandler( new CheckingEvent.Handler()
      {
        @Override
        public void onCheckingEvent( @Nonnull final CheckingEvent event )
        {
          LOG.info( "onCheckingEvent(" + event + ")" );
        }
      } );
      cache.addDownloadingHandler( new DownloadingEvent.Handler()
      {
        @Override
        public void onDownloadingEvent( @Nonnull final DownloadingEvent event )
        {
          LOG.info( "onDownloadingEvent(" + event + ")" );
        }
      } );
      cache.addErrorHandler( new ErrorEvent.Handler()
      {
        @Override
        public void onErrorEvent( @Nonnull final ErrorEvent event )
        {
          LOG.info( "onErrorEvent(" + event + ")" );
        }
      } );
      cache.addNoUpdateHandler( new NoUpdateEvent.Handler()
      {
        @Override
        public void onNoUpdateEvent( @Nonnull final NoUpdateEvent event )
        {
          LOG.info( "onNoUpdateEvent(" + event + ")" );
        }
      } );
      cache.addObsoleteHandler( new ObsoleteEvent.Handler()
      {
        @Override
        public void onObsoleteEvent( @Nonnull final ObsoleteEvent event )
        {
          LOG.info( "onObsoleteEvent(" + event + ")" );
        }
      } );
      cache.addProgressHandler( new ProgressEvent.Handler()
      {
        @Override
        public void onProgressEvent( @Nonnull final ProgressEvent event )
        {
          LOG.info( "onProgressEvent(" + event.getLoaded() + "," + event.getTotal() + ")" );
        }
      } );
      cache.addUpdateReadyHandler( new UpdateReadyEvent.Handler()
      {
        @Override
        public void onUpdateReadyEvent( @Nonnull final UpdateReadyEvent event )
        {
          LOG.info( "onUpdateReadyEvent(" + event + ")" );
        }
      } );
      cache.requestUpdate();
    }
    final VerticalPanel panel = new VerticalPanel();
    if( null != cache )
    {
      final Button button = new Button( "Update from Cache" );
      button.addClickHandler( new ClickHandler()
      {
        @Override
        public void onClick( final ClickEvent event )
        {
          cache.requestUpdate();
        }
      } );
      panel.add( button );
      final Button button2 = new Button( "Remove from Cache" );
      button2.addClickHandler( new ClickHandler()
      {
        @Override
        public void onClick( final ClickEvent event )
        {
          cache.removeCache();
        }
      } );
      panel.add( button2 );

    }
    final FootprintsGinjector injector = GWT.create( FootprintsGinjector.class );
    panel.add( injector.getSimpleUI().asWidget() );
    RootPanel.get().add( panel );
  }
}
