require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/jacoco'
require 'buildr/single_intermediate_layout'

VALIDATOR_JARS = [
  'org.hibernate:hibernate-validator:jar:5.0.1.Final',
  'org.jboss.logging:jboss-logging:jar:3.1.3.GA',
  'com.fasterxml:classmate:jar:0.9.0'
]

desc "Footprints: See who has been walking all over our code."
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints,
                           "server",
                           [:ee,
                            :gwt,
                            :gwt_rpc_server_service,
                            :gwt_rpc_shared_service,
                            :gwt_rpc_client_service,
                            :gwt_client_jso,
                            :imit,
                            :imit_json,
                            :imit_jpa,
                            :imit_gwt_proxy],
                           _(:target, :generated, "domgen"))

  compile.with :javaee_api,
               VALIDATOR_JARS,
               :replicant,
               :eventbinder,
               :javax_annotation,
               :jackson_core,
               :google_guice,
               :google_guice_assistedinject,
               :aopalliance,
               :jackson_mapper,
               :gwt_datatypes,
               :gwt_user,
               :gwt_dev,
               :gwt_gin,
               :javax_validation_sources

  gwt_superdev_runner("footprints.FootprintsDev",
                      :java_args => ["-Xms512M", "-Xmx1024M", "-XX:PermSize=128M", "-XX:MaxPermSize=256M"],
                      :draft_compile => (ENV["FAST_GWT"] == 'true'))
  gwt(["footprints.Footprints"],
      :java_args => ["-Xms512M", "-Xmx1024M", "-XX:PermSize=128M", "-XX:MaxPermSize=256M"],
      :draft_compile => (ENV["FAST_GWT"] == 'true'))

  package(:war).tap do |war|
    project.assets.paths.each do |asset|
      war.enhance([asset])
      war.include asset, :as => '.'
    end
  end

  check package(:war), "should contain resources and generated classes" do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/code_metrics/Collection.class')
  end

  desc "DB Archive"
  define 'db' do
    project.no_iml
    Dbt.define_database_package(:default, project, :include_code => false)
  end

  # Remove generated database directories
  clean { rm_rf "#{File.dirname(__FILE__)}/artifacts" }
  clean { rm_rf "#{File.dirname(__FILE__)}/database/generated" }

  jacoco.generate_xml = true
  jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  ipr.extra_modules << '../dbt/dbt.iml'
  ipr.extra_modules << '../dbt_doc/dbt_doc.iml'
  ipr.extra_modules << '../domgen/domgen.iml'

  iml.add_gwt_facet({'footprints.FootprintsDev' => true,
                     'footprints.Footprints' => false},
                    :settings => {:compilerMaxHeapSize => "1024",
                                  :additionalCompilerParameters => '-Dgwt.usearchives=false -Dgwt.persistentunitcache=false'})

  iml.add_ejb_facet
  iml.add_jpa_facet
  iml.add_web_facet
  iml.add_jruby_facet

  ipr.add_exploded_war_artifact(project,
                                :build_on_make => true,
                                :enable_ejb => true,
                                :enable_jpa => true,
                                :enable_gwt => true,
                                :enable_war => true,
                                :dependencies => [project,
                                                  :gwt_user
                                ])
end
