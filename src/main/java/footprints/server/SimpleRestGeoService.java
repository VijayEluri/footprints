/* DO NOT EDIT: File is auto-generated */
package footprints.server;

import footprints.server.entity.geo.Sector;
import footprints.server.entity.geo.dao.SectorRepository;
import java.util.List;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.geolatte.geom.DimensionalFlag;
import org.geolatte.geom.PointSequenceBuilder;
import org.geolatte.geom.PointSequenceBuilders;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CrsId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings({ "UnusedDeclaration", "JavaDoc" })
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Path("/geo")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class SimpleRestGeoService
{
  @EJB
  private SectorRepository _service;

  @GET
  public String calculateResultValue4()
    throws JSONException
  {
    final Sector s = new Sector();
    s.setName( "Sector: " + System.currentTimeMillis() );
    final PointSequenceBuilder builder =
      PointSequenceBuilders.variableSized( DimensionalFlag.d2D, CrsId.UNDEFINED );
    builder.add( 1, 1 );
    builder.add( 1, 0 );
    builder.add( 0, 0 );
    builder.add( 1, 1 );
    final Polygon geom = new Polygon( builder.toPointSequence() );
    s.setLocation( geom );
    _service.persist( s );
    final JSONObject json = new JSONObject();
    final JSONArray jsonSectors = new JSONArray();
    json.put( "sectors", jsonSectors );
    final List<Sector> sectors = _service.findAll();
    int index = 0;
    for ( final Sector sector : sectors )
    {
      final JSONObject jsonSector = new JSONObject();
      jsonSector.put( "id", sector.getID() );
      jsonSector.put( "name", sector.getName() );
      jsonSector.put( "location", sector.getLocation().asText() );
      jsonSectors.put( index++, jsonSector );
    }
    return json.toString();
  }

}
