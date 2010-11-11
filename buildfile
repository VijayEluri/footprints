require 'buildr_bnd'
require 'buildr_iidea'

class CentralLayout < Layout::Default
  def initialize(key, top_level, use_subdir)
    super()
    prefix = top_level ? '' : '../'
    subdir = use_subdir ? "/#{key}" : ''
    self[:target] = "#{prefix}target#{subdir}"
    self[:target, :main] = "#{prefix}target#{subdir}"
    self[:reports] = "#{prefix}reports#{subdir}"
  end
end

def define_with_central_layout(name, top_level = false, use_subdir = true, & block)
  define(name, :layout => CentralLayout.new(name, top_level, use_subdir), & block)
end

desc "Footprints: See whos been walking all over our code."
define_with_central_layout("footprints", true, false) do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.no_ipr

  define_with_central_layout("ejb",false) do
    compile.enhance ["domgen:jpa"]
    compile.from _("generated/main/domgen/java")

    compile.with :javancss, :jhbasic, :ccl, :intellij_annotations, :javaee_api, :javax_validation

    task :clean do
      rm_rf _('generated')
    end

    package(:jar)
  end


  define_with_central_layout("web",false) do
    compile.with projects("ejb")

    iml.add_facet("Web","web") do |facet|
      facet.configuration do |conf|
        conf.descriptors do |desc|
          desc.deploymentDescriptor :name => 'web.xml',
            :url => "file://$MODULE_DIR$/src/main/webapp/WEB-INF/web.xml", :optional => "false", :version => "3.0"
        end
        conf.webroots do |webroots|
          webroots.root :url => "file://$MODULE_DIR$/src/main/webapp", :relative => "/"
        end
      end
    end

    package(:war)
  end

  define "ear" do
    package(:ear).tap do |ear|
      ear.display_name = "Footprints Code Dashboard"
      ear.add project("web").package(:war), :context_root => "footprints"
      ear.add :ejb => project('ejb'), :display_name => "Footprints Backend"
    end
  end
end
