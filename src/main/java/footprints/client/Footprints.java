package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
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
          log( "onCachedEvent(" + event + ")" );
        }
      } );
      cache.addCheckingHandler( new CheckingEvent.Handler()
      {
        @Override
        public void onCheckingEvent( @Nonnull final CheckingEvent event )
        {
          log( "onCheckingEvent(" + event + ")" );
        }
      } );
      cache.addDownloadingHandler( new DownloadingEvent.Handler()
      {
        @Override
        public void onDownloadingEvent( @Nonnull final DownloadingEvent event )
        {
          log( "onDownloadingEvent(" + event + ")" );
        }
      } );
      cache.addErrorHandler( new ErrorEvent.Handler()
      {
        @Override
        public void onErrorEvent( @Nonnull final ErrorEvent event )
        {
          log( "onErrorEvent(" + event + ")" );
        }
      } );
      cache.addNoUpdateHandler( new NoUpdateEvent.Handler()
      {
        @Override
        public void onNoUpdateEvent( @Nonnull final NoUpdateEvent event )
        {
          log( "onNoUpdateEvent(" + event + ")" );
        }
      } );
      cache.addObsoleteHandler( new ObsoleteEvent.Handler()
      {
        @Override
        public void onObsoleteEvent( @Nonnull final ObsoleteEvent event )
        {
          log( "onObsoleteEvent(" + event + ")" );
        }
      } );
      cache.addProgressHandler( new ProgressEvent.Handler()
      {
        @Override
        public void onProgressEvent( @Nonnull final ProgressEvent event )
        {
          log( "onProgressEvent(" + event + ")" );
        }
      } );
      cache.addUpdateReadyHandler( new UpdateReadyEvent.Handler()
      {
        @Override
        public void onUpdateReadyEvent( @Nonnull final UpdateReadyEvent event )
        {
          log( "onUpdateReadyEvent(" + event + ")" );
        }
      } );
      cache.update();
    }
    final FootprintsGinjector injector = GWT.create( FootprintsGinjector.class );
    RootPanel.get().add( injector.getSimpleUI().asWidget() );
  }

  private void log( final String msg )
  {
    LOG.info( msg );
  }
}
