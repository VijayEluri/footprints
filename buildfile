SLF4J = [:slf4j_api, :slf4j_jdk14, :jcl_over_slf4j]

HIBERNATE = [:hibernate_persistence,
             :hibernate_annotations,
             :javax_transaction,
             :javax_validation,
             :hibernate_validator,
             :hibernate_entitymanager,
             :hibernate_core,
             :hibernate_ehcache,
             :hibernate_c3p0,
             :dom4j,
             :hibernate_commons_annotations,
             :javassist,
             :commons_collections,
             :antlr] + SLF4J

EMF = [:eclipse_uml,
       :eclipse_common,
       :eclipse_ecore,
       :eclipse_emf_common,
       :eclipse_ecore2xml,
       :eclipse_ecore_xmi,
       :eclipse_resources]

class CentralLayout < Layout::Default
  # +key+ is the name of the project
  # +prefix+ is the path to the parent directory for the target and dist directories
  def initialize(key, prefix)
    super()
    self[:target] = "#{prefix}target/#{key}"
    self[:target, :main] = "#{prefix}target/#{key}"
    self[:reports] = "#{prefix}reports/#{key}"
    self[:target, :generated] = "generated"
  end
end

# +key+ is the name of the project
# +top_level+ if the base directory for project is the top level
def define_with_central_layout(name, top_level = false, options = {}, &block)
  options = options.dup
  if !top_level
    options[:layout] = CentralLayout.new(name, '../')
  else
    options[:layout] = CentralLayout.new(name, '')
  end
  define(name, options) do
    project.instance_eval &block
    project.iml.local_repository_env_override = nil if project.iml?
    project.clean { rm_rf _(:target, :generated) }
    project
  end
end


desc "Footprints: See whos been walking all over our code."
define_with_central_layout('footprints', true) do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.ipr.extra_modules << "../dbt/dbt.iml"
  project.ipr.extra_modules << "../replicant/replicant.iml"
  project.ipr.extra_modules << "../domgen/domgen.iml"

  define_with_central_layout('ejb') do

    define_persistence_unit(project, :Footprints, 'footprints/server/entity/code_metrics/Collection.class')

    compile.with :javancss,
                 :jhbasic,
                 :ccl,
                 :intellij_annotations,
                 :javaee_api,
                 :javax_validation,
                 :javax_annotation,
                 :replicant,
                 :json,
                 :google_guice,
                 :google_guice_assistedinject,
                 :gwt_gin,
                 :gwt_user,
                 :gwt_dev

    task :clean do
      rm_rf _('generated')
    end

    iml.add_ejb_facet
    iml.add_jpa_facet
  end

  define_with_central_layout('web') do
    compile.with projects('ejb')

    iml.add_web_facet

    package(:war)
  end

  define_with_central_layout('ear') do
    project.no_iml

    package(:ear, :file => _(:target, :main, "footprints.ear")).tap do |ear|
      ear.display_name = "Footprints Code Dashboard"
      ear.add project("web").package(:war), :context_root => "footprints"
      ear.add :ejb => project('ejb'), :display_name => "Footprints Backend"
    end
  end

  iml.add_jruby_facet

    # Remove generated database directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  # Remove all generated directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/target" }

    # Exclude intermediate dirs from IDEA projects
  project.iml.excluded_directories << "#{File.dirname(__FILE__)}/target"
  project.iml.excluded_directories << "#{File.dirname(__FILE__)}/reports"

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}
  doc.from projects('web', 'ejb')

  ipr.extra_modules << '../replicant/replicant.iml'
end

define_dbt_tasks(Buildr.project("footprints"))