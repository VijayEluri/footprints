/* DO NOT EDIT: File is auto-generated */
package footprints.client.service.code_metrics;

@javax.annotation.Generated( "Domgen" )
@SuppressWarnings( { "UnusedDeclaration", "JavaDoc" } )
public class FormatErrorException
  extends footprints.client.service.code_metrics.BaseFormatErrorException
{
  private
  @javax.annotation.Nonnull
  String File;

  private int Line;

  public FormatErrorException( final @javax.annotation.Nonnull String File, final int Line )
  {
    this( File, Line, null, null );
  }

  public FormatErrorException( final @javax.annotation.Nonnull String File, final int Line, final String message )
  {
    this( File, Line, message, null );
  }

  public FormatErrorException( final @javax.annotation.Nonnull String File,
                               final int Line,
                               final String message,
                               final Throwable cause )
  {
    super( File, message, cause );
    this.Line = Line;
  }


  public
  @javax.annotation.Nonnull
  String getFile()
  {
    return File;
  }

  public int getLine()
  {
    return Line;
  }
}
