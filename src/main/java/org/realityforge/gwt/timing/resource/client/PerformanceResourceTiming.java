package org.realityforge.gwt.timing.resource.client;

public interface PerformanceResourceTiming
  extends PerformanceEntry
{
  enum InitiatorType
  {
    CSS, EMBED, IMG, LINK, OBJECT, SCRIPT, SUBDOCUMENT, SVG, XMLHTTPREQUEST, OTHER
  }

  InitiatorType getInitiatorType();

  double getRedirectStart();

  double getRedirectEnd();

  double getFetchStart();

  double getDomainLookupStart();

  double getDomainLookupEnd();

  double getConnectStart();

  double getConnectEnd();

  double getSecureConnectStart();

  double getRequestStart();

  double getResponseStart();

  double getResponseEnd();
}
