def add_atmosphere_jquery_js(project)
  target_dir = project._(:target, :generated, "atmosphere_jquery/main/webapp")
  project.assets.paths << project.file(target_dir) do
    download_task = artifact(:atmosphere_jquery)
    download_task.invoke
    unzip(target_dir => download_task.name).exclude('WEB-INF/**').exclude('META-INF/**').extract unless File.exist?(target_dir)
  end
end
