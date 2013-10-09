package footprints.server.entity.code_metrics;

import footprints.server.entity.box.Attribute;
import footprints.server.entity.box.AttributeType;
import footprints.server.entity.box.Block;
import footprints.server.entity.box.BlockType;
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
    final Block object = new Block( new BlockType() );
    assertValid( validator, object );
    object.setA( 1 );
    assertInvalid( validator, object );
    object.setB( 1 );
    assertValid( validator, object );
    object.setC( 1 );
    assertValid( validator, object );
    object.setD( 1 );
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
    final Attribute attribute = new Attribute( object );
    final AttributeType attributeType = new AttributeType( object.getBlockType() );
    attribute.setAttributeType( attributeType );
    assertValid( validator, attribute );
    final AttributeType attributeType2 = new AttributeType( new BlockType() );
    attribute.setAttributeType( attributeType2 );
    assertInvalid( validator, attribute );
  }

  private void assertValid( final Validator validator, final Object object )
  {
    assertTrue( validator.validate( object ).isEmpty() );
  }

  private void assertInvalid( final Validator validator, final Object object )
  {
    assertFalse( validator.validate( object ).isEmpty() );
  }
}
