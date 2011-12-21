package footprints.javancss;

import footprints.tester.model.BaseX;
import footprints.tester.model.CalculateResultValueZang;
import footprints.tester.model.CloneAction;
import footprints.tester.service.Collector;
import footprints.tester.service.ProblemException;
import footprints.tester.service.TestsFailedException;
import java.math.BigDecimal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import test_module.Fooish;

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
