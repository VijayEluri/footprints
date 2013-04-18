package footprints.server;

import footprints.server.service.code_metrics.FormatErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map the NoResultException to an appropriate HTTP response.
 */
@Provider
public class FormatErrorExceptionMapper
  implements ExceptionMapper<FormatErrorException>
{
  public Response toResponse( final FormatErrorException fee )
  {
    return Response.
      status( 500 ).
      entity( fee.getMessage() ).
      type( MediaType.TEXT_PLAIN ).
      build();
  }
}