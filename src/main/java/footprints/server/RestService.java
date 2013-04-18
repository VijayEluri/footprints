package footprints.server;

import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.service.code_metrics.FormatErrorException;
import java.util.List;

@javax.ws.rs.Path( "/collections" )
@javax.ws.rs.Produces( { javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_XML } )
public interface RestService
{
  @javax.ws.rs.Consumes( javax.ws.rs.core.MediaType.APPLICATION_XML )
  @javax.ws.rs.PUT
  void uploadCollection( String content )
    throws FormatErrorException;

  @javax.ws.rs.GET
  List<CollectionDTO> getCollections();

  @javax.ws.rs.GET
  @javax.ws.rs.Produces( javax.ws.rs.core.MediaType.APPLICATION_JSON )
  @javax.ws.rs.Path( "/{id}" )
  CollectionDTO getCollection( @javax.ws.rs.PathParam( "id" ) int id );
}
