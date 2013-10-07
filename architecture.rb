Domgen.repository(:Footprints) do |repository|
  repository.enable_facet(:jpa)
  repository.enable_facet(:jackson)
  repository.enable_facet(:ruby)
  repository.enable_facet(:ejb)
  repository.enable_facet(:jaxrs)
  repository.enable_facet(:jms)
  repository.enable_facet(:xml)
  repository.enable_facet(:imit)
  #repository.enable_facet(:restygwt)

  repository.jpa.provider = :eclipselink
  repository.jpa.exclude_unlisted_classes = false
  repository.jpa.properties["eclipselink.session-event-listener"] = "org.realityforge.jeo.geolatte.jpa.eclipselink.GeolatteExtension"

  repository.data_module(:CodeMetrics) do |data_module|

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

  repository.data_module(:Box) do |data_module|
    data_module.entity(:Block) do |t|
      t.integer(:ID, :primary_key => true)
      t.integer(:A, :nullable => true)
      t.integer(:B, :nullable => true)
      t.integer(:C, :nullable => true)
      t.integer(:D, :nullable => true)
      t.integer(:D2, :nullable => true)
      t.integer(:E, :nullable => true)
      t.integer(:F, :nullable => true)
      t.integer(:G, :nullable => true)
      t.integer(:H, :nullable => true)

      t.codependent_constraint([:A, :B])
      t.incompatible_constraint([:C, :D, :D2])
      t.dependency_constraint(:E, [:F])
      t.relationship_constraint(:eq, :G, :H)
    end
  end

  repository.data_module(:Geo) do |data_module|
    data_module.entity(:MobilePOI) do |t|
      t.integer(:ID, :primary_key => true)
      t.text(:Name)
    end
    data_module.entity(:POITrack) do |t|
      t.integer(:ID, :primary_key => true)
      t.reference(:MobilePOI)
      t.point(:Location)
    end
    data_module.entity(:Sector) do |t|
      t.integer(:ID, :primary_key => true)
      t.text(:Name)
      t.polygon(:Location)

      t.sql.index([:Location], :index_type => :gist)
    end

    data_module.entity(:OtherGeom) do |t|
      t.integer(:ID, :primary_key => true)
      t.geometry(:Location, "geometry.srid" => 3124)
      t.geometry(:Location2)
      t.geometry(:Location3, "geometry.dimensions" => 3)
    end
  end
end
