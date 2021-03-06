require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/jacoco'
require 'buildr/single_intermediate_layout'

PROVIDED_DEPS = [:javaee_api, :javax_jsr305, :findbugs_annotations]
COMPILE_DEPS = []
PACKAGE_DEPS = [] + COMPILE_DEPS
TEST_DEPS = [:google_guice, :google_guice_assistedinject, :aopalliance, :guiceyloops, :mockito, :glassfish_embedded, :postgresql]

desc 'Footprints: See who has been walking all over our code.'
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  Domgen::Build.define_generate_task([:ee, :jpa_dao_test, :jpa_test_qa, :ejb_test_service_test])

  compile.with PROVIDED_DEPS, COMPILE_DEPS

  test.using :testng
  test.with TEST_DEPS

  package(:war).tap do |war|
    war.libs.clear
    war.libs.concat Buildr.artifacts(PACKAGE_DEPS)
  end

  check package(:war), 'should contain resources and generated classes' do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/Collection.class')
  end

  desc 'DB Archive'
  define 'db' do
    project.no_iml
    Dbt.define_database_package(:default, :include_code => false)
  end

  # Remove generated database directories
  clean { rm_rf "#{File.dirname(__FILE__)}/artifacts" }

  jacoco.generate_xml = true
  jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  iml.add_ejb_facet
  iml.add_jpa_facet
  iml.add_web_facet
  iml.add_jruby_facet

  ipr.add_exploded_war_artifact(project,
                                :build_on_make => true,
                                :enable_ejb => true,
                                :enable_jpa => true,
                                :enable_war => true,
                                :dependencies => [project, PACKAGE_DEPS])

  ipr.add_default_testng_configuration(:jvm_args => "-ea -Xmx2024M -XX:MaxPermSize=364m -Dtest.db.url=#{Dbt.jdbc_url_with_credentials(:default, 'test')}")
end
