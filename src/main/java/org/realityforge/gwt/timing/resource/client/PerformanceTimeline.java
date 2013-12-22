package org.realityforge.gwt.timing.resource.client;

import com.google.gwt.core.shared.GWT;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.gwt.timing.resource.client.PerformanceEntry.EntryType;

public abstract class PerformanceTimeline
{
  private static PerformanceTimeline g_cache;

  public static PerformanceTimeline get()
  {
    if ( null == g_cache )
    {
      if ( GWT.isClient() && Html5PerformanceTimeline.isSupported() )
      {
        register( new Html5PerformanceTimeline() );
      }
    }
    return g_cache;
  }

  public static void register( @Nonnull final PerformanceTimeline performanceTimeline )
  {
    g_cache = performanceTimeline;
  }

  public static boolean deregister( @Nonnull final PerformanceTimeline performanceTimeline )
  {
    if ( g_cache != performanceTimeline )
    {
      return false;
    }
    else
    {
      g_cache = null;
      return true;
    }
  }

  @Nonnull
  public abstract List<PerformanceEntry> getEntries();

  @Nonnull
  public abstract <T extends PerformanceEntry> List<T> getEntriesByType( @Nonnull EntryType entryType );

  @Nonnull
  public abstract <T extends PerformanceEntry> List<T> getEntriesByName( @Nonnull String name, @Nullable EntryType entryType );
}
