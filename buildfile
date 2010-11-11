require 'buildr_bnd'
require 'buildr_iidea'

desc "Footprints: See whos been walking all over our code."
define "footprints" do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.no_ipr

  define "ejb" do
    compile.enhance ["domgen:jpa"]
    compile.from _("generated/main/domgen/java")

    compile.with :javancss, :jhbasic, :ccl, :intellij_annotations, :javaee_api, :javax_validation

    task :clean do
      rm_rf _('generated')
    end

    package(:jar)
  end
end
