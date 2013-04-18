package footprints.server;

import java.sql.SQLException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

public class PostgisConverter
  implements Converter
{
  @Override
  public Object convertObjectValueToDataValue( final Object objectValue, final Session session )
  {
    return new PGgeometry( (Geometry) objectValue );
  }

  @Override
  public Object convertDataValueToObjectValue( final Object dataValue, final Session session )
  {
    if ( dataValue instanceof PGgeometry )
    {
      return ( (PGgeometry) dataValue ).getGeometry();
    }
    try
    {
      return PGgeometry.geomFromString( (String) dataValue );
    }
    catch ( final SQLException sqle )
    {
      throw new IllegalStateException( "Invalid type extracted from database", sqle );
    }
  }

  @Override
  public boolean isMutable()
  {
    return true;
  }

  @Override
  public void initialize( final DatabaseMapping mapping, final Session session )
  {
    final DatabaseField field;
    if ( mapping instanceof DirectCollectionMapping )
    {
      field = ( (DirectCollectionMapping) mapping ).getDirectField();
    }
    else
    {
      field = mapping.getField();
    }

    field.setSqlType( java.sql.Types.OTHER );
    field.setTypeName( "geometry" );
    field.setColumnDefinition( "Geometry" );
  }
}
