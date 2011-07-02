Domgen.define_repository(:footprints) do |repository|
  repository.define_generator(:mssql)
  repository.define_generator(:pgsql)
  repository.define_generator(:jpa_model)
  repository.define_generator(:jpa_ejb)
  repository.define_generator(:jpa_jta_persistence)

  repository.define_data_module(:JavaNCSS) do |data_module|
    data_module.java.package = 'footprints.javancss.model'

    data_module.define_object_type(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true)

      t.sql.constraint("TestConstraint", :sql => "#{Domgen::Sql.dialect.quote("CollectedAt")} IS NOT NULL")
      t.sql.index([:CollectedAt], :filter => "#{Domgen::Sql.dialect.quote("CollectedAt")} IS NOT NULL", :unique => true)
    end

    data_module.define_object_type(:MethodMetric) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Collection, :immutable => true)
      t.string(:PackageName, 500, :immutable => true)
      t.string(:ClassName, 500, :immutable => true)
      t.string(:MethodName, 500, :immutable => true)

      # Non commenting source statements
      t.integer(:NCSS, :immutable => true)

      #Cyclomatic complexity
      t.integer(:CCN, :immutable => true)

      #Javadoc comments
      t.integer(:JVDC, :immutable => true)
    end
  end
end
