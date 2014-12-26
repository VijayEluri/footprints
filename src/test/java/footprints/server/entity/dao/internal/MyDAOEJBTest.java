package footprints.server.entity.dao.internal;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyDAOEJBTest
  extends AbstractMyDAOEJBTest
{
  @BeforeMethod
  @Override
  public void preTest()
    throws Exception
  {
    System.setProperty( "test.db.driver", "org.postgresql.Driver" );
    super.preTest();
  }

  @Test
  public void findAllCollection()
  {
    service().findAllCollection();
  }

  @Test
  public void findCollectionResult()
  {
    service().findCollectionResult();
  }

  @Test
  public void findCollectionCount()
  {
    service().findCollectionCount();
  }

  @Test
  public void findAllPackageCount()
  {
    service().findAllPackageCount();
  }
}
