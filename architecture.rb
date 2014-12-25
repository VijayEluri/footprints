Domgen.repository(:Footprints) do |repository|
  repository.enable_facet(:jpa)
  repository.enable_facet(:ejb)
  repository.enable_facet(:pgsql)

  repository.jpa.provider = :eclipselink
  repository.jpa.base_entity_test_name = repository.jpa.abstract_entity_test_name

  repository.data_module(:CodeMetrics) do |data_module|
    data_module.entity(:Collection) do |t|
      t.integer(:ID, :primary_key => true)
      t.datetime(:CollectedAt, :immutable => true)
    end

    data_module.entity(:MethodMetric) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:Collection, :immutable => true, 'inverse.traversable' => true)
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

    data_module.struct(:CollectionResultDTO) do |s|
      s.integer(:ID)
      s.datetime(:CollectedAt)
    end

    data_module.dao(:MyDAO) do |d|
      d.query(:FindAllCollection, 'jpa.jpql' => 'SELECT O From CodeMetrics_Collection O') do |q|
        q.result_entity = :Collection
      end
      d.query(:FindCollectionResult, 'jpa.sql' => 'SELECT ID, CollectedAt FROM CodeMetrics.tblCollection') do |q|
        q.result_struct = :CollectionResultDTO
      end
      d.query(:FindCollectionCount, 'jpa.jpql' => 'SELECT COUNT(*) From CodeMetrics_Collection O') do |q|
        q.result_type = :integer
      end
      d.query(:FindAllPackageCount, 'jpa.jpql' => 'SELECT COUNT(*) From CodeMetrics_Collection O JOIN O.methodMetrics M GROUP BY M.packageName') do |q|
        q.result_type = :integer
      end
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
        m.text(:Output)
        m.exception(:FormatError)
      end
      s.method(:GetCollections) do |m|
        m.returns(:struct, :referenced_struct => :CollectionDTO, :collection_type => :sequence)
      end
      s.method(:GetCollection) do |m|
        m.integer(:ID)
        m.returns(:struct, :referenced_struct => :CollectionDTO)
      end
    end
  end
end
