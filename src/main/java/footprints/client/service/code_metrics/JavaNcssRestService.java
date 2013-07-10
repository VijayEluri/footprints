package footprints.client.service.code_metrics;

import footprints.client.data_type.code_metrics.CollectionDTO;
import java.util.List;
import javax.ws.rs.HttpMethod;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

@javax.annotation.Generated( "Domgen" )
@SuppressWarnings( { "UnusedDeclaration", "JavaDoc" } )
@javax.ws.rs.Path( JavaNcssRestService.PATH )
@javax.ws.rs.Produces( {javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_XML} )
@javax.ws.rs.Consumes( {javax.ws.rs.core.MediaType.APPLICATION_JSON, javax.ws.rs.core.MediaType.APPLICATION_XML} )
public interface JavaNcssRestService
  extends RestService
{

  String PATH = "/java_ncss" ;

  @javax.ws.rs.Path("/upload_java_ncss_output")
  @javax.ws.rs.POST
  void uploadJavaNcssOutput( @javax.ws.rs.QueryParam( "output" ) @javax.annotation.Nonnull String output,
                             MethodCallback<Void> callback )
    throws footprints.client.service.code_metrics.FormatErrorException;


  @javax.ws.rs.Path("/get_collections")
  @javax.ws.rs.GET
  @javax.annotation.Nonnull void getCollections(MethodCallback<List<CollectionDTO>> callback);


  @javax.ws.rs.Path("/get_collection/{id}")
  @javax.ws.rs.GET
  @javax.annotation.Nonnull void getCollection( @javax.ws.rs.PathParam( "id" ) int id,
                                                MethodCallback<CollectionDTO> callback );

}
