
Domgen.define_schema_set(:iris) do |ss|
  ss.define_generator(:sql)
  ss.define_generator(:jpa)

  ss.define_schema('JavaNCSS') do |s|
    s.sql.schema = 'JavaNCSS'
    s.java.package = 'footprints.javancss.model'

    s.define_object_type(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true)
    end

    s.define_object_type(:MethodMetric) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Collection, :immutable => true)
      t.string(:PackageName, 125, :immutable => true)
      t.string(:ClassName, 125, :immutable => true)
      t.string(:MethodName, 125, :immutable => true)

      # Non commenting source statements
      t.integer(:NCSS, :immutable => true)

      #Cyclomatic complexity
      t.integer(:CCN, :immutable => true)

      #Javadoc comments
      t.integer(:JVDC, :immutable => true)
    end
  end
end
