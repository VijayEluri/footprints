package footprints.javancss;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "method" )
public class MethodDTO
{
  private int id;
  private String packageName;
  private String className;
  private String methodName;
  private int ncss;
  private int ccnd;
  private int jvdc;

  public MethodDTO()
  {
  }

  public MethodDTO( final int id, final String packageName, final String className, final String methodName, final int ncss, final int ccnd, final int jvdc )
  {
    this.id = id;
    this.packageName = packageName;
    this.className = className;
    this.methodName = methodName;
    this.ncss = ncss;
    this.ccnd = ccnd;
    this.jvdc = jvdc;
  }

  public int getId()
  {
    return id;
  }

  public String getPackageName()
  {
    return packageName;
  }

  public String getClassName()
  {
    return className;
  }

  public String getMethodName()
  {
    return methodName;
  }

  public int getNcss()
  {
    return ncss;
  }

  public int getCcnd()
  {
    return ccnd;
  }

  public int getJvdc()
  {
    return jvdc;
  }
}
