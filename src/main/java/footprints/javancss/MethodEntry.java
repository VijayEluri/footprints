package footprints.javancss;

final class MethodEntry
{
  final String packageName;
  final String className;
  final String methodName;
  final int ncss;
  final int ccn;
  final int jvdc;

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

  @Override
  public String toString()
  {
    return "[" + packageName + "/" + className + "#" + methodName + " NCSS=" + ncss + " CCN=" + ccn + " JVDC=" + jvdc + "]";
  }
}
