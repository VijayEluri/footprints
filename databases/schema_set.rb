Domgen.define_repository(:footprints) do |repository|
  repository.define_generator(:sql)
  repository.define_generator(:jpa_model)
  repository.define_generator(:jpa_ejb)
  repository.define_generator(:jpa_jta_persistence)

  repository.define_data_module(:JavaNCSS) do |data_module|
    data_module.java.package = 'footprints.javancss.model'

    data_module.define_object_type(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true)
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
