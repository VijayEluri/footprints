Domgen.repository(:Footprints) do |repository|
  repository.enable_facet(:jpa)
  repository.enable_facet(:jackson)
  repository.enable_facet(:ruby)
  repository.enable_facet(:ejb)
  repository.enable_facet(:jaxrs)
  repository.enable_facet(:jms)
  repository.enable_facet(:xml)
  repository.enable_facet(:imit)

  repository.jpa.provider = :eclipselink
  repository.jpa.exclude_unlisted_classes = false

  repository.data_module(:CodeMetrics) do |data_module|

    data_module.entity(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true) do |a|
        a.disable_facet(:imit)
      end
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

    data_module.struct(:MethodDTO) do |ss|
      ss.string(:PackageName, 500)
      ss.string(:ClassName, 500)
      ss.string(:MethodName, 500)

      # Non commenting source statements
      ss.integer(:NCSS)

      #Cyclomatic complexity
      ss.integer(:CCN)

      #Javadoc comments
      ss.integer(:JVDC)
    end

    data_module.struct(:CollectionDTO) do |s|
      s.integer(:ID)
      s.datetime(:CollectedAt)
      s.struct(:Method, :MethodDTO, :collection_type => :sequence)
    end

    data_module.exception(:BaseFormatError, :final => false) do |e|
      e.text(:File)
    end

    data_module.exception(:FormatError, :extends => :BaseFormatError) do |e|
      e.integer(:Line)
    end

    data_module.service(:JavaNcss) do |s|
      s.ejb.generate_boundary = true
      s.method(:UploadJavaNcssOutput) do |m|
        #m.jms.mdb = true
        m.text(:Output)
        m.exception(:FormatError)
      end
      s.method(:GetCollections) do |m|
        m.returns(:struct, :referenced_struct => :CollectionDTO, :collection_type => :sequence)
      end
      s.method(:GetCollection) do |m|
        m.integer(:ID, "jaxrs.param_type" => :path)
        m.returns(:struct, :referenced_struct => :CollectionDTO)
      end
    end
  end
end
