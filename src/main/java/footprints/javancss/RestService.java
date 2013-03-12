package footprints.javancss;

import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.service.code_metrics.FormatErrorException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path( "/collections" )
@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
public interface RestService
{
  @Consumes( MediaType.APPLICATION_XML )
  @PUT
  void uploadCollection( String content )
    throws FormatErrorException;

  @GET
  List<CollectionDTO> getCollections();

  @GET
  @Produces( MediaType.APPLICATION_JSON )
  @Path( "/{id}" )
  CollectionDTO getCollection( @PathParam( "id" ) int id );
}
