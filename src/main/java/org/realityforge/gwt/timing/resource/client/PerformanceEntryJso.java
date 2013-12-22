package org.realityforge.gwt.timing.resource.client;

import com.google.gwt.core.client.JavaScriptObject;

class PerformanceEntryJso
  extends JavaScriptObject
  implements PerformanceEntry
{
  protected PerformanceEntryJso()
  {
  }

  @Override
  public final native String getName() /*-{
    return this.name;
  }-*/;

  @Override
  public final EntryType getEntryType()
  {
    return EntryType.valueOf( getEntryType0() );
  }

  private native String getEntryType0() /*-{
    return this.entryType;
  }-*/;

  @Override
  public final native double getStartTime() /*-{
    return this.startTime;
  }-*/;

  @Override
  public final native double getDuration() /*-{
    return this.duration;
  }-*/;
}
