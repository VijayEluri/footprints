package footprints.server.parse;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class MethodEntryTest
{
  @Test
  public void x()
  {
    final ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure().buildValidatorFactory();
    final Validator validator = validatorFactory.getValidator();
  }
}
