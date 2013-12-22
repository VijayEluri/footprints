package org.realityforge.gwt.timing.resource.client;

import com.google.gwt.core.client.JsArray;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.gwt.timing.resource.client.PerformanceEntry.EntryType;

@SuppressWarnings( "unchecked" )
public class Html5PerformanceTimeline
  extends PerformanceTimeline
{
  public static native boolean isSupported()/*-{
    return typeof ($wnd.performance) == "object";
  }-*/;

  @Nonnull
  public List<PerformanceEntry> getEntries()
  {
    return (List<PerformanceEntry>) (List) new JsoReadOnlyList<>( getEntries0() );
  }

  private native JsArray<PerformanceEntryJso> getEntries0()/*-{
    return $wnd.performance.getEntries();
  }-*/;

  @Nonnull
  public final <T extends PerformanceEntry> List<T> getEntriesByType( @Nonnull final EntryType entryType )
  {
    return (List<T>) (List) new JsoReadOnlyList<>( getEntriesByType0( entryType.name() ) );
  }

  private native JsArray<PerformanceEntryJso> getEntriesByType0( String entryType )/*-{
    return $wnd.performance.getEntriesByType( entryType );
  }-*/;

  @Nonnull
  public final <T extends PerformanceEntry> List<T> getEntriesByName( @Nonnull final String name,
                                                        @Nullable final EntryType entryType )
  {
    final String typeKey = null != entryType ? entryType.name() : null;
    return (List<T>) (List) new JsoReadOnlyList<>( getEntriesByName0( name, typeKey ) );
  }

  private native JsArray<PerformanceEntryJso> getEntriesByName0( String name, String entryType )/*-{
    return $wnd.performance.getEntriesByName( name, entryType );
  }-*/;
}
