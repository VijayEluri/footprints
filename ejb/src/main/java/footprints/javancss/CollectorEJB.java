package footprints.javancss;

import footprints.server.data_type.test_module.CalculateResultValueZang;
import footprints.server.data_type.test_module.CloneAction;
import footprints.server.data_type.test_module.Fooish;
import footprints.server.entity.test_module.BaseX;
import footprints.server.service.test_module.Collector;
import footprints.server.service.test_module.ProblemException;
import footprints.server.service.test_module.TestsFailedException;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Dummy service implementation so that application can be deployed
 */
@Stateless( name = Collector.EJB_NAME )
@TransactionAttribute( TransactionAttributeType.REQUIRED )
public class CollectorEJB
  implements Collector
{
  @Override
  public void runAllTheTests( final boolean force )
    throws TestsFailedException, ProblemException
  {
  }

  @Override
  @Nullable
  public BigDecimal calculateResultValue(
    @Nonnull final BigDecimal input,
    @Nonnull final Fooish x, @Nonnull final BaseX baseX, @Nonnull final CalculateResultValueZang zang )
    throws ProblemException
  {
    return null;
  }

  @Override
  @Nonnull
  public BaseX calculateResultValue2()
  {
    throw new IllegalStateException();
  }

  @Override
  @Nonnull
  public CloneAction calculateResultValue3()
  {
    return CloneAction.CLONE;
  }

  @Override
  @Nullable
  public BaseX calculateResultValue4()
  {
    return null;
  }
}
