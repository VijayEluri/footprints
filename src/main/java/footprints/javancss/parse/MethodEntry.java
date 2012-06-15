package footprints.javancss.parse;

public final class MethodEntry
{
  private final String packageName;
  private final String className;
  private final String methodName;
  private final int ncss;
  private final int ccn;
  private final int jvdc;

  MethodEntry( final String packageName,
               final String className,
               final String methodName,
               final int ncss,
               final int ccn,
               final int jvdc )
  {
    this.packageName = packageName;
    this.className = className;
    this.methodName = methodName;
    this.ncss = ncss;
    this.ccn = ccn;
    this.jvdc = jvdc;
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

  public int getCcn()
  {
    return ccn;
  }

  public int getJvdc()
  {
    return jvdc;
  }

  @Override
  public String toString()
  {
    return "[" + packageName + "/" + className + "#" + methodName + " NCSS=" + ncss + " CCN=" + ccn + " JVDC=" + jvdc + "]";
  }
}
