package footprints.javancss;

import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.data_type.code_metrics.FormatErrorException;
import footprints.server.service.code_metrics.JavaNcss;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path( "/collections" )
@Produces( { "application/json", "application/xml" } )
@RequestScoped
@Stateless
public class RestService
{
  @EJB
  private JavaNcss _service;

  @Consumes( "application/xml" )
  @PUT
  public void uploadCollection( final String content )
    throws FormatErrorException
  {
    _service.uploadJavaNcssOutput( content );
  }

  @GET
  public List<CollectionDTO> getCollections()
  {
    return _service.getCollections();
  }

  @GET
  @Produces( "application/json" )
  @Path( "/{id}" )
  public CollectionDTO getCollection( @PathParam( "id" ) int id )
  {
    return _service.getCollection( id );
  }
}
