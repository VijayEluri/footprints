package footprints.server;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Vector;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.postgis.Point;

public class ConverterInitializer
  extends SessionEventAdapter
{
  @Override
  public void preLogin( final SessionEvent event )
  {
    final Session session = event.getSession();
    final Map<Class, ClassDescriptor> descriptorMap = session.getDescriptors();

    // Walk through all descriptors...
    for ( final Map.Entry<Class, ClassDescriptor> entry : descriptorMap.entrySet() )
    {
      final ClassDescriptor desc = entry.getValue();
      final Vector<DatabaseMapping> mappings = desc.getMappings();
      // walk through all mappings for some class...
      for ( final DatabaseMapping mapping : mappings )
      {
        if ( mapping instanceof DirectToFieldMapping )
        {
          final DirectToFieldMapping dfm = (DirectToFieldMapping) mapping;
          if ( isCandidateConverter( dfm ) )
          {
            final Class type = entry.getKey();
            final String attributeName = dfm.getAttributeName();
            final Field field = getField( type, attributeName );
            if ( Point.class == field.getType() )
            {
              final Converter converter = new PostgisConverter();
              converter.initialize( mapping, session );
              dfm.setConverter( converter );
            }
          }
        }
      }
    }
  }

  private Field getField( final Class type, final String attributeName )
  {
    final Field field;
    try
    {
      field = type.getDeclaredField( attributeName );
    }
    catch ( final NoSuchFieldException nsfe )
    {
      throw new IllegalStateException( "Unable to find field: " + attributeName, nsfe );
    }
    return field;
  }

  // only consider mappings that are deemed to produce
  // byte[] database fields from objects...
  private boolean isCandidateConverter( final DirectToFieldMapping mapping )
  {
    final Converter converter = mapping.getConverter();
    return null != converter && converter instanceof SerializedObjectConverter;
  }
}