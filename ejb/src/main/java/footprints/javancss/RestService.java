package footprints.javancss;

import footprints.javancss.model.Collection;
import footprints.javancss.model.MethodMetric;
import footprints.javancss.model.dao.CollectionDAO;
import footprints.javancss.model.dao.MethodMetricDAO;
import footprints.javancss.service.FormatErrorException;
import footprints.javancss.service.JavaNcss;
import java.util.ArrayList;
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
  @EJB
  private CollectionDAO _collectionDAO;
  @EJB
  private MethodMetricDAO _methodMetricDAO;

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
    final ArrayList<CollectionDTO> collections = new ArrayList<CollectionDTO>();
    for( final Collection collection : _collectionDAO.findAll() )
    {
      collections.add( toCollection( collection ) );
    }
    return collections;
  }

  @GET
  @Produces( "application/json" )
  @Path( "/{id}" )
  public CollectionDTO getCollection( @PathParam( "id" ) int id )
  {
    final Collection collection = _collectionDAO.getByID( id );
    return toCollection( collection );
  }

  private CollectionDTO toCollection( final Collection collection )
  {
    final ArrayList<MethodDTO> methods = new ArrayList<MethodDTO>();
    for( final MethodMetric methodMetric : collection.getMethodMetrics() )
    {
      final MethodDTO dto =
        new MethodDTO( methodMetric.getID(),
                       methodMetric.getPackageName(),
                       methodMetric.getClassName(),
                       methodMetric.getMethodName(),
                       methodMetric.getNCSS(),
                       methodMetric.getCCN(),
                       methodMetric.getJVDC() );
      methods.add( dto );
    }

    return new CollectionDTO( collection.getID(), collection.getCollectedAt(), methods );
  }
}
