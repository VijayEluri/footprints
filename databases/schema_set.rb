Domgen.repository(:Footprints) do |repository|
  repository.enable_facet(:sql)
  repository.enable_facet(:jpa)
  repository.enable_facet(:ruby)
  repository.enable_facet(:ejb)
  repository.enable_facet(:imit)
  repository.enable_facet(:gwt)

  repository.data_module(:CodeMetrics) do |data_module|
    data_module.jpa.entity_package = 'footprints.javancss.model'
    data_module.ejb.service_package = 'footprints.javancss.service'
    data_module.imit.entity_package = 'footprints.javancss.imit'
        data_module.gwt.enabled = true


    data_module.entity(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true)
    end

    data_module.entity(:MethodMetric) do |t|
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


    data_module.service(:JavaNcss) do |s|
      s.method(:UploadJavaNcssOutput) do |m|
        m.text(:Output)
        m.exception(:FormatError)
      end
    end
  end

  repository.data_module(:TestModule) do |data_module|
    data_module.jpa.entity_package = 'footprints.tester.model'
    data_module.ejb.service_package = 'footprints.tester.service'
    data_module.imit.entity_package = 'footprints.tester.imit'
    data_module.gwt.enabled = true

    data_module.struct(:Fooish) do |s|
      s.text(:Project)
      s.text(:Branch)
      s.text(:Version)
    end

    data_module.entity(:BaseX, :final => false) do |t|
      t.integer(:ID, :primary_key => true)

      t.jpa.query('LookAtMe', "O.ID = :Foo OR O.ID = :ID OR :ElementType = '22'") do |q|
        q.description("Some reason to do stuff")
        q.integer(:Foo, :nullable => true)
        q.s_enum(:ElementType, { "PhysicalUnit" => "PhysicalUnit",
                                 "Crew" => "Crew",
                                 "RoleType" => "RoleType",
                                 "SpecificTask" => "SpecificTask",
                                 "TemplateTask" => "TemplateTask",
                                 "ManagementProject" => "ManagementProject",
                                 "TaskClassification" => "TaskClassification",
                                 "Classification" => "Classification" })

      end
    end

    data_module.enumeration(:CloneAction, :integer, :values => {"CLONE" => 0, "MOVE" => 1, "SKIP" => 2}) do |e|
      e.description(<<-TEXT)
        The action that should be taken on resources when cloning the containing CrewRequirement or PositionRequirement.
        * CLONE : The resources should be copied into new structure
        * MOVE : The resources should be moved into new structure
        * SKIP : The resources should be left in the old structure
      TEXT
    end

    data_module.service(:Collector) do |s|

      s.description("Test Service definition")

      s.method(:RunAllTheTests) do |m|
        m.description("All the F#####g time!")
        m.parameter(:Force, :boolean) do |p|
          p.description("Should we run all the tests or stop at first failing?")
        end
        m.exception(:TestsFailed)
        m.exception(:Problem)
      end

      s.method(:CalculateResultValue) do |m|
        m.parameter(:Input, "java.math.BigDecimal")
        m.reference(:BaseX)
        m.s_enum(:Zang, { "X" => "X", "Y" => "Y" })
        m.returns("java.math.BigDecimal", :nullable => true)
        m.exception(:Problem)
      end
      s.method(:CalculateResultValue2) do |m|
        m.returns(:reference, :references => :BaseX)
      end
      s.method(:CalculateResultValue3) do |m|
        m.returns(:enumeration, :enumeration => data_module.enumeration_by_name(:CloneAction))
      end
      s.method(:CalculateResultValue4) do |m|
        m.returns(:reference, :references => :BaseX, :nullable => true)
      end
    end

    data_module.service(:MyService) do |s|

      s.method(:DoStuff) do |m|
        m.exception(:Problem)
      end
    end

    data_module.entity(:Foo) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:A, :immutable => true)
      t.string(:ZX, 44, :immutable => true)
      t.text(:ZY, :immutable => true)
    end

    data_module.entity(:Bar) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Foo, :immutable => true)
    end

    data_module.entity(:Tester) do |t|
      t.integer(:ID, :primary_key => true)
      t.date(:ADate, :immutable => true)
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
      t.i_enum(:LinkType, { "URL" => 0, "JAVA" => 1 }) do |a|
        a.description(<<TEXT)
  The type of the link.

  * A link that starts an external browser using the URL derived from the Target attribute.
  * A link that invokes some java code with the class name specified in the Target attribute.
TEXT
        t.s_enum(:ElementType, { "PhysicalUnit" => "PhysicalUnit",
                                 "Crew" => "Crew",
                                 "RoleType" => "RoleType",
                                 "SpecificTask" => "SpecificTask",
                                 "TemplateTask" => "TemplateTask",
                                 "ManagementProject" => "ManagementProject",
                                 "TaskClassification" => "TaskClassification",
                                 "Classification" => "Classification" })
      end

    end

    data_module.entity(:ExtendedX, :extends => :BaseX, :final => false) do |t|
      t.string(:Name, 50, :immutable => true)
    end

    data_module.entity(:ExtendedExtendedX, :extends => :ExtendedX) do |t|
      t.string(:Description, 50, :immutable => true)
    end
  end

end
