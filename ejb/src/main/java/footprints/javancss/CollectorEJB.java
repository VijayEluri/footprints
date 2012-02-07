package footprints.javancss;

import footprints.server.data_type.test_module.CalculateResultValueZang;
import footprints.server.data_type.test_module.CloneAction;
import footprints.server.data_type.test_module.Fooish;
import footprints.server.data_type.test_module.RunAllTheTestsZing;
import footprints.server.entity.test_module.BaseX;
import footprints.server.service.test_module.Collector;
import footprints.server.service.test_module.ProblemException;
import footprints.server.service.test_module.TestsFailedException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
  public void runAllTheTests( final boolean force, @Nonnull final List<RunAllTheTestsZing> zing )
    throws TestsFailedException, ProblemException
  {
  }

  @Override
  public void subscribe( @Nonnull final String sessionID )
  {
  }

  @Override
  public void subscribeWithGuff(
    @Nonnull final String sessionID, @Nonnull final String permutationName, @Nonnull final Set<String> someOtherParam )
  {
  }

  @Override
  @Nullable
  public List<BigDecimal> calculateResultValue(
    @Nonnull final BigDecimal input,
    @Nonnull final Fooish x, @Nonnull final Set<BaseX> baseX, @Nonnull final Set<CalculateResultValueZang> zang )
    throws ProblemException
  {
    return null;
  }

  @Override
  @Nonnull
  public Set<BaseX> calculateResultValue2()
  {
    throw new IllegalStateException();
  }

  @Override
  @Nonnull
  public Set<CloneAction> calculateResultValue3()
  {
    return new HashSet<CloneAction>(  );
  }

  @Override
  @Nullable
  public Set<BaseX> calculateResultValue4()
  {
    return null;
  }
}
