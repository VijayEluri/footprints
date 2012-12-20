def add_atmosphere_jquery_js(project)
  target_dir = project._(:target, :generated, "atmosphere_jquery/main/webapp")
  task 'unzip_atmosphere_jquery' do
    download_task = artifact(:atmosphere_jquery)
    download_task.invoke
    unzip(target_dir => download_task.name).exclude('WEB-INF/**').exclude('META-INF/**').extract unless File.exist?(target_dir)
  end
  project.resources do
    task('unzip_atmosphere_jquery').invoke
  end

  target_dir
end
