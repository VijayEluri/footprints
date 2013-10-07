package footprints.server.entity.code_metrics;

import footprints.server.entity.box.Block;
import java.util.Date;
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
    assertTrue( validator.validate( object ).isEmpty() );
    object.setA( 1 );
    assertFalse( validator.validate( object ).isEmpty() );
    object.setB( 1 );
    assertTrue( validator.validate( object ).isEmpty() );
    object.setC( 1 );
    assertTrue( validator.validate( object ).isEmpty() );
    object.setD( 1 );
    assertFalse( validator.validate( object ).isEmpty() );
    object.setD( null );
    assertTrue( validator.validate( object ).isEmpty() );
    object.setE( 1 );
    assertFalse( validator.validate( object ).isEmpty() );
    object.setF( 1 );
    assertTrue( validator.validate( object ).isEmpty() );
  }
}
