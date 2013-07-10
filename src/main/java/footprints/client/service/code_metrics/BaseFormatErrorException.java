package footprints.client.service.code_metrics;

@javax.annotation.Generated( "Domgen" )
@SuppressWarnings( { "UnusedDeclaration", "JavaDoc" } )
public class BaseFormatErrorException
  extends Exception
{

  private @javax.annotation.Nonnull String File;

  public BaseFormatErrorException( final @javax.annotation.Nonnull String File ) { this( File, null, null ); }
  public BaseFormatErrorException( final @javax.annotation.Nonnull String File, final String message ) { this( File, message, null ); }
  public BaseFormatErrorException( final @javax.annotation.Nonnull String File,
                                   final String message,
                                   final Throwable cause )
  {
    super( message, cause );
      this.File = File;
  }


public @javax.annotation.Nonnull String getFile()
  {
  return File;
}
  }
