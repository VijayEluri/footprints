package org.realityforge.gwt.timing.resource.client;

class PerformanceResourceTimingJso
  extends PerformanceEntryJso
  implements PerformanceResourceTiming
{
  protected PerformanceResourceTimingJso()
  {
  }

  @Override
  public final InitiatorType getInitiatorType()
  {
    return InitiatorType.valueOf( getInitiatorType0().toUpperCase() );
  }

  private native String getInitiatorType0()/*-{
    return this.initiatorType;
  }-*/;

  @Override
  public final native double getRedirectStart() /*-{
    return this.redirectStart;
  }-*/;

  @Override
  public final native double getRedirectEnd() /*-{
    return this.redirectEnd;
  }-*/;

  @Override
  public final native double getFetchStart() /*-{
    return this.fetchStart;
  }-*/;

  @Override
  public final native double getDomainLookupStart() /*-{
    return this.redirectStart;
  }-*/;

  @Override
  public final native double getDomainLookupEnd() /*-{
    return this.domainLookupEnd;
  }-*/;

  @Override
  public final native double getConnectStart() /*-{
    return this.connectStart;
  }-*/;

  @Override
  public final native double getConnectEnd() /*-{
    return this.connectEnd;
  }-*/;

  @Override
  public final native double getSecureConnectStart() /*-{
    return this.secureConnectStart;
  }-*/;

  @Override
  public final native double getRequestStart() /*-{
    return this.requestStart;
  }-*/;

  @Override
  public final native double getResponseStart() /*-{
    return this.responseStart;
  }-*/;

  @Override
  public final native double getResponseEnd() /*-{
    return this.responseEnd;
  }-*/;
}
