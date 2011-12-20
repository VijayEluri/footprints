package footprints.javancss.service;

@javax.annotation.Generated( "Domgen" )
@javax.jws.WebService
public interface JavaNcssWS extends java.rmi.Remote
{
  String WS_NAME = "footprints.javancss.service.JavaNcssWS";

  @javax.jws.WebResult()
  @javax.jws.WebMethod()
  void uploadJavaNcssOutput(
    @javax.jws.WebParam( name = "output", partName = "", targetNamespace = "", mode = javax.jws.WebParam.Mode.IN, header = false ) @javax.annotation.Nonnull final String output )
    throws java.rmi.RemoteException, FormatErrorException;
}
