package footprints.server.entity.code_metrics;

import footprints.server.entity.box.Block;
import java.util.Date;
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
    final Block object = new Block();
    assertValid( validator, object );
    object.setA( 1 );
    assertInvalid( validator, object );
    object.setB( 1 );
    assertValid( validator, object );
    object.setC( 1 );
    assertValid( validator, object );
    object.setD( 1 );
    Set<ConstraintViolation<Block>> validate = validator.validate( object );
    assertInvalid( validator, object );
    object.setD( null );
    assertValid( validator, object );
    object.setE( 1 );
    assertInvalid( validator, object );
    object.setF( 1 );
    assertValid( validator, object );
    object.setG( 1 );
    assertValid( validator, object );
    object.setH( 2 );
    assertInvalid( validator, object );
    object.setH( 1 );
    assertValid( validator, object );
  }

  private void assertValid( final Validator validator, final Block object )
  {
    assertTrue( validator.validate( object ).isEmpty() );
  }

  private void assertInvalid( final Validator validator, final Block object )
  {
    assertFalse( validator.validate( object ).isEmpty() );
  }
}
