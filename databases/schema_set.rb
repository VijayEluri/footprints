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
    end

    data_module.define_object_type(:MethodMetric) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Collection, :immutable => true) do |a|
        a.inverse.traversable = true
      end
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

    data_module.define_object_type(:Foo) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:A, :immutable => true)
    end

    data_module.define_object_type(:Bar) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Foo, :immutable => true)
    end

    data_module.define_object_type(:Tester) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:A, :immutable => true)
      t.datetime(:B, :nullable => true)
      t.integer(:C, :nullable => true)
      t.integer(:D, :nullable => true)
      t.integer(:E, :nullable => true)
      t.integer(:F, :nullable => true)
      t.reference(:Foo, :immutable => true) do |a|
        a.inverse.multiplicity = :one
        a.inverse.traversable = true
      end
      t.reference(:Bar) do |a|
        a.inverse.traversable = true
      end

      t.sql.constraint("TestConstraint", :sql => "#{Domgen::Sql.dialect.quote("A")} IS NOT NULL")
      t.sql.index([:B], :filter => "#{Domgen::Sql.dialect.quote("B")} IS NOT NULL", :unique => true)

      t.cycle_constraint(:Bar, [:Foo])
      t.unique_constraint([:C])
      t.incompatible_constraint([:B, :C])
      t.dependency_constraint(:D, [:E])
      t.codependent_constraint([:E, :F])

      t.relationship_constraint(:eq, :A, :B)
      t.relationship_constraint(:neq, :A, :B)
      t.relationship_constraint(:gt, :A, :B)
      t.relationship_constraint(:lt, :A, :B)
      t.relationship_constraint(:gte, :A, :B)
      t.relationship_constraint(:lte, :A, :B)
      t.relationship_constraint(:lte, :C, :D)
    end
  end
end
