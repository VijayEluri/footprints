package footprints.javancss;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "collection" )
public class CollectionDTO
{
  private int id;
  private Date collectedAt;
  private List<MethodDTO> methods;

  public CollectionDTO()
  {
  }

  public CollectionDTO( final int id, final Date collectedAt, final List<MethodDTO> methods )
  {
    this.id = id;
    this.collectedAt = collectedAt;
    this.methods = methods;
  }

  public int getId()
  {
    return id;
  }

  public Date getCollectedAt()
  {
    return collectedAt;
  }

  public List<MethodDTO> getMethods()
  {
    return methods;
  }
}
